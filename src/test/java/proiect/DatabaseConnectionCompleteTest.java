package proiect;

import org.junit.jupiter.api.*;
import javafx.scene.layout.AnchorPane;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the DatabaseConnection class.
 * This class tests the behavior of the DatabaseConnection class
 * when database connections fail.
 * 
 * Note: These tests assume that the database connection will fail
 * with the provided credentials. In a production environment,
 * you would want to use a test database or mock the database connection.
 */
class DatabaseConnectionCompleteTest {

    /**
     * A test implementation of ControllerLibrarian for testing purposes.
     * This class extends ControllerLibrarian and tracks when setOnlyMenu is called.
     */
    private static class TestControllerLibrarian extends ControllerLibrarian {
        boolean setOnlyMenuCalled = false;
        AnchorPane lastMenuSet = null;

        /**
         * Overrides the setOnlyMenu method to track when it's called.
         * 
         * @param menu the menu to set
         */
        @Override
        public void setOnlyMenu(AnchorPane menu) {
            setOnlyMenuCalled = true;
            lastMenuSet = menu;
        }
    }

    private static final String URL_GOOD = "jdbc:mysql://localhost:3306/mydb";
    private static final String USER_GOOD = "root";
    private static final String PASSWORD_GOOD = "root";

    private static final String URL_BAD = "jdbc:mysql://localhost:3305/mydb";
    private static final String USER_BAD = "baduser";
    private static final String PASSWORD_BAD = "badpassword";

    private DatabaseConnection dbConnection;
    private TestControllerLibrarian testController;

    /**
     * Sets up the test environment before each test.
     * Creates a TestControllerLibrarian to use in the DatabaseConnection constructor.
     */
    @BeforeEach
    void setUp() {
        testController = new TestControllerLibrarian();
        testController.connectionFailed_menu = new AnchorPane();
    }

    /**
     * Cleans up the test environment after each test.
     * Closes the database connection if it exists.
     */
    @AfterEach
    void tearDown() {
        if (dbConnection != null) {
            dbConnection.close();
        }
    }

    /**
     * Tests creating a DatabaseConnection with valid credentials.
     * Verifies that the connection is successfully established.
     */
    @Test
    void testConstructorWithValidCredentials() {
        dbConnection = new DatabaseConnection(URL_GOOD, USER_GOOD, PASSWORD_GOOD, testController);
        assertNotNull(dbConnection.getConnection(), "Connection should not be null with valid credentials");
        assertFalse(testController.setOnlyMenuCalled, "setOnlyMenu should not be called with valid credentials");
    }

    /**
     * Tests creating a DatabaseConnection with invalid credentials.
     * Verifies that the setFailed method is called on the controller.
     */
    @Test
    void testConstructorWithInvalidCredentials() {
        dbConnection = new DatabaseConnection(URL_BAD, USER_BAD, PASSWORD_BAD, testController);
        assertTrue(testController.setOnlyMenuCalled, "setOnlyMenu should be called with invalid credentials");
        assertEquals(testController.connectionFailed_menu, testController.lastMenuSet, 
            "connectionFailed_menu should be set when connection fails");
    }

    /**
     * Tests the close method of DatabaseConnection.
     * Verifies that the connection is properly closed.
     */
    @Test
    void testClose() {
        dbConnection = new DatabaseConnection(URL_GOOD, USER_GOOD, PASSWORD_GOOD, testController);
        Connection conn = dbConnection.getConnection();
        assertNotNull(conn, "Connection should not be null before closing");

        dbConnection.close();

        assertDoesNotThrow(() -> {
            if (conn != null) {
                assertTrue(conn.isClosed(), "Connection should be closed after calling close()");
            }
        });
    }

