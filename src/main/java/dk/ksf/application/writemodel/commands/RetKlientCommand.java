package dk.ksf.application.writemodel.commands;

import dk.ksf.cqrs.commands.Command;
import dk.ksf.cqrs.events.annotations.TargetAggregateIdentifier;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class RetKlientCommand extends Command {
    @TargetAggregateIdentifier
    String cpr;
    String fornavn;
    String efternavn;
}
