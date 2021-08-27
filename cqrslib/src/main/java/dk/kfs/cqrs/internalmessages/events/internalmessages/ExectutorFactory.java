package dk.kfs.cqrs.internalmessages.events.internalmessages;

import dk.kfs.cqrs.internalmessages.events.annotations.CommandHandler;
import dk.kfs.cqrs.internalmessages.events.annotations.EventHandler;
import dk.kfs.cqrs.internalmessages.events.annotations.EventSourcingHandler;
import dk.kfs.cqrs.internalmessages.events.internalmessages.cqrsscanner.CqrsMetaInfo;
import dk.kfs.cqrs.internalmessages.exceptions.MessageException;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;

@Component
public class ExectutorFactory {
    final CqrsMetaInfo metaInfo;

    public ExectutorFactory(CqrsMetaInfo metaInfo) {
        this.metaInfo = metaInfo;
    }

    AbstractExecutor createHandlerExecutor(ExecutorFactoryParams params) throws Exception {
        final Annotation annotation = params.getAnnotation();

        if (annotation instanceof EventHandler) {
            return new EventExecutor(params.getOwner(), params.getMethod(), params.getTargetType(), params.getFactory());
        }
        if (annotation instanceof CommandHandler) {
            return new CommandExecutor(params.getOwner(), params.getMethod(), params.getTargetType(), params.getFactory(), metaInfo);
        }
        if (annotation instanceof EventSourcingHandler) {
            return new EventSourcingExecutor(params.getOwner(), params.getMethod(), params.getTargetType(), params.getFactory(), metaInfo);
        }
        throw new MessageException("Illegal annotion used... can not create handler-excecutor");

    }
}
