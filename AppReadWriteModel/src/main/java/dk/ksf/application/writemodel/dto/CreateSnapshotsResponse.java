package dk.ksf.application.writemodel.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CreateSnapshotsResponse {
    List<String> snapshotsCreatedForAggretateTypes = new ArrayList<>();
}
