package ui.websocket;

public class Notification extends ServerMessage{
    private String message;

    public Notification(String message) {
        this.serverMessageType = ServerMessageType.NOTIFICATION;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
