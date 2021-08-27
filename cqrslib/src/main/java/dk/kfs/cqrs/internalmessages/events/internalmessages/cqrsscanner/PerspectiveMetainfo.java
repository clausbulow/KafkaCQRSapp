package dk.kfs.cqrs.internalmessages.events.internalmessages.cqrsscanner;

import dk.kfs.cqrs.internalmessages.events.annotations.Perspective;
import lombok.Builder;
import lombok.Data;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@Data
@Builder
public class PerspectiveMetainfo implements HandlerMetainfoHolder {
    Perspective annotation;
    Field keyField;
    Map<Object, HandlerMetaInfo> handlers = new HashMap<>();

    public Map<Object, HandlerMetaInfo> getHandlers(){
        if (handlers == null){
            handlers = new HashMap<>();
        }
        return this.handlers;
    }
}
