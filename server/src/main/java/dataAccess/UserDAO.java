package dataAccess;

import model.UserData;

public interface UserDAO {
    public UserData getUser(String username) throws DataAccessException;
    public void createUser(UserData user) throws DataAccessException;
    public void deleteUser() throws DataAccessException;
    public String checkPassword(String username) throws DataAccessException;
}
