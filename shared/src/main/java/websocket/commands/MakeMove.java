package websocket.commands;
import chess.ChessMove;

import static websocket.commands.UserGameCommand.CommandType.MAKE_MOVE;

public class MakeMove extends UserGameCommand{
    ChessMove move;

    public MakeMove(String authToken, int gameID, ChessMove move){
        super(MAKE_MOVE, authToken, gameID);
        this.move = move;
    }

    public ChessMove getMove() {return move;}
}
