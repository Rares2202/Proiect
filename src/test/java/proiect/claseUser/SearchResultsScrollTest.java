package proiect.claseUser;

import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the SearchResultsScroll class.
 * 
 * This class uses TestFX to test the JavaFX components in the SearchResultsScroll class.
 * It tests the following methods:
 * 
 * 1. Constructor - Verify it initializes the books field correctly
 * 2. initialize() - Verify it sets up the ScrollPane correctly
 * 3. generateGridContent() - Verify it populates the grid correctly
 * 4. setOnCoverClick() - Verify it sets the handler correctly
 */
@ExtendWith(ApplicationExtension.class)
class SearchResultsScrollTest {

    private SearchResultsScroll searchResultsScrollWithBooks;
    private SearchResultsScroll searchResultsScrollWithoutBooks;
    private List<Book> testBooks;
    private Stage stage;

    /**
     * Sets up the test environment before each test.
     * This method is called by TestFX before each test method.
     *
     * @param stage the primary stage for the test
     */
    @Start
    private void start(Stage stage) {
        this.stage = stage;
        
        // Create test books
        testBooks = new ArrayList<>();
        testBooks.add(new Book(1, "Test Book 1", "Test Author 1", "Test Description 1", "Test Genre 1", "test_cover_1.jpg"));
        testBooks.add(new Book(2, "Test Book 2", "Test Author 2", "Test Description 2", "Test Genre 2", "test_cover_2.jpg"));
        
        // Create SearchResultsScroll instances
        searchResultsScrollWithBooks = new SearchResultsScroll(testBooks);
        searchResultsScrollWithoutBooks = new SearchResultsScroll(new ArrayList<>());
        
        stage.setScene(new javafx.scene.Scene(searchResultsScrollWithBooks, 800, 600));
        stage.show();
    }

    /**
     * Verifies that the SearchResultsScroll class exists.
     * This is a simple test that doesn't require JavaFX initialization.
     */
    @Test
    void testClassExists() {
        try {
            assertDoesNotThrow(() -> {
                Class.forName("proiect.claseUser.SearchResultsScroll", false, this.getClass().getClassLoader());
            }, "SearchResultsScroll class should exist");

            assertTrue(true, "SearchResultsScroll class exists");
        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }

    /**
     * Tests the constructor of the SearchResultsScroll class.
     * Verifies that it correctly initializes the books field.
     */
    @Test
    void testConstructor() throws Exception {
        Field booksField = SearchResultsScroll.class.getDeclaredField("books");
        booksField.setAccessible(true);
        
        assertSame(testBooks, booksField.get(searchResultsScrollWithBooks), "books field should be initialized correctly");
        
        List<?> emptyBooks = (List<?>) booksField.get(searchResultsScrollWithoutBooks);
        assertTrue(emptyBooks.isEmpty(), "books field should be empty for searchResultsScrollWithoutBooks");
    }

    /**
     * Tests the initialize method of the SearchResultsScroll class.
     * Verifies that it correctly sets up the ScrollPane.
     */
    @Test
    void testInitialize() {
        // Test horizontal scrollbar policy
        assertEquals(ScrollPane.ScrollBarPolicy.NEVER, searchResultsScrollWithBooks.getHbarPolicy(), 
                "Horizontal scrollbar policy should be NEVER");
        
        // Test vertical scrollbar policy
        assertEquals(ScrollPane.ScrollBarPolicy.AS_NEEDED, searchResultsScrollWithBooks.getVbarPolicy(), 
                "Vertical scrollbar policy should be AS_NEEDED");
        
        // Test fitToWidth property
        assertTrue(searchResultsScrollWithBooks.isFitToWidth(), "fitToWidth should be true");
        
        // Test content
        assertNotNull(searchResultsScrollWithBooks.getContent(), "Content should not be null");
        assertTrue(searchResultsScrollWithBooks.getContent() instanceof GridPane, "Content should be a GridPane");
        
        // Test GridPane properties
        GridPane gridPane = (GridPane) searchResultsScrollWithBooks.getContent();
        assertEquals(10, gridPane.getHgap(), "GridPane horizontal gap should be 10");
        assertEquals(10, gridPane.getVgap(), "GridPane vertical gap should be 10");
        assertEquals("-fx-padding: 10px;", gridPane.getStyle(), "GridPane style should be set correctly");
    }

    /**
     * Tests the generateGridContent method of the SearchResultsScroll class with books.
     * Verifies that it correctly populates the grid with book panes.
     */
    @Test
    void testGenerateGridContentWithBooks() {
        GridPane gridPane = (GridPane) searchResultsScrollWithBooks.getContent();
        
        // The grid should have children (one for each book)
        assertEquals(testBooks.size(), gridPane.getChildren().size(), 
                "GridPane should have one child for each book");
        
        // Each child should be a PaneCarte
        for (int i = 0; i < gridPane.getChildren().size(); i++) {
            assertTrue(gridPane.getChildren().get(i) instanceof PaneCarte, 
                    "Child " + i + " should be a PaneCarte");
        }
    }

    /**
     * Tests the generateGridContent method of the SearchResultsScroll class without books.
     * Verifies that it correctly displays a "Not found!" message.
     */
    @Test
    void testGenerateGridContentWithoutBooks() {
        GridPane gridPane = (GridPane) searchResultsScrollWithoutBooks.getContent();
        
        // The grid should have one child (the "Not found!" label)
        assertEquals(1, gridPane.getChildren().size(), "GridPane should have one child");
        
        // The child should be a Label with the text "Not found!"
        assertTrue(gridPane.getChildren().get(0) instanceof Label, "Child should be a Label");
        Label label = (Label) gridPane.getChildren().get(0);
        assertEquals("Not found!", label.getText(), "Label text should be 'Not found!'");
        assertEquals("-fx-font-size: 16px; -fx-text-fill: #666;", label.getStyle(), 
                "Label style should be set correctly");
    }

    /**
     * Tests the setOnCoverClick method of the SearchResultsScroll class.
     * Verifies that it correctly sets the handler.
     */
    @Test
    void testSetOnCoverClick() throws Exception {
        // Create a test handler
        Consumer<String> testHandler = url -> System.out.println("Test handler: " + url);
        
        // Set the handler
        searchResultsScrollWithBooks.setOnCoverClick(testHandler);
        
        // Access the private onCoverClickHandler field
        Field handlerField = SearchResultsScroll.class.getDeclaredField("onCoverClickHandler");
        handlerField.setAccessible(true);
        
        // Verify that the handler was set correctly
        assertSame(testHandler, handlerField.get(searchResultsScrollWithBooks), 
                "Handler should be set correctly");
    }
}