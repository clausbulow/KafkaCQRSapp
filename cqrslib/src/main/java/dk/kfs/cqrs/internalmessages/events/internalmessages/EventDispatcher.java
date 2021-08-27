package dk.kfs.cqrs.internalmessages.events.internalmessages;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("testdispatcher")
@Slf4j
public class EventDispatcher {
    @Autowired
    AllExecutablesContainer executableContainers;

    public EventDispatcher(AllExecutablesContainer executableContainers) {
        this.executableContainers = executableContainers;
    }


    public void publishEventToEventSourcing(MessageContext context, Object event) throws Exception {
        executableContainers.signalEventSourcingHandlers(context, event);
    }

    public void publishEventToEventHandlers(MessageContext context, Object event) throws Exception {
        executableContainers.signalEventHandlers(context, event);
    }

    public void publishCommand(MessageContext context, Object command) throws Exception {
        executableContainers.signalCommandHandlers(context, command);
    }


}
