package dk.ksf.cqrs.events.internalmessages;

import dk.ksf.cqrs.events.annotations.Aggregate;
import dk.ksf.cqrs.events.annotations.AggregateIdentifier;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.repository.CrudRepository;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class EventSourcingExecutor extends AbstractExecutor {
    private  Field aggregateIdentifier;
    public final CrudRepository repository;
    public EventSourcingExecutor(Object owner, Method method, ResolvableType supportsType, AutowireCapableBeanFactory factory, CqrsMetaInfo metaInfo) {
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
