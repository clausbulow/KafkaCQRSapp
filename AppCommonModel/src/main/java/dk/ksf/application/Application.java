package dk.ksf.application;

import dk.kfs.cqrs.internalmessages.events.annotations.EnableCqrs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
@EnableCqrs
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
