package dk.ksf.cqrs.events.internalmessages;

import dk.ksf.cqrs.events.annotations.Aggregate;
import dk.ksf.cqrs.events.annotations.AggregateIdentifier;
import dk.ksf.cqrs.events.annotations.TargetAggregateIdentifier;
import dk.ksf.cqrs.events.model.BusinessEvent;
import dk.ksf.cqrs.events.service.CqrsMetaInfo;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.repository.CrudRepository;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Optional;

public class EventSourcingHandlerExecutor extends HandlerExecutor {
    private  Field aggregateIdentifier;
    public CrudRepository repository;
    public EventSourcingHandlerExecutor(Object owner, Method method, ResolvableType supportsType, AutowireCapableBeanFactory factory, CqrsMetaInfo metaInfo) {
        super(owner,method, supportsType,factory);
        Aggregate classAnnotation = AnnotationUtils.findAnnotation(method.getDeclaringClass(), Aggregate.class);
        repository = factory.getBean(classAnnotation.repository());

        Class<?> parameterType = method.getParameterTypes()[1];

        ReflectionUtils.doWithLocalFields(parameterType, field -> {
            AggregateIdentifier annotation = AnnotationUtils.findAnnotation(field, AggregateIdentifier.class);
            if (annotation != null){
                this.aggregateIdentifier = field;
                ReflectionUtils.makeAccessible(field);
                metaInfo.registerAggrateIdentifer(parameterType, field);
            }
        });

    }


}
