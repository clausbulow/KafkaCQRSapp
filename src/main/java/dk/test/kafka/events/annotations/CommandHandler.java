package dk.test.kafka.events.annotations;

import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.*;

@Inherited
@EventListener
@Transactional
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandHandler {
}
