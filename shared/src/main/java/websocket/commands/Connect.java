package websocket.commands;

import static websocket.commands.UserGameCommand.CommandType.CONNECT;
public class Connect extends UserGameCommand{
    public Connect(String authToken, int gameID) {
        super(CONNECT, authToken, gameID);
    }
}
