package dk.ksf.application.common.eventobjects;


import dk.kfs.cqrs.internalmessages.events.annotations.AggregateIdentifier;
import dk.kfs.cqrs.internalmessages.events.annotations.BusinessEvent;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@BusinessEvent(eventName = "klientRettet_event")
public class KlientRettetObject {
    @AggregateIdentifier
    String cpr;
    String fornavn;
    String efternavn;
}
