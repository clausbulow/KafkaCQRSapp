package dk.ksf.application.matrikel.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MatrikelResponseDTO {
    String id;
    String vejnavn;
    long version;
}
