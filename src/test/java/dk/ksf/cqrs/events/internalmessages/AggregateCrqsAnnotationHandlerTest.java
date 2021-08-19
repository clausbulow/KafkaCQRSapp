package dk.ksf.cqrs.events.internalmessages;

import dk.ksf.cqrs.events.CqrsContext;
import dk.ksf.cqrs.events.annotations.Aggregate;
import dk.ksf.cqrs.events.model.BusinessEvent;
import dk.ksf.cqrs.events.service.CqrsMetaInfo;
import dk.ksf.cqrs.events.service.EventService;
import dk.ksf.testclasses.*;
import org.hibernate.annotations.ManyToAny;
import org.junit.Assert;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration
//@SpringBootTest
class AggregateCrqsAnnotationHandlerTest {
    AggregateHandlerContainer aggregateCrqsAnnotationHandler;

    @Mock
    AutowireCapableBeanFactory factory;

    @Mock
    CqrsMetaInfo metaInfo;


    Aggregate annotation;

    @Mock
    EventService eventService;

    @Before
    public void before(){
        Mockito.when(factory.getBean(TestKlientAggregate.class)).thenReturn(new TestKlientAggregate());
        annotation = AnnotationUtils.findAnnotation(TestKlientAggregate.class, Aggregate.class);
    }



    @Test
    void testHandlersFound() throws Exception {

        AggregateHandlerContainer aggregateCrqsAnnotationHandler = new AggregateHandlerContainer(annotation, TestKlientAggregate.class, factory, metaInfo, eventService);
        aggregateCrqsAnnotationHandler.scanForAnnotations();
        Assert.assertEquals(2,aggregateCrqsAnnotationHandler.getEventHandlerExecutors().size());
    }

    @Test
    void testOnlyOneHandlerCalled() throws Exception {

        AggregateHandlerContainer aggregateCrqsAnnotationHandler = new AggregateHandlerContainer(annotation, TestKlientAggregate.class, factory, metaInfo, eventService);
        aggregateCrqsAnnotationHandler.scanForAnnotations();

        TestBusinessObject1 bo = new TestBusinessObject1("Test1");
        BusinessEvent<TestBusinessObject1> event = BusinessEvent.<TestBusinessObject1>builder().eventNavn("test").object(bo).build();
        aggregateCrqsAnnotationHandler.signalEventHandlers(CqrsContext.builder().requestId("AggregateCqrsAnnotationHanlderTest").build(),event);

       // Assert.assertEquals(1, target.getCounter());
    }

    @Test
    void testCommandHandler() throws Exception {
        AutowireCapableBeanFactory factory = Mockito.mock(AutowireCapableBeanFactory.class);
        TestKlientAggregate target = new TestKlientAggregate();
        Mockito.when(factory.getBean(TestKlientAggregate.class)).thenReturn(target);
        TestRepository repository = new TestRepository();
        Mockito.when(factory.getBean(TestRepository.class)).thenReturn(repository);

        Aggregate annotation = AnnotationUtils.findAnnotation(TestKlientAggregate.class, Aggregate.class);

        CqrsMetaInfo metaInfo = Mockito.mock(CqrsMetaInfo.class);
        AggregateHandlerContainer aggregateCrqsAnnotationHandler = new AggregateHandlerContainer(annotation, TestKlientAggregate.class, factory, metaInfo,eventService);
        aggregateCrqsAnnotationHandler.scanForAnnotations();
        CqrsContext cqrsContext = CqrsContext.builder().requestId("AggregateCqrsAnnotationHanlderTest").build();

        TestCommand1 command1 = new TestCommand1();
        command1.key = "key";
        command1.value = "value1";
        aggregateCrqsAnnotationHandler.signalCommandHandlers(cqrsContext,command1);
        Optional<TestKlientAggregate> optionalTestAggregate1 = repository.findById("key");
        Assert.assertNotNull(optionalTestAggregate1.get());
        TestKlientAggregate testAggregate1 = optionalTestAggregate1.get();
        Assert.assertEquals(testAggregate1.getLastAction(),"c1");

    }

    @Test
    void testEventSourcingHandler() throws Exception {
        AutowireCapableBeanFactory factory = Mockito.mock(AutowireCapableBeanFactory.class);
        TestKlientAggregate target = new TestKlientAggregate();
        Mockito.when(factory.getBean(TestKlientAggregate.class)).thenReturn(target);
        TestRepository repository = new TestRepository();
        Mockito.when(factory.getBean(TestRepository.class)).thenReturn(repository);

        Aggregate annotation = AnnotationUtils.findAnnotation(TestKlientAggregate.class, Aggregate.class);
        CqrsMetaInfo metaInfo = Mockito.mock(CqrsMetaInfo.class);

        AggregateHandlerContainer aggregateCrqsAnnotationHandler = new AggregateHandlerContainer(annotation, TestKlientAggregate.class, factory, metaInfo, eventService);
        aggregateCrqsAnnotationHandler.scanForAnnotations();
        CqrsContext cqrsContext = CqrsContext.builder().requestId("AggregateCqrsAnnotationHanlderTest").build();


        TestCommand1 command1 = new TestCommand1();
        command1.key = "key";
        command1.value = "value1";
        aggregateCrqsAnnotationHandler.signalCommandHandlers(cqrsContext,command1);
        Optional<TestKlientAggregate> optionalTestAggregate1 = repository.findById("key");
        Assert.assertNotNull(optionalTestAggregate1.get());
        TestKlientAggregate testAggregate1 = optionalTestAggregate1.get();
        Assert.assertEquals(testAggregate1.getLastAction(),"c1");

        TestBusinessObject2 bo = new TestBusinessObject2("Test1");
        BusinessEvent<TestBusinessObject2> event = BusinessEvent.<TestBusinessObject2>builder().eventNavn("test").key("key").object(bo).build();

        aggregateCrqsAnnotationHandler.signalEventSourcingHandlers(cqrsContext, event);
        Optional<TestKlientAggregate> key = repository.findById("key");
        Assert.assertNotNull(key.get());
        Assert.assertEquals("s1", key.get().getLastAction());


    }

}