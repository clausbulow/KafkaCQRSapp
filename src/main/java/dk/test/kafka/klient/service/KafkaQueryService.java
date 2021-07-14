package dk.test.kafka.klient.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dk.test.kafka.events.model.BusinessEvent;
import dk.test.kafka.klient.controller.KlientDTO;
import dk.test.kafka.klient.model.repos.KlientKafkaRepository;
import dk.test.kafka.klient.model.repos.StateStoreKafkaRepository;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class KafkaQueryService {

    @Autowired
    KlientKafkaRepository klientRepo;

    @Autowired
    StateStoreKafkaRepository stateStoreRepo;

    @Autowired
    ObjectMapper mapper;


    public List<KlientDTO> getAllKlienter() throws Exception{
        List<KlientDTO> result = new ArrayList<>();
        KeyValueIterator<String, JsonNode> allKlienter = klientRepo.getAllKlienter();
        while (allKlienter.hasNext()){
            KeyValue<String, JsonNode> entry = allKlienter.next();
            JsonNode businessEvent = entry.value;
            JsonNode businessObject = businessEvent.get("object");
            KlientDTO klientDTO = mapper.treeToValue(businessObject, KlientDTO.class);
            result.add(klientDTO);
        }
        return result;
    }

    public List<BusinessEvent<?>> getEventStoreItems() throws Exception{
        List<BusinessEvent<?>> result = new ArrayList<>();
        KeyValueIterator<Long, JsonNode> allEvents = stateStoreRepo.getAllEvents();
        while (allEvents.hasNext()){
            KeyValue<Long, JsonNode> entry = allEvents.next();
            BusinessEvent businessEvent = mapper.treeToValue(entry.value, BusinessEvent.class);
            result.add(businessEvent);
        }
        return result;
    }

}
