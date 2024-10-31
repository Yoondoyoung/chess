package service;

import dataAccess.*;
import model.AuthData;
import model.GameData;
import model.JoinGameRequset;
import model.UserData;
import model.result.GameResult;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;
import java.util.Objects;

public class MyService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;
    final GameDAO gameDAO;
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
        String hashedPassword = userDAO.getPassword(userData.username());
        if(hashedPassword != null){
            if (BCrypt.checkpw(userData.password(), hashedPassword)){
                System.out.println("Login Successes");
                AuthData auth = authDAO.createAuth(userData.username());
                authDAO.insertAuth(auth);
                return auth.authToken();
            } else {
                throw new DataAccessException("Error: unauthorized");
            }
        }
        throw new DataAccessException("Error: unauthorized");
    }

    public void logout(String authToken) throws Exception {
        if(authDAO.isValidAuth(authToken)){
            authDAO.deleteAuth(authToken);
        }else{
            throw new Exception();
        }
    }

    public int createGame(String gameName) throws Exception {
        GameData game = gameDAO.createGame(gameName);
        return game.gameID();
    }

    public boolean isValidAuth(String authToken) throws Exception {
        return authDAO.isValidAuth(authToken);
    }

    public void joinGame(JoinGameRequset joinGameRequset, String authToken) throws Exception{
        GameData game = gameDAO.getGame(joinGameRequset.gameID());
        String username = authDAO.getUser(authToken);

        if(Objects.equals(joinGameRequset.playerColor(), "WHITE")){
            if(game.whiteUserName() == null){
                game = new GameData(game.gameID(), username, game.blackUserName(), game.gameName(), game.game());
                gameDAO.updateGame(game, "WHITE");
            }else{
                throw new DataAccessException("Error: WHITE is already taken");
            }
        }else if(Objects.equals(joinGameRequset.playerColor(), "BLACK")){
            if(game.blackUserName() == null){
                game = new GameData(game.gameID(), game.whiteUserName(), username, game.gameName(), game.game());
                gameDAO.updateGame(game, "BLACK");
            }else{
                throw new DataAccessException("Error: BLACK is already taken");
            }
        } else if (Objects.equals(joinGameRequset.playerColor(), null)){
            throw new DataAccessException("Error: Bad request");
        }
    }

    public List<GameResult> listGames(String authToken) throws Exception{
        List<GameResult> gameList;
        if(isValidAuth(authToken)){
            gameList = gameDAO.getGameList();
        }else{
            throw new DataAccessException("Error: Bad request");
        }
        return gameList;
    }
}