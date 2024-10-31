package dataAccess;

import dataAccess.MemoryUserDAO;
import dataAccess.DataAccessException;
import model.UserData;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.mindrot.jbcrypt.BCrypt;

class MemoryUserDAOTest {

    private MemoryUserDAO userDAO;

    @BeforeEach
    void setUp() throws DataAccessException {
        userDAO = new MemoryUserDAO();
        userDAO.clear(); // Clear the database before each test
    }

    // 1. Test for getUser
    @Test
    void testGetUserPositive() throws DataAccessException {
        UserData user = new UserData("john_doe", "password123", "john@example.com");
        userDAO.createUser(user);

        UserData retrievedUser = userDAO.getUser("john_doe");
        assertNotNull(retrievedUser, "User should be retrieved");
        assertEquals("john_doe", retrievedUser.username());
        assertEquals("john@example.com", retrievedUser.email());
    }

    @Test
    void testGetUserNegative() throws DataAccessException {
        UserData retrievedUser = userDAO.getUser("non_existent_user");
        assertNull(retrievedUser, "Non-existent user should return null");
    }

    // 2. Test for createUser
    @Test
    void testCreateUserPositive() throws DataAccessException {
        UserData user = new UserData("jane_doe", "mySecret", "jane@example.com");
        userDAO.createUser(user);

        UserData retrievedUser = userDAO.getUser("jane_doe");
        assertNotNull(retrievedUser, "User should be created successfully");
        assertTrue(BCrypt.checkpw("mySecret", retrievedUser.password()),
                "Password should match the hashed password");
    }

    @Test
    void testCreateUserNegative() throws DataAccessException {
        UserData user1 = new UserData("duplicate_user", "password1", "user1@example.com");
        UserData user2 = new UserData("duplicate_user", "password2", "user2@example.com");

        userDAO.createUser(user1); // Create the first user

        // Attempt to create a second user with the same username, expecting an exception
        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            userDAO.createUser(user2);
        });

        assertTrue(exception.getMessage().contains("Unable to read data"),
                "Exception message should indicate data access error");
    }

    // 3. Test for getPassword
    @Test
    void testGetPasswordPositive() throws DataAccessException {
        UserData user = new UserData("sam_doe", "passwordXYZ", "sam@example.com");
        userDAO.createUser(user);

        String hashedPassword = userDAO.getPassword("sam_doe");
        assertNotNull(hashedPassword, "Password should be retrieved");
        assertTrue(BCrypt.checkpw("passwordXYZ", hashedPassword),
                "Hashed password should match the original password");
    }

    @Test
    void testGetPasswordNegative() throws DataAccessException {
        String password = userDAO.getPassword("non_existent_user");
        assertNull(password, "Password retrieval for non-existent user should return null");
    }

    // 4. Test for clear
    @Test
    void testClearPositive() throws DataAccessException {
        UserData user1 = new UserData("user1", "pass1", "user1@example.com");
        UserData user2 = new UserData("user2", "pass2", "user2@example.com");
        userDAO.createUser(user1);
        userDAO.createUser(user2);

        userDAO.clear();

        assertNull(userDAO.getUser("user1"), "User1 should be removed after clear");
        assertNull(userDAO.getUser("user2"), "User2 should be removed after clear");
    }

    @Test
    void testClearNegative() throws DataAccessException {
        userDAO.clear(); // Clear when no users exist (edge case)

        assertNull(userDAO.getUser("any_user"),
                "Clear on empty database should still result in no users found");
    }
}
