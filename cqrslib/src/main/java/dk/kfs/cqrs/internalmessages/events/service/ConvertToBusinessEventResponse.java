package dk.kfs.cqrs.internalmessages.events.service;

import dk.kfs.cqrs.internalmessages.events.internalmessages.MessageContext;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ConvertToBusinessEventResponse {
    MessageContext context;
    Object businessEvent;
}
