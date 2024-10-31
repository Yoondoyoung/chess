package dataAccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

public class MemoryUserDAO implements UserDAO{

    private static MemoryUserDAO instance;
    public static synchronized UserDAO getInstance() throws DataAccessException {
        if (instance == null){
            instance = new MemoryUserDAO();
        }
        return instance;
    }

    public MemoryUserDAO() throws DataAccessException {
        String[] createStatements = {
                """
            CREATE TABLE IF NOT EXISTS  users (
              `username` varchar(255) NOT NULL,
              `password` TEXT NOT NULL,
              `email` TEXT NOT NULL,
              PRIMARY KEY (`username`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
        };
        DatabaseManager.configureDatabase(createStatements);
    }
    @Override
    public UserData getUser(String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, password, email FROM users WHERE username=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        var password = rs.getString("password");
                        var email = rs.getString("email");
                        return new UserData(username, password, email);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        try {
            var statement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";

            String passwordHash = BCrypt.hashpw(user.password(), BCrypt.gensalt());
            var id = DatabaseManager.executeUpdate(statement, user.username(), passwordHash, user.email());
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
    }

    public void clear() throws DataAccessException {
        var statement = "TRUNCATE users";
        DatabaseManager.executeUpdate(statement);
    }

    @Override
    public String getPassword(String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT password FROM users WHERE username=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getString("password");
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

}