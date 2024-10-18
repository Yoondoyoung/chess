package dataAccess;

import model.AuthData;

import javax.xml.crypto.Data;

public interface AuthDAO {
    public AuthData createAuth(String username) throws DataAccessException;
    public AuthData getAuth(String username) throws DataAccessException;
    public void insertAuth(AuthData authData) throws DataAccessException;
    public void deleteAuth(String username) throws DataAccessException;
    public boolean isValidAuth(String authToken) throws DataAccessException;


    public void clear() throws DataAccessException;
}
