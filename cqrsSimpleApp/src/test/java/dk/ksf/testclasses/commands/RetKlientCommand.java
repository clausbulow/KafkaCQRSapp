package dk.ksf.testclasses.commands;


import dk.kfs.cqrs.internalmessages.events.annotations.TargetAggregateIdentifier;
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
