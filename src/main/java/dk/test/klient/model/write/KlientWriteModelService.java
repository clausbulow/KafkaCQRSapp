package dk.test.klient.model.write;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dk.test.kafka.events.model.*;
import dk.test.kafka.events.service.EventProcessor;
import dk.test.klient.model.KlientDTO;
import dk.test.klient.model.eventobjects.KlientOprettetObject;
import dk.test.klient.model.eventobjects.KlientRettetObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
@Slf4j
public class KlientWriteModelService {
    private AtomicLong currentEventVersion = new AtomicLong(Long.valueOf(-1));
    @Autowired
    KlientWriteModelRepository klientRepository;

    @Autowired
    EventStoreRepository eventStoreRepository;

    @Autowired
    AggregateRepository aggregateRepository;

    @Autowired
    SnapshotRepository snapshotRepository;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    EventProcessor eventProcessor;

    @Autowired
    EventStore2EventSourceProcessor eventStore2EventSourceProcessor;

    public void retKlient(KlientRettetObject klient, long version) throws Exception{
        KlientItem klientItem = klientRepository.findById(klient.getCpr()).orElse(new KlientItem());
        if (!currentEventVersion.compareAndSet(version-1,version)) {
            log.error("Invalid version when writing to klient write model.");
            //throw new InvalidEventVersionException("Invalid version when reading klient write model");
        }
        klientItem.setCpr(klient.getCpr());
        klientItem.setEfternavn(klient.getEfternavn());
        klientItem.setFornavn(klient.getFornavn());
        klientRepository.save(klientItem);
    }

    public void opretKlient(KlientOprettetObject klient, long version) throws Exception{
        KlientItem klientItem = new KlientItem();
        klientItem.setCpr(klient.getCpr());
        klientItem.setEfternavn(klient.getEfternavn());
        klientItem.setFornavn(klient.getFornavn());
        klientRepository.save(klientItem);
    }

    public List <KlientDTO> getAllKlienter(){
        final List<KlientDTO> result = new ArrayList<>();
        klientRepository.findAll().stream().forEach(klientItem -> result.add(KlientDTO.builder().cpr(klientItem.getCpr()).fornavn(klientItem.getFornavn()).efternavn(klientItem.getEfternavn()).build()));
        return result;
    }

    public List<JsonNode> getEventStore(){
        return eventStore2EventSourceProcessor.execute();
    }

    public Optional<KlientDTO> getKlient(String cpr) {
        final Optional<KlientItem> optionalKlientItem = klientRepository.findById(cpr);
        KlientDTO klient = null;
        if (optionalKlientItem.isPresent()){
            KlientItem klientItem = optionalKlientItem.get();
            klient = KlientDTO.builder().cpr(klientItem.getCpr()).fornavn(klientItem.getFornavn()).efternavn(klientItem.getEfternavn()).build();
        }
        return Optional.ofNullable(klient);
    }


    @Transactional
    public void createSnapshots() throws Exception{
        Collection<KlientItem> allKlients = klientRepository.findAll();
        for (KlientItem klient: allKlients){
            AggregateItem aggregateItem = aggregateRepository.findByTypeAndKey(AggregateTypes.klient.name(), klient.getCpr());
            KlientOprettetObject klientOprettetObject = KlientOprettetObject.builder().
                    cpr(klient.getCpr()).
                    fornavn(klient.getFornavn()).
                    efternavn(klient.getEfternavn()).
                    build();
            BusinessEvent<Object> businessEvent = BusinessEvent.builder().
                    eventNavn("klientOprettet_event").
                    key(klient.getCpr()).
                    requestId("snapshotter").
                    actor("KS").aggregateType(AggregateTypes.klient).
                    created_at(Instant.now()).
                    version(aggregateItem.getVersion()).
                    object(klientOprettetObject).
                    build();
            String strData = mapper.writeValueAsString(businessEvent);
            SnapshotItem snapshotItem = SnapshotItem.builder().
                    id(UUID.randomUUID()).
                    actor("KS").
                    type(AggregateTypes.klient.name()).
                    businesskey(klient.getCpr()).
                    version(aggregateItem.getVersion()).
                    created_at(new Date(Instant.now().toEpochMilli())).
                    data(strData).
                    build();
            snapshotRepository.save(snapshotItem);
            aggregateItem.setVersion(aggregateItem.getVersion()+1);
            aggregateRepository.save(aggregateItem);
        }

    }


    @EventListener
    @Order(10)
    public void initRepo(ContextRefreshedEvent event){
        eventStore2EventSourceProcessor.execute().forEach(eventStoreItem -> eventProcessor.process(eventStoreItem) );

    }
}
