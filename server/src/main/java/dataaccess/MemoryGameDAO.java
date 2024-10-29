package dataaccess;

import chess.ChessBoard;
import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import model.result.GameResult;

import java.util.*;
import java.util.random.RandomGenerator;

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
        return gameStore.get(gameId);
    }

    @Override
    public List<GameResult> getGameList() throws DataAccessException {
        List<GameResult> gameDataList = new ArrayList<>();
        GameResult result;
        GameData gameData;
        for(int game : gameStore.keySet()){
            System.out.println(gameStore.get(game));
            gameData = gameStore.get(game);
            result = new GameResult(gameData.gameID(), gameData.gameName(), gameData.whiteUserName(), gameData.blackUserName());
            gameDataList.add(result);
        }
        return gameDataList;
    }

    @Override
    public GameData createGame(String gameName) throws DataAccessException {
        ChessGame newGame = new ChessGame();
        ChessBoard board = newGame.getBoard();
        board.resetBoard();
        newGame.setBoard(board);
        System.out.println("CreateGame");
        int gameID = RandomGenerator.getDefault().nextInt(100);
        GameData game = new GameData(gameID, null, null, gameName, newGame);
        gameStore.put(gameID, game);
        var statement = "INSERT INTO games (whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?)";
        String gameJson = new Gson().toJson(game.game());
        DatabaseManager.executeUpdate(statement, game.whiteUserName(), game.blackUserName(), game.gameName(), gameJson);
        return new GameData(gameID, null, null, gameName, newGame);
    }


    @Override
    public void updateGame(GameData game, String color) throws DataAccessException {
        gameStore.put(game.gameID(), game);
    }

    @Override
    public void clear() throws DataAccessException {
        String[] statement = new String[]{"TRUNCATE games"};
        DatabaseManager.configureDatabase(statement);
    }
}
