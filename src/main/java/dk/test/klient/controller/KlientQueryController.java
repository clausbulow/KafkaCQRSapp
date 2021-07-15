package dk.test.klient.controller;

import dk.test.klient.model.write.KlientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/queries")

@Slf4j
public class KlientQueryController {

    @Autowired
    KlientService service;


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
