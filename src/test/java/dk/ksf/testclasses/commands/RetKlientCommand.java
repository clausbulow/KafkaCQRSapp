package dk.ksf.testclasses.commands;

import dk.ksf.cqrs.events.annotations.TargetAggregateIdentifier;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class RetKlientCommand {
    @TargetAggregateIdentifier
    String cpr;
    String fornavn;
    String efternavn;
}
