package dk.test.klient.controller;

import dk.test.klient.service.KlientService;
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
    KlientService service;


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
