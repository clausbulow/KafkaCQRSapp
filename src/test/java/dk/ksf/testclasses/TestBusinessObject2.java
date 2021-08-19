package dk.ksf.testclasses;

import dk.ksf.cqrs.events.annotations.BusinessObject;
import lombok.Value;

@BusinessObject
@Value
public class TestBusinessObject2 {
    String myValue;
}
