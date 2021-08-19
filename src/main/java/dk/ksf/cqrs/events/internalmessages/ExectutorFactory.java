package dk.ksf.cqrs.events.internalmessages;

import dk.ksf.cqrs.events.annotations.CommandHandler;
import dk.ksf.cqrs.events.annotations.EventHandler;
import dk.ksf.cqrs.events.annotations.EventSourcingHandler;
import dk.ksf.cqrs.exceptions.MessageException;
import org.springframework.stereotype.Component;

@Component
public class ExectutorFactory {
    final CqrsMetaInfo metaInfo;

    public ExectutorFactory(CqrsMetaInfo metaInfo) {
        this.metaInfo = metaInfo;
    }

    AbstractExecutor createHandlerExecutor(ExecutorFactoryParams params) throws Exception {
        if (params.getAnnotationClass() == EventHandler.class) {
            return new EventExecutor(params.getOwner(),params.getMethod(), params.getTargetType(), params.getFactory());
        }
        if (params.getAnnotationClass() == CommandHandler.class) {
            return new CommandExecutor(params.getOwner(), params.getMethod(), params.getTargetType(), params.getFactory(),metaInfo);
        }
        if (params.getAnnotationClass() == EventSourcingHandler.class) {
            return new EventSourcingExecutor(params.getOwner(), params.getMethod(), params.getTargetType(), params.getFactory(),metaInfo);
        }
        throw new MessageException("Illegal annotion used... can not create handler-excecutor");

    }
}
