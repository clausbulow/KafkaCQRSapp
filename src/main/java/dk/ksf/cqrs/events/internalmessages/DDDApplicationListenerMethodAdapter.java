package dk.ksf.cqrs.events.internalmessages;

import dk.ksf.cqrs.events.annotations.EventHandler;
import dk.ksf.cqrs.events.annotations.EventSourcingHandler;
import org.springframework.context.event.ApplicationListenerMethodAdapter;

import java.lang.reflect.Method;

public class DDDApplicationListenerMethodAdapter extends ApplicationListenerMethodAdapter {
    private boolean isInitializing = true;
    public DDDApplicationListenerMethodAdapter(String beanName, Class<?> type, Method method) {
        super(beanName,type, method);
    }

    @Override
    protected Object doInvoke(Object... args) {
        if (isInitializing){
            return super.doInvoke(args);
        }
        Method targetMethod = this.getTargetMethod();
        EventSourcingHandler annotation = targetMethod.getAnnotation(EventSourcingHandler.class);
        if (annotation == null){
            return super.doInvoke(args);
        }
        return null;
    }
    public void setInitializing(boolean initializing){
        this.isInitializing = initializing;
    }


}
