package dk.ksf.cqrs.events.internalmessages;

import dk.ksf.cqrs.events.annotations.CommandHandler;
import dk.ksf.cqrs.events.annotations.EventHandler;
import dk.ksf.cqrs.events.annotations.EventSourcingHandler;
import dk.ksf.cqrs.events.service.EventService;
import dk.ksf.cqrs.exceptions.ExceptionConsumer;
import dk.ksf.cqrs.exceptions.MessageException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

@Slf4j
public abstract class AbstractExecutablesContainer {
    private final AutowireCapableBeanFactory beanFactory;
    private final CqrsMetaInfo metaInfo;
    private final EventService eventService;
    private final Map<Class, List<AbstractExecutor>> handlerExecutors = new HashMap<>();
    private final Class containerClass;
    private final ExectutorFactory exectutorFactory;

    public AbstractExecutablesContainer(Class containerClass, AutowireCapableBeanFactory beanFactory, CqrsMetaInfo metaInfo, EventService eventService) {
        this.containerClass = containerClass;
        this.beanFactory = beanFactory;
        this.metaInfo = metaInfo;
        this.exectutorFactory = new ExectutorFactory(metaInfo);
        this.eventService = eventService;

    }

    public abstract List<Class> memberAnnotationsOfInterest();

    public Class getContainerClass() {
        return containerClass;
    }

    void scanForAnnotations() {

        ReflectionUtils.doWithMethods(containerClass, method -> {
            List<Class> classes = memberAnnotationsOfInterest();
            classes.forEach(ExceptionConsumer.wrapper(annotationClass -> {
                Annotation annotation = AnnotationUtils.findAnnotation(method, annotationClass);
                if (annotation != null) {
                    createHandlerExecutor(method, annotation, annotationClass, beanFactory);
                }
            }));
        });
    }

    private void createHandlerExecutor(Method method, Annotation annotation, Class annotationClass, AutowireCapableBeanFactory factory) throws Exception {
        //Assert that BusinessEvent and command handler is last param
        final ResolvableType targetType = ResolvableType.forMethodParameter(method, method.getParameterCount() - 1);
        final AbstractExecutor executor = exectutorFactory.createHandlerExecutor(ExecutorFactoryParams.builder().
                method(method).
                targetType(targetType).
                annotationClass(annotationClass).
                factory(factory).
                owner(this).
                annotation(annotation).build());
        final List<AbstractExecutor> executors;
        if (!handlerExecutors.containsKey(annotationClass)) {
            executors = new ArrayList<>();
            handlerExecutors.put(annotationClass, executors);
        } else {
            executors = handlerExecutors.get(annotationClass);
        }
        executors.add(executor);
    }


    public List<AbstractExecutor> getEventSourcingExecutors() {
        if (handlerExecutors.containsKey(EventSourcingHandler.class)) {
            return handlerExecutors.get(EventSourcingHandler.class);
        }
        return new ArrayList<>();
    }

    public List<AbstractExecutor> getEventExecutors() {
        if (handlerExecutors.containsKey(EventHandler.class)) {
            return handlerExecutors.get(EventHandler.class);
        }
        return new ArrayList<>();
    }

    public List<AbstractExecutor> getCommandExecutors() {
        if (handlerExecutors.containsKey(CommandHandler.class)) {
            return handlerExecutors.get(CommandHandler.class);
        }
        return new ArrayList<>();
    }


    public void signalCommandHandlers(MessageContext context, Object command, TransactionTemplate transactionTemplate) throws Exception {
        final List<AbstractExecutor> commandExecutors = getCommandExecutors();
        context.setKey((String) metaInfo.getAggregateIdentifierFromClass(command.getClass()).get(command));
        commandExecutors.forEach(ExceptionConsumer.wrapper(executor -> {
            if (!executor.supports(command)) {
                return;
            }
            Object target;
            if (executor.createsAggregate()) {
                target = this.containerClass.getConstructor().newInstance();
                setTargetKeyValue(target, context.getKey());
                beanFactory.autowireBean(target);
            } else {
                target = getTargetInstance(context, command, context.getKey());
                if (target == null){
                    throw new MessageException("Cannot excecute command, because item "+context.getKey() + " in class "+this.containerClass + " can not be found");
                }
            }
            context.setTargetInstance(target);
            final Object commandResult = executor.execute(context, command);
            if (commandResult != null) {
                if (command.getClass().isArray()) {
                    Object[] commandresults = (Object[]) commandResult;
                    Arrays.asList(commandresults).forEach(ExceptionConsumer.wrapper(result -> processSingleCommandResult(context, result, transactionTemplate)));
                }
                {
                    processSingleCommandResult(context, commandResult, transactionTemplate);
                }
            }
        }));
    }


    private void processSingleCommandResult(MessageContext context, Object event, TransactionTemplate transactionTemplate) throws Exception {
        log.info("Processing event produced from commandhandler");
        if (metaInfo.getEventName(event.getClass()) != null) {
            transactionTemplate.executeWithoutResult( t -> {
                try {
                    signalEventSourcingHandlers(context, event);
                    eventService.fireEvent(context.getTargetInstance(), context, event);
                } catch (Exception e){
                    t.setRollbackOnly();                }
            });
        }

    }

    protected abstract Object getTargetInstance(MessageContext context, Object command, String keyRef) throws Exception;

    protected abstract void setTargetKeyValue(Object target, String keyRef) throws Exception;

    protected abstract void save(Object target, String keyRef) throws Exception;


    public void signalEventHandlers(MessageContext context, Object event) {
        final List<AbstractExecutor> eventExecutors = getEventExecutors();
        context.setTargetInstance(beanFactory.getBean(this.containerClass));
        eventExecutors.forEach(ExceptionConsumer.wrapper(excecutor -> excecutor.execute(context, event)));
    }

    public void signalEventSourcingHandlers(MessageContext context, Object event) throws Exception {
        final List<AbstractExecutor> eventSourcingExecutors = getEventSourcingExecutors();
        context.setKey((String) metaInfo.getAggregateIdentifierFromClass(event.getClass()).get(event));

        eventSourcingExecutors.forEach(ExceptionConsumer.wrapper(executor -> {
            if (!executor.supports(event)) {
                return;
            }
            Object target = context.getTargetInstance();
            if (target == null) {
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
        }));
    }


}
