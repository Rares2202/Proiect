package proiect;

import org.junit.jupiter.api.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for database connection functionality.
 * This class tests the ability to establish connections to a MySQL database
 * using both valid and invalid credentials.
 */
class DatabaseConnectionTest {

    private Connection connection;
    /**
     * Good credentials
     */
    private static final String URL_good = "jdbc:mysql://localhost:3306/mydb";
    private static final String USER_good = "root";
    private static final String PASSWORD_good = "simone";

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

    /**
     * Tests establishing a database connection with valid credentials.
     * Verifies that the connection is not null and is valid.
     */
    @Test
    void testGoodDatabaseConnection() {
        assertNotNull(connection, "Database connection should not be null");
        assertDoesNotThrow(() -> {
            assertTrue(connection.isValid(1), "Database connection should be valid");
        });
    }

    /**
     * Tests attempting to establish a database connection with invalid credentials.
     * Verifies that an SQLException is thrown when trying to connect with bad credentials.
     */
    @Test
    void testBadDatabaseConnection() {
        assertThrows(SQLException.class, () -> {
            Connection badConnection = DriverManager.getConnection(URL_bad, USER_bad, PASSWORD_bad);
        }, "Connection with bad credentials should throw SQLException");
    }
}
