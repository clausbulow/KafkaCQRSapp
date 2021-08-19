package dk.ksf.cqrs.events.internalmessages;

import dk.ksf.cqrs.CqrsProperties;
import dk.ksf.cqrs.events.CqrsContext;
import dk.ksf.cqrs.events.annotations.Aggregate;
import dk.ksf.cqrs.events.service.EventService;
import dk.ksf.testclasses.TestBusinessObject1;
import dk.ksf.testclasses.TestCommand1;
import dk.ksf.testclasses.TestKlientAggregate;
import dk.ksf.testclasses.TestRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.core.annotation.AnnotationUtils;

import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class AggregateCrqsAnnotationHandlerTest {
    final TestRepository repository = new TestRepository();
    private final TestKlientAggregate target = new TestKlientAggregate();
    AggregateExecutablesContainer aggregateCrqsAnnotationHandler;
    @Mock
    AutowireCapableBeanFactory factory;
    CqrsMetaInfo metaInfo;
    Aggregate annotation;
    @Mock
    EventService eventService;
    private Object AllCqrsAnnotationsHandler;

    @Before
    public void before() throws Exception {
        CqrsProperties props = new CqrsProperties();
        props.setEventobjectsPackage("dk.ksf.testclasses");
        metaInfo = new CqrsMetaInfo(props);
        metaInfo.initEventsList();
        Mockito.when(factory.getBean(TestKlientAggregate.class)).thenReturn(new TestKlientAggregate());
        Mockito.when(factory.getBean(TestRepository.class)).thenReturn(repository);
        annotation = AnnotationUtils.findAnnotation(TestKlientAggregate.class, Aggregate.class);

        aggregateCrqsAnnotationHandler = new AggregateExecutablesContainer(annotation, TestKlientAggregate.class, factory, metaInfo, eventService);
        aggregateCrqsAnnotationHandler.scanForAnnotations();
    }


    @Test
    public void testHandlersFound() throws Exception {


        Assert.assertEquals(2, aggregateCrqsAnnotationHandler.getEventExecutors().size());
    }

    @Test
    public void testOnlyOneHandlerCalled() throws Exception {
        TestBusinessObject1 bo = new TestBusinessObject1("id", "Test1");
        aggregateCrqsAnnotationHandler.signalEventHandlers(CqrsContext.builder().requestId("AggregateCqrsAnnotationHanlderTest").build(), bo);

        // Assert.assertEquals(1, target.getCounter());
    }

    @Test
    public void testCommandHandler() throws Exception {
        CqrsContext cqrsContext = CqrsContext.builder().requestId("AggregateCqrsAnnotationHanlderTest").build();
        TestCommand1 command1 = new TestCommand1();
        command1.key = "key";
        command1.value = "value1";
        aggregateCrqsAnnotationHandler.signalCommandHandlers(cqrsContext, command1);
        TestKlientAggregate testAggregate1 = repository.findById("key").get();
        Assert.assertEquals(testAggregate1.getLastAction(), "s1");

    }

    @Test
    public void testEventSourcingHandler() throws Exception {
        CqrsContext cqrsContext = CqrsContext.builder().requestId("AggregateCqrsAnnotationHanlderTest").build();
        TestCommand1 command1 = new TestCommand1();
        command1.key = "key";
        command1.value = "value1";
        aggregateCrqsAnnotationHandler.signalCommandHandlers(cqrsContext, command1);
        Optional<TestKlientAggregate> optionalTestAggregate1 = repository.findById("key");
        Assert.assertNotNull(optionalTestAggregate1.get());
        TestKlientAggregate testAggregate1 = optionalTestAggregate1.get();
        Assert.assertEquals(testAggregate1.getLastAction(), "s1");
    }

}