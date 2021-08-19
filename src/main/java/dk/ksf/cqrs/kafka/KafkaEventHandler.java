package dk.ksf.cqrs.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import dk.ksf.cqrs.events.internalmessages.EventDispatcher;
import dk.ksf.cqrs.events.service.ConvertToBusinessEventResponse;
import dk.ksf.cqrs.events.service.EventProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaEventHandler {

    @Autowired
    EventProcessor processor;

    @Autowired
    EventValidator validator;
    @Autowired
    EventDispatcher dispatcher;

    @KafkaListener(id = "#{'${spring.application.name}'}", topics = "#{'${kfs.cqrs.topicnames}'}")
    public void listen(@Payload JsonNode jsonBusinessEvent, @Headers MessageHeaders messageHeaders, Acknowledgment ack) {
        try {
            //validator.validateEvent(businessEvent);
            ConvertToBusinessEventResponse eventWithContext = processor.converToBusinessEvent(jsonBusinessEvent);
            dispatcher.publishEventToEventHandlers(eventWithContext.getContext(), eventWithContext.getBusinessEvent());
            ack.acknowledge();
        } catch (Exception e) {
            log.error("An error occured while processing message " + e.getMessage());
            //TODO generic eventhhandlig
        }
    }

}
