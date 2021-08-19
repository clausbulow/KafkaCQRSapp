package dk.ksf.cqrs.events.service;

import dk.ksf.cqrs.events.CqrsContext;
import dk.ksf.cqrs.events.internalmessages.EventDispatcher;
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
    public void apply(Object creator, CqrsContext context, Object businessEvent) throws Exception {
        eventDispatcher.publishEventToEventSourcing(context, businessEvent);
        eventService.fireEvent(creator, context, businessEvent);
    }
}
