package proiect;

import org.junit.jupiter.api.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for database connection closing functionality.
 * This class tests various scenarios related to closing a database connection,
 * including proper closing, closing twice, and attempting operations after closing.
 */
class DatabaseConnectionCloseTest {

    private Connection connection;
    private static final String URL = "jdbc:mysql://localhost:3306/mydb";
    private static final String USER = "root";
    private static final String PASSWORD = "simone";

    @BeforeEach
    void setUp() throws SQLException {
        connection = DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /**
     * Tests proper closing of a database connection.
     * Verifies that the connection is not null before closing and is properly closed afterward.
     * 
     * @throws SQLException if a database access error occurs
     */
    @Test
    void testProperConnectionClose() throws SQLException {
        assertNotNull(connection, "Connection should not be null");
        connection.close();
        assertTrue(connection.isClosed(), "Connection should be closed");
    }

    /**
     * Tests closing a database connection twice.
     * Verifies that closing an already closed connection does not throw an exception
     * and that the connection remains in a closed state.
     * 
     * @throws SQLException if a database access error occurs
     */
    @Test
    void testConnectionClosedTwice() throws SQLException {
        connection.close();
        assertDoesNotThrow(() -> connection.close(), "Closing connection twice should not throw exception");
        assertTrue(connection.isClosed(), "Connection should remain closed");
    }

    /**
     * Tests attempting operations on a closed database connection.
     * Verifies that attempting to perform operations (like creating a statement)
     * on a closed connection throws an SQLException.
     * 
     * @throws SQLException if a database access error occurs
     */
    @Test
    void testOperationsAfterConnectionClosed() throws SQLException {
        connection.close();
        assertThrows(SQLException.class, () -> connection.createStatement(),
                "Operations on closed connection should throw SQLException");
    }
}
