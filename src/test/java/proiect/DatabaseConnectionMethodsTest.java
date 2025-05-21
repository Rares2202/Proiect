package proiect;

import org.junit.jupiter.api.*;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for database operations using direct JDBC connections.
 * This class tests SQL operations similar to those in the DatabaseConnection class,
 * including updates, counting rows, and summing values.
 */
class DatabaseConnectionMethodsTest {

    private Connection connection;
    private static final String URL = "jdbc:mysql://localhost:3306/mydb";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    /**
     * Sets up the test environment before each test.
     * Establishes a direct JDBC connection to the database.
     * 
     * @throws SQLException if a database access error occurs
     */
    @BeforeEach
    void setUp() throws SQLException {
        connection = DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /**
     * Cleans up the test environment after each test.
     * Closes the database connection if it is still open.
     * 
     * @throws SQLException if a database access error occurs
     */
    @AfterEach
    void tearDown() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    /**
     * Tests executing SQL update statements with a direct JDBC connection.
     * Verifies that the statements execute without throwing exceptions.
     */
    @Test
    void testExecuteUpdate() {
        // Create a test table
        String createTableQuery = "CREATE TABLE IF NOT EXISTS test_table (id INT PRIMARY KEY, name VARCHAR(50))";
        assertDoesNotThrow(() -> {
            try (Statement stmt = connection.createStatement()) {
                stmt.executeUpdate(createTableQuery);
            }
        }, "executeUpdate should not throw an exception for valid SQL");

        // Insert a test record
        String insertQuery = "INSERT INTO test_table (id, name) VALUES (1, 'Test') ON DUPLICATE KEY UPDATE name = 'Test'";
        assertDoesNotThrow(() -> {
            try (Statement stmt = connection.createStatement()) {
                stmt.executeUpdate(insertQuery);
            }
        }, "executeUpdate should not throw an exception for valid SQL");

        // Clean up - drop the test table
        String dropTableQuery = "DROP TABLE IF EXISTS test_table";
        assertDoesNotThrow(() -> {
            try (Statement stmt = connection.createStatement()) {
                stmt.executeUpdate(dropTableQuery);
            }
        }, "executeUpdate should not throw an exception for valid SQL");
    }

    /**
     * Tests counting rows in a result set using direct JDBC operations.
     * Verifies that the count operation correctly returns the number of rows.
     * 
     * @throws SQLException if a database access error occurs
     */
    @Test
    void testCountRows() throws SQLException {
        // Create a test table
        String createTableQuery = "CREATE TABLE IF NOT EXISTS test_count_table (id INT PRIMARY KEY, name VARCHAR(50))";
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(createTableQuery);
        }

        // Insert test records
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("INSERT INTO test_count_table (id, name) VALUES (1, 'Test1') ON DUPLICATE KEY UPDATE name = 'Test1'");
            stmt.executeUpdate("INSERT INTO test_count_table (id, name) VALUES (2, 'Test2') ON DUPLICATE KEY UPDATE name = 'Test2'");
            stmt.executeUpdate("INSERT INTO test_count_table (id, name) VALUES (3, 'Test3') ON DUPLICATE KEY UPDATE name = 'Test3'");
        }

        // Count rows
        int count = 0;
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM test_count_table")) {
            while (rs.next()) {
                count++;
            }
        }
        assertEquals(3, count, "Row count should be 3");

        // Clean up - drop the test table
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("DROP TABLE IF EXISTS test_count_table");
        }
    }

    /**
     * Tests summing values in a result set using direct JDBC operations.
     * Verifies that the sum operation correctly calculates the total of values.
     * 
     * @throws SQLException if a database access error occurs
     */
    @Test
    void testSumValues() throws SQLException {
        // Create a test table
        String createTableQuery = "CREATE TABLE IF NOT EXISTS test_sum_table (id INT PRIMARY KEY, value INT)";
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(createTableQuery);
        }

        // Insert test records
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("INSERT INTO test_sum_table (id, value) VALUES (1, 10) ON DUPLICATE KEY UPDATE value = 10");
            stmt.executeUpdate("INSERT INTO test_sum_table (id, value) VALUES (2, 20) ON DUPLICATE KEY UPDATE value = 20");
            stmt.executeUpdate("INSERT INTO test_sum_table (id, value) VALUES (3, 30) ON DUPLICATE KEY UPDATE value = 30");
        }

        // Calculate sum
        int sum = 0;
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT value FROM test_sum_table")) {
            while (rs.next()) {
                sum += rs.getInt(1);
            }
        }
        assertEquals(60, sum, "Sum of values should be 60");

        // Clean up - drop the test table
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("DROP TABLE IF EXISTS test_sum_table");
        }
    }
}
