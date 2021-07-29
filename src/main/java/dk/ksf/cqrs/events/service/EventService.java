package dk.ksf.cqrs.events.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dk.ksf.cqrs.CqrsProperties;
import dk.ksf.cqrs.events.annotations.BusinessObject;
import dk.ksf.cqrs.events.model.BusinessEvent;
import dk.ksf.cqrs.events.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.sql.Date;
import java.time.Instant;
import java.util.*;

@Service
public class EventService {

    @Autowired
    EventStoreRepository eventStoreRepository;

    @Autowired
    AggregateRepository aggregateRepository;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    private ApplicationContext context;

    @Autowired
    CqrsProperties props;

    private Map<Class<?>, String> eventClassesToNames = new HashMap();
    private Map<String, Class<?>> eventNamesToClasses = new HashMap<>();


    @Transactional(transactionManager = "eventstoreTransactionManager")
    public void fireEvent(BusinessEvent businessEvent) throws Exception {
        AggregateItem klientAggregateItem = aggregateRepository.findByTypeAndKey(businessEvent.getAggregateType(), businessEvent.getKey());
        if (klientAggregateItem == null){
            klientAggregateItem = new AggregateItem();
            klientAggregateItem.setId(UUID.randomUUID());
            klientAggregateItem.setBusinesskey(businessEvent.getKey());
            //todo - read this value from properties
            klientAggregateItem.setActor(businessEvent.getActor());
            klientAggregateItem.setAggregatetype(businessEvent.getAggregateType());
        }
        businessEvent.setVersion(klientAggregateItem.getVersion());
        businessEvent.setCreated_at(Instant.now());
        EventStoreItem eventStoreItem = new EventStoreItem();
        eventStoreItem.setId(UUID.randomUUID());
        eventStoreItem.setActor(businessEvent.getActor());
        eventStoreItem.setAggregatetype(businessEvent.getAggregateType());

        //todo inspect this
        String strEvent = null;
        try {
             strEvent = mapper.writeValueAsString(businessEvent);
        } catch (Exception e){
            System.out.println(e);
        }
        eventStoreItem.setData(strEvent);
        eventStoreItem.setBusinesskey(businessEvent.getKey());
        eventStoreItem.setVersion(klientAggregateItem.getVersion());
        eventStoreItem.setRequestId(businessEvent.getRequestId());
        eventStoreItem.setCreated_at(new Date(businessEvent.getCreated_at().toEpochMilli()));
        klientAggregateItem.setVersion(klientAggregateItem.getVersion()+1);
        aggregateRepository.save(klientAggregateItem);
        eventStoreRepository.save(eventStoreItem);
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
            eventClassesToNames.put(clazz, eventName);
            eventNamesToClasses.put(eventName,clazz);

        }
    }

    public String getEventName(Class<?> clazz) {
        return this.eventClassesToNames.get(clazz);
    }

    public Class<?> getEventClass (String eventName){
        return this.eventNamesToClasses.get(eventName);
    }

}
