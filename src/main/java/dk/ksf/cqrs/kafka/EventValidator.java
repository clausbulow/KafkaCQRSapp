package dk.ksf.cqrs.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dk.ksf.application.writemodel.KlientAggregate;
import dk.ksf.application.writemodel.KlientWriteModelRepository;
import dk.ksf.cqrs.exceptions.InvalidDataVersionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

//TODO Should maybe get provided by another application layer
@Service
@Slf4j
public class EventValidator {
    @Autowired
    ObjectMapper mapper;
    @Autowired
    KlientWriteModelRepository repository;

    public void validateEvent(JsonNode businessEvent) throws Exception {
        final String key = businessEvent.get("key").asText();
        final long version = businessEvent.get("version").asLong();
        log.info("validating message " + key);
        final Optional<KlientAggregate> optionalKlient = repository.findById(key);
        if (optionalKlient.isPresent()) {
            if ((optionalKlient.get().getVersion() + 1) != version) {
                throw new InvalidDataVersionException("Wrong Version recieved: " + version + ", last version registered; " + optionalKlient.get().getVersion());
            } else {
                return;
            }

        }
/*
        if (version != 0){
            throw new InvalidEventVersionException("No prior version of aggregate fonud")
        }
*/
        return;

    }
}
