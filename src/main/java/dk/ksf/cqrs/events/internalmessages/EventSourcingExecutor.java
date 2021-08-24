package dk.ksf.cqrs.events.internalmessages;

import dk.ksf.cqrs.events.internalmessages.cqrsscanner.CqrsMetaInfo;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.core.ResolvableType;

import java.lang.reflect.Method;

public class EventSourcingExecutor extends AbstractExecutor {

    public EventSourcingExecutor(Object owner, Method method, ResolvableType supportsType, AutowireCapableBeanFactory factory, CqrsMetaInfo metaInfo) {
        super(owner, method, supportsType, factory);
    }
}