package dk.test.klient.model.read;

import dk.test.klient.model.KlientDTO;
import dk.test.klient.model.write.KlientWriteModelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/readmodel")

@Slf4j
public class KlientReadModelController {

    @Autowired
    KlientWriteModelService service;


    @GetMapping("/klienter")
    public ResponseEntity<List<KlientDTO>> allKlienter(@RequestHeader("requestId") String requestId)  {
        log.debug("query all-clients called");
        return ResponseEntity.accepted().body(service.getAllKlienter());
    }

    @GetMapping("/klienter/{cpr}")
    public ResponseEntity<?> processKlientGetKlientRequest(@PathVariable (name="cpr") String cpr, @RequestHeader("requestId") String requestId){
        ResponseEntity<?> result;
        final Optional<KlientDTO> klient = service.getKlient(cpr);
        if (klient.isPresent()){
            return ResponseEntity.accepted().body(klient.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/klienter/eventstore")
    public ResponseEntity<?> processGetEventstoreRequest(@RequestHeader("requestId") String requestId) {
        return ResponseEntity.accepted().body(service.getEventStore());
    }

}
