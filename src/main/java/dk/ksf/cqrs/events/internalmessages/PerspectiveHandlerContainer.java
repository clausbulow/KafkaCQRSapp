package dk.ksf.cqrs.events.internalmessages;

import dk.ksf.cqrs.events.CqrsContext;
import dk.ksf.cqrs.events.annotations.*;
import dk.ksf.cqrs.events.service.CqrsMetaInfo;
import dk.ksf.cqrs.events.service.EventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class PerspectiveHandlerContainer extends HandlerContainer{
    private final AutowireCapableBeanFactory beanFactory;
    private final Perspective annotation;
    private Field keyField;
    private CrudRepository repository;

    public PerspectiveHandlerContainer(Perspective annotation, Class aggregateClass, AutowireCapableBeanFactory beanFactory, CqrsMetaInfo metaInfo, EventService eventService) {
        super(aggregateClass, beanFactory, metaInfo, eventService);
        this.beanFactory = beanFactory;
        this.annotation = annotation;
    }


    @Override
    public List<Class> memberAnnotationsOfInterest() {
        return Arrays.asList(EventHandler.class);
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

    public Field getKeyField(){
        return this.keyField;
    }

}
