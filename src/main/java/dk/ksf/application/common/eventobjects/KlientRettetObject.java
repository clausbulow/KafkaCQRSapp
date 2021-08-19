package dk.ksf.application.common.eventobjects;

import dk.ksf.cqrs.events.annotations.AggregateIdentifier;
import dk.ksf.cqrs.events.annotations.BusinessObject;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@BusinessObject(eventName = "klientRettet_event")
public class KlientRettetObject {
    @AggregateIdentifier
    String cpr;
    String fornavn;
    String efternavn;
}
