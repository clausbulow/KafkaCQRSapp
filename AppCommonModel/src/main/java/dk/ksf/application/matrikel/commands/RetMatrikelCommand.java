package dk.ksf.application.matrikel.commands;

import dk.kfs.cqrs.internalmessages.events.annotations.TargetAggregateIdentifier;
import dk.ksf.application.matrikel.model.Celle;
import lombok.Builder;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;

@Value
@Builder
public class RetMatrikelCommand {
    @TargetAggregateIdentifier
    String id;
    String vejnavn;
    List<Celle> celler = new ArrayList<>();
}
