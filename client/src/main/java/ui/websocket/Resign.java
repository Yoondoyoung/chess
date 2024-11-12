package ui.websocket;

public class Resign extends UserGameCommand{
    public Resign(String authToken, int gameID) {
        super(authToken, gameID, "RESIGN");
    }
}
