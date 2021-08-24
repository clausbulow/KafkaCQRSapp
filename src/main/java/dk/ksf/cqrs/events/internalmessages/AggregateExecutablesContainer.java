package dk.ksf.cqrs.events.internalmessages;

import dk.ksf.cqrs.events.annotations.Aggregate;
import dk.ksf.cqrs.events.internalmessages.cqrsscanner.AggregateMetainfo;
import dk.ksf.cqrs.events.internalmessages.cqrsscanner.CqrsMetaInfo;
import dk.ksf.cqrs.events.service.EventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

@Slf4j
public class AggregateExecutablesContainer extends AbstractExecutablesContainer {
    private final AutowireCapableBeanFactory beanFactory;
    private final CrudRepository repository;

    public AggregateExecutablesContainer(Class aggregateClass, AutowireCapableBeanFactory beanFactory, CqrsMetaInfo metaInfo, EventService eventService) {
        super(aggregateClass, beanFactory, metaInfo, eventService);
        this.beanFactory = beanFactory;
        this.repository = beanFactory.getBean(metaInfo.getAggregates().get(this.getContainerClass()).getAnnotation().repository());
        scanForAnnotations();
    }

    void scanForAnnotations() {
        AggregateMetainfo aggregateMetainfo = getMetaInfo().getAggregates().get(getContainerClass());
        aggregateMetainfo.getHandlers().forEach((o, handlerMetaInfo) -> {
            try {
                createHandlerExecutor(Aggregate.class, handlerMetaInfo.getMethod(), handlerMetaInfo.getAnnotation(), beanFactory);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    protected Object getTargetInstance(MessageContext context, Object command, String keyRef) throws Exception {
        Optional optional = repository.findById(keyRef);
        if (optional.isPresent()) {
            return optional.get();
        }
        return null;
    }

    @Override
    protected void setTargetKeyValue(Object target, String keyRef) throws Exception {
        this.getMetaInfo().getAggregates().get(this.getContainerClass()).getKeyField().set(target, keyRef);
        //this.keyField.set(target, keyRef);
    }

    @Override
    protected void save(Object target, String keyRef) throws Exception {
        this.repository.save(target);
    }


}
