package dk.ksf.cqrs.exceptions;

public class InvalidDataVersionException extends Exception {
    public InvalidDataVersionException() {
    }

    public InvalidDataVersionException(String message) {
        super(message);
    }

    public InvalidDataVersionException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidDataVersionException(Throwable cause) {
        super(cause);
    }

    public InvalidDataVersionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
