package proiect;

import org.junit.jupiter.api.*;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for database query execution functionality.
 * This class tests various scenarios of SQL query execution against a MySQL database,
 * including valid queries, queries with syntax errors, and queries for non-existent tables.
 */
class DatabaseExecuteQueryTest {

    private Connection connection;
    private Statement statement;
    private static final String URL = "jdbc:mysql://localhost:3306/mydb";
    private static final String USER = "root";
    private static final String PASSWORD = "simone";

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

    /**
     * Tests execution of a valid SQL SELECT query.
     * Verifies that the query returns a non-null result set with at least one row.
     * 
     * @throws SQLException if a database access error occurs
     */
    @Test
    void testValidSelectQuery() throws SQLException {
        String validQuery = "SELECT * FROM user";
        ResultSet resultSet = statement.executeQuery(validQuery);
        assertNotNull(resultSet, "Result set should not be null for valid query");
        assertTrue(resultSet.next(), "Result set should contain at least one row");
    }

    /**
     * Tests execution of an SQL query with invalid syntax.
     * Verifies that executing a query with syntax errors throws an SQLException.
     */
    @Test
    void testInvalidSyntaxQuery() {
        String invalidQuery = "SELCT * FORM user"; // Intentionally misspelled
        assertThrows(SQLException.class, () -> {
            statement.executeQuery(invalidQuery);
        }, "Invalid SQL syntax should throw SQLException");
    }

    /**
     * Tests execution of an SQL query referencing a non-existent table.
     * Verifies that executing a query for a table that doesn't exist throws an SQLException.
     */
    @Test
    void testNonExistentTableQuery() {
        String nonExistentQuery = "SELECT * FROM nonexistent_table"; //Table that doesn't exist
        assertThrows(SQLException.class, () -> {
            statement.executeQuery(nonExistentQuery);
        }, "Query for non-existent table should throw SQLException");
    }
}
