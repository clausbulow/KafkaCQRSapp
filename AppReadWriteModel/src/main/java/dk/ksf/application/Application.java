package dk.ksf.application;

import dk.kfs.cqrs.internalmessages.events.annotations.EnableCqrs;
import dk.ksf.application.writemodel.KlientAggregate;
import dk.ksf.application.writemodel.KlientWriteModelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Slf4j
@Import({KlientAggregate.class, KlientWriteModelService.class})
@EnableCqrs
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
