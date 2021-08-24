package dk.ksf.cqrs.events.internalmessages;

import dk.ksf.cqrs.events.annotations.Aggregate;
import dk.ksf.cqrs.events.annotations.Perspective;
import dk.ksf.cqrs.events.internalmessages.cqrsscanner.CqrsMetaInfo;
import dk.ksf.cqrs.events.service.EventService;
import dk.ksf.cqrs.exceptions.ExceptionConsumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class AllExecutablesContainer {
    final List<AbstractExecutablesContainer> abstractExecutablesContainers = new ArrayList<>();
    private final AutowireCapableBeanFactory factory;
    private final CqrsMetaInfo metaInfo;
    private final EventService eventService;

    private final PlatformTransactionManager transactionManager;

    private final List<Class> classAnnotationsOfInterest = Arrays.asList(
            Aggregate.class, Perspective.class
    );

    public AllExecutablesContainer(AutowireCapableBeanFactory factory, CqrsMetaInfo metaInfo, EventService eventService, @Qualifier("eventstoreTransactionManager") PlatformTransactionManager transactionManager) {
        this.factory = factory;
        this.metaInfo = metaInfo;
        this.eventService = eventService;
        this.transactionManager = transactionManager;
    }


    public void createExecutableContainers(final Class clazz) {
        final Aggregate aggregateAnnotation = (Aggregate) clazz.getAnnotation(Aggregate.class);
        if (aggregateAnnotation != null) {
            abstractExecutablesContainers.add(new AggregateExecutablesContainer(clazz, factory, metaInfo, eventService));
        } else {
            final Perspective perspectiveAnnotation = (Perspective) clazz.getAnnotation(Perspective.class);
            if (perspectiveAnnotation != null) {
                abstractExecutablesContainers.add(new PerspectiveExecutablesContainer(perspectiveAnnotation, clazz, factory, metaInfo, eventService));
            }
        }
    }

    public List getHandlerContainers() {
        return abstractExecutablesContainers;
    }

    public void signalEventHandlers(MessageContext context, Object event) throws Exception {
        abstractExecutablesContainers.forEach(ExceptionConsumer.wrapper(container -> container.signalEventHandlers(context, event)));
    }

    public void signalEventSourcingHandlers(MessageContext context, Object event) throws Exception {
        abstractExecutablesContainers.forEach(ExceptionConsumer.wrapper(container -> container.signalEventSourcingHandlers(context, event)));
    }

    public void signalCommandHandlers(MessageContext context, Object command) throws Exception {
        TransactionTemplate transactionTemplate = new TransactionTemplate(this.transactionManager);
        abstractExecutablesContainers.forEach(ExceptionConsumer.wrapper(container ->
                container.signalCommandHandlers(context, command, transactionTemplate))
        );
    }

    public void init() throws Exception {
        metaInfo.getAggregates().forEach((aggregateClass, metainfo) -> {
            this.createExecutableContainers(aggregateClass);
        });
        metaInfo.getPerspectives().forEach((perspectiveClass, metainfo) -> {
            this.createExecutableContainers(perspectiveClass);
        });
    }
}
