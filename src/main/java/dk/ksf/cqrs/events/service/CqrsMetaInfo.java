package dk.ksf.cqrs.events.service;

import dk.ksf.application.common.eventobjects.KlientOprettetObject;
import dk.ksf.application.writemodel.KlientAggregate;
import dk.ksf.cqrs.CqrsProperties;
import dk.ksf.cqrs.events.annotations.Aggregate;
import dk.ksf.cqrs.events.annotations.BusinessObject;
import dk.ksf.cqrs.events.model.AggregateTypes;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


@Service
public class CqrsMetaInfo {
    @Autowired
    CqrsProperties props;

    private final Map<Class<?>, String> eventClassesToNames = new HashMap<>();
    private final Map<String, Class<?>> eventNamesToClasses = new HashMap<>();

    private final Map<Class<?>, AggregateTypes> aggregateClassesToAggregateType = new HashMap<>();
    private final Map<AggregateTypes, Class<?>> aggregateTypeToClass = new HashMap<>();


    public String getEventName(Class<?> klientOprettetObjectClass) {
        return eventClassesToNames.get(klientOprettetObjectClass);
    }

    public Class<?> getEventClass(String eventNavn) {
        return  eventNamesToClasses.get(eventNavn);
    }

    @PostConstruct
    public void initEventsList() throws Exception {
        scanForBusinessObjects();
    }

    private void scanForBusinessObjects() throws Exception{
        ClassPathScanningCandidateComponentProvider scanner;
        scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(BusinessObject.class));
        //Maybe use: Collection<Object> containers = context.getBeansWithAnnotation(Aggregate.class).values();
        Set<BeanDefinition> candidateComponents = scanner.findCandidateComponents(props.getEventobjectsPackage());
        for (BeanDefinition definition: candidateComponents){
            System.out.println(definition.getBeanClassName());
            Class clazz = Class.forName(definition.getBeanClassName());
            //new TypeDescriptor.OfMethod(clazz.getMethod("test",clazz));
            BusinessObject annotation = AnnotationUtils.findAnnotation(clazz, BusinessObject.class);
            String eventName = (String) AnnotationUtils.getValue(annotation, "eventName");
            eventNamesToClasses.put(eventName,clazz);
            eventClassesToNames.put(clazz, eventName);
        }
    }

    public void registerAggregate(AggregateTypes aggregateType, Class clazz) {
        this.aggregateTypeToClass.put(aggregateType, clazz);
        this.aggregateClassesToAggregateType.put(clazz,aggregateType);
    }

    public Collection<AggregateTypes> getAggregatesSupportedInApplication(){
        return this.aggregateClassesToAggregateType.values();
    }

    public AggregateTypes getAggregateType(Class<?> aClass) {
        return this.aggregateClassesToAggregateType.get(aClass);
    }
}
