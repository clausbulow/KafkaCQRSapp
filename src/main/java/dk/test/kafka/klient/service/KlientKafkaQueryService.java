package dk.test.kafka.klient.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dk.test.kafka.klient.controller.KlientDTO;
import dk.test.kafka.klient.model.persistance.KlientJpaRepository;
import dk.test.kafka.klient.model.persistance.KlientKafkaRepository;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class KlientKafkaQueryService {

    @Autowired
    KlientKafkaRepository repository;

    @Autowired
    ObjectMapper mapper;


    public List<KlientDTO> getAllKlienter() throws Exception{
        List<KlientDTO> result = new ArrayList<>();
        KeyValueIterator<String, JsonNode> allKlienter = repository.getAllKlienter();
        while (allKlienter.hasNext()){
            KeyValue<String, JsonNode> entry = allKlienter.next();
            JsonNode businessEvent = entry.value;
            JsonNode businessObject = businessEvent.get("object");
            KlientDTO klientDTO = mapper.treeToValue(businessObject, KlientDTO.class);
            result.add(klientDTO);
        }
        return result;

    }
}
