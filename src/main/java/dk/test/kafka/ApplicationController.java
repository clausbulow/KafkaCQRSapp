package dk.test.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApplicationController {

    @PostMapping("/sayhi")
    public void sayHi(@RequestBody String value, KafkaTemplate template){
        template.send("topic1","I'm hit with "+value);
    }


}
