package websocket.commands;

import static websocket.commands.UserGameCommand.CommandType.RESIGN;

public class Resign extends UserGameCommand{

    public Resign(String authToken, int gameID){
        super(RESIGN, authToken, gameID);
    }
}
