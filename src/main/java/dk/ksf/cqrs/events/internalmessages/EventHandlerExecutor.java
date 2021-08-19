package dk.ksf.cqrs.events.internalmessages;

import dk.ksf.cqrs.events.service.CqrsMetaInfo;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.core.ResolvableType;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class EventHandlerExecutor extends HandlerExecutor {
    public EventHandlerExecutor(HandlerContainer owner, Method method, ResolvableType supportsType, AutowireCapableBeanFactory factory) {
        super(owner, method,supportsType, factory);
    }
}
