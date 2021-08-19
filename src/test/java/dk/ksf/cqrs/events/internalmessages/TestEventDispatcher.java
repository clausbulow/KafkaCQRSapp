package dk.ksf.cqrs.events.internalmessages;

import dk.ksf.application.common.eventobjects.KlientOprettetObject;
import dk.ksf.application.writemodel.KlientAggregate;
import dk.ksf.application.writemodel.commands.OpretKlientCommand;
import dk.ksf.application.writemodel.commands.RetKlientCommand;
import dk.ksf.cqrs.events.CqrsContext;
import dk.ksf.cqrs.events.model.AggregateTypes;
import dk.ksf.cqrs.events.model.BusinessEvent;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.reflect.InvocationTargetException;

@SpringBootTest
@RunWith(SpringRunner.class)
@Import(EventDispatcher.class)
@Slf4j
public class TestEventDispatcher {
    @Autowired
    EventDispatcher dispatcher;

    @Test
    public void testPublishOpretKlientCommand() throws Exception {
        dispatcher.publishCommand(CqrsContext.builder().requestId("TestDispatcher").build(),OpretKlientCommand.builder().cpr("020869-0214").fornavn("Hans").efternavn("Jensen").build());

    }

    @Test
    public void testPublishRetKlientCommand() throws Exception {
        dispatcher.publishCommand(CqrsContext.builder().requestId("TestDispatcher").build(),OpretKlientCommand.builder().cpr("020869-0214").fornavn("Hans").efternavn("Jensen").build());
        dispatcher.publishCommand(CqrsContext.builder().requestId("TestDispatcher").build(),RetKlientCommand.builder().cpr("020869-0214").fornavn("Hans").efternavn("Petersen").build());

    }

    @Test
    public void testPublishRetKlientEvent() throws Exception {
        dispatcher.publishCommand(CqrsContext.builder().requestId("TestDispatcher").build(),OpretKlientCommand.builder().cpr("020869-0214").fornavn("Hans").efternavn("Jensen").build());
        KlientOprettetObject businessObject = KlientOprettetObject.builder().cpr("020869-0214").efternavn("Jensen").fornavn("Peter").build();
        BusinessEvent<KlientOprettetObject> businessEvent =
                BusinessEvent.<KlientOprettetObject>builder().
                        eventNavn("klient").
                        aggregateType(AggregateTypes.klient).
                        actor("KS").
                        key("020869-0214").
                        requestId("RequestID").
                        object(businessObject).
                        build();

        dispatcher.publishEventToEventHandlers(CqrsContext.builder().requestId("TestDispatcher").build(),businessEvent);

    }


}
