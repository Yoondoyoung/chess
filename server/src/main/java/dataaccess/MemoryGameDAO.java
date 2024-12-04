package dataaccess;

import chess.ChessBoard;
import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import model.result.GameResult;

import java.util.*;

public class MemoryGameDAO implements GameDAO{
    private final Map<Integer, GameData> gameStore = new HashMap<>();

    public MemoryGameDAO() throws DataAccessException {
        String[] createStatements = {
                """
            CREATE TABLE IF NOT EXISTS  games (
              `gameID` int NOT NULL AUTO_INCREMENT,
              `whiteUsername` TEXT DEFAULT NULL,
              `blackUsername` TEXT DEFAULT NULL,
              `gameName` TEXT DEFAULT NULL CHECK (`gameName` <> ''),
              `game` TEXT DEFAULT NULL,
              PRIMARY KEY (`gameID`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
        };
        DatabaseManager.configureDatabase(createStatements);
    }
    @Override
    public GameData getGame(int gameId) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM games WHERE gameID=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameId);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        var gameJson = rs.getString("game");
                        ChessGame game = new Gson().fromJson(gameJson, ChessGame.class);
                        var whiteUsername = rs.getString("whiteUsername");
                        var blackUsername = rs.getString("blackUsername");
                        var gameName = rs.getString("gameName");
                        return new GameData(gameId, whiteUsername, blackUsername, gameName, game);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    @Override
    public List<GameResult> getGameList() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM games";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    List<GameResult> games = new ArrayList<>();
                    while (rs.next()) {
                        int gameID = rs.getInt("gameID");
                        String whiteUsername = rs.getString("whiteUsername");
                        String blackUsername = rs.getString("blackUsername");
                        String gameName = rs.getString("gameName");
                        games.add(new GameResult(gameID, gameName, whiteUsername, blackUsername));
                    }
                    return games;
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
    }


    @Override
    public GameData createGame(String gameName) throws DataAccessException {
        if(gameName.isEmpty()){
            throw new DataAccessException("Unable to read data");
        }
        ChessGame newGame = new ChessGame();
        ChessBoard board = newGame.getBoard();
        board.resetBoard();
        newGame.setBoard(board);
        System.out.println("CreateGame");
        int gameID = 1;
        GameData game = new GameData(gameID, null, null, gameName, newGame);
        var statement = "INSERT INTO games (whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?)";
        String gameJson = new Gson().toJson(game.game());
        DatabaseManager.executeUpdate(statement, game.whiteUserName(), game.blackUserName(), game.gameName(), gameJson);

        return new GameData(getGame(gameName).gameID(), null, null, game.gameName(), game.game());

    }


    @Override
    public void updateGame(GameData game, String color) throws DataAccessException {
        String blankUsername;
        String username;
        if (color.equals("BLACK")) {
            blankUsername = "blackUsername";
            username = game.blackUserName();
        } else if (color.equals("WHITE")) {
            blankUsername = "whiteUsername";
            username = game.whiteUserName();
        } else {
            throw new DataAccessException("Bad color");
        }
        String gameJson = new Gson().toJson(game.game());
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "UPDATE games " +
                    "SET " + blankUsername + " = ?, " +
                        "game = ? " +
                    "WHERE gameID = ?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                ps.setString(2, gameJson);
                ps.setInt(3, game.gameID());
                int rowsAffected = ps.executeUpdate();
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
    }

    @Override
    public void clear() throws DataAccessException {
        String[] statement = new String[]{"TRUNCATE games"};
        DatabaseManager.configureDatabase(statement);
    }

    public GameData getGame(String gameName) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM games WHERE gameName=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, gameName);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        var gameJson = rs.getString("game");
                        var gameId = rs.getInt("gameID");
                        ChessGame game = new Gson().fromJson(gameJson, ChessGame.class);
                        var whiteUsername = rs.getString("whiteUsername");
                        var blackUsername = rs.getString("blackUsername");

                        return new GameData(gameId, whiteUsername, blackUsername, gameName, game);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }
}