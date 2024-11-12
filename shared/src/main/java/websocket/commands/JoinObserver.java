package websocket.commands;

import static websocket.commands.UserGameCommand.CommandType.JOIN_OBSERVER;

public class JoinObserver extends UserGameCommand{
    public JoinObserver(String authToken, int gameID){
        super(JOIN_OBSERVER, authToken, gameID);
    }
}
