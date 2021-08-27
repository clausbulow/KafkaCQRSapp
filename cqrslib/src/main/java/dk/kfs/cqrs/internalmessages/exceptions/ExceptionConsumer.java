package dk.kfs.cqrs.internalmessages.exceptions;

import java.util.function.Consumer;

@FunctionalInterface
public interface ExceptionConsumer<T> {
    static <T> Consumer<T> wrapper(ExceptionConsumer<T> t) {
        return arg -> {
            try {
                t.accept(arg);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    void accept(T t) throws Exception;
}

