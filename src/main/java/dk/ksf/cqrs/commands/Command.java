package dk.ksf.cqrs.commands;

import lombok.Data;

@Data
public class Command {
    public String aggregateId;
    public String requestId;
}
