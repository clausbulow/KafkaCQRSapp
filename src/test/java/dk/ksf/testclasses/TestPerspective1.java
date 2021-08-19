package dk.ksf.testclasses;

import dk.ksf.cqrs.events.annotations.*;
import dk.ksf.cqrs.events.model.AggregateTypes;
import dk.ksf.cqrs.events.model.BusinessEvent;
import lombok.Data;

@Data
@Perspective
public class TestPerspective1 {
    String lastAction;



    @EventHandler
    public void on (BusinessEvent<TestBusinessObject1> event){
        lastAction ="b1";

    }
    @EventHandler
    public void on2 (BusinessEvent<TestBusinessObject2> event){
        lastAction ="b2";

    }


}
