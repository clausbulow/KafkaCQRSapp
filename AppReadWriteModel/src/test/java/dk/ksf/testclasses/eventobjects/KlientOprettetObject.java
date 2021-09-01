package dk.ksf.testclasses.eventobjects;

import dk.kfs.cqrs.internalmessages.events.annotations.AggregateIdentifier;
import dk.kfs.cqrs.internalmessages.events.annotations.BusinessEvent;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@BusinessEvent(eventName = "klientOprettet_event")
public class KlientOprettetObject {
    @AggregateIdentifier
    String cpr;
    String fornavn;
    String efternavn;
}
