package dk.ksf.cqrs.events.internalmessages;

import dk.ksf.cqrs.events.CqrsContext;
import dk.ksf.cqrs.events.model.BusinessEvent;
import dk.ksf.cqrs.events.service.CqrsMetaInfo;
import dk.ksf.cqrs.events.service.EventService;
import dk.ksf.testclasses.TestKlientAggregate;
import dk.ksf.testclasses.TestBusinessObject1;
import dk.ksf.testclasses.TestPerspective1;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(MockitoJUnitRunner.class)

class AllCqrsAnnotationsHandlerTest {
    @Mock
    AutowireCapableBeanFactory factory;

    @Mock
    CqrsMetaInfo metaInfo;

    @Mock
    EventService eventService;

    @Before
    public void before(){
        Mockito.when(factory.getBean(TestKlientAggregate.class)).thenReturn(new TestKlientAggregate());

    }

    @Test
    public void testScanForClassAnnotationsFindsOne() throws Exception{
        AllCqrsAnnotationsHandler allCqrsAnnotationsHandler = new AllCqrsAnnotationsHandler(factory,metaInfo,eventService);
        allCqrsAnnotationsHandler.scanForClassAnnotation("dk.ksf.testclasses");
        assertEquals(2,allCqrsAnnotationsHandler.getHandlerContainers().size());
    }

    @Test
    public void testScanForClassAnnotations2AreCalled() throws Exception{
        AutowireCapableBeanFactory factory = Mockito.mock(AutowireCapableBeanFactory.class);
        TestKlientAggregate a = new TestKlientAggregate();
        Mockito.when(factory.getBean(TestKlientAggregate.class)).thenReturn(a);
        TestPerspective1 p = new TestPerspective1();
        Mockito.when(factory.getBean(TestPerspective1.class)).thenReturn(p);
        CqrsMetaInfo metaInfo = Mockito.mock(CqrsMetaInfo.class);
        AllCqrsAnnotationsHandler allCqrsAnnotationsHandler = new AllCqrsAnnotationsHandler(factory,metaInfo,eventService);
        allCqrsAnnotationsHandler.scanForClassAnnotation("dk.ksf.testclasses");

        TestBusinessObject1 bo = new TestBusinessObject1("Test1");
        BusinessEvent<TestBusinessObject1> event = BusinessEvent.<TestBusinessObject1>builder().eventNavn("test").object(bo).build();

        allCqrsAnnotationsHandler.signalEventHandlers(CqrsContext.builder().requestId("AggregateCqrsAnnotationHanlderTest").build(), event);
        assertEquals("b1",p.getLastAction());
        assertEquals("b1",a.getLastAction());

    }

}