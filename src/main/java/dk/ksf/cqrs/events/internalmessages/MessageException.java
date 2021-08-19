package dk.ksf.cqrs.events.internalmessages;

public class MessageException extends Exception {
    public MessageException(){
        super();
    };

    public MessageException(String msg){
        super(msg);
    }
}
