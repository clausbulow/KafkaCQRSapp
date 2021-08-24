package dk.ksf.cqrs.events.internalmessages;

import dk.ksf.cqrs.CqrsProperties;
import dk.ksf.cqrs.events.internalmessages.cqrsscanner.CqrsMetaInfo;
import dk.ksf.cqrs.events.service.EventService;
import dk.ksf.testclasses.TestBusinessObject1;
import dk.ksf.testclasses.TestKlientAggregate;
import dk.ksf.testclasses.TestPerspective1;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.transaction.PlatformTransactionManager;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class AllExecutablesContainerTest {
    @Mock
    AutowireCapableBeanFactory factory;

    CqrsMetaInfo metaInfo;

    @Mock
    EventService eventService;

    @Mock
    PlatformTransactionManager transactionManager;

    @Before
    public void before() throws Exception {
        CqrsProperties props = new CqrsProperties();
        props.setEventobjectsPackage("dk.ksf.testclasses");
        metaInfo = new CqrsMetaInfo(props);
        metaInfo.init();
        Mockito.when(factory.getBean(TestKlientAggregate.class)).thenReturn(new TestKlientAggregate());
        TestKlientAggregate a = new TestKlientAggregate();
        Mockito.when(factory.getBean(TestKlientAggregate.class)).thenReturn(a);

    }

    @Test
    public void testScanForClassAnnotationsFindsOne() throws Exception {
        AllExecutablesContainer allExecutablesContainer = new AllExecutablesContainer(factory, metaInfo, eventService, transactionManager);
        allExecutablesContainer.init();
        assertEquals(2, allExecutablesContainer.getHandlerContainers().size());
    }

    @Test
    public void testScanForClassAnnotations2AreCalled() throws Exception {
        TestPerspective1 p = new TestPerspective1();
        Mockito.when(factory.getBean(TestPerspective1.class)).thenReturn(p);
        AllExecutablesContainer allExecutablesContainer = new AllExecutablesContainer(factory, metaInfo, eventService, transactionManager);
        allExecutablesContainer.init();

        TestBusinessObject1 bo = new TestBusinessObject1("key", "Test1");

        allExecutablesContainer.signalEventHandlers(MessageContext.builder().requestId("AggregateCqrsAnnotationHanlderTest").build(), bo);
        assertEquals("b1", p.getLastAction());
    }

}