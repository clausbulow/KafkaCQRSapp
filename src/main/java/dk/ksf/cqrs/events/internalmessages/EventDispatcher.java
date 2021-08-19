package dk.ksf.cqrs.events.internalmessages;

import dk.ksf.cqrs.events.CqrsContext;
import dk.ksf.cqrs.events.annotations.*;
import dk.ksf.cqrs.events.model.BusinessEvent;
import dk.ksf.cqrs.events.service.CqrsMetaInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.data.util.ClassTypeInformation;
import org.springframework.data.util.TypeInformation;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import javax.annotation.PostConstruct;
import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Stream;

@Component("testdispatcher")
@Slf4j
public class EventDispatcher {
    @Autowired
    AllCqrsAnnotationsHandler cqrsHandlers;

    public EventDispatcher (AllCqrsAnnotationsHandler cqrsHandlers) {
        this.cqrsHandlers = cqrsHandlers;
    }


    public void publishEventToEventSourcing(CqrsContext context, Object event) throws Exception{
        cqrsHandlers.signalEventSourcingHandlers(context,event);
    }
    public void publishEventToEventHandlers(CqrsContext context,Object event) throws Exception{
        cqrsHandlers.signalEventHandlers(context, event);
    }

    public void publishCommand(CqrsContext context,Object command) throws Exception{
        cqrsHandlers.signalCommandHandlers(context,command);
    }



}
