package dk.test.klient.model;

import dk.test.kafka.events.annotations.EventHandler;
import org.springframework.context.event.ApplicationListenerMethodAdapter;

import java.lang.reflect.Method;

public class DDDApplicationListenerMethodAdapter extends ApplicationListenerMethodAdapter {
    private boolean isInitializing = true;
    public DDDApplicationListenerMethodAdapter(String beanName, Class<?> type, Method method) {
        super(beanName,type, method);
    }

    @Override
    protected Object doInvoke(Object... args) {
        Method targetMethod = this.getTargetMethod();
        EventHandler annotation = targetMethod.getAnnotation(EventHandler.class);
        if (annotation == null){
            return super.doInvoke(args);
        }
        if (annotation.onlyOnInit()){
            if (isInitializing){
                return super.doInvoke(args);
            } else {
                return null;
            }
        }
        return super.doInvoke(args);
    }
    public void setInitializing(boolean initializing){
        this.isInitializing = initializing;
    }


}
