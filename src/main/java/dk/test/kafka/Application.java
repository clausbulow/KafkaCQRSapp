package dk.test.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@Slf4j
@Import(ApplicationConfig.class)
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }



    @Component
    static class Listener{
        @KafkaListener (id = "group1", topics = "topic1")
        public void listenToTopic1(@Payload String input, @Headers MessageHeaders messageHeaders, Acknowledgment ack){
            System.out.println("I'm hit: "+input);
            byte[] transactionId = (byte[])messageHeaders.get("transactionId");
            System.out.println("Here is my transactionId: "+new String(transactionId));
            ack.acknowledge();
        }
    }
}
