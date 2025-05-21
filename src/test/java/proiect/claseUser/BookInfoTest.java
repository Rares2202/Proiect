package proiect.claseUser;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the BookInfo class.
 * This class tests all methods of the BookInfo class.
 */
class BookInfoTest {

    private BookInfo bookInfo;
    private Book testBook;
    private static final String TEST_STATUS = "available";

    private static final int TEST_ID = 123;
    private static final String TEST_TITLE = "Test Book Title";
    private static final String TEST_AUTHOR = "Test Author";
    private static final String TEST_DESCRIPTION = "This is a test description";
    private static final String TEST_GENRE = "Test Genre";
    private static final String TEST_COVER_URL = "test_cover.jpg";

    /**
     * Sets up the test environment before each test.
     * Creates a test book and a test bookInfo with predefined values.
     */
    @BeforeEach
    void setUp() {
        testBook = new Book(TEST_ID, TEST_TITLE, TEST_AUTHOR, TEST_DESCRIPTION, TEST_GENRE, TEST_COVER_URL);
        bookInfo = new BookInfo(testBook, TEST_STATUS);
    }

    /**
     * Tests the constructor of the BookInfo class.
     * Verifies that all fields are properly initialized with the provided values.
     */
    @Test
    void testConstructor() {
        assertEquals(TEST_STATUS, bookInfo.getStatus(), "Status should be initialized correctly");
        assertSame(testBook, bookInfo.getBook(), "Book should be initialized correctly");
    }

    /**
     * Tests the getStatus method of the BookInfo class.
     * Verifies that it returns the correct status value.
     */
    @Test
    void testGetStatus() {
        assertEquals(TEST_STATUS, bookInfo.getStatus(), "getStatus should return the correct status");
    }

    /**
     * Tests the getBook method of the BookInfo class.
     * Verifies that it returns the correct Book instance.
     */
    @Test
    void testGetBook() {
        assertSame(testBook, bookInfo.getBook(), "getBook should return the correct Book instance");

        Book returnedBook = bookInfo.getBook();
        assertEquals(TEST_ID, returnedBook.getId(), "Book ID should match");
        assertEquals(TEST_TITLE, returnedBook.getTitle(), "Book title should match");
        assertEquals(TEST_AUTHOR, returnedBook.getAuthor(), "Book author should match");
        assertEquals(TEST_DESCRIPTION, returnedBook.getDescription(), "Book description should match");
        assertEquals(TEST_GENRE, returnedBook.getGenre(), "Book genre should match");
        assertEquals(TEST_COVER_URL, returnedBook.getCoverUrl(), "Book cover URL should match");
    }
}
