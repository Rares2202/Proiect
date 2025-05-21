package proiect.claseUser;

import org.junit.jupiter.api.*;
import java.sql.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the DBComands class.
 * This class tests the database interaction methods in the DBComands class.
 */
class DBCommandsTest {

    private DBComands dbCommands;
    private Connection connection;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/mydb";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root";
    private static final int TEST_USER_ID = 999;
    private static final String TEST_BOOK_TITLE = "Test Book Title";
    private static final String TEST_BOOK_AUTHOR = "Test Author";
    private static final String TEST_BOOK_DESCRIPTION = "Test Description";
    private static final String TEST_BOOK_GENRE = "Test Genre";
    private static final String TEST_BOOK_COVER = "test_cover.jpg";

    /**
     * Sets up the test environment before each test.
     * Creates a database connection and initializes the DBComands instance.
     *
     * @throws SQLException if a database access error occurs
     */
    @BeforeEach
    void setUp() throws SQLException {
        dbCommands = new DBComands();
        connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
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
        ensureTestUserExists();
        ensureTestBookExists();
        ensureTestBookInMyReads();
    }

    /**
     * Ensures that a test user exists in the database.
     *
     * @throws SQLException if a database access error occurs
     */
    private void ensureTestUserExists() throws SQLException {
        String checkQuery = "SELECT COUNT(*) FROM User WHERE idUser = ?";
        try (PreparedStatement statement = connection.prepareStatement(checkQuery)) {
            statement.setInt(1, TEST_USER_ID);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            int count = resultSet.getInt(1);

            if (count == 0) {
                String insertQuery = "INSERT INTO User (idUser, userName, password, librarian) " +
                                    "VALUES (?, 'testuser', 'testpass', 0)";
                try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
                    insertStatement.setInt(1, TEST_USER_ID);
                    insertStatement.executeUpdate();
                }
            }
        }
    }

    /**
     * Ensures that a test book exists in the database.
     *
     * @throws SQLException if a database access error occurs
     */
    private void ensureTestBookExists() throws SQLException {
        String checkQuery = "SELECT COUNT(*) FROM carte WHERE coverCarte = ?";
        try (PreparedStatement statement = connection.prepareStatement(checkQuery)) {
            statement.setString(1, TEST_BOOK_COVER);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            int count = resultSet.getInt(1);

            if (count == 0) {
                String insertQuery = "INSERT INTO carte (idCarte, titluCarti, autorCarte, descriere, genCarte, numarCarte, coverCarte) " +
                                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
                    insertStatement.setInt(1, 999);
                    insertStatement.setString(2, TEST_BOOK_TITLE);
                    insertStatement.setString(3, TEST_BOOK_AUTHOR);
                    insertStatement.setString(4, TEST_BOOK_DESCRIPTION);
                    insertStatement.setString(5, TEST_BOOK_GENRE);
                    insertStatement.setInt(6, 1);
                    insertStatement.setString(7, TEST_BOOK_COVER);
                    insertStatement.executeUpdate();
                }
            }
        }
    }

    /**
     * Ensures that the test book is in the myreads table for the test user.
     *
     * @throws SQLException if a database access error occurs
     */
    private void ensureTestBookInMyReads() throws SQLException {
        String checkQuery = "SELECT COUNT(*) FROM myreads WHERE user_idUser = ? AND coverCarte = ?";
        try (PreparedStatement statement = connection.prepareStatement(checkQuery)) {
            statement.setInt(1, TEST_USER_ID);
            statement.setString(2, TEST_BOOK_COVER);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            int count = resultSet.getInt(1);

            if (count == 0) {
                String insertQuery = "INSERT INTO myreads (user_idUser, coverCarte) VALUES (?, ?)";
                try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
                    insertStatement.setInt(1, TEST_USER_ID);
                    insertStatement.setString(2, TEST_BOOK_COVER);
                    insertStatement.executeUpdate();
                }
            }
        }
    }

    /**
     * Tests the SELECT_COVER_FROM_MYREADS method.
     * Verifies that it correctly retrieves cover URLs for a user.
     */
    @Test
    void testSelectCoverFromMyReads() {
        List<String> covers = dbCommands.SELECT_COVER_FROM_MYREADS(DB_URL, DB_USER, DB_PASSWORD, TEST_USER_ID);

        assertFalse(covers.isEmpty(), "Should return at least one cover URL");
        assertTrue(covers.contains(TEST_BOOK_COVER), "Should contain the test book cover URL");
    }

    /**
     * Tests the REZULTATE method.
     * Verifies that it correctly searches for books by title.
     */
    @Test
    void testRezultateByTitle() {
        String searchTitle = "The";

        List<Book> books = dbCommands.REZULTATE(DB_URL, DB_USER, DB_PASSWORD, searchTitle, "");

        assertFalse(books.isEmpty(), "Should return at least one book");

        System.out.println("[DEBUG_LOG] Searching for books with title containing: " + searchTitle);
        System.out.println("[DEBUG_LOG] Found " + books.size() + " books");

        boolean foundMatchingBook = false;
        for (Book book : books) {
            System.out.println("[DEBUG_LOG] Book: " + book.getTitle() + " by " + book.getAuthor());
            if (book.getTitle().contains(searchTitle)) {
                foundMatchingBook = true;
                break;
            }
        }

        assertTrue(foundMatchingBook, "Should find at least one book with title containing: " + searchTitle);
    }

    /**
     * Tests the REZULTATE method.
     * Verifies that it correctly searches for books by author.
     */
    @Test
    void testRezultateByAuthor() {
        String searchAuthor = "Tolkien";

        List<Book> books = dbCommands.REZULTATE(DB_URL, DB_USER, DB_PASSWORD, "", searchAuthor);

        assertFalse(books.isEmpty(), "Should return at least one book");

        System.out.println("[DEBUG_LOG] Searching for books with author containing: " + searchAuthor);
        System.out.println("[DEBUG_LOG] Found " + books.size() + " books");

        boolean foundMatchingBook = false;
        for (Book book : books) {
            System.out.println("[DEBUG_LOG] Book: " + book.getTitle() + " by " + book.getAuthor());
            if (book.getAuthor().contains(searchAuthor)) {
                foundMatchingBook = true;
                break;
            }
        }

        assertTrue(foundMatchingBook, "Should find at least one book with author containing: " + searchAuthor);
    }

    /**
     * Tests the REZULTATE method with non-existent search terms.
     * Verifies that it correctly handles the case when no books are found.
     */
    @Test
    void testRezultateWithNoResults() {
        List<Book> books = dbCommands.REZULTATE(DB_URL, DB_USER, DB_PASSWORD, "NonExistentBookTitle", "NonExistentAuthor");

        assertTrue(books.isEmpty(), "Should return an empty list when no books are found");
    }
}
