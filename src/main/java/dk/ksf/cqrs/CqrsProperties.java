package dk.ksf.cqrs;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "kfs.cqrs")
@Component
@Data
public class CqrsProperties {
    String topicNames;
    String eventobjectsPackage;
    String producingActorId;
}
