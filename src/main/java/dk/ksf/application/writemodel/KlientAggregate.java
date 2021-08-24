//inspired by https://blog.nebrass.fr/playing-with-cqrs-and-event-sourcing-in-spring-boot-and-axon/
package dk.ksf.application.writemodel;

import dk.ksf.application.common.eventobjects.KlientOprettetObject;
import dk.ksf.application.common.eventobjects.KlientRettetObject;
import dk.ksf.application.writemodel.commands.OpretKlientCommand;
import dk.ksf.application.writemodel.commands.RetKlientCommand;
import dk.ksf.cqrs.events.internalmessages.MessageContext;
import dk.ksf.cqrs.events.annotations.Aggregate;
import dk.ksf.cqrs.events.annotations.AggregateIdentifier;
import dk.ksf.cqrs.events.annotations.CommandHandler;
import dk.ksf.cqrs.events.annotations.EventSourcingHandler;
import dk.ksf.cqrs.events.model.AggregateTypes;
import dk.ksf.cqrs.events.service.AggregateLifecycle;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@NoArgsConstructor
@Data
@Aggregate(aggregateType = AggregateTypes.klient, repository = KlientWriteModelRepository.class)
public class KlientAggregate {

    @AggregateIdentifier
    String cpr;

    String fornavn;
    String efternavn;
    long version;


    @CommandHandler(createsAggregate = true)
    public KlientOprettetObject opretKlient(MessageContext context, OpretKlientCommand command) throws Exception {
        //Validate command if necessary
        return KlientOprettetObject.builder().
                cpr(command.getCpr()).
                efternavn(command.getEfternavn()).
                fornavn(command.getFornavn()).
                build();
    }

    @EventSourcingHandler
    public void onKlientOprettetEvent(MessageContext context, KlientOprettetObject event) throws Exception {
        fornavn = event.getFornavn();
        efternavn = event.getEfternavn();
        cpr = event.getCpr();
        version = context.getVersion();
        log.info("Klient oprettet i writemodel");
    }

    @CommandHandler
    public KlientRettetObject onRetKlientCommand(MessageContext context, RetKlientCommand command) throws Exception {
        return KlientRettetObject.builder().cpr(command.getCpr()).efternavn(command.getEfternavn()).fornavn(command.getFornavn()).build();
    }

    @EventSourcingHandler
    public void onKlientRettetEvent(MessageContext context, KlientRettetObject event) throws Exception {
        fornavn = event.getFornavn();
        efternavn = event.getEfternavn();
        cpr = event.getCpr();
        version = context.getVersion();
        log.info("Klient rettet i writemodel");
    }
}
