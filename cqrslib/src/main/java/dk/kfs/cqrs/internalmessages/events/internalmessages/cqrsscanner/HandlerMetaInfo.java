package dk.kfs.cqrs.internalmessages.events.internalmessages.cqrsscanner;

import lombok.Builder;
import lombok.Data;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

@Data
@Builder
public class HandlerMetaInfo {
    Method method;
    Annotation annotation;

}
