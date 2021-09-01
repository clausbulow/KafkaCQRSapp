//inspired by https://blog.nebrass.fr/playing-with-cqrs-and-event-sourcing-in-spring-boot-and-axon/
package dk.ksf.application.matrikel.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;

@Slf4j
@NoArgsConstructor
@Data
@Entity
@Table(name = "celle")
public class Celle {
    @Id
    String id;
    int nummer;
    int antalPladser;

    @JsonIgnore
    @ManyToOne (fetch = FetchType.LAZY, targetEntity = Matrikel.class)
    Matrikel matrikel;
}
