package dk.test.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import dk.test.kafka.events.service.EventService;
import dk.test.kafka.klient.EventProcessor;
import dk.test.kafka.klient.service.EventTransformer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Named;
import org.apache.kafka.streams.state.KeyValueBytesStoreSupplier;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerde;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Configuration
@EnableKafka
@EnableKafkaStreams

public class ApplicationConfig {
    @Autowired
    KafkaProperties kafkaProperties;

    @Autowired
    EventService eventService;

    @Autowired
    EventProcessor processor;

    @Autowired
    EventTransformer transformer;

    @Bean
    public Map<String, Object> producerConfigs() {
        Map<String, Object> props =
                new HashMap<>(kafkaProperties.buildProducerProperties());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class);
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG,true);
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.MAX_BLOCK_MS_CONFIG,600000);

        props.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, "producer-1");
        props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION,5);
        //props.put(ProducerConfig.)
        return props;
    }

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> props = new HashMap<>(kafkaProperties.buildConsumerProperties());
        props.put(
                ConsumerConfig.GROUP_ID_CONFIG,
                "myGroup");
        props.put(
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class);
        props.put(
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,false);


        return new DefaultKafkaConsumerFactory<>(props);
    }


    @Bean
    public KafkaStreams kafkaStreams(KafkaProperties kafkaProperties,
                                     @Value("${spring.application.name}") String appName,
                                     StreamsBuilder streamsBuilder) {
        final Properties props = new Properties();

        // inject SSL related properties
        props.putAll(kafkaProperties.getSsl().buildProperties());
        props.putAll(kafkaProperties.getProperties());
        // stream config centric ones
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, appName);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, JsonSerde.class);
        props.put(StreamsConfig.STATE_DIR_CONFIG, "data");
        // others
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, JsonNode.class);

        final KafkaStreams kafkaStreams = new KafkaStreams(kafkaStreamTopology(streamsBuilder), props);
        kafkaStreams.start();

        return kafkaStreams;
    }

    @Bean
    public Topology kafkaStreamTopology(StreamsBuilder streamsBuilder) {
//        KeyValueBytesStoreSupplier perspectiveStoreSupplier = Stores.persistentKeyValueStore("klienterceller_perspektiv");
//        StoreBuilder<KeyValueStore<String, Object>> perspectiveStoreStoreBuilder = Stores.keyValueStoreBuilder(perspectiveStoreSupplier, Serdes.String(), new JsonSerde<>());
//        streamsBuilder.addStateStore(perspectiveStoreStoreBuilder);

        KeyValueBytesStoreSupplier klienter_table_supplier = Stores.persistentKeyValueStore("klienter_table");
        StoreBuilder<KeyValueStore<String, Object>> klientTableStoreBuilder = Stores.keyValueStoreBuilder(klienter_table_supplier, Serdes.String(), new JsonSerde<>());

        KStream<String, JsonNode> inputStream = streamsBuilder.stream("event.dataengineer.inventory.eventstore");
        inputStream.transformValues(() -> transformer).toTable(Named.as("klienter"),Materialized.as(klienter_table_supplier));
        return streamsBuilder.build();
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
