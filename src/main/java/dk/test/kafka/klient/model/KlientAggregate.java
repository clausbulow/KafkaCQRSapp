//inspired by https://blog.nebrass.fr/playing-with-cqrs-and-event-sourcing-in-spring-boot-and-axon/
package dk.test.kafka.klient.model;

import dk.test.kafka.events.model.AggregateTypes;
import dk.test.kafka.events.model.BusinessEvent;
import dk.test.kafka.events.annotations.CommandHandler;
import dk.test.kafka.events.annotations.EventHandler;
import dk.test.kafka.events.service.EventService;
import dk.test.kafka.klient.model.commands.OpretKlientCommand;
import dk.test.kafka.klient.model.commands.RetKlientCommand;
import dk.test.kafka.klient.model.events.KlientOprettetObject;
import dk.test.kafka.klient.model.events.KlientRettetObject;
import dk.test.kafka.klient.service.KlientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class KlientAggregate  {
    public static AggregateTypes this_aggregate_type = AggregateTypes.klient;

    @Autowired
    AggregateLifecycle aggregateLifecycle;

    @Autowired
    EventService eventService;

    @Autowired
    KlientService klientService;



    @EventHandler
    public void onKlientRettetEvent(BusinessEvent<KlientRettetObject> event) throws Exception{
        KlientRettetObject klient = event.getObject();
        klientService.retKlient(klient);
        System.out.println("Klient rettet");
    }

    @EventHandler
    public void onKlientOprettetEvent(BusinessEvent<KlientOprettetObject> event) throws Exception{
        KlientOprettetObject klient = event.getObject();
        klientService.opretKlient(klient);
        System.out.println("Klient oprettet");
    }
    @EventHandler
    public void onGenereel(BusinessEvent<?> event) throws Exception{

        System.out.println("Klient oprettet");
    }


    @CommandHandler
    public void onOpretKlientCommand(OpretKlientCommand command) throws Exception{
        KlientOprettetObject businessObject = KlientOprettetObject.builder().cpr(command.getCpr()).efternavn(command.getEfternavn()).fornavn(command.getFornavn()).build();
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

    @CommandHandler
    public void onRetKlientCommand(RetKlientCommand command) throws Exception{
        KlientOprettetObject businessObject = KlientOprettetObject.builder().cpr(command.getCpr()).efternavn(command.getEfternavn()).fornavn(command.getFornavn()).build();
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

}
