//inspired by https://blog.nebrass.fr/playing-with-cqrs-and-event-sourcing-in-spring-boot-and-axon/
package dk.ksf.application.writemodel;

import dk.ksf.cqrs.CqrsProperties;
import dk.ksf.cqrs.events.annotations.*;
import dk.ksf.cqrs.events.model.AggregateTypes;
import dk.ksf.cqrs.events.model.BusinessEvent;
import dk.ksf.cqrs.events.service.EventService;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import dk.ksf.application.writemodel.commands.*;
import dk.ksf.application.common.eventobjects.*;

@Component
@Slf4j
@Aggregate(aggregateType = AggregateTypes.klient, repository = KlientWriteModelRepository.class)
@NoArgsConstructor
@Data
public class KlientAggregate  {
    public static AggregateTypes this_aggregate_type = AggregateTypes.klient;

    @AggregateIdentifier
    String cpr;
    String fornavn;
    String efternavn;
    long version;

    @Autowired
    AggregateLifecycle aggregateLifecycle;

    @Autowired
    EventService eventService;

    @Autowired
    CqrsProperties props;



    @CommandHandler(createsAggregate = true)
    public void opretKlient(OpretKlientCommand command) throws Exception{
        KlientOprettetObject businessObject = KlientOprettetObject.builder().cpr(command.getCpr()).efternavn(command.getEfternavn()).fornavn(command.getFornavn()).build();
        BusinessEvent<KlientOprettetObject> businessEvent =
                BusinessEvent.<KlientOprettetObject>builder().
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
        fornavn = klient.getFornavn();
        efternavn = klient.getEfternavn();
        cpr = klient.getCpr();
        version = event.getVersion();
        log.info("Klient oprettet i writemodel");
    }



    @CommandHandler
    public void onRetKlientCommand(RetKlientCommand command) throws Exception{
        KlientRettetObject businessObject = KlientRettetObject.builder().cpr(command.getCpr()).efternavn(command.getEfternavn()).fornavn(command.getFornavn()).build();
        BusinessEvent businessEvent =
                BusinessEvent.<KlientRettetObject>builder().
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
        fornavn = klient.getFornavn();
        efternavn = klient.getEfternavn();
        cpr = klient.getCpr();
        version = event.getVersion();
        log.info("Klient rettet i writemodel");
    }
}
