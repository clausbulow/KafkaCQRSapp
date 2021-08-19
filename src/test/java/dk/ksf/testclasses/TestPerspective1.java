package dk.ksf.testclasses;

import dk.ksf.cqrs.events.CqrsContext;
import dk.ksf.cqrs.events.annotations.*;
import lombok.Data;

@Data
@Perspective
public class TestPerspective1 {
    String lastAction;



    @EventHandler
    public void on (CqrsContext context, TestBusinessObject1 event){
        lastAction ="b1";

    }
    @EventHandler
    public void on2 (CqrsContext context, TestBusinessObject2 event){
        lastAction ="b2";

    }


}
