package proiect.claseUser;

import org.junit.jupiter.api.*;
import java.sql.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the Review class.
 * This class tests the methods of the Review class.
 */
class ReviewTest {

    private Review review;
    private Connection connection;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/mydb";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root";
    private static final String TEST_REVIEW_TEXT = "This is a test review";
    private static final int TEST_RATING = 5;
    private static final int TEST_USER_ID = 999;
    private static final int TEST_BOOK_ID = 999;
    private static final int NON_EXISTENT_USER_ID = 9999;
    private static final int NON_EXISTENT_BOOK_ID = 9999;

    /**
     * Sets up the test environment before each test.
     * Creates a database connection and initializes the Review instance.
     *
     * @throws SQLException if a database access error occurs
     */
    @BeforeEach
    void setUp() throws SQLException {
        System.setOut(new PrintStream(outContent));
        connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        review = new Review(TEST_REVIEW_TEXT, TEST_RATING, TEST_USER_ID, TEST_BOOK_ID);
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
        removeTestReview();
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
        String checkQuery = "SELECT COUNT(*) FROM carte WHERE idCarte = ?";
        try (PreparedStatement statement = connection.prepareStatement(checkQuery)) {
            statement.setInt(1, TEST_BOOK_ID);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            int count = resultSet.getInt(1);

            if (count == 0) {
                String insertQuery = "INSERT INTO carte (idCarte, titluCarti, autorCarte, descriere, genCarte, numarCarte, coverCarte) " +
                                    "VALUES (?, 'Test Book Title', 'Test Author', 'Test Description', 'Test Genre', 1, 'test_cover.jpg')";
                try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
                    insertStatement.setInt(1, TEST_BOOK_ID);
                    insertStatement.executeUpdate();
                }
            }
        }
    }

    /**
     * Removes any existing test review from the database.
     * This ensures that the test starts with a clean state.
     *
     * @throws SQLException if a database access error occurs
     */
    private void removeTestReview() throws SQLException {
        String deleteQuery = "DELETE FROM review WHERE User_idUser = ? AND Carte_idCarte = ?";
        try (PreparedStatement statement = connection.prepareStatement(deleteQuery)) {
            statement.setInt(1, TEST_USER_ID);
            statement.setInt(2, TEST_BOOK_ID);
            statement.executeUpdate();
        }
    }

    /**
     * Tests the constructor of the Review class.
     * Verifies that it correctly initializes all fields.
     */
    @Test
    void testConstructor() {
        assertEquals(TEST_REVIEW_TEXT, review.getReviewText(), "Review text should be initialized correctly");
        assertEquals(TEST_RATING, review.getReviewRating(), "Review rating should be initialized correctly");
        assertEquals(TEST_USER_ID, review.getUser_idUser(), "User ID should be initialized correctly");
    }

    /**
     * Tests the getReviewRating method of the Review class.
     * Verifies that it returns the correct rating value.
     */
    @Test
    void testGetReviewRating() {
        assertEquals(TEST_RATING, review.getReviewRating(), "getReviewRating should return the correct rating");
    }

    /**
     * Tests the getUser_idUser method of the Review class.
     * Verifies that it returns the correct user ID.
     */
    @Test
    void testGetUser_idUser() {
        assertEquals(TEST_USER_ID, review.getUser_idUser(), "getUser_idUser should return the correct user ID");
    }

    /**
     * Tests the getReviewText method of the Review class.
     * Verifies that it returns the correct review text.
     */
    @Test
    void testGetReviewText() {
        assertEquals(TEST_REVIEW_TEXT, review.getReviewText(), "getReviewText should return the correct review text");
    }

    /**
     * Tests the trimiteReview method with a valid review.
     * Verifies that the review is added to the database.
     */
    @Test
    void testTrimiteReviewSuccess() throws SQLException {
        outContent.reset();
        review.trimiteReview(review);

        String output = outContent.toString();
        assertTrue(output.contains("Review adaugat cu succes."), "Should display success message for adding review");

        String checkQuery = "SELECT COUNT(*) FROM review WHERE User_idUser = ? AND Carte_idCarte = ?";
        try (PreparedStatement statement = connection.prepareStatement(checkQuery)) {
            statement.setInt(1, TEST_USER_ID);
            statement.setInt(2, TEST_BOOK_ID);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            int count = resultSet.getInt(1);
            assertEquals(1, count, "Review should be added to the database");
        }
    }

    /**
     * Tests the trimiteReview method when updating an existing review.
     * Verifies that the review is updated in the database.
     */
    @Test
    void testTrimiteReviewUpdate() throws SQLException {
        // First, add a review
        review.trimiteReview(review);
        outContent.reset();

        // Then update it
        String updatedText = "Updated review text";
        int updatedRating = 4;
        Review updatedReview = new Review(updatedText, updatedRating, TEST_USER_ID, TEST_BOOK_ID);
        updatedReview.trimiteReview(updatedReview);

        String output = outContent.toString();
        assertTrue(output.contains("Review actualizat cu succes."), "Should display success message for updating review");

        // Verify the review was updated
        String checkQuery = "SELECT reviewText, reviewRating FROM review WHERE User_idUser = ? AND Carte_idCarte = ?";
        try (PreparedStatement statement = connection.prepareStatement(checkQuery)) {
            statement.setInt(1, TEST_USER_ID);
            statement.setInt(2, TEST_BOOK_ID);
            ResultSet resultSet = statement.executeQuery();
            assertTrue(resultSet.next(), "Review should exist in the database");
            assertEquals(updatedText, resultSet.getString("reviewText"), "Review text should be updated");
            assertEquals(updatedRating, resultSet.getInt("reviewRating"), "Review rating should be updated");
        }
    }

    /**
     * Tests the trimiteReview method with a non-existent user.
     * Verifies that a RuntimeException is thrown.
     */
    @Test
    void testTrimiteReviewNonExistentUser() {
        Review invalidUserReview = new Review(TEST_REVIEW_TEXT, TEST_RATING, NON_EXISTENT_USER_ID, TEST_BOOK_ID);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            invalidUserReview.trimiteReview(invalidUserReview);
        });

        String expectedMessage = "Userul cu ID " + NON_EXISTENT_USER_ID + " nu există.";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage), "Exception message should indicate non-existent user");
    }

    /**
     * Tests the trimiteReview method with a non-existent book.
     * Verifies that a RuntimeException is thrown.
     */
    @Test
    void testTrimiteReviewNonExistentBook() {
        Review invalidBookReview = new Review(TEST_REVIEW_TEXT, TEST_RATING, TEST_USER_ID, NON_EXISTENT_BOOK_ID);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            invalidBookReview.trimiteReview(invalidBookReview);
        });

        String expectedMessage = "Cartea cu ID " + NON_EXISTENT_BOOK_ID + " nu există.";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage), "Exception message should indicate non-existent book");
    }

    /**
     * Tests the existaReview method when a review exists.
     * Verifies that it returns true.
     */
    @Test
    void testExistaReviewExists() throws SQLException {
        // First, add a review
        review.trimiteReview(review);

        // Then check if it exists
        boolean exists = review.existaReview(connection, TEST_USER_ID, TEST_BOOK_ID);
        assertTrue(exists, "existaReview should return true when review exists");
    }

    /**
     * Tests the existaReview method when a review does not exist.
     * Verifies that it returns false.
     */
    @Test
    void testExistaReviewDoesNotExist() throws SQLException {
        // Ensure the review doesn't exist
        removeTestReview();

        // Check if it exists
        boolean exists = review.existaReview(connection, TEST_USER_ID, TEST_BOOK_ID);
        assertFalse(exists, "existaReview should return false when review does not exist");
    }

    /**
     * Tests the SELECT_USER_FROM_REVIEWS method with a valid user ID.
     * Verifies that it returns the correct username.
     */
    @Test
    void testSelectUserFromReviews() throws SQLException {
        String username = review.SELECT_USER_FROM_REVIEWS(DB_URL, DB_USER, DB_PASSWORD, TEST_USER_ID);
        assertEquals("testuser", username, "SELECT_USER_FROM_REVIEWS should return the correct username");
    }

    /**
     * Tests the SELECT_USER_FROM_REVIEWS method with a non-existent user ID.
     * Verifies that it returns an empty string.
     */
    @Test
    void testSelectUserFromReviewsNonExistentUser() throws SQLException {
        String username = review.SELECT_USER_FROM_REVIEWS(DB_URL, DB_USER, DB_PASSWORD, NON_EXISTENT_USER_ID);
        assertEquals(" ", username, "SELECT_USER_FROM_REVIEWS should return an empty string for non-existent user");
    }
}