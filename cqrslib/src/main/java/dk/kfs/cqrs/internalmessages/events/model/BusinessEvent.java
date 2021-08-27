package dk.kfs.cqrs.internalmessages.events.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import org.springframework.core.ResolvableType;
import org.springframework.core.ResolvableTypeProvider;

import java.time.Instant;

@Builder
@Data
public class BusinessEvent<T> implements ResolvableTypeProvider {
    T object;
    private String eventNavn;
    private String requestId;
    private String key;
    private String actor;
    private long version;
    private Instant created_at;
    private AggregateTypes aggregateType;

    @Override
    @JsonIgnore
    public ResolvableType getResolvableType() {
        return ResolvableType.forClassWithGenerics(getClass(), object.getClass());
    }
}
