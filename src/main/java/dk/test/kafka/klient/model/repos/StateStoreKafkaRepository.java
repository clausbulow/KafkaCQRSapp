package dk.test.kafka.klient.model.repos;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.errors.InvalidStateStoreException;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class StateStoreKafkaRepository<String, Json>{
    @Autowired
    KafkaStreams streams;

    public KeyValueIterator<Long, Json> getAllEvents() throws Exception{
        return eventStore().all();

    }

    private ReadOnlyKeyValueStore<Long, Json> eventStore() throws Exception {
        while (true) {
            try {
                StoreQueryParameters<ReadOnlyKeyValueStore<Long, Json>> parameters = StoreQueryParameters.fromNameAndType("eventstore", QueryableStoreTypes.keyValueStore());
                return streams.store(parameters);
            }  catch (InvalidStateStoreException e){
                log.warn("Message store is not ready for read. Exeption is "+e.getMessage()+", Exception type: "+e.getClass());
                Thread.sleep(1000);
            }
        }
    }
}