    /**
     * Tests the executeQuery method with a valid query.
     * Verifies that the query executes successfully and returns a ResultSet.
     */
    @Test
    void testExecuteQueryWithValidQuery() {
        dbConnection = new DatabaseConnection(URL_GOOD, USER_GOOD, PASSWORD_GOOD, testController);

        assertDoesNotThrow(() -> {
            ResultSet rs = dbConnection.executeQuery("SELECT 1");
            assertNotNull(rs, "ResultSet should not be null for valid query");
            assertTrue(rs.next(), "ResultSet should have at least one row");
        });
    }

    /**
     * Tests the executeQuery method with an invalid query.
     * Verifies that a RuntimeException is thrown and setFailed is called.
     */
    @Test
    void testExecuteQueryWithInvalidQuery() {
        dbConnection = new DatabaseConnection(URL_GOOD, USER_GOOD, PASSWORD_GOOD, testController);

        // Reset the flag before the test
        testController.setOnlyMenuCalled = false;

        assertThrows(RuntimeException.class, () -> {
            dbConnection.executeQuery("SELECT * FROM non_existent_table");
        }, "Invalid query should throw RuntimeException");

        assertTrue(testController.setOnlyMenuCalled, "setOnlyMenu should be called for invalid query");
    }

    /**
     * Tests the executeUpdate method with a valid update statement.
     * Verifies that the update executes successfully.
     */
    @Test
    void testExecuteUpdateWithValidUpdate() {
        dbConnection = new DatabaseConnection(URL_GOOD, USER_GOOD, PASSWORD_GOOD, testController);

        assertDoesNotThrow(() -> {
            dbConnection.executeUpdate("CREATE TABLE IF NOT EXISTS test_table (id INT PRIMARY KEY)");
            dbConnection.executeUpdate("DROP TABLE IF EXISTS test_table");
        }, "Valid update statements should not throw exceptions");
    }

    /**
     * Tests the executeUpdate method with an invalid update statement.
     * Verifies that a RuntimeException is thrown and setFailed is called.
     */
    @Test
    void testExecuteUpdateWithInvalidUpdate() {
        dbConnection = new DatabaseConnection(URL_GOOD, USER_GOOD, PASSWORD_GOOD, testController);

        // Reset the flag before the test
        testController.setOnlyMenuCalled = false;

        assertThrows(RuntimeException.class, () -> {
            dbConnection.executeUpdate("UPDATE non_existent_table SET column = 'value'");
        }, "Invalid update should throw RuntimeException");

        assertTrue(testController.setOnlyMenuCalled, "setOnlyMenu should be called for invalid update");
    }

    /**
     * Tests the getQueryCount method.
     * Verifies that it correctly counts the number of rows in a result set.
     */
    @Test
    void testGetQueryCount() {
        dbConnection = new DatabaseConnection(URL_GOOD, USER_GOOD, PASSWORD_GOOD, testController);

        assertDoesNotThrow(() -> {
            dbConnection.executeUpdate("CREATE TABLE IF NOT EXISTS test_count (id INT)");
            dbConnection.executeUpdate("INSERT INTO test_count VALUES (1), (2), (3)");

            int count = dbConnection.getQueryCount("SELECT * FROM test_count");
            assertEquals(3, count, "Count should be 3 for the test table");

            dbConnection.executeUpdate("DROP TABLE IF EXISTS test_count");
        });
    }

    /**
     * Tests the getQuerrySum method.
     * Verifies that it correctly calculates the sum of values in a result set.
     */
    @Test
    void testGetQuerrySum() {
        dbConnection = new DatabaseConnection(URL_GOOD, USER_GOOD, PASSWORD_GOOD, testController);

        assertDoesNotThrow(() -> {
            dbConnection.executeUpdate("CREATE TABLE IF NOT EXISTS test_sum (value INT)");
            dbConnection.executeUpdate("INSERT INTO test_sum VALUES (10), (20), (30)");

            int sum = dbConnection.getQuerrySum("SELECT value FROM test_sum");
            assertEquals(60, sum, "Sum should be 60 for the test values");

            dbConnection.executeUpdate("DROP TABLE IF EXISTS test_sum");
        });
    }
}
