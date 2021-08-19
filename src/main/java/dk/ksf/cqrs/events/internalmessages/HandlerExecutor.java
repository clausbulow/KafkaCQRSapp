package dk.ksf.cqrs.events.internalmessages;

import dk.ksf.cqrs.events.CqrsContext;
import dk.ksf.cqrs.events.annotations.CommandHandler;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.core.ResolvableType;
import org.springframework.core.ResolvableTypeProvider;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public abstract class HandlerExecutor {

    private Object owner;
    private final ResolvableType supportsType;
    private final Method method;
    private final AutowireCapableBeanFactory factory;

    public HandlerExecutor (Object owner, Method method, ResolvableType supportsType, AutowireCapableBeanFactory factory){
        this.owner = owner;
        this.method = method;
        this.supportsType = supportsType;
        this.factory = factory;
    }



    public Object execute (CqrsContext context,Object event) throws Exception{

        if (this.supports(event)) {
            Object result = ReflectionUtils.invokeMethod(method, context.getTargetInstance(), context, event);
            return result;
        } else {
            return null;
        }
    }

    public boolean supports(Object event){
        ResolvableType eventType;
        if (event instanceof ResolvableTypeProvider) {
            eventType = ((ResolvableTypeProvider) event).getResolvableType();
        } else {
            eventType = ResolvableType.forInstance(event);
        }
        return this.supportsType.isAssignableFrom(eventType);

    }

    public ResolvableType getSupportsType() {
        return supportsType;
    }
    public Method getMethod(){
        return this.method;
    }
    public boolean createsAggregate(){
        return false;
    }

    protected Object getOwner(){
        return this.owner;
    }

}
