package dataAccess;

import model.AuthData;

public interface AuthDAO {
    public AuthData createAuth(String username) throws DataAccessException;
    public AuthData getAuth(String username) throws DataAccessException;
    public void insertAuth(AuthData authToken) throws DataAccessException;
    public void deleteAuth(String authToken) throws DataAccessException;
    public void clear() throws DataAccessException;
}
