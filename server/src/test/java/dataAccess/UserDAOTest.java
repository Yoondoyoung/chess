package dataAccess;

import dataAccess.DataAccessException;
import dataAccess.MemoryUserDAO;
import dataAccess.UserDAO;
import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

class SQLUserDAOTest {
    static private UserDAO userDao;

    @AfterEach
    void clear() {
        assertDoesNotThrow(() -> userDao.clear());
    }

    @BeforeAll
    static void getInstance() {
        assertDoesNotThrow(() -> userDao = userDao.getInstance());
    }


    @Test
    void insertUserPos() {
        UserData expected = new UserData("username", "password", "email");
        assertDoesNotThrow(() -> userDao.insertUser(expected));
    }

    @Test
    void insertUserNeg() {
        UserData expected = new UserData("username", null, "email");
        assertThrows(DataAccessException.class, () -> {
            userDao.insertUser(expected);
        });
    }

    @Test
    void getUserPos() {
        UserData insertedUser = new UserData("username", "password", "email");
        assertDoesNotThrow(() -> userDao.insertUser(insertedUser));
        UserData actual = assertDoesNotThrow(() -> userDao.getUser(insertedUser.username()));

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        assertTrue(encoder.matches("password", actual.password()));
        assertEquals("username", actual.username());
        assertEquals("email", actual.email());
    }

    @Test
    void getUserNeg() {
        UserData actual = assertDoesNotThrow(() -> userDao.getUser(null));
        assertNull(actual);
    }

    @Test
    void selectPasswordPos() {
        UserData insertedUser = new UserData("username", "password", "email");
        assertDoesNotThrow(() -> userDao.insertUser(insertedUser));
        String passwordHash = assertDoesNotThrow(() -> userDao.selectPassword(insertedUser.username()));

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        assertTrue(encoder.matches("password", passwordHash));
    }

    @Test
    void selectPasswordNeg() {
        String actual = assertDoesNotThrow(() -> userDao.selectPassword(null));
        assertNull(actual);
    }
}
