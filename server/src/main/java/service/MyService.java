package service;

import chess.ChessGame;
import dataaccess.*;
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

    public void setGame(GameData game, String color) throws DataAccessException {
        gameDAO.updateGame(game, color);
    }

    public GameData getGame(int gameID) throws DataAccessException {
        return gameDAO.getGame(gameID);
    }

    public String getUsername(String authToken) throws DataAccessException {
        return authDAO.getUser(authToken);
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
        } else if (game == null){
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

    public boolean checkIsResigned(int gameID) throws DataAccessException {
        GameData gameData = gameDAO.getGame(gameID);
        ChessGame game = gameData.game();
        return game.isResigned();
    }

    public void setGameResign(int gameID, String username) throws DataAccessException {
        GameData gameData = gameDAO.getGame(gameID);
        ChessGame game = gameData.game();
        checkUserColor(username, gameData);
        if (checkIsResigned(gameID)) {
            throw new DataAccessException("Game is resigned.");
        }
        game.setResigned(true);
        gameDAO.updateGame(gameData, checkUserColor(username, gameData).name());
    }

    public ChessGame.TeamColor checkUserColor(String username, GameData gameData) throws DataAccessException {
        if ((!Objects.equals(gameData.whiteUserName(), username) && !Objects.equals(gameData.blackUserName(), username))) {
            throw new DataAccessException("You are not a player and can't resign the game.");
        } else if ((Objects.equals(gameData.whiteUserName(), username))) {
            return ChessGame.TeamColor.WHITE;
        } else {
            return ChessGame.TeamColor.BLACK;
        }
    }
}