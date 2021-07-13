package dk.test.kafka.events.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@Table(name="aggregates")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class AggregateItem {
    @Id
    @org.hibernate.annotations.Type(type="org.hibernate.type.PostgresUUIDType")
    UUID aggregateid;
    String type;
    long version;
}
