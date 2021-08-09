package dk.ksf.cqrs.events.internalmessages;

import dk.ksf.cqrs.events.annotations.*;
import dk.ksf.cqrs.events.model.BusinessEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.data.util.ClassTypeInformation;
import org.springframework.data.util.TypeInformation;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import javax.annotation.PostConstruct;
import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Stream;

@Component("testdispatcher")
@Slf4j
public class EventDispatcher {
    @Autowired
    ApplicationContext context;

    Map<TypeInformation, HandlerInfo> commandHandlers = new HashMap<>();
    Map<String, HandlerInfo> eventSourcingHandlers = new HashMap<>();
    Map<String, List<HandlerInfo>> eventHandlers = new HashMap<>();
    Map<TypeInformation, AggregateInfo> aggregates = new HashMap<>();

    @Autowired
    AutowireCapableBeanFactory beanFactory;


    @PostConstruct
    public void initEventsList() throws Exception {
        scanForCQRSObjects();
    }

    private void scanForCQRSObjects() throws Exception {
        final ClassPathScanningCandidateComponentProvider scanner;
        scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(Aggregate.class));
        scanner.addIncludeFilter(new AnnotationTypeFilter(Perspective.class));
        final Set<BeanDefinition> candidateComponents = scanner.findCandidateComponents("dk.ksf.application");
        log.info("scanning done");
        for (BeanDefinition definition : candidateComponents) {
            System.out.println(definition.getBeanClassName());
            Class clazz = Class.forName(definition.getBeanClassName());
            ClassTypeInformation clazzTypeInformation = ClassTypeInformation.from(clazz);
            Aggregate aggregateAnnotation = (Aggregate) clazz.getAnnotation(Aggregate.class);
            if (aggregateAnnotation != null){
                proceessAggregate(clazz,clazzTypeInformation, aggregateAnnotation);
            } else {
                Perspective perspectiveAnnotation = (Perspective)  clazz.getAnnotation(Perspective.class);
                if (perspectiveAnnotation != null){
                    processPerspective(clazz, clazzTypeInformation, perspectiveAnnotation);
                }

            }
        }
    }

    private void processPerspective(Class clazz, ClassTypeInformation clazzTypeInformation, Perspective perspectiveAnnotation) {
        AggregateInfo aggregateInfo = new AggregateInfo();
        aggregateInfo.setAggregateClass(clazz);
        final Stream<Method> executables = Arrays.stream(clazz.getMethods());
        executables.forEach(method -> {
            final EventHandler eventHandler = AnnotationUtils.findAnnotation(method, EventHandler.class);
            if (eventHandler != null) {
                processEventHandler(clazzTypeInformation, method, eventHandler);
            }
        });

    }

    private void processEventHandler(ClassTypeInformation clazzTypeInformation, Method method, EventHandler eventHandler) {
        List <TypeInformation<?>> parameterTypes = clazzTypeInformation.getParameterTypes(method);
        TypeInformation info = parameterTypes.iterator().next();
        ResolvableType resolvableType = ResolvableType.forMethodParameter(method, 0);
        HandlerInfo handlerInfo = HandlerInfo.createHandlerInfo(clazzTypeInformation, method, false, info.getType());
        String key = resolvableType.toString();
        List<HandlerInfo> handlers;
        if (!eventHandlers.containsKey(key)){
            handlers = new ArrayList<>();
            eventHandlers.put(key, handlers);
        } else {
            handlers = eventHandlers.get(key);
        }
        handlers.add(handlerInfo);
    }

    private void proceessAggregate(Class clazz, ClassTypeInformation clazzTypeInformation, Aggregate aggregateAnnotation) {
        AggregateInfo aggregateInfo = new AggregateInfo();
        aggregateInfo.setAggregateClass(clazz);
        aggregateInfo.setAggregateType(aggregateAnnotation.aggregateType());
        aggregateInfo.setRepository(beanFactory.getBean(aggregateAnnotation.repository()));


        ReflectionUtils.doWithLocalFields(clazz, field -> {
            AggregateIdentifier annotation = field.getAnnotation(AggregateIdentifier.class);
            if (annotation != null) {
                ReflectionUtils.makeAccessible(field);
                aggregateInfo.setKeyField(field);
            }
        });
        aggregates.put(clazzTypeInformation, aggregateInfo);

        //new TypeDescriptor.OfMethod(clazz.getMethod("test",clazz));
        final Stream<Method> executables = Arrays.stream(clazz.getMethods());
        executables.forEach(method -> {
            final CommandHandler commandHandlerAnnotation = AnnotationUtils.findAnnotation(method, CommandHandler.class);
            if (commandHandlerAnnotation != null) {
                processCommandHandler(clazzTypeInformation, method, commandHandlerAnnotation);
            } else {
                final EventSourcingHandler eventSourcingHandler = AnnotationUtils.findAnnotation(method, EventSourcingHandler.class);
                if (eventSourcingHandler != null){
                    processEventSourcingHandler(clazzTypeInformation, method, eventSourcingHandler);
                }
            }
        });

    }

    private void processEventSourcingHandler(ClassTypeInformation clazzTypeInformation, Method method, EventSourcingHandler eventSourcingHandler) {
        List <TypeInformation<?>> parameterTypes = clazzTypeInformation.getParameterTypes(method);
        TypeInformation info = parameterTypes.iterator().next();
        ResolvableType resolvableType = ResolvableType.forMethodParameter(method, 0);
        HandlerInfo handlerInfo = HandlerInfo.createHandlerInfo(clazzTypeInformation, method, false, info.getType());
        eventSourcingHandlers.put(resolvableType.toString(), handlerInfo);
    }

    private void processCommandHandler(ClassTypeInformation clazzTypeInformation, Method method, CommandHandler commandHandlerAnnotation) {
        List <TypeInformation<?>> parameterTypes = clazzTypeInformation.getParameterTypes(method);
        TypeInformation commandType =  parameterTypes.iterator().next();
        boolean createsAggregates = commandHandlerAnnotation.createsAggregate();
        HandlerInfo handlerInfo = HandlerInfo.createHandlerInfo(clazzTypeInformation, method, createsAggregates, commandType.getType());
        commandHandlers.put(commandType, handlerInfo);
    }

    public void publishEventToEventSourcing(BusinessEvent<?> event) throws Exception{
        ClassTypeInformation<?> classTypeInformation = ClassTypeInformation.from(event.getClass());
        ResolvableType resolvableType = event.getResolvableType();
        //ResolvableType resolvableType = ResolvableType.forInstance(event);
        TypeInformation<?> requiredActualType = classTypeInformation.getRequiredActualType();
        signalEventSourcingHandler(event,classTypeInformation, resolvableType);
    }
    public void publishEventToEventHandlers(BusinessEvent<?> event) throws Exception{
        ClassTypeInformation<?> classTypeInformation = ClassTypeInformation.from(event.getClass());
        ResolvableType resolvableType = event.getResolvableType();
        //ResolvableType resolvableType = ResolvableType.forInstance(event);
        TypeInformation<?> requiredActualType = classTypeInformation.getRequiredActualType();
        signalEventHandlers(event,classTypeInformation, resolvableType);
    }

    public void publishEvent(BusinessEvent<?> event) throws Exception {
        
        ClassTypeInformation<?> classTypeInformation = ClassTypeInformation.from(event.getClass());
        ResolvableType resolvableType = event.getResolvableType();
        //ResolvableType resolvableType = ResolvableType.forInstance(event);
        TypeInformation<?> requiredActualType = classTypeInformation.getRequiredActualType();
        signalEventSourcingHandler(event,classTypeInformation, resolvableType);
        signalEventHandlers(event,classTypeInformation, resolvableType);
    }

    private void signalEventHandlers(BusinessEvent<?> event,ClassTypeInformation<?> classTypeInformation, ResolvableType resolvableType) {
        log.info("Signaling event handlers");
        final List<HandlerInfo> handlers = eventHandlers.get(resolvableType.toString());
        handlers.forEach(handler ->{
            Object bean = beanFactory.getBean(handler.getAggregate().getType());
            ReflectionUtils.invokeMethod( handler.getHandlerMethod(), bean, event);
        });
    }

    private void signalEventSourcingHandler(BusinessEvent<?> event,ClassTypeInformation<?> classTypeInformation, ResolvableType resolvableType) throws Exception{
        log.info("Signaling event sourcing handler");
        final HandlerInfo handlerInfo = eventSourcingHandlers.get(resolvableType.toString());
        final AggregateInfo aggregateInfo = aggregates.get(handlerInfo.getAggregate());
        final Optional optionalAggregateItem = aggregateInfo.repository.findById(event.getKey());
        Object aggregate;
        if (optionalAggregateItem.isPresent()) {
            aggregate = optionalAggregateItem.get();
        } else {
            aggregate = aggregateInfo.getAggregateClass().getDeclaredConstructor().newInstance();
            beanFactory.autowireBean(aggregate);
        }
        ReflectionUtils.invokeMethod( handlerInfo.getHandlerMethod(), aggregate, event);
        aggregateInfo.repository.save(aggregate);
    }

    public void publishCommand(Object command) throws InvocationTargetException, IllegalAccessException, InstantiationException, NoSuchMethodException {
        log.info("Publishing command");
        final ClassTypeInformation<?> classTypeInformation = ClassTypeInformation.from(command.getClass());
        final HandlerInfo handlerInfo = commandHandlers.get(classTypeInformation);
        final Method method = handlerInfo.getHandlerMethod();
        final Field keyRefField = handlerInfo.getKeyRefField();


        final ClassTypeInformation aggregatType = handlerInfo.getAggregate();
        final AggregateInfo aggregateInfo = aggregates.get(aggregatType);
        final Field keyField = aggregateInfo.getKeyField();
        if (handlerInfo.isCreatesAggregate()) {
            final Object aggregateItem = aggregateInfo.getAggregateClass().getDeclaredConstructor().newInstance();
            beanFactory.autowireBean(aggregateItem);
            keyField.set(aggregateItem,keyRefField.get(command));
            ReflectionUtils.invokeMethod( method, aggregateItem, command);
            aggregateInfo.repository.save(aggregateItem);
            log.info("Aggregate created..");
        } else {
            final Optional optionalAggregateItem = aggregateInfo.repository.findById(keyRefField.get(command));
            optionalAggregateItem.ifPresent(aggregateItem -> {
                ReflectionUtils.invokeMethod((Method) method, aggregateItem, command);
                aggregateInfo.repository.save(aggregateItem);
                log.info("Aggregate updated");
            });
        }

    }

}
