//inspired by https://blog.nebrass.fr/playing-with-cqrs-and-event-sourcing-in-spring-boot-and-axon/
package dk.ksf.application.writemodel;

import dk.ksf.cqrs.CqrsProperties;
import dk.ksf.cqrs.events.annotations.Aggregate;
import dk.ksf.cqrs.events.annotations.EventSourcingHandler;
import dk.ksf.cqrs.events.model.AggregateTypes;
import dk.ksf.cqrs.events.model.BusinessEvent;
import dk.ksf.cqrs.events.annotations.CommandHandler;
import dk.ksf.cqrs.events.annotations.EventHandler;
import dk.ksf.cqrs.events.service.EventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import dk.ksf.application.writemodel.commands.*;
import dk.ksf.application.common.eventobjects.*;

@Component
@Slf4j
@Aggregate
public class KlientAggregate  {
    public static AggregateTypes this_aggregate_type = AggregateTypes.klient;

    @Autowired
    AggregateLifecycle aggregateLifecycle;

    @Autowired
    EventService eventService;

    @Autowired
    KlientWriteModelService klientWriteModelService;

    @Autowired
    CqrsProperties props;


    @CommandHandler
    public void onRetKlientCommand(RetKlientCommand command) throws Exception{
        KlientRettetObject businessObject = KlientRettetObject.builder().cpr(command.getCpr()).efternavn(command.getEfternavn()).fornavn(command.getFornavn()).build();
        klientWriteModelService.retKlient(businessObject,0);
        BusinessEvent businessEvent =
                BusinessEvent.builder().
                        eventNavn(eventService.getEventName(KlientRettetObject.class)).
                        aggregateType(KlientAggregate.this_aggregate_type).
                        actor(props.getProducingActorId()).
                        requestId(command.getRequestId()).
                        key(command.getCpr()).
                        object(businessObject).
                        build();
        aggregateLifecycle.apply(businessEvent);
    }

    @EventSourcingHandler
    public void onKlientRettetEvent(BusinessEvent<KlientRettetObject> event) throws Exception{
        KlientRettetObject klient = event.getObject();
        klientWriteModelService.retKlient(klient, event.getVersion());
        log.debug("Klient rettet i writemodel");
    }

    @CommandHandler
    public void onOpretKlientCommand(OpretKlientCommand command) throws Exception{

        KlientOprettetObject businessObject = KlientOprettetObject.builder().cpr(command.getCpr()).efternavn(command.getEfternavn()).fornavn(command.getFornavn()).build();
        klientWriteModelService.opretKlient(businessObject,0);
        BusinessEvent businessEvent =
                BusinessEvent.builder().
                        eventNavn(eventService.getEventName(KlientOprettetObject.class)).
                        aggregateType(KlientAggregate.this_aggregate_type).
                        actor(props.getProducingActorId()).
                        key(command.getCpr()).
                        requestId(command.getRequestId()).
                        object(businessObject).
                        build();
        aggregateLifecycle.apply(businessEvent);
    }

    @EventSourcingHandler
    public void onKlientOprettetEvent(BusinessEvent<KlientOprettetObject> event) throws Exception{
        KlientOprettetObject klient = event.getObject();
        klientWriteModelService.opretKlient(klient, event.getVersion());
        log.info("Klient oprettet i writemodel");
    }

}
