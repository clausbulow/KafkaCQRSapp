package dk.kfs.cqrs.internalmessages.events.annotations;

import java.lang.annotation.*;

@Inherited
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)

public @interface TargetAggregateIdentifier {
}
