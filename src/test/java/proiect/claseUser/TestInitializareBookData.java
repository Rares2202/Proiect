package proiect.claseUser;

import org.junit.jupiter.api.*;
import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the Book.initializare method.
 * This class tests the functionality of initializing a Book object with data from the database.
 */
class TestInitializareBookData {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/mydb";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "simone";
    private static final String TEST_COVER_URL = "test_cover.jpg";

    private Connection connection;
    private Book book;

    /**
     * Sets up the test environment before each test.
     * Creates a database connection, initializes a test book with empty values,
     * and ensures that test data exists in the database.
     *
     * @throws SQLException if a database access error occurs
     */
    @BeforeEach
    void setUp() throws SQLException {
        connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        book = new Book(0, "", "", "", "", "");
        ensureTestDataExists();
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
     * Ensures that test data exists in the database.
     * If the test data doesn't exist, it creates it.
     *
     * @throws SQLException if a database access error occurs
     */
    private void ensureTestDataExists() throws SQLException {
        String checkQuery = "SELECT COUNT(*) FROM carte WHERE coverCarte = ?";
        try (PreparedStatement statement = connection.prepareStatement(checkQuery)) {
            statement.setString(1, TEST_COVER_URL);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            int count = resultSet.getInt(1);

            if (count == 0) {
                String insertQuery = "INSERT INTO carte (idCarte, titluCarti, autorCarte, descriere, genCarte, numarCarte, coverCarte) " +
                                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
                    insertStatement.setInt(1, 999);
                    insertStatement.setString(2, "Test Title");
                    insertStatement.setString(3, "Test Author");
                    insertStatement.setString(4, "Test Description");
                    insertStatement.setString(5, "Test Genre");
                    insertStatement.setInt(6, 1);
                    insertStatement.setString(7, TEST_COVER_URL);
                    insertStatement.executeUpdate();
                }
            }
        }
    }

    /**
     * Tests the initializare method of the Book class.
     * Verifies that the Book object is properly initialized with data from the database.
     */
    @Test
    void testInitializare() {
        Book initializedBook = book.initializare(TEST_COVER_URL);

        assertEquals(999, initializedBook.getId(), "Book ID should match the test data");
        assertEquals("Test Title", initializedBook.getTitle(), "Book title should match the test data");
        assertEquals("Test Author", initializedBook.getAuthor(), "Book author should match the test data");
        assertEquals("Test Description", initializedBook.getDescription(), "Book description should match the test data");
        assertEquals("Test Genre", initializedBook.getGenre(), "Book genre should match the test data");
        assertEquals(TEST_COVER_URL, initializedBook.getCoverUrl(), "Book cover URL should match the test data");

        assertSame(book, initializedBook, "The initializare method should return the same Book object");
    }

    /**
     * Tests the initializare method with a non-existent cover URL.
     * Verifies that the Book object remains unchanged when no data is found.
     */
    @Test
    void testInitializareWithNonExistentCoverUrl() {
        Book testBook = new Book(1, "Initial Title", "Initial Author", "Initial Description", "Initial Genre", "Initial Cover");

        Book initializedBook = testBook.initializare("non_existent_cover.jpg");

        assertEquals(1, initializedBook.getId(), "Book ID should remain unchanged");
        assertEquals("Initial Title", initializedBook.getTitle(), "Book title should remain unchanged");
        assertEquals("Initial Author", initializedBook.getAuthor(), "Book author should remain unchanged");
        assertEquals("Initial Description", initializedBook.getDescription(), "Book description should remain unchanged");
        assertEquals("Initial Genre", initializedBook.getGenre(), "Book genre should remain unchanged");
        assertEquals("Initial Cover", initializedBook.getCoverUrl(), "Book cover URL should remain unchanged");

        assertSame(testBook, initializedBook, "The initializare method should return the same Book object");
    }
}
