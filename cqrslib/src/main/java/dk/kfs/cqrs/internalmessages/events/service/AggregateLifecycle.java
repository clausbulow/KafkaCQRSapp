package dk.kfs.cqrs.internalmessages.events.service;

import dk.kfs.cqrs.internalmessages.events.internalmessages.EventDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AggregateLifecycle {
    @Autowired
    EventDispatcher eventDispatcher;
    @Autowired
    EventService eventService;
}
