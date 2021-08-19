package dk.ksf.testclasses;

import dk.ksf.cqrs.events.annotations.AggregateIdentifier;
import dk.ksf.cqrs.events.annotations.BusinessObject;
import lombok.Value;

@BusinessObject
@Value
public class TestBusinessObject1 {
    @AggregateIdentifier
    String id;
    String myValue;
}
