package ui.websocket;

public abstract class UserGameCommand {
    protected String authToken;
    protected int gameID;
    protected String commandType;

    public UserGameCommand(String authToken, int gameID, String commandType) {
        this.authToken = authToken;
        this.gameID = gameID;
        this.commandType = commandType;
    }
}
