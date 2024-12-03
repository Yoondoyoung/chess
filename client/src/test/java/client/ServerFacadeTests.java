package client;

import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import chess.InvalidMoveException;
import model.GameNameResponse;
import model.JoinGameData;
import model.LoginData;
import model.UserData;
import model.result.GameListResult;
import org.eclipse.jetty.server.Authentication;
import org.junit.jupiter.api.*;
import server.Server;
import ui.ServerFacade;
import ui.websocket.NotificationHandler;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGame;
import websocket.messages.Notification;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;
    @BeforeAll
    public static void init() throws IOException {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);

        String testUrl = "http://localhost:"+port;  // Replace with actual server URL if needed
        NotificationHandler notificationHandler = new NotificationHandler() {
            @Override
            public void loadGame(LoadGame game) {

            }

            @Override
            public void error(ErrorMessage error) {

            }

            @Override
            public void notify(Notification notification) {

            }
        }; // Replace with actual handler implementation if needed
        facade = new ServerFacade(testUrl, notificationHandler);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @AfterEach
    void deleteAll() throws Exception {
        facade.deleteAll();
    }

    @Test
    public void registerSuccess() throws Exception {
        var authData = facade.registerUser(new UserData("player1", "password", "p1@email.com"));
        assertTrue(authData.authToken().length() > 10);
    }

    @Test
    public void registerFail() throws Exception {
        var authData = facade.registerUser(new UserData(null, "password", "p1@email.com"));
        assertNull(authData.authToken());
    }

    @Test
    public void loginSuccess() throws Exception {
        var register = facade.registerUser(new UserData("player1", "password", "p1@email.com"));
        var login = facade.loginUser(new LoginData("player1", "password"));
        assertTrue(login.authToken().length() > 10);
    }

    @Test
    public void loginFail() throws Exception {
        assertThrows(Exception.class, () -> {
                facade.loginUser(new LoginData(null, null));
        });
    }

    @Test
    public void logoutSuccess() throws Exception {
        var register = facade.registerUser(new UserData("player1", "password", "p1@email.com"));
        var login = facade.loginUser(new LoginData("player1", "password"));
        assertDoesNotThrow(() -> {
            facade.logoutUser(login.authToken());
        });
    }

    @Test
    public void logoutFail() throws Exception {
        assertThrows(Exception.class, () -> {
            facade.logoutUser(null);
        });
    }

    @Test
    public void listGameSucess() throws Exception {
        var register = facade.registerUser(new UserData("player1", "password", "p1@email.com"));
        var login = facade.loginUser(new LoginData("player1", "password"));
        GameNameResponse gameName = new GameNameResponse("Test Game");
        int gameId = facade.createGame(login.authToken(), gameName);

        assertTrue(gameId > 0);
        GameListResult result = facade.listGames(login.authToken());
        assertNotNull(result);
    }

    @Test
    public void listGameFail() throws Exception {
        assertThrows(Exception.class, () -> {
            facade.listGames("");
        });
    }

    @Test
    public void createGameSuccess() throws Exception {
        var register = facade.registerUser(new UserData("player1", "password", "p1@email.com"));
        var login = facade.loginUser(new LoginData("player1", "password"));
        GameNameResponse gameName = new GameNameResponse("Test Game");
        int gameId = facade.createGame(login.authToken(), gameName);

        assertTrue(gameId > 0);
    }

    @Test
    public void createGameFail() throws Exception {
        var register = facade.registerUser(new UserData("player1", "password", "p1@email.com"));
        var login = facade.loginUser(new LoginData("player1", "password"));

        assertThrows(Exception.class, () -> {
            facade.createGame(login.authToken(), null);
        });
    }

    @Test
    public void deletreAllSuccess() throws Exception {
        facade.deleteAll();
    }

    @Test
    public void joinGameSuccess() throws Exception {
        var register = facade.registerUser(new UserData("player1", "password", "p1@email.com"));
        var login = facade.loginUser(new LoginData("player1", "password"));
        GameNameResponse gameName = new GameNameResponse("Test Game");
        int gameId = facade.createGame(login.authToken(), gameName);

        assertThrows(IllegalStateException.class, () -> {
            facade.joinGame(login.authToken(), new JoinGameData("WHITE", gameId));
        });
    }

    @Test
    public void joinGameFail() throws Exception {
        var register = facade.registerUser(new UserData("player1", "password", "p1@email.com"));
        var login = facade.loginUser(new LoginData("player1", "password"));

        assertThrows(Exception.class, () -> {
            facade.joinGame(login.authToken(), null);
        });
    }

    @Test
    public void makeMoveSuccess() throws Exception {
        var register = facade.registerUser(new UserData("player1", "password", "p1@email.com"));
        var login = facade.loginUser(new LoginData("player1", "password"));
        GameNameResponse gameName = new GameNameResponse("Test Game");
        int gameId = facade.createGame(login.authToken(), gameName);

        assertDoesNotThrow(() -> {
            facade.makeMove(login.authToken(), gameId, new ChessMove(new ChessPosition(7, 1), new ChessPosition(6, 1), ChessPiece.PieceType.PAWN));
        });
    }

    @Test
    public void makeMoveFail() throws Exception {

        assertThrows(IllegalStateException.class, () -> {
            facade.makeMove(null, 0, null);
        });
    }

    @Test
    public void leaveGameSuccess() throws Exception {
        var register = facade.registerUser(new UserData("player1", "password", "p1@email.com"));
        var login = facade.loginUser(new LoginData("player1", "password"));
        GameNameResponse gameName = new GameNameResponse("Test Game");
        int gameId = facade.createGame(login.authToken(), gameName);

        assertThrows(IllegalStateException.class, () -> {
            facade.leaveGame(login.authToken(), gameId);
        });
    }

    @Test
    public void leaveGameFail() throws Exception {

        assertDoesNotThrow(() -> {
            facade.leaveGame(null, 123);
        });
    }

    @Test
    public void resignGameSuccess() throws Exception {
        var register = facade.registerUser(new UserData("player1", "password", "p1@email.com"));
        var login = facade.loginUser(new LoginData("player1", "password"));
        GameNameResponse gameName = new GameNameResponse("Test Game");
        int gameId = facade.createGame(login.authToken(), gameName);

        assertThrows(IllegalStateException.class, () -> {
            facade.resignGame(login.authToken(), gameId);
        });
    }

    @Test
    public void resignGameFail() throws Exception {

        assertThrows(IllegalStateException.class, () -> {
            facade.resignGame(null, 123);
        });
    }
}
