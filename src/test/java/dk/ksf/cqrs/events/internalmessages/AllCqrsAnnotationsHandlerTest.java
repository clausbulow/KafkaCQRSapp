package dk.ksf.cqrs.events.internalmessages;

import dk.ksf.cqrs.CqrsProperties;
import dk.ksf.cqrs.events.CqrsContext;
import dk.ksf.cqrs.events.service.EventService;
import dk.ksf.testclasses.TestKlientAggregate;
import dk.ksf.testclasses.TestBusinessObject1;
import dk.ksf.testclasses.TestPerspective1;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(MockitoJUnitRunner.class)
public class AllCqrsAnnotationsHandlerTest {
    @Mock
    AutowireCapableBeanFactory factory;

    CqrsMetaInfo metaInfo;

    @Mock
    EventService eventService;

    @Before
    public void before() throws Exception{
        CqrsProperties props = new CqrsProperties();
        props.setEventobjectsPackage("dk.ksf.testclasses");
        metaInfo = new CqrsMetaInfo(props);
        metaInfo.initEventsList();
        Mockito.when(factory.getBean(TestKlientAggregate.class)).thenReturn(new TestKlientAggregate());
        TestKlientAggregate a = new TestKlientAggregate();
        Mockito.when(factory.getBean(TestKlientAggregate.class)).thenReturn(a);

    }

    @Test
    public void testScanForClassAnnotationsFindsOne() throws Exception{
        AllCqrsAnnotationsHandler allCqrsAnnotationsHandler = new AllCqrsAnnotationsHandler(factory,metaInfo,eventService);
        allCqrsAnnotationsHandler.scanForClassAnnotation("dk.ksf.testclasses");
        assertEquals(2,allCqrsAnnotationsHandler.getHandlerContainers().size());
    }

    @Test
    public void testScanForClassAnnotations2AreCalled() throws Exception{
        TestPerspective1 p = new TestPerspective1();
        Mockito.when(factory.getBean(TestPerspective1.class)).thenReturn(p);
        AllCqrsAnnotationsHandler allCqrsAnnotationsHandler = new AllCqrsAnnotationsHandler(factory,metaInfo,eventService);
        allCqrsAnnotationsHandler.scanForClassAnnotation("dk.ksf.testclasses");

        TestBusinessObject1 bo = new TestBusinessObject1("key","Test1");

        allCqrsAnnotationsHandler.signalEventHandlers(CqrsContext.builder().requestId("AggregateCqrsAnnotationHanlderTest").build(), bo);
        assertEquals("b1",p.getLastAction());
    }

}