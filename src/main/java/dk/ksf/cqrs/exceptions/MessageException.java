package dk.ksf.cqrs.exceptions;

public class MessageException extends Exception {
    public MessageException() {
        super();
    }

    public MessageException(String msg) {
        super(msg);
    }
}
