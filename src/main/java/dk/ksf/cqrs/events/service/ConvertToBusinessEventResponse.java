package dk.ksf.cqrs.events.service;

import dk.ksf.cqrs.events.CqrsContext;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ConvertToBusinessEventResponse {
    CqrsContext context;
    Object businessEvent;
}
