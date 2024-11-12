package ui.websocket;

public class Leave extends UserGameCommand{
    public Leave(String authToken, int gameID) {
        super(authToken, gameID, "LEAVE");
    }
}
