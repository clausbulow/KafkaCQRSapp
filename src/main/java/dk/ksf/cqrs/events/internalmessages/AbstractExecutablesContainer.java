package dk.ksf.cqrs.events.internalmessages;

import dk.ksf.cqrs.events.CqrsContext;
import dk.ksf.cqrs.events.annotations.CommandHandler;
import dk.ksf.cqrs.events.annotations.EventHandler;
import dk.ksf.cqrs.events.annotations.EventSourcingHandler;
import dk.ksf.cqrs.events.service.EventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

@Slf4j
public abstract class AbstractExecutablesContainer {
    private final AutowireCapableBeanFactory beanFactory;
    private final CqrsMetaInfo metaInfo;
    private final EventService eventService;

    public abstract List<Class> memberAnnotationsOfInterest();


    private final Map<Class, List<AbstractExecutor>> handlerExecutors = new HashMap<>();

    private Class containerClass;

    private ExectutorFactory exectutorFactory;

    public AbstractExecutablesContainer(Class containerClass, AutowireCapableBeanFactory beanFactory, CqrsMetaInfo metaInfo, EventService eventService) {
        this.containerClass = containerClass;
        this.beanFactory = beanFactory;
        this.metaInfo = metaInfo;
        this.exectutorFactory = new ExectutorFactory(metaInfo);
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
        AbstractExecutor abstractExecutor = exectutorFactory.createHandlerExecutor(ExecutorFactoryParams.builder().
                method(method).
                targetType(targetType).
                annotationClass(annotationClass).
                factory(factory).
                owner(this).
                annotation(annotation).build());
        List<AbstractExecutor> executors;
        if (!handlerExecutors.containsKey(annotationClass)) {
            executors = new ArrayList<>();
            handlerExecutors.put(annotationClass, executors);
        } else {
            executors = handlerExecutors.get(annotationClass);
        }
        executors.add(abstractExecutor);
    }



    public List<AbstractExecutor> getEventSourcingHandlerExecutors() {
        if (handlerExecutors.containsKey(EventSourcingHandler.class)) {
            return handlerExecutors.get(EventSourcingHandler.class);
        }
        return new ArrayList<>();
    }

    public List<AbstractExecutor> getEventHandlerExecutors() {
        if (handlerExecutors.containsKey(EventHandler.class)) {
            return handlerExecutors.get(EventHandler.class);
        }
        return new ArrayList<>();
    }

    public List<AbstractExecutor> getCommandHandlerExecutors() {
        if (handlerExecutors.containsKey(CommandHandler.class)) {
            return handlerExecutors.get(CommandHandler.class);
        }
        return new ArrayList<>();
    }


    public void signalCommandHandlers(CqrsContext context, Object command) throws Exception {
        List<AbstractExecutor> commandAbstractExecutors = getCommandHandlerExecutors();
        context.setKey((String)metaInfo.getAggregateIdentifierFromClass(command.getClass()).get(command));
        commandAbstractExecutors.forEach(executor -> {
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
        List<AbstractExecutor> eventAbstractExecutors = getEventHandlerExecutors();
        context.setTargetInstance(beanFactory.getBean(this.containerClass));
        eventAbstractExecutors.forEach(executor -> {
            try {
                executor.execute(context, event);
            } catch (Exception e) {
            }
        });
    }

    public void signalEventSourcingHandlers(CqrsContext context, Object event) throws Exception {
        List<AbstractExecutor> eventAbstractExecutors = getEventSourcingHandlerExecutors();
        context.setKey((String)metaInfo.getAggregateIdentifierFromClass(event.getClass()).get(event));

        eventAbstractExecutors.forEach(executor -> {
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
