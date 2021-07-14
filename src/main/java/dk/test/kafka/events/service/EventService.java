package dk.test.kafka.events.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dk.test.kafka.events.annotations.BusinessObject;
import dk.test.kafka.events.model.BusinessEvent;
import dk.test.kafka.events.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
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

    private Map<Class<?>, String> eventClassesToNames = new HashMap();
    private Map<String, Class<?>> eventNamesToClasses = new HashMap<>();


    @Transactional
    public void fireEvent(BusinessEvent businessEvent) throws Exception {
        AggregateItem klientAggregateItem = aggregateRepository.findByType(businessEvent.getAggregateType().name());
        businessEvent.setVersion(klientAggregateItem.getVersion());
        businessEvent.setCreated_at(Instant.now());
        EventStoreItem eventStoreItem = new EventStoreItem();
        eventStoreItem.setActor(businessEvent.getActor());
        eventStoreItem.setAggregateid(klientAggregateItem.getAggregateid());

        String strEvent = null;
        try {
             strEvent = mapper.writeValueAsString(businessEvent);
        } catch (Exception e){
            System.out.println(e);
        }
        eventStoreItem.setData(strEvent);
        eventStoreItem.setKey(businessEvent.getKey());
        eventStoreItem.setVersion(klientAggregateItem.getVersion());
        eventStoreItem.setRequestId(businessEvent.getRequestId());
        eventStoreItem.setCreated_at(new Date(businessEvent.getCreated_at().toEpochMilli()));
        eventStoreRepository.saveAndFlush(eventStoreItem);
        klientAggregateItem.setVersion(klientAggregateItem.getVersion()+1);
        aggregateRepository.save(klientAggregateItem);
    }

    @PostConstruct
    public void initEventsList() throws Exception {
        ClassPathScanningCandidateComponentProvider scanner;
        scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(BusinessObject.class));
        //TODO more refined component-scanning here!
        Set<BeanDefinition> candidateComponents = scanner.findCandidateComponents("dk.test.klient.model.events");
        for (BeanDefinition definition: candidateComponents){
            System.out.println(definition.getBeanClassName());
            Class clazz = Class.forName(definition.getBeanClassName());
            BusinessObject annotation = AnnotationUtils.findAnnotation(clazz, BusinessObject.class);
            String eventName = (String) AnnotationUtils.getValue(annotation, "eventName");
            eventClassesToNames.put(clazz, eventName);
            eventNamesToClasses.put(eventName,clazz);

        }
    }

    public String getEventName(Class<?> clazz) {
        return this.eventClassesToNames.get(clazz);
    }

    public boolean hasEvent(String eventName){
        return this.eventNamesToClasses.containsKey(eventName);
    }

    public Class<?> getEventClass (String eventName){
        return this.eventNamesToClasses.get(eventName);
    }
}
