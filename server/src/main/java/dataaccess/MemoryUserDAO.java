package dataaccess;

import model.UserData;

import java.util.Map;
import java.util.HashMap;
public class MemoryUserDAO implements UserDAO{
    private final Map<String, UserData> userStore = new HashMap<>();
    @Override
    public UserData getUser(String username) throws DataAccessException {
        return userStore.get(username);
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        userStore.put(user.username(), user);
    }

//    @Override
//    public void deleteUser() throws DataAccessException {
//        userStore.clear();
//    }

//    @Override
//    public String checkPassword(String username) throws DataAccessException {
//        return null;
//    }

    public void clear() throws DataAccessException {
        userStore.clear();
    }

}
