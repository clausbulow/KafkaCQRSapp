package dk.ksf.application.writemodel;

import dk.ksf.cqrs.commands.CommandDispatcher;
import dk.ksf.application.common.dto.KlientDTO;
import dk.ksf.application.writemodel.commands.OpretKlientCommand;
import dk.ksf.application.writemodel.commands.RetKlientCommand;
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
