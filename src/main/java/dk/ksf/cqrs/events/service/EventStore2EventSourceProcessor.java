package dk.ksf.cqrs.events.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dk.ksf.cqrs.CqrsProperties;
import dk.ksf.cqrs.events.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Slf4j
public class EventStore2EventSourceProcessor {

    @Autowired
    ObjectMapper mapper;
    @Autowired
    SnapshotRepository snapshotRepository;
    @Autowired
    AggregateRepository aggregateRepository;
    @Autowired
    EventStoreRepository eventStoreRepository;
    @Autowired
    CqrsProperties props;
    @Autowired
    EventProcessor eventProcessor;

    public  List<JsonNode> execute(AggregateTypes aggregateType){
        final List<JsonNode> result = new ArrayList<>();
        final Map<String, Long> snapshotVersions = new HashMap<>();

        //First: find and process the latest snapshots for each instance of an aggregate for the aggregate-type:
        final List<SnapshotItem> allSnaphots = snapshotRepository.findLatestSnapShotsForAggregate(aggregateType);
        for (SnapshotItem snapshotItem: allSnaphots){
            try {
                log.info("Snapshotting for "+snapshotItem.getId()+", businessValue: "+snapshotItem.getBusinesskey());
                result.add(mapper.readTree(snapshotItem.getData()));
                //Save the lates version for the instance, so we know from what version to apply business-events
                snapshotVersions.put(snapshotItem.getBusinesskey(), snapshotItem.getVersion());
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

        final List<AggregateItem> klientAggregates = aggregateRepository.findByTypeAndKey(aggregateType);

        for (AggregateItem aggregateItem: klientAggregates) {
            final String key = aggregateItem.getBusinesskey();
            //Find the version of a snapshot applied for the instance (if any)...
            final Long version = Optional.<Long>ofNullable(snapshotVersions.get(key)).orElse(Long.valueOf(-1));
            //And proceed producing events fram that version from the eventstore
            final List<EventStoreItem> events = eventStoreRepository.getEventStoreItemByAggregateIdAndVersion(aggregateType, key, version);
            events.stream().forEach(item -> {
                try {
                    log.info("Sourcing for event " + item.getId() + ", businessValue: " + item.getBusinesskey());
                    result.add(mapper.readTree(item.getData()));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            });
        }
        return result;
    }


    //Executes on application initialization
    @EventListener
    @Order(10)
    public void initRepo(ContextRefreshedEvent event){
        props.getInitializeFromAggregates().forEach(aggregateType -> execute(aggregateType).forEach(eventStoreItem -> eventProcessor.process(eventStoreItem)));

    }

}
