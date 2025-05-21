package proiect;

import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseConnectionTest {

    private Connection connection;
    /**
     * Good credentials
     */
    private static final String URL_good = "jdbc:mysql://localhost:3306/mydb";
    private static final String USER_good = "root";
    private static final String PASSWORD_good = "root";

    /**
     * Bad credentials
     */
    private static final String URL_bad = "jdbc:mysql://localhost:3305/mydb";
    private static final String USER_bad = "other";
    private static final String PASSWORD_bad = "password";

    @BeforeEach
    void setUp() throws SQLException {
        connection = DriverManager.getConnection(URL_good, USER_good, PASSWORD_good);
    }

    @AfterEach
    void tearDown() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    @Test
    void testGoodDatabaseConnection() {
        assertNotNull(connection, "Database connection should not be null");
        assertDoesNotThrow(() -> {
            assertTrue(connection.isValid(1), "Database connection should be valid");
        });
    }

    @Test
    void testBadDatabaseConnection() {
        assertThrows(SQLException.class, () -> {
            Connection badConnection = DriverManager.getConnection(URL_bad, USER_bad, PASSWORD_bad);
        }, "Connection with bad credentials should throw SQLException");
    }
}