package dk.ksf.cqrs.events.internalmessages;

import dk.ksf.cqrs.events.model.AggregateTypes;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.repository.CrudRepository;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

@Data
@NoArgsConstructor
public class AggregateInfo {
    Field keyField;
    Constructor constructor;
    CrudRepository repository;
    AggregateTypes aggregateType;
    Class aggregateClass;
}
