package dk.kfs.cqrs.internalmessages.events.internalmessages;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MessageContext {
    private String requestId;
    private Object targetInstance;
    private String key;
    private String eventNavn;
    private String actor;
    private long version;
}
