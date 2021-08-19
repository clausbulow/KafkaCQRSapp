package dk.ksf.cqrs.events.internalmessages;

import dk.ksf.cqrs.events.service.CqrsMetaInfo;
import lombok.Builder;
import lombok.Value;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.core.ResolvableType;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

@Value
@Builder
public class HandlerFactoryParams {
    Method method;
    ResolvableType targetType;
    Annotation annotation;
    Class annotationClass;
    AutowireCapableBeanFactory factory;
    HandlerContainer owner;
}
