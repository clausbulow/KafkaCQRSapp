package dk.ksf.cqrs.events.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dk.ksf.cqrs.events.model.BusinessEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class EventProcessor  {
    @Autowired
    EventService eventService;
    @Autowired
    ObjectMapper mapper;

    @Autowired
    ApplicationEventPublisher publisher;


    public void process(JsonNode json) {
        final String eventNavn = json.get("eventNavn").asText();
        final String requestId =  json.get("requestId").asText();
        final String key = json.get("key").asText();
        String actor = json.get("actor").asText();
        long version = 0;
        final Optional<JsonNode> optionalVersion = Optional.ofNullable(json.get("version"));
        if (optionalVersion.isPresent()){
            version = optionalVersion.get().asLong();
        }
       // AggregateTypes aggregateType = AggregateTypes.valueOf(json.get("actor").asText());
        if (eventNavn != null) {
            Class<?> eventClass = eventService.getEventClass(eventNavn);

            final JsonNode event = json.get("object");
            try {
                final Object eventObj = (Object) mapper.treeToValue((ObjectNode) event, eventClass);
                BusinessEvent businessEvent =
                        BusinessEvent.builder().
                                eventNavn(eventNavn).
                                requestId(requestId).
                                key(key).
                                actor(actor).
                                version(version).
                                object(eventObj).
                                build();
                publisher.publishEvent(businessEvent);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            };

        };
    }
}