package dk.test.klient.controller;

import dk.test.kafka.events.model.BusinessEvent;
import dk.test.kafka.commands.CommandDispatcher;
import dk.test.klient.model.commands.OpretKlientCommand;
import dk.test.klient.model.commands.RetKlientCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;

@RestController
@RequestMapping("/commands")
public class KlientCommandController {
    @Autowired
    CommandDispatcher commandDispatcher;





    @PostMapping("/sayhiwithheaders")
    @ResponseBody
    @Transactional
    public BusinessEvent sayHiWithHeaders(@RequestBody KlientDTO klientDTO, @RequestHeader("transactionId") String transactionId) throws Exception {



//        BusinessEvent businessEvent = new BusinessEvent();
//        businessEvent.setTransactionId(transactionId);
//        businessEvent.setEventNavn("KlientForløbStartet_event");
//        businessEvent.setKey(opretRetKlientCommand.getCpr());
//        businessEvent.setData(opretRetKlientCommand);
//        String event = mapper.writeValueAsString(businessEvent);
//        EventInfo eventInfo = EventInfo.builder().actor("KafkaTest").aggregateType(AggregateTypes.klient).transactionId(transactionId).build();
//        klientService.gemKlient(opretRetKlientCommand);
//        eventService.fireEvent(eventInfo, event);

//        return businessEvent;
        return null;
       // template.send(producerRecord);
    }

    @PostMapping("/klient")
    @ResponseBody
    public ResponseEntity<?> processKlientOpretRequest(@RequestBody KlientDTO klientDTO, @RequestHeader("requestId") String requestId) throws Exception {
        OpretKlientCommand opretKlientCommand = OpretKlientCommand.builder().
                cpr(klientDTO.cpr).
                fornavn(klientDTO.fornavn).
                efternavn(klientDTO.efternavn).
                build();
        commandDispatcher.apply(requestId,opretKlientCommand);
        return ResponseEntity.accepted().build();
    }

    @PutMapping("/klient/{cpr}")
    @ResponseBody
    public ResponseEntity<?> processKlientOpretRequest(@PathVariable (name="cpr") String cpr,@RequestBody KlientDTO klientDTO, @RequestHeader("requestId") String requestId) throws Exception {
        RetKlientCommand retKlientCommand = RetKlientCommand.builder().
                fornavn(klientDTO.fornavn).
                efternavn(klientDTO.efternavn).
                cpr(cpr).
                build();
        commandDispatcher.apply(requestId, retKlientCommand);
        return ResponseEntity.accepted().build();
    }

}
