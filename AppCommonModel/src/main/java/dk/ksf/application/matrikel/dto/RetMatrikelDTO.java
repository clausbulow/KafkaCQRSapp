package dk.ksf.application.matrikel.dto;

import dk.ksf.application.matrikel.model.Celle;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class RetMatrikelDTO {
    String id;
    String vejnavn;
    List<Celle> celler = new ArrayList<>();
    long version;
}
