package dk.ksf.testclasses;

import dk.ksf.cqrs.events.CqrsContext;
import dk.ksf.cqrs.events.annotations.*;
import dk.ksf.cqrs.events.model.AggregateTypes;
import lombok.Data;

@Data
@Aggregate(aggregateType = AggregateTypes.klient, repository = TestRepository.class)
public class TestKlientAggregate {
    @AggregateIdentifier
    String id;

    String lastAction;


    int counter = 0;

    @EventHandler
    public void on(CqrsContext context, TestBusinessObject1 event) {
        id = event.getMyValue();
        counter = counter + 1;
        lastAction = "b1";

    }

    @EventHandler
    public void on2(CqrsContext context, TestBusinessObject2 event) {
        id = event.getMyValue();
        counter = counter + 1;
        lastAction = "b2";

    }

    @CommandHandler(createsAggregate = true)
    public TestBusinessObject2 on3(CqrsContext context, TestCommand1 command) {
        lastAction = "c1";
        System.out.println("Command invoked");
        return new TestBusinessObject2(command.key, "c1");
    }

    @EventSourcingHandler
    public void onS1(CqrsContext context, TestBusinessObject2 event) {
        counter = counter + 1;
        lastAction = "s1";

    }

    public int getCounter() {
        return counter;
    }


}
