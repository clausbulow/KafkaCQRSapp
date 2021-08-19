package dk.ksf.cqrs.events;

import dk.ksf.cqrs.CqrsProperties;
import dk.ksf.cqrs.events.internalmessages.CqrsMetaInfo;
import dk.ksf.cqrs.events.model.BusinessEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.time.Instant;

@Component
public class BusinessEventFactory {
    @Autowired
    CqrsProperties props;

    @Autowired
    CqrsMetaInfo metaInfo;

    public BusinessEventFactory(CqrsProperties props, CqrsMetaInfo metaInfo) {
        this.props = props;
        this.metaInfo = metaInfo;
    }

    public <T> BusinessEvent<T> createBusinessEvent(Object creator, CqrsContext context, T businessObject) throws Exception {
        Field keyField = metaInfo.getKeyField(creator.getClass());
        keyField.get(creator);

        return BusinessEvent.<T>builder().
                eventNavn(metaInfo.getEventName(businessObject.getClass())).
                actor(props.getProducingActorId()).
                key((String) keyField.get(creator)).
                requestId(context.getRequestId()).
                aggregateType(metaInfo.getAggregateType(creator.getClass())).
                created_at(Instant.now()).
                object(businessObject).
                build();
    }

}
