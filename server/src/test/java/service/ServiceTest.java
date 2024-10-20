package service;

import dataAccess.DataAccessException;
import model.AuthData;
import model.GameData;
import model.JoinGameRequset;
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
    public void testRegister_Success() throws DataAccessException {
        UserData user = new UserData("testUser", "password123", "abcd@gmail.com");
        String token = service.register(user);
        assertNotNull(token, "Token should not be null on successful registration.");
    }

    @Test
    public void testRegister_Fail_AlreadyExists() throws DataAccessException {
        UserData user = new UserData("testUser", "password123", "abcd@gmail.com");
        service.register(user);
        String token = service.register(user);  // Registering same user again
        assertNull(token, "Token should be null for already registered user.");
    }

    // --------------------- Login Tests ---------------------
    @Test
    public void testLogin_Success() throws DataAccessException {
        UserData user = new UserData("testUser", "password123", "abcd@gmail.com");
        service.register(user);  // Register the user first
        String token = service.login(user);
        assertNotNull(token, "Login should return a valid token.");
    }

    @Test
    public void testLogin_Fail_WrongPassword() throws DataAccessException {
        UserData user = new UserData("testUser", "password123", "abcd@gmail.com");
        service.register(user);
        UserData wrongPasswordUser = new UserData("testUser", "wrongPassword", "wrong@mail.com");
        String token = service.login(wrongPasswordUser);
        assertNull(token, "Login with incorrect password should return null.");
    }

    @Test
    public void testLogin_Fail_UserNotFound() throws DataAccessException {
        UserData user = new UserData("nonExistentUser", "password123", "test@mail.com");
        String token = service.login(user);
        assertNull(token, "Login for non-existent user should return null.");
    }

    // --------------------- Logout Tests ---------------------
    @Test
    public void testLogout_Success() throws Exception {
        UserData user = new UserData("testUser", "password123", "abcd@gmail.com");
        String token = service.register(user);
        service.logout(token);  // Logout using the token
        assertFalse(service.isValidAuth(token), "Auth token should be invalid after logout.");
    }

    @Test
    public void testLogout_Fail_InvalidToken() {
        Exception exception = assertThrows(Exception.class, () -> {
            service.logout("invalidToken");
        });
        assertNotNull(exception, "Logout with invalid token should throw an exception.");
    }

    // --------------------- Create Game Tests ---------------------
    @Test
    public void testCreateGame_Success() throws Exception {
        String gameName = "Test Game";
        int gameID = service.createGame(gameName);
        assertTrue(gameID > 0, "Game ID should be greater than 0.");
    }

    // --------------------- Join Game Tests ---------------------
    @Test
    public void testJoinGame_Success() throws Exception {
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
    public void testJoinGame_Fail_AlreadyTaken() throws Exception {
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
    public void testJoinGame_Fail_InvalidColor() throws Exception {
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
    public void testListGames_Success() throws Exception {
        String gameName = "Test Game";
        int gameID = service.createGame(gameName);

        UserData user = new UserData("testUser", "password123", "abcd@gmail.com");
        String token = service.register(user);

        List<GameResult> games = service.listGames(token);
        assertEquals(1, games.size(), "There should be one game listed.");
    }

    @Test
    public void testListGames_Fail_InvalidToken() {
        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            service.listGames("invalidToken");
        });

        assertEquals("Error: Bad request", exception.getMessage());
    }
}
