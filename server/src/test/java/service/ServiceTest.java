package service;

import dataaccess.DataAccessException;
import model.GameData;
import model.JoinGameRequset;
import model.LeaveGameRequest;
import model.UserData;
import model.result.GameResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ServiceTest {
    private MyService service;

    @BeforeEach
    public void setUp() throws DataAccessException {
        service = MyService.getInstance();
        service.clear();
    }

    // --------------------- Register Tests ---------------------
    @Test
    public void testRegisterSuccess() throws DataAccessException {
        UserData user = new UserData("testUser", "password123", "abcd@gmail.com");
        String token = service.register(user);
        assertNotNull(token, "Token should not be null on successful registration.");
    }

    @Test
    public void testRegisterFailAlreadyExists() throws DataAccessException {
        UserData user = new UserData("testUser", "password123", "abcd@gmail.com");
        service.register(user);
        String token = service.register(user);  // Registering same user again
        assertNull(token, "Token should be null for already registered user.");
    }

    // --------------------- Login Tests ---------------------
    @Test
    public void testLoginSuccess() throws DataAccessException {
        UserData user = new UserData("testUser", "password123", "abcd@gmail.com");
        service.register(user);  // Register the user first
        String token = service.login(user);
        assertNotNull(token, "Login should return a valid token.");
    }

    @Test
    public void testLoginFailWrongPassword() throws DataAccessException {
        UserData user = new UserData("testUser", "password123", "abcd@gmail.com");
        service.register(user);
        UserData wrongPasswordUser = new UserData("testUser", "wrongPassword", "wrong@mail.com");

        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            service.login(wrongPasswordUser);
        });

        assertEquals("Error: unauthorized", exception.getMessage());

    }

    @Test
    public void testLoginFailUserNotFound() throws DataAccessException {
        UserData user = new UserData("nonExistentUser", "password123", "test@mail.com");
        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            service.login(user);
        });

        assertEquals("Error: unauthorized", exception.getMessage());

    }

    // --------------------- Logout Tests ---------------------
    @Test
    public void testLogoutSuccess() throws Exception {
        UserData user = new UserData("testUser", "password123", "abcd@gmail.com");
        String token = service.register(user);
        service.logout(token);  // Logout using the token
        assertFalse(service.isValidAuth(token), "Auth token should be invalid after logout.");
    }

    @Test
    public void testLogoutFailInvalidToken() {
        Exception exception = assertThrows(Exception.class, () -> {
            service.logout("invalidToken");
        });
        assertNotNull(exception, "Logout with invalid token should throw an exception.");
    }

    // --------------------- Create Game Tests ---------------------
    @Test
    public void testCreateGameSuccess() throws Exception {
        String gameName = "Test Game";
        int gameID = service.createGame(gameName);
        assertTrue(gameID > 0, "Game ID should be greater than 0.");
    }

    // --------------------- Join Game Tests ---------------------
    @Test
    public void testJoinGameSuccess() throws Exception {
        String gameName = "Test Game";
        int gameID = service.createGame(gameName);

        UserData user = new UserData("testUser", "password123", "abcd@gmail.com");
        String token = service.register(user);

        JoinGameRequset request = new JoinGameRequset("WHITE", gameID);
        service.joinGame(request, token);
        GameData game = service.gameDAO.getGame(gameID);
        System.out.println(game);
        assertEquals("testUser", game.whiteUserName(), "White user should be set to testUser.");
    }

    @Test
    public void testJoinGameFailAlreadyTaken() throws Exception {
        String gameName = "Test Game";
        int gameID = service.createGame(gameName);

        UserData user1 = new UserData("testUser", "password123", "abcd@gmail.com");
        String token1 = service.register(user1);
        service.joinGame(new JoinGameRequset("WHITE", gameID), token1);

        UserData user2 = new UserData("testUser2", "123password123", "123@gmail.com");
        String token2 = service.register(user2);

        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            service.joinGame(new JoinGameRequset("WHITE", gameID), token2);
        });

        assertEquals("Error: WHITE is already taken", exception.getMessage());
    }

    @Test
    public void testJoinGameFailInvalidColor() throws Exception {
        String gameName = "Test Game";
        int gameID = service.createGame(gameName);

        UserData user = new UserData("testUser", "password123", "abcd@gmail.com");
        String token = service.register(user);

        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            service.joinGame(new JoinGameRequset(null, gameID), token);
        });

        assertEquals("Error: Bad request", exception.getMessage());
    }

    // --------------------- List Games Tests ---------------------
    @Test
    public void testListGamesSuccess() throws Exception {
        String gameName = "Test Game";
        int gameID = service.createGame(gameName);

        UserData user = new UserData("testUser", "password123", "abcd@gmail.com");
        String token = service.register(user);

        List<GameResult> games = service.listGames(token);
        assertEquals(1, games.size(), "There should be one game listed.");
    }

    @Test
    public void testListGamesFailInvalidToken() {
        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            service.listGames("invalidToken");
        });

        assertEquals("Error: Bad request", exception.getMessage());
    }

    @Test
    public void testLeaveGamesFailInvalidToken() throws Exception {
        String gameName = "Test Game";
        int gameID = service.createGame(gameName);

        UserData user = new UserData("testUser", "password123", "abcd@gmail.com");

        LeaveGameRequest leaveGameRequest = new LeaveGameRequest("WHITE", gameID);
        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            service.leaveGame(leaveGameRequest, "bad Token");
        });
    }

    @Test
    public void testLeaveGamesFailBadRequest() throws Exception {
        String gameName = "Test Game";
        int gameID = service.createGame(gameName);

        UserData user = new UserData("testUser", "password123", "abcd@gmail.com");
        String token = service.register(user);
        LeaveGameRequest leaveGameRequest = new LeaveGameRequest("WHITE", 100);
        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            service.leaveGame(leaveGameRequest, token);
        });
    }

    @Test
    public void testLeaveGamesSuccess() throws Exception {
        String gameName = "Test Game";
        int gameID = service.createGame(gameName);

        UserData user = new UserData("testUser", "password123", "abcd@gmail.com");
        String token = service.register(user);
        LeaveGameRequest leaveGameRequest = new LeaveGameRequest("WHITE", gameID);
        assertDoesNotThrow(() -> service.leaveGame(leaveGameRequest, token));

    }

}
