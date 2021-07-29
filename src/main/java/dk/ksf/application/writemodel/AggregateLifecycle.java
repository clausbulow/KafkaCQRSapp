package dk.ksf.application.writemodel;

import dk.ksf.cqrs.events.model.BusinessEvent;
import dk.ksf.cqrs.events.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AggregateLifecycle {
    @Autowired
    EventService eventService;

    public void apply(BusinessEvent businessEvent) throws Exception {
        eventService.fireEvent(businessEvent);


    }
}
