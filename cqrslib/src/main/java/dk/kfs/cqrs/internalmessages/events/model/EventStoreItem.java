package dk.kfs.cqrs.internalmessages.events.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "eventstore")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventStoreItem {
    @Id
    @org.hibernate.annotations.Type(type = "org.hibernate.type.PostgresUUIDType")
    UUID id;

    @SequenceGenerator(name = "eventstore_sequencenumber_seq",
            sequenceName = "eventstore_sequencenumber_seq",
            allocationSize = 1)

    @Column(nullable = false)
    String businesskey;
    @Column(nullable = false)
    String actor;
    String requestId;
    long version;
    @Column(nullable = false)
    Date created_at;
    @Column(length = 32000)
    String data;
    @Enumerated(EnumType.STRING)
    private AggregateTypes aggregatetype;

}
