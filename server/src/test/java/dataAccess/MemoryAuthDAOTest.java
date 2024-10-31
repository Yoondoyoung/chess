package dataAccess;

import dataAccess.MemoryAuthDAO;
import dataAccess.DataAccessException;
import model.AuthData;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class MemoryAuthDAOTest {

    private MemoryAuthDAO authDAO;

    @BeforeEach
    void setUp() throws DataAccessException {
        authDAO = new MemoryAuthDAO();
        authDAO.clear();
    }

    // 1. Test for createAuth
    @Test
    void testCreateAuthPositive() throws DataAccessException {
        AuthData authData = authDAO.createAuth("testUser");
        assertNotNull(authData, "AuthData should be created successfully");
        assertNotNull(authData.authToken(), "Auth token should not be null");
        assertEquals("testUser", authData.username(), "Username should match the input username");
    }

    @Test
    void testCreateAuthNegative() {
        Exception exception = assertThrows(DataAccessException.class, () -> {
            authDAO.createAuth(null); // Null username is invalid
        });
        assertTrue(exception.getMessage().contains("Unable to read data"),
                "Exception message should indicate an issue with data access");
    }

    // 2. Test for getAuth
    @Test
    void testGetAuthPositive() throws DataAccessException {
        AuthData authData = authDAO.createAuth("testUser");
        authDAO.insertAuth(authData);

        String retrievedToken = authDAO.getAuth("testUser");
        assertNotNull(retrievedToken, "Auth token should be retrieved successfully");
        assertEquals(authData.authToken(), retrievedToken, "Auth tokens should match");
    }

    @Test
    void testGetAuthNegative() throws DataAccessException {
        String authToken = authDAO.getAuth("nonExistentUser"); // User not in auth store
        assertNull(authToken, "Auth token should be null for non-existent user");
    }

    // 3. Test for insertAuth
    @Test
    void testInsertAuthPositive() throws DataAccessException {
        AuthData authData = new AuthData("insertUser", "uniqueToken");
        authDAO.insertAuth(authData);

        String storedToken = authDAO.getAuth("insertUser");
        assertEquals("uniqueToken", storedToken, "Stored token should match the inserted token");
    }

    @Test
    void testInsertAuthNegative() {
        AuthData invalidAuthData = new AuthData(null, null); // Invalid auth data

        Exception exception = assertThrows(DataAccessException.class, () -> {
            authDAO.insertAuth(invalidAuthData);
        });
        assertTrue(exception.getMessage().contains("Unable to read data"), "Exception should indicate data issue");
    }

    // 4. Test for deleteAuth
    @Test
    void testDeleteAuthPositive() throws DataAccessException {
        AuthData authData = authDAO.createAuth("userToDelete");
        authDAO.insertAuth(authData);

        authDAO.deleteAuth(authData.authToken());
        assertFalse(authDAO.isValidAuth(authData.authToken()), "Auth token should be invalid after deletion");
    }

    @Test
    void testDeleteAuthNegative() {
        Exception exception = assertThrows(DataAccessException.class, () -> {
            authDAO.deleteAuth("invalidToken");
        });
        assertTrue(exception.getMessage().contains("auth doesn't exist"),
                "Exception should indicate non-existent auth token");
    }

    // 5. Test for isValidAuth
    @Test
    void testIsValidAuthPositive() throws DataAccessException {
        AuthData authData = authDAO.createAuth("validUser");
        authDAO.insertAuth(authData);

        assertTrue(authDAO.isValidAuth(authData.authToken()), "Auth token should be valid");
    }

    @Test
    void testIsValidAuthNegative() throws DataAccessException {
        assertFalse(authDAO.isValidAuth("invalidAuthToken"), "Non-existent auth token should be invalid");
    }

    // 6. Test for clear
    @Test
    void testClearPositive() throws DataAccessException {
        authDAO.createAuth("user1");
        authDAO.createAuth("user2");

        authDAO.clear();
        assertNull(authDAO.getAuth("user1"), "Auth token should be null after clear");
        assertNull(authDAO.getAuth("user2"), "Auth token should be null after clear");
    }

    @Test
    void testClearNegative() throws DataAccessException {
        authDAO.clear();
        assertNull(authDAO.getAuth("nonExistentUser"), "Auth token should be null if database is already empty");
    }

    // 7. Test for getUser
    @Test
    void testGetUserPositive() throws DataAccessException {
        AuthData authData = authDAO.createAuth("testUserForGet");
        authDAO.insertAuth(authData);

        String retrievedUser = authDAO.getUser(authData.authToken());
        assertEquals("testUserForGet", retrievedUser, "Username should match the one associated with auth token");
    }

    @Test
    void testGetUserNegative() throws DataAccessException {
        String user = authDAO.getUser("invalidToken");
        assertNull(user, "User should be null for a non-existent auth token");
    }
}
