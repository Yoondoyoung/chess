package websocket.messages;

public class ErrorMessage extends ServerMessage{
    String errorMessage;
    public ErrorMessage(String errorMessage){
        super(ServerMessageType.ERROR);
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {return this.errorMessage;}

    @Override
    public String toString() {
        return "ErrorMessage{" +
                "errorMessage='" + errorMessage + '\'' +
                ", serverMessageType=" + serverMessageType +
                '}';
    }
}
