package dk.test.klient.model.write;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dk.test.kafka.events.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Consumer;

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

    public  List<JsonNode> execute(){
        final List<JsonNode> result = new ArrayList<>();
        final Map<String, Long> snapshotVersions = new HashMap<>();
        final List<SnapshotItem> allSnaphots = snapshotRepository.findLatestSnapShots();
        for (SnapshotItem snapshotItem: allSnaphots){
            try {
                log.info("Snapshotting for "+snapshotItem.getId()+", businessValue: "+snapshotItem.getBusinesskey());
                result.add(mapper.readTree(snapshotItem.getData()));
                snapshotVersions.put(snapshotItem.getBusinesskey(), snapshotItem.getVersion());
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

        final List<AggregateItem> klientAggregates = aggregateRepository.findByTypeAndKey(AggregateTypes.klient);

        for (AggregateItem aggregateItem: klientAggregates) {
            final String key = aggregateItem.getBusinesskey();
            final Long version = Optional.<Long>ofNullable(snapshotVersions.get(key)).orElse(Long.valueOf(-1));
            final List<EventStoreItem> events = eventStoreRepository.getEventStoreItemByAggregateIdAndVersion(key, version);
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
}
