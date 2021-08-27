package dk.kfs.cqrs.internalmessages.events.internalmessages;

import dk.kfs.cqrs.internalmessages.events.annotations.CommandHandler;
import dk.kfs.cqrs.internalmessages.events.internalmessages.cqrsscanner.CqrsMetaInfo;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Method;

public class CommandExecutor extends AbstractExecutor {
    private final boolean createsAggregate;

    public CommandExecutor(AbstractExecutablesContainer owner, Method method, ResolvableType supportsType, AutowireCapableBeanFactory factory, CqrsMetaInfo metaInfo) {
        super(owner, method, supportsType, factory);
        CommandHandler handlerAnnotation = AnnotationUtils.findAnnotation(method, CommandHandler.class);
        createsAggregate = handlerAnnotation.createsAggregate();
    }

    @Override
    public boolean createsAggregate() {
        return this.createsAggregate;
    }

}
