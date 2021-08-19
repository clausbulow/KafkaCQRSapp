package dk.ksf.cqrs.events.internalmessages;

import dk.ksf.cqrs.events.CqrsContext;
import dk.ksf.cqrs.events.annotations.CommandHandler;
import dk.ksf.cqrs.events.annotations.EventHandler;
import dk.ksf.cqrs.events.annotations.EventSourcingHandler;
import dk.ksf.cqrs.events.model.BusinessEvent;
import dk.ksf.cqrs.events.service.CqrsMetaInfo;
import dk.ksf.cqrs.events.service.EventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.*;

@Slf4j
public abstract class HandlerContainer {
    private final AutowireCapableBeanFactory beanFactory;
    private final CqrsMetaInfo metaInfo;
    private final EventService eventService;

    public abstract List<Class> memberAnnotationsOfInterest();


    private final Map<Class, List<HandlerExecutor>> handlerExecutors = new HashMap<>();

    private Class containerClass;

    private HandlerExectutorFactory handlerExectutorFactory;

    public HandlerContainer(Class containerClass, AutowireCapableBeanFactory beanFactory, CqrsMetaInfo metaInfo, EventService eventService) {
        this.containerClass = containerClass;
        this.beanFactory = beanFactory;
        this.metaInfo = metaInfo;
        this.handlerExectutorFactory = new HandlerExectutorFactory(metaInfo);
        this.eventService = eventService;

    }

    public Class getContainerClass() {
        return containerClass;
    }

    void scanForAnnotations() throws Exception {

        ReflectionUtils.doWithMethods(containerClass, method -> {
            List<Class> classes = memberAnnotationsOfInterest();
            classes.forEach(annotationClass -> {
                Annotation annotation = AnnotationUtils.findAnnotation(method, annotationClass);
                if (annotation != null) {
                    try {
                        createHandlerExecutor(method, annotation, annotationClass, beanFactory);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        });
    }

    private void createHandlerExecutor(Method method, Annotation annotation, Class annotationClass, AutowireCapableBeanFactory factory) throws Exception {
        //Assert that BusinessEvent and command handler is last param
        ResolvableType targetType = ResolvableType.forMethodParameter(method, method.getParameterCount()-1);
        HandlerExecutor handlerExecutor = handlerExectutorFactory.createHandlerExecutor(HandlerFactoryParams.builder().
                method(method).
                targetType(targetType).
                annotationClass(annotationClass).
                factory(factory).
                owner(this).
                annotation(annotation).build());
        List<HandlerExecutor> executors;
        if (!handlerExecutors.containsKey(annotationClass)) {
            executors = new ArrayList<>();
            handlerExecutors.put(annotationClass, executors);
        } else {
            executors = handlerExecutors.get(annotationClass);
        }
        executors.add(handlerExecutor);
    }



    public List<HandlerExecutor> getEventSourcingHandlerExecutors() {
        if (handlerExecutors.containsKey(EventSourcingHandler.class)) {
            return handlerExecutors.get(EventSourcingHandler.class);
        }
        return new ArrayList<>();
    }

    public List<HandlerExecutor> getEventHandlerExecutors() {
        if (handlerExecutors.containsKey(EventHandler.class)) {
            return handlerExecutors.get(EventHandler.class);
        }
        return new ArrayList<>();
    }

    public List<HandlerExecutor> getCommandHandlerExecutors() {
        if (handlerExecutors.containsKey(CommandHandler.class)) {
            return handlerExecutors.get(CommandHandler.class);
        }
        return new ArrayList<>();
    }


    public void signalCommandHandlers(CqrsContext context, Object command) throws Exception {
        List<HandlerExecutor> commandHandlerExecutors = getCommandHandlerExecutors();
        context.setKey((String)metaInfo.getAggregateIdentifierFromClass(command.getClass()).get(command));
        commandHandlerExecutors.forEach(executor -> {
            if (!executor.supports(command)){
                return;
            }
            try {
                Object target;
                if (executor.createsAggregate()){
                    target = this.containerClass.getConstructor().newInstance();
                    setTargetKeyValue(target, context.getKey());
                    beanFactory.autowireBean(target);
                } else {
                    target= getTargetInstance(context, command, context.getKey());
                }
                context.setTargetInstance(target);
                Object commandResult = executor.execute(context, command);
                if (commandResult != null){
                    if (command.getClass().isArray()) {
                        Object[] commandresults = (Object[]) commandResult;
                        Arrays.asList(commandresults).forEach(result -> {
                            try {
                                processSingleCommandResult(context,result);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                    } {
                        processSingleCommandResult(context,commandResult);
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("Unable to execute: " + e.getMessage());
            }
        });
    }


    private void processSingleCommandResult(CqrsContext context, Object event) throws Exception {
        log.info("Processing event produced from handler");
        if (metaInfo.getEventName(event.getClass()) != null) {
            signalEventSourcingHandlers(context, event);
            eventService.fireEvent(context.getTargetInstance(),context,event);
        }

    }

    protected abstract Object getTargetInstance(CqrsContext context, Object command, String keyRef) throws Exception;

    protected abstract void setTargetKeyValue(Object target, String keyRef) throws Exception;

    protected abstract void save(Object target, String keyRef) throws Exception;


    public void signalEventHandlers(CqrsContext context, Object event) throws Exception {
        List<HandlerExecutor> eventHandlerExecutors = getEventHandlerExecutors();
        context.setTargetInstance(beanFactory.getBean(this.containerClass));
        eventHandlerExecutors.forEach(executor -> {
            try {
                executor.execute(context, event);
            } catch (Exception e) {
            }
        });
    }

    public void signalEventSourcingHandlers(CqrsContext context, Object event) throws Exception {
        List<HandlerExecutor> eventHandlerExecutors = getEventSourcingHandlerExecutors();
        context.setKey((String)metaInfo.getAggregateIdentifierFromClass(event.getClass()).get(event));

        eventHandlerExecutors.forEach(executor -> {
            try {
                if (!executor.supports(event)){
                    return;
                }
                Object target = context.getTargetInstance();
                if (target == null){
                       target = getTargetInstance(context, event, context.getKey());
                        if (target == null) {
                            target = this.containerClass.getConstructor().newInstance();
                            setTargetKeyValue(target, context.getKey());
                            beanFactory.autowireBean(target);
                        }
                        context.setTargetInstance(target);
                }

                executor.execute(context, event);
                save(target, context.getKey());

            } catch (Exception e) {
            }
        });
    }


}
