package dataAccess;

import model.GameData;
import model.UserData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemoryGameDAO implements GameDAO{
    private final Map<String, GameData> gameStore = new HashMap<>();
    @Override
    public GameData getGame(int gameId) throws DataAccessException {
        return null;
    }

    @Override
    public List<GameData> getGameList() throws DataAccessException {
        return null;
    }

    @Override
    public void createGame(String gameName) throws DataAccessException {

    }

    @Override
    public void removeGame(int gameId) throws DataAccessException {

    }

    @Override
    public void updateGame(int gameId) throws DataAccessException {

    }
}
