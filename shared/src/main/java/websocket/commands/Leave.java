package websocket.commands;

import static websocket.commands.UserGameCommand.CommandType.LEAVE;

public class Leave extends UserGameCommand{
    public Leave(String authToken, int gameID){
        super(LEAVE, authToken, gameID);
    }
}
