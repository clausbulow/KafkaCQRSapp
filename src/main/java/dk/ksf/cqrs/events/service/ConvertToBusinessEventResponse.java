package dk.ksf.cqrs.events.service;

import dk.ksf.cqrs.events.internalmessages.MessageContext;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ConvertToBusinessEventResponse {
    MessageContext context;
    Object businessEvent;
}
