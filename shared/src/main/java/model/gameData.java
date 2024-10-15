package model;

import chess.ChessGame;

public record gameData(int gameID, String whiteUserName, String blackUserName, String gameName, ChessGame game) {
}
