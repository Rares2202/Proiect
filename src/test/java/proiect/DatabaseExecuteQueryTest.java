package proiect;

import org.junit.jupiter.api.*;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseExecuteQueryTest {

    private Connection connection;
    private Statement statement;
    private static final String URL = "jdbc:mysql://localhost:3306/mydb";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    @BeforeEach
    void setUp() throws SQLException {
        connection = DriverManager.getConnection(URL, USER, PASSWORD);
        statement = connection.createStatement();
    }

    @AfterEach
    void tearDown() throws SQLException {
        if (statement != null) {
            statement.close();
        }
        if (connection != null) {
            connection.close();
        }
    }

    @Test
    void testValidSelectQuery() throws SQLException {
        String validQuery = "SELECT * FROM user";
        ResultSet resultSet = statement.executeQuery(validQuery);
        assertNotNull(resultSet, "Result set should not be null for valid query");
        assertTrue(resultSet.next(), "Result set should contain at least one row");
    }

    @Test
    void testInvalidSyntaxQuery() {
        String invalidQuery = "SELCT * FORM user"; // Intentionally misspelled
        assertThrows(SQLException.class, () -> {
            statement.executeQuery(invalidQuery);
        }, "Invalid SQL syntax should throw SQLException");
    }

    @Test
    void testNonExistentTableQuery() {
        String nonExistentQuery = "SELECT * FROM nonexistent_table";
        assertThrows(SQLException.class, () -> {
            statement.executeQuery(nonExistentQuery);
        }, "Query for non-existent table should throw SQLException");
    }
}