package dk.test.klient.model.eventobjects;

import dk.test.kafka.events.annotations.BusinessObject;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@BusinessObject(eventName = "klientOprettet_event")
public class KlientOprettetObject {
    String cpr;
    String fornavn;
    String efternavn;
}
