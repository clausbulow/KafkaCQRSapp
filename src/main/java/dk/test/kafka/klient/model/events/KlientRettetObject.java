package dk.test.kafka.klient.model.events;

import dk.test.kafka.events.annotations.BusinessObject;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@BusinessObject(eventName = "klientRettet_event")
public class KlientRettetObject {
    String cpr;
    String fornavn;
    String efternavn;
}
