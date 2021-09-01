package dk.ksf.application.matrikel.model;

import dk.kfs.cqrs.internalmessages.events.annotations.Aggregate;
import dk.kfs.cqrs.internalmessages.events.annotations.AggregateIdentifier;
import dk.kfs.cqrs.internalmessages.events.annotations.CommandHandler;
import dk.kfs.cqrs.internalmessages.events.internalmessages.MessageContext;
import dk.kfs.cqrs.internalmessages.events.model.AggregateTypes;
import dk.ksf.application.matrikel.commands.OpretMatrikelCommand;
import dk.ksf.application.matrikel.events.MatrikelOprettetEvent;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@NoArgsConstructor
@Data
@Entity
@Table(name = "matrikel")
@Aggregate(aggregateType = AggregateTypes.matrikel, repository = MatrikelRepository.class)

public class Matrikel {

    @AggregateIdentifier
    @Id
    String id;

    public String vejnavn;

    @OneToMany (fetch = FetchType.LAZY, cascade = CascadeType.ALL, targetEntity = Celle.class)
    public List<Celle> celler = new ArrayList<>();

    long version;

    @CommandHandler(createsAggregate = true)
    public MatrikelOprettetEvent onOpretMatrikelCommand(MessageContext context, OpretMatrikelCommand command){
        this.id = command.getId();
        this.vejnavn = command.getVej();
        this.version =context.getVersion();
        command.getCeller().forEach(celle -> {
            Celle newCelle = new Celle();
            newCelle.setMatrikel(this);
            newCelle.setId(celle.getId());
            newCelle.setAntalPladser(celle.getAntalPladser());
            newCelle.setNummer(celle.nummer);
            celler.add(newCelle);
            celle.setMatrikel(this);
        });

        MatrikelOprettetEvent event =  MatrikelOprettetEvent.builder().vej(this.vejnavn).id(this.id).build();
        event.setCeller(this.celler);

        return event;
    }

}
