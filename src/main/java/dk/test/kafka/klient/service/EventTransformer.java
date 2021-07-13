package dk.test.kafka.klient.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dk.test.kafka.events.model.BusinessEvent;
import dk.test.kafka.events.service.EventService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.kstream.ValueTransformer;
import org.apache.kafka.streams.kstream.ValueTransformerSupplier;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EventTransformer implements ValueTransformer <JsonNode,JsonNode> {
    @Autowired
    EventService eventService;
    @Autowired
    ObjectMapper mapper;

    @Autowired
    ApplicationEventPublisher publisher;


    @Override
    public void init(ProcessorContext processorContext) {

    }

    @Override
    public JsonNode transform(JsonNode json) {
        String eventNavn = json.get("eventNavn").asText();
        String requestId =  json.get("requestId").asText();
        String key = json.get("key").asText();
        String actor = json.get("actor").asText();
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
                                object(eventObj).
                                build();
                publisher.publishEvent(businessEvent);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            };

        };
        return json;
    }


    @Override
    public void close() {

    }
}
