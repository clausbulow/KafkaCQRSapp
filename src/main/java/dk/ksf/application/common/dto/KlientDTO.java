package dk.ksf.application.common.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KlientDTO {
    String cpr;
    String fornavn;
    String efternavn;
}
