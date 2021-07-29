package dk.ksf.cqrs.events.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name="snapshots")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class SnapshotItem {
    @Id
    @org.hibernate.annotations.Type(type="org.hibernate.type.PostgresUUIDType")
    UUID id;
    @Enumerated(EnumType.STRING)
    AggregateTypes aggregatetype;
    String businesskey;
    String actor;
    long version;
    Date created_at;
    String data;
}
