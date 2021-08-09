package dk.ksf.application.writemodel;

import dk.ksf.application.writemodel.commands.OpretKlientCommand;
import dk.ksf.cqrs.events.internalmessages.EventDispatcher;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class TestKlientAggregate {
    @Autowired
    EventDispatcher dispatcher;
    @Test
    public void testNewKlient() throws Exception{
        dispatcher.publishCommand(OpretKlientCommand.builder().cpr("020869-0214test1").fornavn("Hans").efternavn("Jensen").build());
    }
}
