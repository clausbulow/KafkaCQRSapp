package dk.kfs.cqrs.internalmessages.events.internalmessages;

import dk.kfs.cqrs.internalmessages.events.annotations.Perspective;
import dk.kfs.cqrs.internalmessages.events.internalmessages.cqrsscanner.CqrsMetaInfo;
import dk.kfs.cqrs.internalmessages.events.internalmessages.cqrsscanner.PerspectiveMetainfo;
import dk.kfs.cqrs.internalmessages.events.service.EventService;
import dk.kfs.cqrs.internalmessages.exceptions.MessageException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.data.repository.CrudRepository;

@Slf4j
public class PerspectiveExecutablesContainer extends AbstractExecutablesContainer {
    private final AutowireCapableBeanFactory beanFactory;
    private CrudRepository repository;

    public PerspectiveExecutablesContainer(Perspective annotation, Class aggregateClass, AutowireCapableBeanFactory beanFactory, CqrsMetaInfo metaInfo, EventService eventService) {
        super(aggregateClass, beanFactory, metaInfo, eventService);
        this.beanFactory = beanFactory;
        scanForAnnotations();
    }


    protected void scanForAnnotations()  {
        PerspectiveMetainfo perspectiveMetainfo = getMetaInfo().getPerspectives().get(getContainerClass());
        perspectiveMetainfo.getHandlers().forEach((o, handlerMetaInfo) -> {
            try {
                createHandlerExecutor(Perspective.class, handlerMetaInfo.getMethod(), handlerMetaInfo.getAnnotation(), beanFactory);
            } catch (Exception e) {
                throw new RuntimeException("Not able to scan perspective for annotations ",e);
            }
        });
    }

    @Override
    protected Object getTargetInstance(MessageContext context, Object command, String keyRef) throws Exception {
        return beanFactory.getBean(getContainerClass());
    }

    @Override
    protected void setTargetKeyValue(Object target, String keyRef) throws Exception {
        throw new MessageException("Setting target key value should not be done within a perspective");

    }

    @Override
    protected void save(Object target, String keyRef) throws Exception {
        throw new MessageException("Saving should not be done within a perspective");
    }
}
