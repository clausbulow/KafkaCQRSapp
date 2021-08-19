package dk.ksf.cqrs.events.internalmessages;

import dk.ksf.cqrs.events.CqrsContext;
import dk.ksf.cqrs.events.annotations.*;
import dk.ksf.cqrs.events.service.EventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
public class AllCqrsAnnotationsHandler {
    private final AutowireCapableBeanFactory factory;
    private final CqrsMetaInfo metaInfo;
    private final EventService eventService;

    public AllCqrsAnnotationsHandler(AutowireCapableBeanFactory factory, CqrsMetaInfo metaInfo, EventService eventService){
        this.factory = factory;
        this.metaInfo = metaInfo;
        this.eventService = eventService;
    }
    private List<Class> classAnnotationsOfInterest = Arrays.asList(
            Aggregate.class, Perspective.class
    );
    List<AbstractExecutablesContainer> abstractExecutablesContainers = new ArrayList<>();

    public void scanForClassAnnotation(String  basePackage) throws Exception {
        final ClassPathScanningCandidateComponentProvider scanner;
        scanner = new ClassPathScanningCandidateComponentProvider(false);
        classAnnotationsOfInterest.forEach(clazz -> scanner.addIncludeFilter(new AnnotationTypeFilter(clazz)));
        final Set<BeanDefinition> candidateComponents = scanner.findCandidateComponents(basePackage);
        for (BeanDefinition definition : candidateComponents) {
            Class clazz = Class.forName(definition.getBeanClassName());
            scanClassForHandlerContainers(clazz);
        }
        scanHandlerContainersForHandlers();
        log.info("scanning done");


    }

    public void scanHandlerContainersForHandlers() throws Exception {
        abstractExecutablesContainers.forEach(handlerContainer -> {
            try {
                handlerContainer.scanForAnnotations();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void scanClassForHandlerContainers(final Class clazz) {
        final Aggregate aggregateAnnotation = (Aggregate) clazz.getAnnotation(Aggregate.class);
        if (aggregateAnnotation != null){
            abstractExecutablesContainers.add(new AggregateExecutablesContainer(aggregateAnnotation, clazz, factory, metaInfo, eventService));
        } else {
            final Perspective perspectiveAnnotation = (Perspective)  clazz.getAnnotation(Perspective.class);
            if (perspectiveAnnotation != null){
                abstractExecutablesContainers.add(new PerspectiveExecutablesContainer(perspectiveAnnotation, clazz,factory, metaInfo, eventService));
            }
        }
    }

    public List getHandlerContainers() {
        return abstractExecutablesContainers;
    }

    public void signalEventHandlers (CqrsContext context, Object event) throws Exception{
        abstractExecutablesContainers.forEach(container -> {
            try {
                container.signalEventHandlers(context, event);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void signalEventSourcingHandlers (CqrsContext context, Object event) throws Exception{
        abstractExecutablesContainers.forEach(container -> {
            try {
                container.signalEventSourcingHandlers(context, event);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void signalCommandHandlers (CqrsContext context, Object command) throws Exception{
        abstractExecutablesContainers.forEach(container -> {
            try {
                container.signalCommandHandlers(context,command);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }



    public void initEventsList() throws Exception {
        this.scanForClassAnnotation("dk.ksf.application");
    }


}
