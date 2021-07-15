package dk.test.klient.model.write;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dk.test.kafka.events.model.EventStoreItem;
import dk.test.kafka.events.model.EventStoreRepository;
import dk.test.kafka.events.service.EventProcessor;
import dk.test.klient.controller.KlientDTO;
import dk.test.klient.model.KlientItem;
import dk.test.klient.model.events.KlientOprettetObject;
import dk.test.klient.model.events.KlientRettetObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class KlientService {
    @Autowired
    KlientEventRepository repository;

    @Autowired
    EventStoreRepository eventStore;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    EventProcessor processor;

    public void retKlient(KlientRettetObject klient) throws Exception{
        KlientItem klientItem = repository.findById(klient.getCpr()).orElse(new KlientItem());
        klientItem.setCpr(klient.getCpr());
        klientItem.setEfternavn(klient.getEfternavn());
        klientItem.setFornavn(klient.getFornavn());
        repository.save(klientItem);
    }

    public void opretKlient(KlientOprettetObject klient) throws Exception{
        KlientItem klientItem = new KlientItem();
        klientItem.setCpr(klient.getCpr());
        klientItem.setEfternavn(klient.getEfternavn());
        klientItem.setFornavn(klient.getFornavn());
        repository.save(klientItem);
    }

    public List <KlientDTO> getAllKlienter(){
        final List<KlientDTO> result = new ArrayList<>();
        repository.findAll().stream().forEach( klientItem -> result.add(KlientDTO.builder().cpr(klientItem.getCpr()).fornavn(klientItem.getFornavn()).efternavn(klientItem.getEfternavn()).build()));
        return result;
    }

    public List<JsonNode> getEventStore(){
        final List<EventStoreItem> eventStoreItems = eventStore.getEventStoryByAggregate(KlientAggregate.this_aggregate_type.name());
        final ArrayList<JsonNode> result = new ArrayList<>();
        eventStoreItems.stream().forEach(item -> {
            try {
                result.add(mapper.readTree(item.getData()));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        });
        return result;
    }

    public Optional<KlientDTO> getKlient(String cpr) {
        final Optional<KlientItem> optionalKlientItem = repository.findById(cpr);
        KlientDTO klient = null;
        if (optionalKlientItem.isPresent()){
            KlientItem klientItem = optionalKlientItem.get();
            klient = KlientDTO.builder().cpr(klientItem.getCpr()).fornavn(klientItem.getFornavn()).efternavn(klientItem.getEfternavn()).build();
        }
        return Optional.ofNullable(klient);
    }


    @EventListener
    public void initRepo(ContextRefreshedEvent event){
        final List<EventStoreItem> eventStoreItems = eventStore.getEventStoryByAggregate(KlientAggregate.this_aggregate_type.name());
        eventStoreItems.stream().forEach(item -> {
            try {
                processor.process(mapper.readTree(item.getData()));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        });

    }
}
