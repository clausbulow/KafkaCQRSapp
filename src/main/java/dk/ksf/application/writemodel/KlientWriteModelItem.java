package dk.ksf.application.writemodel;

import dk.ksf.cqrs.events.annotations.Aggregate;
import dk.ksf.cqrs.events.annotations.AggregateIdentifier;
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
public class KlientWriteModelItem {
    @AggregateIdentifier
    @Id
    String cpr;
    String fornavn;
    String efternavn;
    long version;
}