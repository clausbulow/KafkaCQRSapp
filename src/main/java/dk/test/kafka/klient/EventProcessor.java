package dk.test.kafka.klient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dk.test.kafka.events.model.AggregateTypes;
import dk.test.kafka.events.model.BusinessEvent;
import dk.test.kafka.events.service.EventService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.processor.StateStore;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
public class EventProcessor implements Processor<String, JsonNode> {
    @Autowired
    EventService eventService;
    @Autowired
    ObjectMapper mapper;

    @Autowired
    ApplicationEventPublisher publisher;

    KeyValueStore<String, JsonNode> store;

    @Override
    public void init(ProcessorContext processorContext) {
        store = (KeyValueStore<String, JsonNode>) processorContext. getStateStore("klienter");

    }

    @Override
    public void process(String s, JsonNode json) {
        String eventNavn = json.get("eventNavn").asText();
        String requestId =  json.get("requestId").asText();
        String key = json.get("key").asText();
        String actor = json.get("actor").asText();
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
                                object(eventObj).
                                build();
                store.put(key,event);
                //store.flush();
                publisher.publishEvent(businessEvent);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            };

        };
    }

    @Override
    public void close() {

    }
}
