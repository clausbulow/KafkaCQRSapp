package dk.test.klient.model.commands;

import dk.test.kafka.commands.Command;
import dk.test.kafka.events.annotations.TargetAggregateIdentifier;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class OpretKlientCommand extends Command {
    @TargetAggregateIdentifier
    String cpr;

    String fornavn;
    String efternavn;
}
