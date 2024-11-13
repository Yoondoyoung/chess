package client;

import model.LoginData;
import model.UserData;
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
        var port = server.run(8080);
        System.out.println("Started test HTTP server on " + port);

        String testUrl = "http://localhost:8080";  // Replace with actual server URL if needed
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
    public void sampleTest() {
        assertTrue(true);
    }

    @Test
    public void register_success() throws Exception {
        var authData = facade.registerUser(new UserData("player1", "password", "p1@email.com"));
        assertTrue(authData.authToken().length() > 10);
    }

    @Test
    public void register_fail() throws Exception {
        var authData = facade.registerUser(new UserData(null, "password", "p1@email.com"));
        assertNull(authData.authToken());
    }

    @Test
    public void login_success() throws Exception {
        var register = facade.registerUser(new UserData("player1", "password", "p1@email.com"));
        var login = facade.loginUser(new LoginData("player1", "password"));
        assertTrue(login.authToken().length() > 10);
    }

    @Test
    public void login_fail() throws Exception {
        assertThrows(IOException.class, () -> {
                facade.loginUser(new LoginData(null, null));
        });
    }

    @Test
    public void logout_success() throws Exception {
        var register = facade.registerUser(new UserData("player1", "password", "p1@email.com"));
        var login = facade.loginUser(new LoginData("player1", "password"));
        assertDoesNotThrow(() -> {
            facade.logoutUser(login.authToken());
        });
    }

    @Test
    public void logout_fail() throws Exception {
        assertThrows(IOException.class, () -> {
            facade.logoutUser(null);
        });
    }

    


}
