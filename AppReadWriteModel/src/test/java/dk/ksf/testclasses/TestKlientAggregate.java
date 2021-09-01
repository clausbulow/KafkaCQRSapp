package dk.ksf.testclasses;

import dk.kfs.cqrs.internalmessages.events.annotations.*;
import dk.kfs.cqrs.internalmessages.events.internalmessages.MessageContext;
import dk.kfs.cqrs.internalmessages.events.model.AggregateTypes;
import lombok.Data;

@Data
@Aggregate(aggregateType = AggregateTypes.klient, repository = TestRepository.class)
public class TestKlientAggregate {
    @AggregateIdentifier
    String id;

    String lastAction;


    int counter = 0;

    @EventHandler
    public void on(MessageContext context, TestBusinessObject1 event) {
        id = event.getMyValue();
        counter = counter + 1;
        lastAction = "b1";

    }

    @EventHandler
    public void on2(MessageContext context, TestBusinessObject2 event) {
        id = event.getMyValue();
        counter = counter + 1;
        lastAction = "b2";

    }

    @CommandHandler(createsAggregate = true)
    public TestBusinessObject2 on3(MessageContext context, TestCommand1 command) {
        lastAction = "c1";
        System.out.println("Command invoked");
        return new TestBusinessObject2(command.key, "c1");
    }

    @EventSourcingHandler
    public void onS1(MessageContext context, TestBusinessObject2 event) {
        counter = counter + 1;
        lastAction = "s1";

    }

    public int getCounter() {
        return counter;
    }


}
