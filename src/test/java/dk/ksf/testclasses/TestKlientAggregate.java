package dk.ksf.testclasses;

import dk.ksf.cqrs.events.annotations.*;
import dk.ksf.cqrs.events.model.AggregateTypes;
import dk.ksf.cqrs.events.model.BusinessEvent;
import lombok.Data;

@Data
@Aggregate(aggregateType = AggregateTypes.klient, repository = TestRepository.class)
public class TestKlientAggregate {
    @AggregateIdentifier
    String id;

    String lastAction;


    int counter = 0;

    @EventHandler
    public void on (BusinessEvent<TestBusinessObject1> event){
        id = event.getObject().getMyValue();
        counter = counter +1;
        lastAction ="b1";

    }
    @EventHandler
    public void on2 (BusinessEvent<TestBusinessObject2> event){
        id = event.getObject().getMyValue();
        counter = counter +1;
        lastAction ="b2";

    }

    @CommandHandler(createsAggregate = true)
    public void on3 (TestCommand1 command){
        lastAction ="c1";
        System.out.println("Command invoked");
    }

    @EventSourcingHandler
    public void onS1 (BusinessEvent<TestBusinessObject2> event){
        counter = counter +1;
        lastAction ="s1";

    }

    public int getCounter() {
        return counter;
    }


}
