package proiect;

import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseConnectionCloseTest {

    private Connection connection;
    private static final String URL = "jdbc:mysql://localhost:3306/mydb";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    @BeforeEach
    void setUp() throws SQLException {
        connection = DriverManager.getConnection(URL, USER, PASSWORD);
    }

    @Test
    void testProperConnectionClose() throws SQLException {
        assertNotNull(connection, "Connection should not be null");
        connection.close();
        assertTrue(connection.isClosed(), "Connection should be closed");
    }

    @Test
    void testConnectionClosedTwice() throws SQLException {
        connection.close();
        assertDoesNotThrow(() -> connection.close(), "Closing connection twice should not throw exception");
        assertTrue(connection.isClosed(), "Connection should remain closed");
    }

    @Test
    void testOperationsAfterConnectionClosed() throws SQLException {
        connection.close();
        assertThrows(SQLException.class, () -> connection.createStatement(),
                "Operations on closed connection should throw SQLException");
    }
}