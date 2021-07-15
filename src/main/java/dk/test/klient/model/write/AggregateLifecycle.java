package dk.test.klient.model.write;

import dk.test.kafka.events.model.BusinessEvent;
import dk.test.kafka.events.service.EventService;
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
