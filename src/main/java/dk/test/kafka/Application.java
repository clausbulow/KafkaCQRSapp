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
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@SpringBootApplication
@Slf4j
@Import(ApplicationConfig.class)
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public NewTopic topic() {
        return new NewTopic("topic1", 1, (short) 1);
    }


    @Component
    static class Listener{
        @KafkaListener (id = "group1", topics = "topic1")
        public void listenToTopic1(ConsumerRecord<String,String> cr, @Payload String input){
            System.out.println("I'm hit: "+input);
            System.out.println("Here are my header values:");
            cr.headers().forEach(header -> System.out.println(header.key()+":"+header.value()));
        }
    }
}
