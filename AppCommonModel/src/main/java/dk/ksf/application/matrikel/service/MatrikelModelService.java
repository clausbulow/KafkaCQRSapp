package dk.ksf.application.matrikel.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dk.kfs.cqrs.internalmessages.CqrsProperties;
import dk.kfs.cqrs.internalmessages.events.model.*;
import dk.kfs.cqrs.internalmessages.events.service.EventStore2EventSourceProcessor;
import dk.ksf.application.matrikel.dto.CreateSnapshotsResponse;
import dk.ksf.application.matrikel.model.Matrikel;
import dk.ksf.application.matrikel.model.MatrikelRepository;
import dk.ksf.application.matrikel.dto.RetMatrikelDTO;
import dk.ksf.application.matrikel.events.MatrikelOprettetEvent;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class MatrikelModelService {
    @Autowired
    MatrikelRepository matrikelRepository;

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


    public List<RetMatrikelDTO> getAllKlienter() {
        final List<RetMatrikelDTO> result = new ArrayList<>();
        matrikelRepository.findAll().stream().forEach(matrikel -> result.add(RetMatrikelDTO.builder().id(matrikel.getId()).vejnavn(matrikel.getVejnavn()).version(matrikel.getVersion()).build()));
        return result;
    }

    public List<JsonNode> getEventStore() {
        return eventStore2EventSourceProcessor.execute(AggregateTypes.klient);
    }


    @Transactional
    public CreateSnapshotsResponse createSnapshots() throws Exception {
        CreateSnapshotsResponse response = new CreateSnapshotsResponse();
        //TDOD this can now get generlized by using metaInf read from Annotations
        Collection<Matrikel> allMatrikler = matrikelRepository.findAll();
        for (Matrikel matrikel : allMatrikler) {
            AggregateItem aggregateItem = aggregateRepository.findByTypeAndKey(AggregateTypes.matrikel, matrikel.getId());
            MatrikelOprettetEvent matrikelOprettetEvent = MatrikelOprettetEvent.builder().
                    id(matrikel.getId()).
                    vej(matrikel.getVejnavn()).
                    build();
            BusinessEvent<Object> businessEvent = BusinessEvent.builder().
                    eventNavn("matrikelOprettet_event").
                    key(matrikel.getId()).
                    requestId("snapshotter").
                    actor(props.getProducingActorId()).aggregateType(AggregateTypes.klient).
                    created_at(Instant.now()).
                    version(aggregateItem.getVersion()).
                    object(matrikelOprettetEvent).
                    build();
            String strData = mapper.writeValueAsString(businessEvent);
            SnapshotItem snapshotItem = SnapshotItem.builder().
                    id(UUID.randomUUID()).
                    actor(props.getProducingActorId()).
                    aggregatetype(AggregateTypes.matrikel).
                    businesskey(matrikel.getId()).
                    version(aggregateItem.getVersion()).
                    created_at(new Date(Instant.now().toEpochMilli())).
                    data(strData).
                    build();
            snapshotRepository.save(snapshotItem);
            aggregateItem.setVersion(aggregateItem.getVersion() + 1);
            aggregateRepository.save(aggregateItem);
        }
        response.getSnapshotsCreatedForAggretateTypes().add("snapshots created for aggregatetype " + AggregateTypes.klient);
        return response;

    }
}
