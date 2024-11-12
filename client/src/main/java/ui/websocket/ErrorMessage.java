package ui.websocket;

public class ErrorMessage {
        private String message;
        public ErrorMessage(String message) {
            this.serverMessageType = ServerMessageType.ERROR;
            this.message = message;
        }
        public String getMessage() {return message;}
}
