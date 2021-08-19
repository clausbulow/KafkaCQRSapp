package dk.ksf.cqrs.events.internalmessages;

import dk.ksf.cqrs.events.CqrsContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("testdispatcher")
@Slf4j
public class EventDispatcher {
    @Autowired
    AllCqrsAnnotationsHandler cqrsHandlers;

    public EventDispatcher (AllCqrsAnnotationsHandler cqrsHandlers) {
        this.cqrsHandlers = cqrsHandlers;
    }


    public void publishEventToEventSourcing(CqrsContext context, Object event) throws Exception{
        cqrsHandlers.signalEventSourcingHandlers(context,event);
    }
    public void publishEventToEventHandlers(CqrsContext context,Object event) throws Exception{
        cqrsHandlers.signalEventHandlers(context, event);
    }

    public void publishCommand(CqrsContext context,Object command) throws Exception{
        cqrsHandlers.signalCommandHandlers(context,command);
    }



}
