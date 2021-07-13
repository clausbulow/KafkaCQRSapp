package dk.test.kafka.klient.controller;

import dk.test.kafka.commands.CommandDispatcher;
import dk.test.kafka.events.model.BusinessEvent;
import dk.test.kafka.klient.model.commands.OpretKlientCommand;
import dk.test.kafka.klient.model.commands.RetKlientCommand;
import dk.test.kafka.klient.service.KlientKafkaQueryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/queries")

@Slf4j
public class KlientQueryController {

    @Autowired
    KlientKafkaQueryService service;

    @GetMapping("/klienter")
    public ResponseEntity<List<KlientDTO>> allKlienter(@RequestHeader("requestId") String requestId) throws Exception {
        log.debug("query all-clients called");
        return ResponseEntity.accepted().body(service.getAllKlienter());
    }

    @GetMapping("/klienter/{cpr}")
    public ResponseEntity<?> processKlientOpretRequest(@PathVariable (name="cpr") String cpr, @RequestHeader("requestId") String requestId) throws Exception {
        return null;
    }

}
