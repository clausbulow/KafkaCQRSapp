//inspired by https://blog.nebrass.fr/playing-with-cqrs-and-event-sourcing-in-spring-boot-and-axon/
package dk.ksf.application.writemodel;

import dk.ksf.cqrs.events.CqrsContext;
import dk.ksf.cqrs.events.annotations.*;
import dk.ksf.cqrs.events.model.AggregateTypes;
import dk.ksf.cqrs.events.service.AggregateLifecycle;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import dk.ksf.application.writemodel.commands.*;
import dk.ksf.application.common.eventobjects.*;

@Slf4j
@NoArgsConstructor
@Data
@Aggregate(aggregateType = AggregateTypes.klient, repository = KlientWriteModelRepository.class)
public class KlientAggregate  {

    @AggregateIdentifier
    String cpr;
    String fornavn;
    String efternavn;
    long version;

    @Autowired
    AggregateLifecycle aggregateLifecycle;

    @CommandHandler(createsAggregate = true)
    public KlientOprettetObject opretKlient(CqrsContext context, OpretKlientCommand command) throws Exception{
        return KlientOprettetObject.builder().
                cpr(command.getCpr()).
                efternavn(command.getEfternavn()).
                fornavn(command.getFornavn()).
                build();
    }

    @EventSourcingHandler
    public void onKlientOprettetEvent(CqrsContext context, KlientOprettetObject event) throws Exception{
        fornavn = event.getFornavn();
        efternavn = event.getEfternavn();
        cpr = event.getCpr();
        version = context.getVersion();
        log.info("Klient oprettet i writemodel");
    }

    @CommandHandler
    public KlientRettetObject onRetKlientCommand(CqrsContext context, RetKlientCommand command) throws Exception{
        return KlientRettetObject.builder().cpr(command.getCpr()).efternavn(command.getEfternavn()).fornavn(command.getFornavn()).build();
    }

    @EventSourcingHandler
    public void onKlientRettetEvent(CqrsContext context,KlientRettetObject event) throws Exception{
        fornavn = event.getFornavn();
        efternavn = event.getEfternavn();
        cpr = event.getCpr();
        version = context.getVersion();
        log.info("Klient rettet i writemodel");
    }
}
