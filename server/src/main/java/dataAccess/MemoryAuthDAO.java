package dataAccess;

import model.AuthData;
import model.UserData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO{
    private final Map<String, AuthData> authStore = new HashMap<>();

    @Override
    public AuthData createAuth(String username) throws DataAccessException {
        String authToken = UUID.randomUUID().toString();
        return new AuthData(username, authToken);
    }

    @Override
    public AuthData getAuth(String username) throws DataAccessException {
        return authStore.get(username);
    }

    @Override
    public void insertAuth(AuthData authData) throws DataAccessException {
        authData = createAuth(authData.username());
        authStore.put(authData.username(), authData);
    }

    @Override
    public void deleteAuth(String username) throws DataAccessException {
        authStore.remove(username);
    }

    @Override
    public void clear() throws DataAccessException {
        authStore.clear();
    }
}
