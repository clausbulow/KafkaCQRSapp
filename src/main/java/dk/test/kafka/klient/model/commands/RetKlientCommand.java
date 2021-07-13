package dk.test.kafka.klient.model.commands;

import dk.test.kafka.commands.Command;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class RetKlientCommand extends Command {
    String cpr;
    String fornavn;
    String efternavn;
}
