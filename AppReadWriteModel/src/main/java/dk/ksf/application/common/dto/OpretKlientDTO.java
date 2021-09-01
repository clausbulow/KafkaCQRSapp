package dk.ksf.application.common.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OpretKlientDTO {
    String cpr;
    String fornavn;
    String efternavn;
}
