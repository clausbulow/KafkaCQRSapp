package dk.ksf.testclasses;

import dk.kfs.cqrs.internalmessages.events.annotations.AggregateIdentifier;
import dk.kfs.cqrs.internalmessages.events.annotations.BusinessEvent;
import lombok.Value;

@BusinessEvent
@Value
public class TestBusinessObject1 {
    @AggregateIdentifier
    String id;
    String myValue;
}
