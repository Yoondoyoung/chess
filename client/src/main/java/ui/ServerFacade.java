package ui;

import chess.ChessMove;
import model.*;
import model.result.GameResult;
import model.result.GameListResult;
import model.result.UserResult;
import ui.websocket.NotificationHandler;
import ui.websocket.WebSocketFacade;

import java.io.IOException;
import java.util.Objects;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;

public class ServerFacade {

    private final ClientCommunicator communicator;
    private final WebSocketFacade ws;

    public ServerFacade(String url, NotificationHandler notificationHandler) throws IOException {
        communicator = new ClientCommunicator(url);
        ws = new WebSocketFacade(url, notificationHandler);
    }

    public UserResult registerUser(UserData user) throws IOException {
        var path = "/user";
        return communicator.makeRequest("POST", path, user, null, UserResult.class);
    }

    public UserResult loginUser(LoginData loginData) throws IOException {
        var path = "/session";
        return communicator.makeRequest("POST", path, loginData, null, UserResult.class);
    }

    public void logoutUser(String authToken) throws IOException {
        var path = "/session";
        communicator.makeRequest("DELETE", path, null, authToken, null);
    }

    public void deleteAll() throws IOException {
        var path = "/db";
        communicator.makeRequest("DELETE", path, null, null, null);
    }

    public GameListResult listGames(String authToken) throws IOException {
        var path = "/game";
        return communicator.makeRequest("GET", path, null, authToken, GameListResult.class);
    }

    public int createGame(String authToken, GameNameResponse gameName) throws IOException {
        var path = "/game";
        return communicator.makeRequest("POST", path, gameName, authToken, GameIDResult.class).gameID();
    }

    public void joinGame(String authToken, JoinGameData joinGameData) throws IOException {
        var path = "/game";
        communicator.makeRequest("PUT", path, joinGameData, authToken, null);
        if (Objects.equals(joinGameData.playerColor(), "WHITE")) {
            ws.joinPlayer(authToken, joinGameData.gameID(), WHITE);
        } else if (Objects.equals(joinGameData.playerColor(), "BLACK")){
            ws.joinPlayer(authToken, joinGameData.gameID(), BLACK);
        } else {
            ws.joinObserver(authToken, joinGameData.gameID());
        }
    }

    public void makeMove(String authToken, int gameID, ChessMove move) throws IOException {
        ws.makeMove(authToken, gameID, move);
    }

    public void leaveGame(String authToken, int gameID) throws IOException {
        ws.leaveGame(authToken, gameID);
    }

    public void resignGame(String authToken, int gameID) throws IOException {
        ws.resignGame(authToken, gameID);
    }

}