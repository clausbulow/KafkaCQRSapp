package dk.test.klient.model.write;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dk.test.kafka.events.model.AggregateItem;
import dk.test.kafka.events.model.AggregateRepository;
import dk.test.kafka.events.model.EventStoreItem;
import dk.test.kafka.events.model.EventStoreRepository;
import dk.test.kafka.events.service.EventProcessor;
import dk.test.klient.model.KlientDTO;
import dk.test.klient.model.KlientItem;
import dk.test.klient.model.eventsobject.KlientOprettetObject;
import dk.test.klient.model.eventsobject.KlientRettetObject;
import dk.test.klient.model.exceptions.InvalidEventVersionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Service
@Slf4j
public class KlientWriteModelService {
    private AtomicLong currentEventVersion = new AtomicLong(Long.valueOf(-1));
    @Autowired
    KlientWriteModelRepository repository;

    @Autowired
    EventStoreRepository eventStoreRepository;

    @Autowired
    AggregateRepository aggregateRepository;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    EventProcessor processor;

    public void retKlient(KlientRettetObject klient, long version) throws Exception{
        KlientItem klientItem = repository.findById(klient.getCpr()).orElse(new KlientItem());
        if (!currentEventVersion.compareAndSet(version-1,version)) {
            log.error("Invalid version when writing to klient write model.");
            //throw new InvalidEventVersionException("Invalid version when reading klient write model");
        }
        klientItem.setCpr(klient.getCpr());
        klientItem.setEfternavn(klient.getEfternavn());
        klientItem.setFornavn(klient.getFornavn());
        repository.save(klientItem);
    }

    public void opretKlient(KlientOprettetObject klient, long version) throws Exception{
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
        final ArrayList<JsonNode> result = new ArrayList<>();
        List<AggregateItem> klientAggregates = aggregateRepository.findByType("klient");
        for (AggregateItem aggregateItem: klientAggregates){
            String key = aggregateItem.getBusinesskey();
            List<EventStoreItem> events = eventStoreRepository.getEventStoreItemByAggregateId(key);
            events.stream().forEach(item -> {
                try {
                    result.add(mapper.readTree(item.getData()));
                } catch (JsonProcessingException e) {
                        e.printStackTrace();
                }
            });
        }

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
        List<AggregateItem> klientAggregates = aggregateRepository.findByType("klient");
        for (AggregateItem aggregateItem: klientAggregates){
            String key = aggregateItem.getBusinesskey();
            List<EventStoreItem> events = eventStoreRepository.getEventStoreItemByAggregateId(key);
            events.stream().forEach(item -> {
                try {
                    log.info("Sourcing for event "+item.getId()+", businessValue: "+item.getBusinesskey());
                    processor.process(mapper.readTree(item.getData()));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            });
        }


        //         = eventSt.findByType("klient");
//        final List<AggregateItem> aggregateStoreItems = aggrateStore.findAll();
//        for (AggregateItem aggregateItem: aggregateStoreItems){
//            List<EventStoreItem> events = aggregateItem.getEvents();
//            events.stream().forEach(item -> {
//                try {
//                    processor.process(mapper.readTree(item.getData()));
//                } catch (JsonProcessingException e) {
//                    e.printStackTrace();
//                }
//            });
//        }
    }
}
