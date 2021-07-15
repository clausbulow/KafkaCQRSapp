package dk.test.klient.model.exceptions;

public class InvalidEventVersionException extends Exception {
    public InvalidEventVersionException() {
    }

    public InvalidEventVersionException(String message) {
        super(message);
    }

    public InvalidEventVersionException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidEventVersionException(Throwable cause) {
        super(cause);
    }

    public InvalidEventVersionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
