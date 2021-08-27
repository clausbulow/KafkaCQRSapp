package dk.ksf.application.writemodel;

import dk.kfs.cqrs.internalmessages.events.internalmessages.EventDispatcher;
import dk.kfs.cqrs.internalmessages.events.internalmessages.MessageContext;
import dk.ksf.application.common.dto.RetKlientDTO;
import dk.ksf.application.writemodel.commands.OpretKlientCommand;
import dk.ksf.application.writemodel.commands.RetKlientCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/commands")
public class KlientCommandController {
    @Autowired
    EventDispatcher eventDispatcher;

    @PostMapping("/klient")
    @ResponseBody
    public ResponseEntity<?> processKlientOpretRequest(@RequestBody RetKlientDTO retKlientDTO, @RequestHeader("requestId") String requestId) throws Exception {
        OpretKlientCommand opretKlientCommand = OpretKlientCommand.builder().
                cpr(retKlientDTO.getCpr()).
                fornavn(retKlientDTO.getFornavn()).
                efternavn(retKlientDTO.getEfternavn()).
                build();
        eventDispatcher.publishCommand(MessageContext.builder().requestId(requestId).version(retKlientDTO.getVersion()).build(),
                opretKlientCommand);
        return ResponseEntity.accepted().build();
    }

    @PutMapping("/klient/{cpr}")
    @ResponseBody
    public ResponseEntity<?> processKlientOpretRequest(@PathVariable(name = "cpr") String cpr, @RequestBody RetKlientDTO retKlientDTO, @RequestHeader("requestId") String requestId) throws Exception {
        RetKlientCommand retKlientCommand = RetKlientCommand.builder().
                fornavn(retKlientDTO.getFornavn()).
                efternavn(retKlientDTO.getEfternavn()).
                cpr(cpr).
                build();
        eventDispatcher.publishCommand(MessageContext.builder().requestId(requestId).version(retKlientDTO.getVersion()).build(),
                retKlientCommand);
        return ResponseEntity.accepted().build();
    }

}
