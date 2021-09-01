package dk.ksf.application.matrikel.controller;

import dk.kfs.cqrs.internalmessages.events.internalmessages.EventDispatcher;
import dk.kfs.cqrs.internalmessages.events.internalmessages.MessageContext;
import dk.ksf.application.matrikel.commands.OpretMatrikelCommand;
import dk.ksf.application.matrikel.commands.RetMatrikelCommand;
import dk.ksf.application.matrikel.dto.RetMatrikelDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/commands")
public class MatrikelCommandController {
    @Autowired
    EventDispatcher eventDispatcher;

    @PostMapping("/matrikel")
    @ResponseBody
    public ResponseEntity<?> matrikelOpretRequest(@RequestBody RetMatrikelDTO retMatrikelDTO, @RequestHeader("requestId") String requestId) throws Exception {
        OpretMatrikelCommand opretMatrikelCommand = OpretMatrikelCommand.builder().
                id(retMatrikelDTO.getId()).
                vej(retMatrikelDTO.getVejnavn()).
                build();

        retMatrikelDTO.getCeller().forEach(celle -> opretMatrikelCommand.getCeller().add(celle));
        eventDispatcher.publishCommand(MessageContext.builder().requestId(requestId).version(retMatrikelDTO.getVersion()).build(),
                opretMatrikelCommand);
        return ResponseEntity.accepted().build();
    }

    @PutMapping("/matrikel/{id}")
    @ResponseBody
    public ResponseEntity<?> matrikelRetRequest(@PathVariable(name = "id") String id, @RequestBody RetMatrikelDTO retMatrikelDTO, @RequestHeader("requestId") String requestId) throws Exception {
        RetMatrikelCommand retMatrikelCommand = RetMatrikelCommand.builder().
                id(retMatrikelDTO.getId()).
                vejnavn(retMatrikelDTO.getVejnavn()).
                build();
        eventDispatcher.publishCommand(MessageContext.builder().requestId(requestId).version(retMatrikelDTO.getVersion()).build(),
                retMatrikelCommand);
        return ResponseEntity.accepted().build();
    }

}
