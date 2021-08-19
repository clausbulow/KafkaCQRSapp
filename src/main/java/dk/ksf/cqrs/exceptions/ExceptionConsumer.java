package dk.ksf.cqrs.exceptions;

import java.util.function.Consumer;

@FunctionalInterface
public interface ExceptionConsumer<T> {
    void accept(T t) throws Exception;

    static <T> Consumer<T> wrapper(ExceptionConsumer<T> t) {
        return arg -> {
            try {
                t.accept(arg);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }
}

