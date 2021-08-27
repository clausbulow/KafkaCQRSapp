package dk.kfs.cqrs.internalmessages.events.annotations;

import dk.kfs.cqrs.internalmessages.CqrsConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import(CqrsConfiguration.class)
public @interface EnableCqrs {
}
