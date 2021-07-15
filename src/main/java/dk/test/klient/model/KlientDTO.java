package dk.test.klient.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KlientDTO {
    String cpr;
    String fornavn;
    String efternavn;
}
