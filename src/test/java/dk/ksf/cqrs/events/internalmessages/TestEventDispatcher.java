package dk.ksf.cqrs.events.internalmessages;

import dk.ksf.application.common.eventobjects.KlientOprettetObject;
import dk.ksf.cqrs.CqrsProperties;
import dk.ksf.cqrs.events.internalmessages.cqrsscanner.CqrsMetaInfo;
import dk.ksf.cqrs.events.model.BusinessEventFactory;
import dk.ksf.cqrs.events.annotations.Aggregate;
import dk.ksf.cqrs.events.annotations.AggregateIdentifier;
import dk.ksf.cqrs.events.annotations.CommandHandler;
import dk.ksf.cqrs.events.annotations.EventSourcingHandler;
import dk.ksf.cqrs.events.model.AggregateTypes;
import dk.ksf.cqrs.events.service.EventService;
import dk.ksf.testclasses.TestKlientAggregateRepository;
import dk.ksf.testclasses.commands.OpretKlientCommand;
import dk.ksf.testclasses.commands.RetKlientCommand;
import dk.ksf.testclasses.eventobjects.KlientRettetObject;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
@Slf4j
@Aggregate(aggregateType = AggregateTypes.klient, repository = TestKlientAggregateRepository.class)
public class TestEventDispatcher {


    static final TestKlientAggregateRepository repository = new TestKlientAggregateRepository();
    EventDispatcher dispatcher;
    @Mock
    AutowireCapableBeanFactory factory;

    @Mock
    PlatformTransactionManager transactionManager;

    CqrsMetaInfo metaInfo;
    AllExecutablesContainer annotationsHandler;
    BusinessEventFactory beFactory;
    MessageContext context;
    @Mock
    EventService eventService;
    @AggregateIdentifier
    String id;
    String fornavn;
    String efternavn;


    @Before
    public void before() throws Exception {
        CqrsProperties cqrsProperties = new CqrsProperties();
        cqrsProperties.setProducingActorId("AggregateTest");
        cqrsProperties.setEventobjectsPackage("dk.ksf");
        metaInfo = new CqrsMetaInfo(cqrsProperties);
        metaInfo.init();
        context = MessageContext.builder().requestId("TestClientAggregate").build();
        beFactory = new BusinessEventFactory(cqrsProperties, metaInfo);
        Mockito.when(factory.getBean(TestKlientAggregateRepository.class)).thenReturn(repository);
        annotationsHandler = new AllExecutablesContainer(factory, metaInfo, eventService, transactionManager);
        annotationsHandler.createExecutableContainers(this.getClass());
        annotationsHandler.init();
        dispatcher = new EventDispatcher(annotationsHandler);
        // annotationsHandler.registerNewAggregateInstance(this);


    }

    @Test
    public void testDispatcherCreateNewAggregeteOnOpretKlientCommand() throws Exception {
        dispatcher.publishCommand(context, OpretKlientCommand.builder().cpr("test1").fornavn("Hans").efternavn("Jensen").build());

        Optional<TestEventDispatcher> optionalNewAggregate = repository.findById("test1");
        Assert.assertTrue(optionalNewAggregate.isPresent());
        TestEventDispatcher aggregate = optionalNewAggregate.get();
        Assert.assertEquals("Hans", aggregate.fornavn);
    }

    @Test
    public void testDispatcherWithExisingAggregateRetKlientCommand() throws Exception {
        this.id = "test1";
        repository.save(this);
        dispatcher.publishCommand(context, RetKlientCommand.builder().cpr("test1").fornavn("Hans").efternavn("Jensen").build());
        Assert.assertEquals("Hans", this.fornavn);
        Assert.assertEquals("Jensen", this.efternavn);
    }

    @Test
    public void testDispatcherWithEventSourcingWithExistingAggregate() throws Exception {
        this.id = "test1";
        repository.save(this);

        KlientRettetObject businessObject = KlientRettetObject.builder().cpr("test1").efternavn("Jensen").fornavn("Hans").build();
        dispatcher.publishEventToEventSourcing(context, businessObject);
        Assert.assertEquals("Hans", this.fornavn);
        Assert.assertEquals("Jensen", this.efternavn);
    }

    @Test
    public void testDispatcherWithEventSourcingWithNewAggregate() throws Exception {
        this.id = "test2";
        this.fornavn = "x";
        this.efternavn = "y";
        repository.save(this);

        KlientRettetObject businessObject = KlientRettetObject.builder().cpr("test1").efternavn("Jensen").fornavn("Hans").build();
        this.id = "test1";

        dispatcher.publishEventToEventSourcing(context, businessObject);
        Assert.assertEquals("x", this.fornavn);
        Assert.assertEquals("y", this.efternavn);
        Optional<TestEventDispatcher> test1 = repository.findById("test1");
        Assert.assertNotNull(test1.get());
        TestEventDispatcher aggregate = test1.get();
        Assert.assertEquals("Hans", aggregate.fornavn);
        Assert.assertEquals("Jensen", aggregate.efternavn);
    }


    @CommandHandler(createsAggregate = true)
    public void onOpretKlientCommand(MessageContext context, OpretKlientCommand opretklientData) {
        fornavn = opretklientData.getFornavn();
        efternavn = opretklientData.getEfternavn();
        repository.save(this);
    }

    @CommandHandler
    public KlientRettetObject onRetKlientCommand(MessageContext context, RetKlientCommand data) throws Exception {
        return KlientRettetObject.builder().cpr(this.id).efternavn(data.getEfternavn()).fornavn(data.getFornavn()).build();
        //return beFactory.createBusinessEvent(this,context,businessObject);
    }

    @EventSourcingHandler
    public void updateOnKlientRettet(MessageContext context, KlientRettetObject event) {
        this.fornavn = event.getFornavn();
        this.efternavn = event.getEfternavn();
    }

    @EventSourcingHandler
    public void updateOnKlientOpRettet(MessageContext context, KlientOprettetObject event) {
        this.fornavn = event.getFornavn();
        this.efternavn = event.getEfternavn();
    }


    public String getId() {
        return id;
    }

}
