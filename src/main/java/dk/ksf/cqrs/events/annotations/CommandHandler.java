package dk.ksf.cqrs.events.annotations;

import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.*;

@Inherited
@Transactional
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR})
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandHandler {
    boolean createsAggregate() default false;
}
