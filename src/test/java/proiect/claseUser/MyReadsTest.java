package proiect.claseUser;

import org.junit.jupiter.api.*;
import java.sql.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the MyReads class.
 * This class tests the methods of the MyReads class.
 */
class MyReadsTest {

    private MyReads myReads;
    private Connection connection;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/mydb";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "simone";
    private static final int TEST_USER_ID = 999;
    private static final String TEST_BOOK_COVER = "test_cover.jpg";
    private static final String NON_EXISTENT_USER_ID = "9999";
    private static final String NON_EXISTENT_BOOK_COVER = "non_existent_cover.jpg";

    /**
     * Sets up the test environment before each test.
     * Creates a database connection and initializes the MyReads instance.
     *
     * @throws SQLException if a database access error occurs
     */
    @BeforeEach
    void setUp() throws SQLException {
        System.setOut(new PrintStream(outContent));
        connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        myReads = new MyReads(TEST_USER_ID, TEST_BOOK_COVER);
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
        System.setOut(originalOut);
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
        removeBookFromMyReads();
    }

    /**
     * Ensures that a test user exists in the database.
     *
     * @throws SQLException if a database access error occurs
     */
    private void ensureTestUserExists() throws SQLException {
        String checkQuery = "SELECT COUNT(*) FROM user WHERE idUser = ?";
        try (PreparedStatement statement = connection.prepareStatement(checkQuery)) {
            statement.setInt(1, TEST_USER_ID);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            int count = resultSet.getInt(1);

            if (count == 0) {
                String insertQuery = "INSERT INTO user (idUser, userName, password, librarian) " +
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
                                    "VALUES (?, 'Test Book Title', 'Test Author', 'Test Description', 'Test Genre', 1, ?)";
                try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
                    insertStatement.setInt(1, 999);
                    insertStatement.setString(2, TEST_BOOK_COVER);
                    insertStatement.executeUpdate();
                }
            }
        }
    }

    /**
     * Removes the test book from the myreads table for the test user.
     * This ensures that the addBook method can be tested properly.
     *
     * @throws SQLException if a database access error occurs
     */
    private void removeBookFromMyReads() throws SQLException {
        String deleteQuery = "DELETE FROM myreads WHERE user_idUser = ? AND coverCarte = ?";
        try (PreparedStatement statement = connection.prepareStatement(deleteQuery)) {
            statement.setInt(1, TEST_USER_ID);
            statement.setString(2, TEST_BOOK_COVER);
            statement.executeUpdate();
        }
    }

    /**
     * Tests the constructor of the MyReads class.
     * Verifies that it correctly initializes the user_idUser and URL fields.
     */
    @Test
    void testConstructor() throws Exception {
        java.lang.reflect.Field userIdField = MyReads.class.getDeclaredField("user_idUser");
        userIdField.setAccessible(true);
        assertEquals(TEST_USER_ID, userIdField.get(myReads), "user_idUser should be set correctly");

        java.lang.reflect.Field urlField = MyReads.class.getDeclaredField("URL");
        urlField.setAccessible(true);
        assertEquals(TEST_BOOK_COVER, urlField.get(myReads), "URL should be set correctly");
    }

    /**
     * Tests the addBook method with a valid user and book.
     * Verifies that the book is added to the user's myreads list.
     */
    @Test
    void testAddBookSuccess() throws SQLException {
        outContent.reset();
        myReads.addBook(myReads);

        String output = outContent.toString();
        assertTrue(output.contains("Carte adaugata in MyReads."), "Should display success message");

        String checkQuery = "SELECT COUNT(*) FROM myreads WHERE user_idUser = ? AND coverCarte = ?";
        try (PreparedStatement statement = connection.prepareStatement(checkQuery)) {
            statement.setInt(1, TEST_USER_ID);
            statement.setString(2, TEST_BOOK_COVER);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            int count = resultSet.getInt(1);
            assertEquals(1, count, "Book should be added to myreads table");
        }
    }

    /**
     * Tests the addBook method when the book is already in the user's myreads list.
     * Verifies that the book is not added again.
     */
    @Test
    void testAddBookAlreadyExists() throws SQLException {
        myReads.addBook(myReads);
        outContent.reset();
        myReads.addBook(myReads);

        String checkQuery = "SELECT COUNT(*) FROM myreads WHERE user_idUser = ? AND coverCarte = ?";
        try (PreparedStatement statement = connection.prepareStatement(checkQuery)) {
            statement.setInt(1, TEST_USER_ID);
            statement.setString(2, TEST_BOOK_COVER);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            int count = resultSet.getInt(1);
            assertEquals(1, count, "Book should not be added again");
        }
    }

    /**
     * Tests the addBook method with a non-existent user.
     * Verifies that a RuntimeException is thrown.
     */
    @Test
    void testAddBookNonExistentUser() {
        MyReads invalidUserReads = new MyReads(Integer.parseInt(NON_EXISTENT_USER_ID), TEST_BOOK_COVER);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            invalidUserReads.addBook(invalidUserReads);
        });

        String expectedMessage = "Userul cu ID " + NON_EXISTENT_USER_ID + " nu exista.";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage), "Exception message should indicate non-existent user");
    }

    /**
     * Tests the addBook method with a non-existent book.
     * Verifies that a RuntimeException is thrown.
     */
    @Test
    void testAddBookNonExistentBook() {
        MyReads invalidBookReads = new MyReads(TEST_USER_ID, NON_EXISTENT_BOOK_COVER);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            invalidBookReads.addBook(invalidBookReads);
        });

        String expectedMessage = "Cartea cu ID " + NON_EXISTENT_BOOK_COVER + " nu exista.";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage), "Exception message should indicate non-existent book");
    }
}
