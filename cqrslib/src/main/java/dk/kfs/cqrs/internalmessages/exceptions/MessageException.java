package dk.kfs.cqrs.internalmessages.exceptions;

public class MessageException extends Exception {
    public MessageException() {
        super();
    }

    public MessageException(String msg) {
        super(msg);
    }
}
