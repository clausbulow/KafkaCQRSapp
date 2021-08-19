package dk.ksf.testclasses;

import dk.ksf.cqrs.events.annotations.TargetAggregateIdentifier;

public class TestCommand1 {
    @TargetAggregateIdentifier
    public String key;
    public String value;
}
