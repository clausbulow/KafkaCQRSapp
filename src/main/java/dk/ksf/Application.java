package dk.ksf;

import dk.ksf.application.writemodel.KlientAggregate;
import dk.ksf.application.writemodel.KlientWriteModelService;
import dk.ksf.cqrs.CqrsConfiguration;
import dk.ksf.cqrs.events.internalmessages.EventDispatcher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Slf4j
@Import({CqrsConfiguration.class, KlientAggregate.class, KlientWriteModelService.class, EventDispatcher.class})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
