package dk.ksf.cqrs.events.internalmessages;

import lombok.Builder;
import lombok.Value;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.core.ResolvableType;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

@Value
@Builder
public class ExecutorFactoryParams {
    Method method;
    ResolvableType targetType;
    Annotation annotation;
    AutowireCapableBeanFactory factory;
    AbstractExecutablesContainer owner;
}
