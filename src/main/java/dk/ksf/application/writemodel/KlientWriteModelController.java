package dk.ksf.application.writemodel;

import com.fasterxml.jackson.databind.JsonNode;
import dk.ksf.application.common.dto.RetKlientDTO;
import dk.ksf.application.writemodel.dto.CreateSnapshotsResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/writemodel")

@Slf4j
public class KlientWriteModelController {

    @Autowired
    KlientWriteModelService service;


    @GetMapping("/klienter")
    public ResponseEntity<List<RetKlientDTO>> allKlienter(@RequestHeader("requestId") String requestId)  {
        log.debug("query all-clients called");
        return ResponseEntity.accepted().body(service.getAllKlienter());
    }


    @GetMapping("/klienter/eventstore")
    public ResponseEntity<List<JsonNode>> processGetEventstoreRequest(@RequestHeader("requestId") String requestId) {
        return ResponseEntity.accepted().body(service.getEventStore());
    }

    @PostMapping("/snapshots")
    public ResponseEntity<CreateSnapshotsResponse> createSnapshots() throws Exception  {
        log.debug("create Snapshots called");
        CreateSnapshotsResponse response = service.createSnapshots();
        return ResponseEntity.accepted().body(response);
    }


}
