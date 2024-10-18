package service;

import dataAccess.*;
import model.AuthData;
import model.UserData;

import java.util.Objects;
import java.util.UUID;

public class MyService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    private static MyService instance;
    public MyService() throws DataAccessException {
        this.userDAO = new MemoryUserDAO();
        this.authDAO = new MemoryAuthDAO();
        this.gameDAO = new MemoryGameDAO();
    }

    public static synchronized MyService getInstance() throws DataAccessException {
        if (instance == null){
            return new MyService();
        } else {
            return instance;
        }
    }

    public void clear() throws DataAccessException {
        userDAO.clear();
        authDAO.clear();
        gameDAO.clear();
    }

    public String register(UserData userData) throws DataAccessException {
        if(userDAO.getUser(userData.username()) == null){
            AuthData auth = authDAO.createAuth(userData.username());
            authDAO.insertAuth(auth);
            userDAO.createUser(userData);
            return auth.authToken();
        }else{
            return null;
        }
    }

    public String login(UserData userData) throws DataAccessException {
        if(userDAO.getUser(userData.username()) != null){
            System.out.println("Login : "+userDAO.getUser(userData.username()));
            UserData exis = userDAO.getUser(userData.username());
            if(exis.password().equals(userData.password())){
                return authDAO.getAuth(userData.username()).authToken();
            }
        }
        return null;
    }

    public void logout(String authToken) throws Exception {
        if(authDAO.isValidAuth(authToken)){
            authDAO.deleteAuth(authToken);
        }else{
            throw new Exception();
        }
    }
}
