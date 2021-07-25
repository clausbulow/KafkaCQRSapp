//inspired by https://blog.nebrass.fr/playing-with-cqrs-and-event-sourcing-in-spring-boot-and-axon/
package dk.test.klient.model.write;

import dk.test.kafka.events.annotations.Aggregate;
import dk.test.kafka.events.model.AggregateTypes;
import dk.test.kafka.events.model.BusinessEvent;
import dk.test.kafka.events.annotations.CommandHandler;
import dk.test.kafka.events.annotations.EventHandler;
import dk.test.kafka.events.service.EventService;
import dk.test.klient.model.commands.OpretKlientCommand;
import dk.test.klient.model.commands.RetKlientCommand;
import dk.test.klient.model.eventsobject.KlientOprettetObject;
import dk.test.klient.model.eventsobject.KlientRettetObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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


    @CommandHandler
    public void onRetKlientCommand(RetKlientCommand command) throws Exception{
        KlientRettetObject businessObject = KlientRettetObject.builder().cpr(command.getCpr()).efternavn(command.getEfternavn()).fornavn(command.getFornavn()).build();
        klientWriteModelService.retKlient(businessObject,0);
        BusinessEvent businessEvent =
                BusinessEvent.builder().
                        eventNavn(eventService.getEventName(KlientRettetObject.class)).
                        aggregateType(KlientAggregate.this_aggregate_type).
                        actor("KS").requestId(command.getRequestId()).
                        key(command.getCpr()).
                        object(businessObject).
                        build();
        aggregateLifecycle.apply(businessEvent);
    }

    @EventHandler (onlyOnInit = true)
    public void onKlientRettetEvent(BusinessEvent<KlientRettetObject> event) throws Exception{
        KlientRettetObject klient = event.getObject();
        klientWriteModelService.retKlient(klient, event.getVersion());
        log.info("Klient rettet i writemodel");
    }

    @CommandHandler
    public void onOpretKlientCommand(OpretKlientCommand command) throws Exception{

        KlientOprettetObject businessObject = KlientOprettetObject.builder().cpr(command.getCpr()).efternavn(command.getEfternavn()).fornavn(command.getFornavn()).build();
        klientWriteModelService.opretKlient(businessObject,0);
        BusinessEvent businessEvent =
                BusinessEvent.builder().
                        eventNavn(eventService.getEventName(KlientOprettetObject.class)).
                        aggregateType(KlientAggregate.this_aggregate_type).
                        actor("KS").
                        key(command.getCpr()).
                        requestId(command.getRequestId()).
                        object(businessObject).
                        build();
        aggregateLifecycle.apply(businessEvent);
    }

    @EventHandler(onlyOnInit = true)
    public void onKlientOprettetEvent(BusinessEvent<KlientOprettetObject> event) throws Exception{
        KlientOprettetObject klient = event.getObject();
        klientWriteModelService.opretKlient(klient, event.getVersion());
        log.info("Klient oprettet i writemodel");
    }

}
