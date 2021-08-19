package dk.ksf.cqrs.events.internalmessages;

import dk.ksf.cqrs.events.CqrsContext;
import dk.ksf.cqrs.events.annotations.Aggregate;
import dk.ksf.cqrs.events.annotations.CommandHandler;
import dk.ksf.cqrs.events.annotations.TargetAggregateIdentifier;
import dk.ksf.cqrs.events.service.CqrsMetaInfo;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.repository.CrudRepository;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class CommandHandlerExecutor extends HandlerExecutor {
    private final CrudRepository repository;
    private final boolean createsAggregate;
    private  Field targetAggregateIdentifier = null;

    public CommandHandlerExecutor(HandlerContainer owner, Method method, ResolvableType supportsType, AutowireCapableBeanFactory factory, CqrsMetaInfo metaInfo) {
        super(owner, method,supportsType, factory);
        Aggregate classAnnotation = AnnotationUtils.findAnnotation(method.getDeclaringClass(), Aggregate.class);
        repository = factory.getBean(classAnnotation.repository());
        CommandHandler handlerAnnotation = AnnotationUtils.findAnnotation(method, CommandHandler.class);
        createsAggregate = handlerAnnotation.createsAggregate();
        //Parameter parameter = method.getParameters()[0];
        Class<?> parameterType = method.getParameterTypes()[1];
        ReflectionUtils.doWithLocalFields(parameterType,field -> {
            TargetAggregateIdentifier annotation = AnnotationUtils.findAnnotation(field, TargetAggregateIdentifier.class);
            if (annotation != null){
                this.targetAggregateIdentifier = field;
                ReflectionUtils.makeAccessible(field);
                metaInfo.registerAggrateIdentifer(parameterType, field);
            }
        });

    }

    @Override
    public boolean createsAggregate(){
        return this.createsAggregate;
    }

}
