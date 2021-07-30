package dk.ksf.application.writemodel;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dk.ksf.cqrs.CqrsProperties;
import dk.ksf.cqrs.events.model.*;
import dk.ksf.application.common.dto.KlientDTO;
import dk.ksf.cqrs.events.service.EventStore2EventSourceProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.Instant;
import java.util.*;

import dk.ksf.application.common.eventobjects.*;

@Service
@Slf4j
public class KlientWriteModelService {
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
    CqrsProperties props;

    @Autowired
    EventStore2EventSourceProcessor eventStore2EventSourceProcessor;

    public void retKlient(KlientRettetObject klient, long version) throws Exception{
        KlientWriteModelItem klientItem = klientRepository.findById(klient.getCpr()).orElse(new KlientWriteModelItem());
        klientItem.setCpr(klient.getCpr());
        klientItem.setEfternavn(klient.getEfternavn());
        klientItem.setFornavn(klient.getFornavn());
        klientItem.setVersion(version);
        klientRepository.save(klientItem);
    }

    public void opretKlient(KlientOprettetObject klient, long version) throws Exception{
        KlientWriteModelItem klientItem = new KlientWriteModelItem();
        klientItem.setCpr(klient.getCpr());
        klientItem.setEfternavn(klient.getEfternavn());
        klientItem.setFornavn(klient.getFornavn());
        klientItem.setVersion(version);
        klientRepository.save(klientItem);
    }

    public List <KlientDTO> getAllKlienter(){
        final List<KlientDTO> result = new ArrayList<>();
        klientRepository.findAll().stream().forEach(klientItem -> result.add(KlientDTO.builder().cpr(klientItem.getCpr()).fornavn(klientItem.getFornavn()).efternavn(klientItem.getEfternavn()).version(klientItem.getVersion()).build()));
        return result;
    }

    public List<JsonNode> getEventStore(){
        return eventStore2EventSourceProcessor.execute(AggregateTypes.klient);
    }

    public Optional<KlientDTO> getKlient(String cpr) {
        final Optional<KlientWriteModelItem> optionalKlientItem = klientRepository.findById(cpr);
        KlientDTO klient = null;
        if (optionalKlientItem.isPresent()){
            KlientWriteModelItem klientItem = optionalKlientItem.get();
            klient = KlientDTO.builder().cpr(klientItem.getCpr()).fornavn(klientItem.getFornavn()).efternavn(klientItem.getEfternavn()).version(klientItem.getVersion()).build();
        }
        return Optional.ofNullable(klient);
    }


    @Transactional
    public void createSnapshots() throws Exception{
        Collection<KlientWriteModelItem> allKlients = klientRepository.findAll();
        for (KlientWriteModelItem klient: allKlients){
            AggregateItem aggregateItem = aggregateRepository.findByTypeAndKey(AggregateTypes.klient, klient.getCpr());
            KlientOprettetObject klientOprettetObject = KlientOprettetObject.builder().
                    cpr(klient.getCpr()).
                    fornavn(klient.getFornavn()).
                    efternavn(klient.getEfternavn()).
                    build();
            BusinessEvent<Object> businessEvent = BusinessEvent.builder().
                    eventNavn("klientOprettet_event").
                    key(klient.getCpr()).
                    requestId("snapshotter").
                    actor(props.getProducingActorId()).aggregateType(AggregateTypes.klient).
                    created_at(Instant.now()).
                    version(aggregateItem.getVersion()).
                    object(klientOprettetObject).
                    build();
            String strData = mapper.writeValueAsString(businessEvent);
            SnapshotItem snapshotItem = SnapshotItem.builder().
                    id(UUID.randomUUID()).
                    actor(props.getProducingActorId()).
                    aggregatetype(AggregateTypes.klient).
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
}