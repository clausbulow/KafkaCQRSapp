package dk.ksf.cqrs.events.internalmessages;

import dk.ksf.cqrs.events.annotations.TargetAggregateIdentifier;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.util.ClassTypeInformation;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

@Data
@NoArgsConstructor
public class HandlerInfo {
    public static HandlerInfo createHandlerInfo(ClassTypeInformation aggregateInfo, Method method, boolean createsAggregate, Class clazz){
        HandlerInfo result = new HandlerInfo();
        result.setAggregate(aggregateInfo);
        result.setHandlerMethod(method);
        result.setCreatesAggregate(createsAggregate);
        ReflectionUtils.doWithLocalFields(clazz, field -> {
            TargetAggregateIdentifier annotation = field.getAnnotation(TargetAggregateIdentifier.class);
            if (annotation != null){
                result.setKeyRefField(field);
                ReflectionUtils.makeAccessible(field);
            }
        });


        return result;

    }
    Field keyRefField = null;
    Method handlerMethod = null;
    ClassTypeInformation aggregate;
    boolean createsAggregate;


}
