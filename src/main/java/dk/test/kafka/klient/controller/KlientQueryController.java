package dk.test.kafka.klient.controller;

import dk.test.kafka.events.model.BusinessEvent;
import dk.test.kafka.klient.service.KafkaQueryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/queries")

@Slf4j
public class KlientQueryController {

    @Autowired
    KafkaQueryService service;


    @GetMapping("/klienter")
    public ResponseEntity<List<KlientDTO>> allKlienter(@RequestHeader("requestId") String requestId) throws Exception {
        log.debug("query all-clients called");
        return ResponseEntity.accepted().body(service.getAllKlienter());
    }

    @GetMapping("/eventstore")
    public ResponseEntity<List<BusinessEvent<?>>> eventSTroe(@RequestHeader("requestId") String requestId) throws Exception {
        log.debug("query all-clients called");
        return ResponseEntity.accepted().body(service.getEventStoreItems());
    }
    @GetMapping("/klienter/{cpr}")
    public ResponseEntity<?> processKlientOpretRequest(@PathVariable (name="cpr") String cpr, @RequestHeader("requestId") String requestId) throws Exception {
        return null;
    }

}
