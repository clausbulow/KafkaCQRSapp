package dk.test.kafka.events.annotations;

import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.*;

@EventListener
@Inherited
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Transactional
public @interface EventHandler {
    boolean onlyOnInit() default false;
}
