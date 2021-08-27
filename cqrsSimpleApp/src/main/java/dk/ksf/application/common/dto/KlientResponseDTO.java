package dk.ksf.application.common.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KlientResponseDTO {
    String cpr;
    String fornavn;
    String efternavn;
    long version;
}
