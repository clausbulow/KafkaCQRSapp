package dk.ksf.cqrs.events.internalmessages;

import dk.ksf.cqrs.events.CqrsContext;
import dk.ksf.cqrs.events.annotations.EventHandler;
import dk.ksf.cqrs.events.annotations.Perspective;
import dk.ksf.cqrs.events.service.EventService;
import dk.ksf.cqrs.exceptions.MessageException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.data.repository.CrudRepository;

import java.lang.reflect.Field;
import java.util.List;

@Slf4j
public class PerspectiveExecutablesContainer extends AbstractExecutablesContainer {
    private final AutowireCapableBeanFactory beanFactory;
    private Field keyField;
    private CrudRepository repository;

    public PerspectiveExecutablesContainer(Perspective annotation, Class aggregateClass, AutowireCapableBeanFactory beanFactory, CqrsMetaInfo metaInfo, EventService eventService) {
        super(aggregateClass, beanFactory, metaInfo, eventService);
        this.beanFactory = beanFactory;
    }


    @Override
    public List<Class> memberAnnotationsOfInterest() {
        return List.of(EventHandler.class);
    }

    @Override
    protected Object getTargetInstance(CqrsContext context, Object command, String keyRef) throws Exception {
        return beanFactory.getBean(getContainerClass());
    }

    @Override
    protected void setTargetKeyValue(Object target, String keyRef) throws Exception {
        throw new MessageException("Setting target key value should not be done within a perspective");

    }

    @Override
    protected void save(Object target, String keyRef) throws Exception {

    }

    public Field getKeyField() {
        return this.keyField;
    }

}
