package dk.ksf.testclasses;

import dk.kfs.cqrs.internalmessages.events.annotations.AggregateIdentifier;
import dk.kfs.cqrs.internalmessages.events.annotations.BusinessObject;
import lombok.Value;

@BusinessObject
@Value
public class TestBusinessObject2 {
    @AggregateIdentifier
    String id;
    String myValue;
}
