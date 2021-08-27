package dk.ksf.testclasses;


import dk.kfs.cqrs.internalmessages.events.annotations.EventHandler;
import dk.kfs.cqrs.internalmessages.events.annotations.Perspective;
import dk.kfs.cqrs.internalmessages.events.internalmessages.MessageContext;
import lombok.Data;

@Data
@Perspective
public class TestPerspective1 {
    String lastAction;


    @EventHandler
    public void on(MessageContext context, TestBusinessObject1 event) {
        lastAction = "b1";

    }

    @EventHandler
    public void on2(MessageContext context, TestBusinessObject2 event) {
        lastAction = "b2";

    }


}
