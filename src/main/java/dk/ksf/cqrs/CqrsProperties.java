package dk.ksf.cqrs;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "kfs.cqrs")
@Component
@Data
public class CqrsProperties {
    String topicNames;
    String eventobjectsPackage;
    String producingActorId;
}
