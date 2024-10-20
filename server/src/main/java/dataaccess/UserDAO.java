package dataaccess;

import model.UserData;

public interface UserDAO {
    public UserData getUser(String username) throws DataAccessException;
    public void createUser(UserData user) throws DataAccessException;

    public void clear() throws DataAccessException;
}
