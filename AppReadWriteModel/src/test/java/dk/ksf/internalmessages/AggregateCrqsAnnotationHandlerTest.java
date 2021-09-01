package dk.ksf.internalmessages;


import dk.kfs.cqrs.internalmessages.CqrsProperties;
import dk.kfs.cqrs.internalmessages.events.annotations.Aggregate;
import dk.kfs.cqrs.internalmessages.events.internalmessages.AggregateExecutablesContainer;
import dk.kfs.cqrs.internalmessages.events.internalmessages.MessageContext;
import dk.kfs.cqrs.internalmessages.events.internalmessages.cqrsscanner.CqrsMetaInfo;
import dk.kfs.cqrs.internalmessages.events.service.EventService;
import dk.ksf.testclasses.TestBusinessObject1;
import dk.ksf.testclasses.TestCommand1;
import dk.ksf.testclasses.TestKlientAggregate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;
import dk.ksf.testclasses.*;

import java.util.Arrays;
import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class AggregateCrqsAnnotationHandlerTest {
    final TestRepository repository = new TestRepository();
    private final TestKlientAggregate target = new TestKlientAggregate();
    AggregateExecutablesContainer aggregateCrqsAnnotationHandler;
    CqrsMetaInfo metaInfo;
    private Object AllCqrsAnnotationsHandler;

    Aggregate annotation;
    @Mock
    EventService eventService;

    @Mock
    AutowireCapableBeanFactory factory;

    @Mock
    TransactionTemplate transactionTemplate;
    @Mock
    TransactionStatus transactionStatus;
    @Mock
    PlatformTransactionManager transactionManager;


    @Before
    public void before() throws Exception {
        CqrsProperties props = new CqrsProperties();
        props.setEventobjectsPackages(Arrays.asList("dk.ksf.testclasses"));
        transactionTemplate = new TransactionTemplate(transactionManager);
        Mockito.when(transactionManager.getTransaction(ArgumentMatchers.any())).thenReturn(transactionStatus);
        metaInfo = new CqrsMetaInfo(props);
        metaInfo.init();
        Mockito.when(factory.getBean(TestKlientAggregate.class)).thenReturn(new TestKlientAggregate());
        Mockito.when(factory.getBean(TestRepository.class)).thenReturn(repository);
        annotation = AnnotationUtils.findAnnotation(TestKlientAggregate.class, Aggregate.class);

        aggregateCrqsAnnotationHandler = new AggregateExecutablesContainer(TestKlientAggregate.class, factory, metaInfo, eventService);
        aggregateCrqsAnnotationHandler.scanForAnnotations();
    }


    @Test
    public void testHandlersFound() throws Exception {
        Assert.assertEquals(4, aggregateCrqsAnnotationHandler.getEventExecutors().size());
    }

    @Test
    public void testOnlyOneHandlerCalled() throws Exception {
        TestBusinessObject1 bo = new TestBusinessObject1("id", "Test1");
        aggregateCrqsAnnotationHandler.signalEventHandlers(MessageContext.builder().requestId("AggregateCqrsAnnotationHanlderTest").build(), bo);

        // Assert.assertEquals(1, target.getCounter());
    }

    @Test
    public void testCommandHandler() throws Exception {
        MessageContext messageContext = MessageContext.builder().requestId("AggregateCqrsAnnotationHanlderTest").build();
        TestCommand1 command1 = new TestCommand1();
        command1.key = "key";
        command1.value = "value1";
        aggregateCrqsAnnotationHandler.signalCommandHandlers(messageContext, command1,transactionTemplate );
        TestKlientAggregate testAggregate1 = repository.findById("key").get();
        Assert.assertEquals(testAggregate1.getLastAction(), "s1");

    }

    @Test
    public void testEventSourcingHandler() throws Exception {
        MessageContext messageContext = MessageContext.builder().requestId("AggregateCqrsAnnotationHanlderTest").build();
        TestCommand1 command1 = new TestCommand1();
        command1.key = "key";
        command1.value = "value1";
        aggregateCrqsAnnotationHandler.signalCommandHandlers(messageContext, command1, transactionTemplate);
        Optional<TestKlientAggregate> optionalTestAggregate1 = repository.findById("key");
        Assert.assertNotNull(optionalTestAggregate1.get());
        TestKlientAggregate testAggregate1 = optionalTestAggregate1.get();
        Assert.assertEquals(testAggregate1.getLastAction(), "s1");
    }

}