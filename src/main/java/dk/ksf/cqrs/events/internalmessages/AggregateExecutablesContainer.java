package dk.ksf.cqrs.events.internalmessages;

import dk.ksf.cqrs.events.annotations.*;
import dk.ksf.cqrs.events.service.EventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
public class AggregateExecutablesContainer extends AbstractExecutablesContainer {
    private final AutowireCapableBeanFactory beanFactory;
    private final Aggregate annotation;
    private final CqrsMetaInfo metaInfo;
    private Field keyField;
    private CrudRepository repository;

    public AggregateExecutablesContainer(Aggregate aggregateAnnotation, Class aggregateClass, AutowireCapableBeanFactory beanFactory, CqrsMetaInfo metaInfo, EventService eventService) {
        super(aggregateClass, beanFactory, metaInfo, eventService);
        this.beanFactory = beanFactory;
        this.annotation = aggregateAnnotation;
        this.metaInfo = metaInfo;
        metaInfo.registerAggregate(aggregateAnnotation.aggregateType(), aggregateClass);
    }

    @Override
    void scanForAnnotations() {
        ReflectionUtils.doWithLocalFields(getContainerClass(), field -> {
            AggregateIdentifier annotation = field.getAnnotation(AggregateIdentifier.class);
            if (annotation != null) {
                ReflectionUtils.makeAccessible(field);
                this.keyField = field;
                metaInfo.registerKeyField(this.getContainerClass(), field);

            }
        });
        this.repository = beanFactory.getBean(annotation.repository());
        super.scanForAnnotations();
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
        this.keyField.set(target, keyRef);
    }

    @Override
    protected void save(Object target, String keyRef) throws Exception {
        this.repository.save(target);
    }


    @Override
    public List<Class> memberAnnotationsOfInterest() {
        return Arrays.asList(EventHandler.class, EventSourcingHandler.class, CommandHandler.class);
    }

    public Field getKeyField() {
        return this.keyField;
    }

}
