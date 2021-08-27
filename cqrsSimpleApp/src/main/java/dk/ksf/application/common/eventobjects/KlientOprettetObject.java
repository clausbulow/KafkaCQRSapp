package dk.ksf.application.common.eventobjects;

import dk.kfs.cqrs.internalmessages.events.annotations.AggregateIdentifier;
import dk.kfs.cqrs.internalmessages.events.annotations.BusinessObject;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@BusinessObject(eventName = "klientOprettet_event")
public class KlientOprettetObject {
    @AggregateIdentifier
    String cpr;
    String fornavn;
    String efternavn;
}