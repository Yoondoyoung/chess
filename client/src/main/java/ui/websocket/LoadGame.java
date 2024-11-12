package ui.websocket;

import chess.ChessGame;

public class LoadGame extends ServerMessage{
    private int gameID;
    public ChessGame game;

    public LoadGame(int gameID, ChessGame game) {
        this.serverMessageType = ServerMessageType.LOAD_GAME;
        this.gameID = gameID;
        this.game = game;
    }

    public int getGameID() { return gameID; }
    public ChessGame getGame() { return game; }
}
