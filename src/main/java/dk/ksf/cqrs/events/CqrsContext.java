package dk.ksf.cqrs.events;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CqrsContext {
    private String requestId;
    private Object targetInstance;
    private String key;
    private String eventNavn;
    private String actor;
    private long version;
}
