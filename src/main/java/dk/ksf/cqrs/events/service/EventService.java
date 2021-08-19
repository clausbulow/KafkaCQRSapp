package dk.ksf.cqrs.events.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dk.ksf.cqrs.events.BusinessEventFactory;
import dk.ksf.cqrs.events.CqrsContext;
import dk.ksf.cqrs.events.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.Instant;
import java.util.UUID;

@Service
public class EventService {

    @Autowired
    EventStoreRepository eventStoreRepository;

    @Autowired
    AggregateRepository aggregateRepository;

    @Autowired
    BusinessEventFactory beFactory;

    @Autowired
    ObjectMapper mapper;

    public void fireEvent(Object creator, CqrsContext context, Object event) throws Exception {
        BusinessEvent<?> businessEvent = beFactory.createBusinessEvent(creator, context, event);
        AggregateItem klientAggregateItem = aggregateRepository.findByTypeAndKey(businessEvent.getAggregateType(), businessEvent.getKey());
        if (klientAggregateItem == null) {
            klientAggregateItem = new AggregateItem();
            klientAggregateItem.setId(UUID.randomUUID());
            klientAggregateItem.setBusinesskey(businessEvent.getKey());
            //todo - read this value from properties
            klientAggregateItem.setActor(businessEvent.getActor());
            klientAggregateItem.setAggregatetype(businessEvent.getAggregateType());
        }
        businessEvent.setVersion(klientAggregateItem.getVersion());
        businessEvent.setCreated_at(Instant.now());
        EventStoreItem eventStoreItem = new EventStoreItem();
        eventStoreItem.setId(UUID.randomUUID());
        eventStoreItem.setActor(businessEvent.getActor());
        eventStoreItem.setAggregatetype(businessEvent.getAggregateType());

        //todo inspect this
        String strEvent = null;
        try {
            strEvent = mapper.writeValueAsString(businessEvent);
        } catch (Exception e) {
            System.out.println(e);
        }
        eventStoreItem.setData(strEvent);
        eventStoreItem.setBusinesskey(businessEvent.getKey());
        eventStoreItem.setVersion(klientAggregateItem.getVersion());
        eventStoreItem.setRequestId(businessEvent.getRequestId());
        eventStoreItem.setCreated_at(new Date(businessEvent.getCreated_at().toEpochMilli()));
        klientAggregateItem.setVersion(klientAggregateItem.getVersion() + 1);
        aggregateRepository.save(klientAggregateItem);
        eventStoreRepository.save(eventStoreItem);
    }


}
