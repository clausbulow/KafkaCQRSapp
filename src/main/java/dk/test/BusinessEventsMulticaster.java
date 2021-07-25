
package dk.test;
import dk.test.kafka.events.model.BusinessEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.PayloadApplicationEvent;
import org.springframework.context.event.ApplicationListenerMethodAdapter;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class BusinessEventsMulticaster extends SimpleApplicationEventMulticaster {


    @Override
    public void multicastEvent(ApplicationEvent event, ResolvableType eventType) {
        if (event instanceof PayloadApplicationEvent){
            PayloadApplicationEvent payLoadEvent = (PayloadApplicationEvent) event;
            ResolvableType resolvableType = payLoadEvent.getResolvableType();
            Object payloadObject = payLoadEvent.getPayload();
            Collection<ApplicationListener<?>> applicationListeners = getApplicationListeners(event, eventType);
            applicationListeners.forEach(listener -> {
                invokeListener(listener,event);
            });


        }
        super.multicastEvent(event, eventType);
    }

    @Override
    protected void invokeListener(ApplicationListener<?> listener, ApplicationEvent event){
        if (event instanceof PayloadApplicationEvent){
            if (listener instanceof ApplicationListenerMethodAdapter) {
                ApplicationListenerMethodAdapter methodAdapter = (ApplicationListenerMethodAdapter) listener;
                PayloadApplicationEvent payLoadEvent = (PayloadApplicationEvent) event;
                ResolvableType resolvableType = payLoadEvent.getResolvableType();
                Object payloadObject = payLoadEvent.getPayload();
                System.out.println("Testing");

            }
        }
        super.invokeListener(listener, event);

    }

}
