package dk.ksf.cqrs.events.internalmessages;

import dk.ksf.cqrs.events.annotations.CommandHandler;
import dk.ksf.cqrs.events.annotations.EventHandler;
import dk.ksf.cqrs.events.annotations.EventSourcingHandler;
import dk.ksf.cqrs.events.service.CqrsMetaInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;

@Component
public class HandlerExectutorFactory {
    CqrsMetaInfo metaInfo;

    public HandlerExectutorFactory(CqrsMetaInfo metaInfo) {
        this.metaInfo = metaInfo;
    }

    HandlerExecutor createHandlerExecutor(HandlerFactoryParams params) throws Exception {
        if (params.getAnnotationClass() == EventHandler.class) {
            return new EventHandlerExecutor(params.getOwner(),params.getMethod(), params.getTargetType(), params.getFactory());
        }
        if (params.getAnnotationClass() == CommandHandler.class) {
            return new CommandHandlerExecutor(params.getOwner(), params.getMethod(), params.getTargetType(), params.getFactory(),metaInfo);
        }
        if (params.getAnnotationClass() == EventSourcingHandler.class) {
            return new EventSourcingHandlerExecutor(params.getOwner(), params.getMethod(), params.getTargetType(), params.getFactory(),metaInfo);
        }
        throw new MessageException("Illegal annotion used... can not create handler-excecutor");

    }
}
