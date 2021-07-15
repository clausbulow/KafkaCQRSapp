package dk.test.klient.model;

import dk.test.kafka.events.annotations.Aggregate;
import dk.test.kafka.events.annotations.AggregateIdentifier;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name="klienter")
@Aggregate
public class KlientItem {
    @AggregateIdentifier
    @Id
    String cpr;
    String fornavn;
    String efternavn;

}
