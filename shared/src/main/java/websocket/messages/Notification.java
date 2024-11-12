package websocket.messages;

public class Notification extends ServerMessage{
    String message;

    public Notification(String message){
        super(ServerMessageType.NOTIFICATION);
        this.message = message;
    }

    public String getMessage(){return this.message;}

    @Override
    public String toString() {
        return "Notification{" +
                "message='" + message + '\'' +
                ", serverMessageType=" + serverMessageType +
                '}';
    }
}
