package ui.websocket;

import chess.ChessMove;

public class MakeMove extends UserGameCommand{
    private ChessMove move;

    public MakeMove(String authToken, int gameID, ChessMove move) {
        super(authToken, gameID, "MAKE_MOVE");
        this.move = move;
    }
}
