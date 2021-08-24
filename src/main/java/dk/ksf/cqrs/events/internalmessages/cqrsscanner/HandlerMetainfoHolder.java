package dk.ksf.cqrs.events.internalmessages.cqrsscanner;

import java.util.Map;

interface HandlerMetainfoHolder {
    Map<Object, HandlerMetaInfo> getHandlers();
}
