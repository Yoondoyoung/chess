package dataAccess;

import dataAccess.MemoryGameDAO;
import dataAccess.DataAccessException;
import model.GameData;
import model.result.GameResult;
import chess.ChessGame;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

class MemoryGameDAOTest {

    private MemoryGameDAO gameDAO;

    @BeforeEach
    void setUp() throws DataAccessException {
        gameDAO = new MemoryGameDAO();
        gameDAO.clear(); // Clear the database before each test
    }

    // 1. Test for getGame
    @Test
    void testGetGamePositive() throws DataAccessException {
        GameData game = gameDAO.createGame("Test Game");
        GameData retrievedGame = gameDAO.getGame(game.gameID());
        assertNotNull(retrievedGame, "Game should be retrieved successfully");
        assertEquals(game.gameName(), retrievedGame.gameName(), "Game names should match");
    }

    @Test
    void testGetGameNegative() throws DataAccessException {
        GameData game = gameDAO.getGame(999); // Non-existent game ID
        assertNull(game, "Non-existent game should return null");
    }

    // 2. Test for getGameList
    @Test
    void testGetGameListPositive() throws DataAccessException {
        gameDAO.createGame("Game 1");
        gameDAO.createGame("Game 2");

        List<GameResult> gameList = gameDAO.getGameList();
        assertNotNull(gameList, "Game list should not be null");
        assertEquals(2, gameList.size(), "Game list should contain 2 games");
    }

    @Test
    void testGetGameListNegative() throws DataAccessException {
        List<GameResult> gameList = gameDAO.getGameList(); // No games added
        assertNotNull(gameList, "Game list should not be null even if empty");
        assertTrue(gameList.isEmpty(), "Game list should be empty");
    }

    // 3. Test for createGame
    @Test
    void testCreateGamePositive() throws DataAccessException {
        GameData game = gameDAO.createGame("New Game");
        assertNotNull(game, "Game should be created successfully");
        assertEquals("New Game", game.gameName(), "Game name should match the input name");
    }

    @Test
    void testCreateGameNegative() {
        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            gameDAO.createGame(""); // Invalid game name
        });
        assertTrue(exception.getMessage().contains("Unable to read data"),
                "Exception should indicate an issue with data access");
    }

    // 4. Test for updateGame
    @Test
    void testUpdateGamePositive() throws DataAccessException {
        GameData game = gameDAO.createGame("Game to Update");
        game = new GameData(game.gameID(), "whitePlayer", "blackPlayer", "Game to Update", new ChessGame());
        gameDAO.updateGame(game, "WHITE");

        GameData updatedGame = gameDAO.getGame(game.gameID());
        assertNotNull(updatedGame, "Updated game should not be null");
        assertEquals("whitePlayer", updatedGame.whiteUserName(), "White player username should be updated");
    }

    @Test
    void testUpdateGameNegative() {
        GameData invalidGame = new GameData(999, "whitePlayer", "blackPlayer", "Non-Existent Game", new ChessGame());
        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            gameDAO.updateGame(invalidGame, "INVALID_COLOR"); // Invalid color
        });
        assertTrue(exception.getMessage().contains("Bad color"), "Exception should indicate a bad color input");
    }

    // 5. Test for clear
    @Test
    void testClearPositive() throws DataAccessException {
        gameDAO.createGame("Game to Clear 1");
        gameDAO.createGame("Game to Clear 2");

        gameDAO.clear();
        List<GameResult> gameList = gameDAO.getGameList();
        assertNotNull(gameList, "Game list should not be null after clearing");
        assertTrue(gameList.isEmpty(), "Game list should be empty after clearing");
    }

    @Test
    void testClearNegative() throws DataAccessException {
        gameDAO.clear(); // Clear on an already empty database
        List<GameResult> gameList = gameDAO.getGameList();
        assertNotNull(gameList, "Game list should not be null even if database was already empty");
        assertTrue(gameList.isEmpty(), "Game list should remain empty if database was already empty");
    }
}
