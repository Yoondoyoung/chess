package dataAccess;

import model.AuthData;

public interface AuthDAO {
    public AuthData createAuth(String username) throws DataAccessException;
    public String getAuth(String username) throws DataAccessException;
    public void insertAuth(AuthData authData) throws DataAccessException;
    public void deleteAuth(String username) throws DataAccessException;
    public boolean isValidAuth(String authToken) throws DataAccessException;


    public void clear() throws DataAccessException;

    String getUser(String authToken) throws DataAccessException;
}
