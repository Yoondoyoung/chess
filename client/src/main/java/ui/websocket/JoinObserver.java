package ui.websocket;

public class JoinObserver extends UserGameCommand{
    public JoinObserver(String authToken, int gameID) {
        super(authToken, gameID, "JOIN_OBSERVER");
    }
}
