package ui.websocket;

import chess.ChessGame;

public class JoinPlayer extends UserGameCommand{
    private ChessGame.TeamColor teamColor;

    public JoinPlayer(String authToken, int gameID, ChessGame.TeamColor teamColor) {
        super(authToken, gameID, "JOIN_PLAYER");
        this.teamColor = teamColor;
    }
}
