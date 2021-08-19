package dk.ksf.cqrs.events.internalmessages;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.core.ResolvableType;

import java.lang.reflect.Method;

public class EventExecutor extends AbstractExecutor {
    public EventExecutor(AbstractExecutablesContainer owner, Method method, ResolvableType supportsType, AutowireCapableBeanFactory factory) {
        super(owner, method,supportsType, factory);
    }
}
