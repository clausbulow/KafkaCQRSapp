package dk.test.kafka.klient.model.persistance;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.errors.InvalidStateStoreException;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Map;

@Component
@Slf4j
public class KlientKafkaRepository <String, Json>{
    @Autowired
    KafkaStreams streams;

    public KeyValueIterator<String, Json> getAllKlienter() throws Exception{
        return ordersStore().all();

    }

    private ReadOnlyKeyValueStore<String, Json> ordersStore() throws Exception {
        while (true) {
            try {
                StoreQueryParameters<ReadOnlyKeyValueStore<String, Json>> parameters = StoreQueryParameters.fromNameAndType("klienter_table", QueryableStoreTypes.keyValueStore());
                return streams.store(parameters);
            }  catch (InvalidStateStoreException e){
                log.warn("Message store is not ready for read. Exeption is "+e.getMessage()+", Exception type: "+e.getClass());
                Thread.sleep(100);
            }
        }
    }
}
