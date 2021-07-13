package dk.test.kafka.klient.model.persistance;

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

public class KlientItem {
    @Id
    String cpr;
    String fornavn;
    String efternavn;

}
