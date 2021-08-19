package dk.ksf.cqrs.events.annotations;

import dk.ksf.cqrs.events.model.AggregateTypes;
import org.springframework.data.repository.CrudRepository;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Aggregate {
    AggregateTypes aggregateType();

    Class<? extends CrudRepository> repository();
}
