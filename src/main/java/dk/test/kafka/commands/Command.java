package dk.test.kafka.commands;

import lombok.Data;
import lombok.Value;

@Data
public class Command {
    public String aggregateId;
    public String requestId;
}
