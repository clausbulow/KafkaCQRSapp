package dk.test.kafka.events.internal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.DefaultEventListenerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@Component
public class DDDEventListenerFactory extends DefaultEventListenerFactory {
    List<DDDApplicationListenerMethodAdapter> adapters = new ArrayList();
    @Autowired
    ApplicationContext context;

    DDDApplicationListenerMethodAdapter adapter = null;
    @Override
    public void setOrder(int order) {
        super.setOrder(order);
    }

    @Override
    public int getOrder() {
        return 51;
    }

    @Override
    public boolean supportsMethod(Method method) {
        return super.supportsMethod(method);
    }

    @Override
    public ApplicationListener<?> createApplicationListener(String beanName, Class<?> type, Method method) {
        adapter = new DDDApplicationListenerMethodAdapter(beanName, type, method);
        adapters.add(adapter);
        return adapter;
    }


    @EventListener
    @Order(100)
    public void init(ContextRefreshedEvent event){
        adapters.forEach(a -> a.setInitializing(false));
    }

}
