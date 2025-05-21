package proiect.claseUser;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the Book class.
 * This class tests all methods of the Book class except for the initializare method,
 * which is already tested in TestInitializareBookData.
 */
class BookTest {

    private Book book;
    private static final int TEST_ID = 123;
    private static final String TEST_TITLE = "Test Book Title";
    private static final String TEST_AUTHOR = "Test Author";
    private static final String TEST_DESCRIPTION = "This is a test description";
    private static final String TEST_GENRE = "Test Genre";
    private static final String TEST_COVER_URL = "test_cover.jpg";

    /**
     * Sets up the test environment before each test.
     * Creates a test book with predefined values.
     */
    @BeforeEach
    void setUp() {
        book = new Book(TEST_ID, TEST_TITLE, TEST_AUTHOR, TEST_DESCRIPTION, TEST_GENRE, TEST_COVER_URL);
    }

    /**
     * Tests the constructor of the Book class.
     * Verifies that all fields are properly initialized with the provided values.
     */
    @Test
    void testConstructor() {
        assertEquals(TEST_ID, book.getId(), "ID should be initialized correctly");
        assertEquals(TEST_TITLE, book.getTitle(), "Title should be initialized correctly");
        assertEquals(TEST_AUTHOR, book.getAuthor(), "Author should be initialized correctly");
        assertEquals(TEST_DESCRIPTION, book.getDescription(), "Description should be initialized correctly");
        assertEquals(TEST_GENRE, book.getGenre(), "Genre should be initialized correctly");
        assertEquals(TEST_COVER_URL, book.getCoverUrl(), "Cover URL should be initialized correctly");
    }

    /**
     * Tests the getId method of the Book class.
     * Verifies that it returns the correct ID value.
     */
    @Test
    void testGetId() {
        assertEquals(TEST_ID, book.getId(), "getId should return the correct ID");
    }

    /**
     * Tests the getTitle method of the Book class.
     * Verifies that it returns the correct title value.
     */
    @Test
    void testGetTitle() {
        assertEquals(TEST_TITLE, book.getTitle(), "getTitle should return the correct title");
    }

    /**
     * Tests the getAuthor method of the Book class.
     * Verifies that it returns the correct author value.
     */
    @Test
    void testGetAuthor() {
        assertEquals(TEST_AUTHOR, book.getAuthor(), "getAuthor should return the correct author");
    }

    /**
     * Tests the getDescription method of the Book class.
     * Verifies that it returns the correct description value.
     */
    @Test
    void testGetDescription() {
        assertEquals(TEST_DESCRIPTION, book.getDescription(), "getDescription should return the correct description");
    }

    /**
     * Tests the getGenre method of the Book class.
     * Verifies that it returns the correct genre value.
     */
    @Test
    void testGetGenre() {
        assertEquals(TEST_GENRE, book.getGenre(), "getGenre should return the correct genre");
    }

    /**
     * Tests the getCoverUrl method of the Book class.
     * Verifies that it returns the correct cover URL value.
     */
    @Test
    void testGetCoverUrl() {
        assertEquals(TEST_COVER_URL, book.getCoverUrl(), "getCoverUrl should return the correct cover URL");
    }
}
