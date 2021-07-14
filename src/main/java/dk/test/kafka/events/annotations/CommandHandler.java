package dk.test.kafka.events.annotations;

import org.springframework.context.event.EventListener;

import java.lang.annotation.*;

@Inherited
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)

public @interface CommandHandler {
}
