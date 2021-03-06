package dk.kfs.cqrs.internalmessages;

import dk.kfs.cqrs.internalmessages.events.internalmessages.AllExecutablesContainer;
import dk.kfs.cqrs.internalmessages.events.internalmessages.EventDispatcher;
import dk.kfs.cqrs.internalmessages.events.internalmessages.cqrsscanner.CqrsMetaInfo;
import dk.kfs.cqrs.internalmessages.events.model.BusinessEventFactory;
import dk.kfs.cqrs.internalmessages.events.model.EventStoreRepository;
import dk.kfs.cqrs.internalmessages.events.service.AggregateLifecycle;
import dk.kfs.cqrs.internalmessages.events.service.EventProcessor;
import dk.kfs.cqrs.internalmessages.events.service.EventService;
import dk.kfs.cqrs.internalmessages.events.service.EventStore2EventSourceProcessor;
import dk.kfs.cqrs.internalmessages.kafka.KafkaEventHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Import({AggregateLifecycle.class, EventService.class, EventProcessor.class, EventStore2EventSourceProcessor.class, BusinessEventFactory.class,CqrsMetaInfo.class, CqrsProperties.class, EventStoreDatasourceConfig.class, KafkaEventHandler.class, AllExecutablesContainer.class, EventDispatcher.class})
@EnableKafka
@Slf4j
public class CqrsConfiguration {
    @Autowired
    KafkaProperties kafkaProperties;


    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> props = new HashMap<>(kafkaProperties.buildConsumerProperties());
        props.put(
                ConsumerConfig.GROUP_ID_CONFIG,
                "klienter_application");
        props.put(
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class);
        props.put(
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                JsonDeserializer.class);
        props.put("spring.json.trusted.packages", "*");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        return new DefaultKafkaConsumerFactory<>(props);
    }


    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String>
    kafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        return factory;
    }

}
