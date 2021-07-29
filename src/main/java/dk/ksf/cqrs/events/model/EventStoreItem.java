package dk.ksf.cqrs.events.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name="eventstore")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventStoreItem {
    @Id
    @org.hibernate.annotations.Type(type="org.hibernate.type.PostgresUUIDType")
    UUID id;

    @SequenceGenerator(name="eventstore_sequencenumber_seq",
            sequenceName="eventstore_sequencenumber_seq",
            allocationSize=1)

    String businesskey;
    String actor;
    @Enumerated(EnumType.STRING)
    private AggregateTypes aggregatetype;
    String requestId;
    long version;
    Date created_at;
    String data;

}
