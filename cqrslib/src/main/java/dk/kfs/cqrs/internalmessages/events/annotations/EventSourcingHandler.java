package dk.kfs.cqrs.internalmessages.events.annotations;

import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.*;

@Inherited
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Transactional
public @interface EventSourcingHandler {
}
