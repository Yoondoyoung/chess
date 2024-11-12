package websocket.commands;

import chess.ChessGame;

import static websocket.commands.UserGameCommand.CommandType.JOIN_PLAYER;

public class JoinPlayer extends UserGameCommand{
    ChessGame.TeamColor playerColor;

    public JoinPlayer(String authToken, int gameID, ChessGame.TeamColor playerColor){
        super(JOIN_PLAYER, authToken, gameID);
    }

    public ChessGame.TeamColor getPlayerColor(){return this.playerColor;}
}