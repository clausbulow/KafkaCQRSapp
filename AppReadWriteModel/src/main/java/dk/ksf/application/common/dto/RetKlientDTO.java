package dk.ksf.application.common.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RetKlientDTO {
    String cpr;
    String fornavn;
    String efternavn;
    long version;
}
