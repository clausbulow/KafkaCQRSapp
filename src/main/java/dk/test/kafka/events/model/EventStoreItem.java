package dk.test.kafka.events.model;

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
    @SequenceGenerator(name="eventstore_sequencenumber_seq",
            sequenceName="eventstore_sequencenumber_seq",
            allocationSize=1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "eventstore_sequencenumber_seq")
    Long sequencenumber;

    @Column(name="aggregateid", updatable = false, nullable = false)
    @org.hibernate.annotations.Type(type="org.hibernate.type.PostgresUUIDType")
    UUID aggregateid;

    String key;

    String actor;
    @Column(name = "requestid")
    String requestId;
    long version;
    Date created_at;
    String data;

}
