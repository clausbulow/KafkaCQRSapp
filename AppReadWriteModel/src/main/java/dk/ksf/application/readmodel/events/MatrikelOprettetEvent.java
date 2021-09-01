package dk.ksf.application.readmodel.events;

import dk.kfs.cqrs.internalmessages.events.annotations.AggregateIdentifier;
import dk.kfs.cqrs.internalmessages.events.annotations.BusinessEvent;
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
    List<Object> celler = new ArrayList<>();
}
