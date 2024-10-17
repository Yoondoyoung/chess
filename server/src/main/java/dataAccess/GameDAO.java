package dataAccess;

import model.GameData;
import model.UserData;

import java.util.List;

public interface GameDAO {
    public GameData getGame(int gameId) throws DataAccessException;
    public List<GameData> getGameList() throws DataAccessException;
    public void createGame(String gameName) throws DataAccessException;
    public void removeGame(int gameId) throws DataAccessException;
    public void updateGame(int gameId) throws DataAccessException;

}
