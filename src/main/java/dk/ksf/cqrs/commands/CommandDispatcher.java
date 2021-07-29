package dk.ksf.cqrs.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class CommandDispatcher {
    @Autowired
    ApplicationEventPublisher publisher;

    //TODO how to handle requestId ??
    public void apply (String requestId,Command command){
        command.setRequestId(requestId);
        publisher.publishEvent(command);
    }
}
