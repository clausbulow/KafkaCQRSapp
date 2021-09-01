package dk.ksf.application.readmodel;

import dk.kfs.cqrs.internalmessages.events.annotations.AggregateIdentifier;
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
@Table(name = "klienter")
public class KlientReadModelItem {
    @AggregateIdentifier
    @Id
    String cpr;
    String fornavn;
    String efternavn;
    long version;
}
