package dk.ksf.application.matrikel.events;

import dk.kfs.cqrs.internalmessages.events.annotations.AggregateIdentifier;
import dk.kfs.cqrs.internalmessages.events.annotations.BusinessEvent;
import dk.ksf.application.matrikel.model.Celle;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@BusinessEvent(eventName = "MatrikelOprettet_event")
public class MatrikelOprettetEvent {
    @AggregateIdentifier
    String id;
    String vej;
    List<Celle> celler = new ArrayList<>();
}
