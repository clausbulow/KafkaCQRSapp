package dk.ksf.testclasses;


import dk.kfs.cqrs.internalmessages.events.annotations.TargetAggregateIdentifier;

public class TestCommand1 {
    @TargetAggregateIdentifier
    public String key;
    public String value;
}
