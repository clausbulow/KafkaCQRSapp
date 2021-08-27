package dk.kfs.cqrs.internalmessages.events.internalmessages.cqrsscanner;

import java.util.Map;

interface HandlerMetainfoHolder {
    Map<Object, HandlerMetaInfo> getHandlers();
}
