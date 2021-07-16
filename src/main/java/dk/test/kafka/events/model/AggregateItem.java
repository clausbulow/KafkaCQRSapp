package dk.test.kafka.events.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name="aggregates")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class AggregateItem {
    @Id
    @org.hibernate.annotations.Type(type="org.hibernate.type.PostgresUUIDType")
    UUID id;
    long version;
    String businesskey;
    String type;
}
