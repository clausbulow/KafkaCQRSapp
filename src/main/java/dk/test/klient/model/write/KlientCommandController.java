package dk.test.klient.model.write;

import dk.test.kafka.commands.CommandDispatcher;
import dk.test.klient.model.KlientDTO;
import dk.test.klient.model.commands.OpretKlientCommand;
import dk.test.klient.model.commands.RetKlientCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/commands")
public class KlientCommandController {
    @Autowired
    CommandDispatcher commandDispatcher;

    @PostMapping("/klient")
    @ResponseBody
    public ResponseEntity<?> processKlientOpretRequest(@RequestBody KlientDTO klientDTO, @RequestHeader("requestId") String requestId) throws Exception {
        OpretKlientCommand opretKlientCommand = OpretKlientCommand.builder().
                cpr(klientDTO.getCpr()).
                fornavn(klientDTO.getFornavn()).
                efternavn(klientDTO.getEfternavn()).
                build();
        commandDispatcher.apply(requestId,opretKlientCommand);
        return ResponseEntity.accepted().build();
    }

    @PutMapping("/klient/{cpr}")
    @ResponseBody
    public ResponseEntity<?> processKlientOpretRequest(@PathVariable (name="cpr") String cpr,@RequestBody KlientDTO klientDTO, @RequestHeader("requestId") String requestId) throws Exception {
        RetKlientCommand retKlientCommand = RetKlientCommand.builder().
                fornavn(klientDTO.getFornavn()).
                efternavn(klientDTO.getEfternavn()).
                cpr(cpr).
                build();
        commandDispatcher.apply(requestId, retKlientCommand);
        return ResponseEntity.accepted().build();
    }

}
