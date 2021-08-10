package dk.ksf.cqrs.events.service;

import dk.ksf.cqrs.events.internalmessages.EventDispatcher;
import dk.ksf.cqrs.events.model.BusinessEvent;
import dk.ksf.cqrs.events.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AggregateLifecycle {
    @Autowired
    EventDispatcher eventDispatcher;
    @Autowired
    EventService eventService;

    @Transactional(transactionManager = "eventstoreTransactionManager")
    public void apply(BusinessEvent<?> businessEvent) throws Exception {
        eventDispatcher.publishEventToEventSourcing(businessEvent);
        eventService.fireEvent(businessEvent);
    }
}
