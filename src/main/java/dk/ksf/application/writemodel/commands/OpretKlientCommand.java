package dk.ksf.application.writemodel.commands;

import dk.ksf.cqrs.events.annotations.TargetAggregateIdentifier;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class OpretKlientCommand{
    @TargetAggregateIdentifier
    String cpr;
    String fornavn;
    String efternavn;
}
