package dataAccess;

import model.GameData;
import model.result.GameResult;

import java.util.List;

public interface GameDAO {
    public GameData getGame(int gameId) throws DataAccessException;
    public List<GameResult> getGameList() throws DataAccessException;
    public GameData createGame(String gameName) throws DataAccessException;
    public void updateGame(GameData game, String color) throws DataAccessException;
    public void clear() throws DataAccessException;

}
