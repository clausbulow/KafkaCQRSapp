package dk.ksf.cqrs.events.internalmessages;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("testdispatcher")
@Slf4j
public class EventDispatcher {
    @Autowired
    AllExecutablesContainer cqrsHandlers;

    public EventDispatcher(AllExecutablesContainer cqrsHandlers) {
        this.cqrsHandlers = cqrsHandlers;
    }


    public void publishEventToEventSourcing(MessageContext context, Object event) throws Exception {
        cqrsHandlers.signalEventSourcingHandlers(context, event);
    }

    public void publishEventToEventHandlers(MessageContext context, Object event) throws Exception {
        cqrsHandlers.signalEventHandlers(context, event);
    }

    public void publishCommand(MessageContext context, Object command) throws Exception {
        cqrsHandlers.signalCommandHandlers(context, command);
    }


}
