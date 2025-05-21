package proiect.claseUser;

import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the PaneCarte class.
 * 
 * This class uses TestFX to test the JavaFX components in the PaneCarte class.
 * It tests the following methods:
 * 
 * 1. Constructor - Verify it initializes the book, coverImageView, and textContainer fields correctly
 * 2. initialize() - Verify it sets up the layout properties correctly
 * 3. setupCoverImage() - Verify it configures the ImageView with correct dimensions
 * 4. setupTextContainer() - Verify it creates and configures the labels correctly
 * 5. loadBook() - Verify it populates the UI components with book data correctly
 */
@ExtendWith(ApplicationExtension.class)
class PaneCarteTest {

    private PaneCarte paneCarteWithBook;
    private PaneCarte paneCarteWithNullBook;
    private Book testBook;
    private Stage stage;

    private static final int TEST_ID = 123;
    private static final String TEST_TITLE = "Test Book Title";
    private static final String TEST_AUTHOR = "Test Author";
    private static final String TEST_DESCRIPTION = "This is a test description";
    private static final String TEST_GENRE = "Test Genre";
    private static final String TEST_COVER_URL = "test_cover.jpg";

    /**
     * Sets up the test environment before each test.
     * This method is called by TestFX before each test method.
     *
     * @param stage the primary stage for the test
     */
    @Start
    private void start(Stage stage) {
        this.stage = stage;
        testBook = new Book(TEST_ID, TEST_TITLE, TEST_AUTHOR, TEST_DESCRIPTION, TEST_GENRE, TEST_COVER_URL);
        paneCarteWithBook = new PaneCarte(testBook);
        paneCarteWithNullBook = new PaneCarte(null);

        stage.setScene(new javafx.scene.Scene(paneCarteWithBook, 800, 600));
        stage.show();
    }

    /**
     * Tests the constructor of the PaneCarte class.
     * Verifies that it correctly initializes the book, coverImageView, and textContainer fields.
     */
    @Test
    void testConstructor() throws Exception {
        Field bookField = PaneCarte.class.getDeclaredField("book");
        bookField.setAccessible(true);
        assertSame(testBook, bookField.get(paneCarteWithBook), "book field should be initialized correctly");

        Field coverImageViewField = PaneCarte.class.getDeclaredField("coverImageView");
        coverImageViewField.setAccessible(true);
        Object coverImageView = coverImageViewField.get(paneCarteWithBook);
        assertNotNull(coverImageView, "coverImageView should not be null");
        assertTrue(coverImageView instanceof ImageView, "coverImageView should be an ImageView");

        Field textContainerField = PaneCarte.class.getDeclaredField("textContainer");
        textContainerField.setAccessible(true);
        Object textContainer = textContainerField.get(paneCarteWithBook);
        assertNotNull(textContainer, "textContainer should not be null");
        assertTrue(textContainer instanceof VBox, "textContainer should be a VBox");
    }

    /**
     * Tests the layout properties of the PaneCarte instance.
     * Verifies that the initialize method correctly sets up the layout properties.
     */
    @Test
    void testLayoutProperties() {
        assertEquals(javafx.geometry.Pos.CENTER_LEFT, paneCarteWithBook.getAlignment(), "Alignment should be CENTER_LEFT");
        assertEquals(20, paneCarteWithBook.getSpacing(), "Spacing should be 20");
        assertEquals(new javafx.geometry.Insets(15), paneCarteWithBook.getPadding(), "Padding should be 15 on all sides");
        assertEquals("-fx-background-color: #f9f9f9; -fx-border-color: #ddd; -fx-border-width: 1px;", paneCarteWithBook.getStyle(), "Style should be set correctly");
        assertEquals(180, paneCarteWithBook.getPrefHeight(), "Preferred height should be 180");
    }

    /**
     * Tests the setupCoverImage method of the PaneCarte class.
     * Verifies that it correctly configures the ImageView with the right dimensions and properties.
     */
    @Test
    void testSetupCoverImage() throws Exception {
        Field coverImageViewField = PaneCarte.class.getDeclaredField("coverImageView");
        coverImageViewField.setAccessible(true);
        ImageView coverImageView = (ImageView) coverImageViewField.get(paneCarteWithBook);

        assertEquals(120, coverImageView.getFitWidth(), "Cover image width should be 120");
        assertEquals(180, coverImageView.getFitHeight(), "Cover image height should be 180");
        assertFalse(coverImageView.isPreserveRatio(), "Preserve ratio should be false");
        // The style could be either the initial style or the fallback style if image loading fails
        String style = coverImageView.getStyle();
        assertTrue(
            style.equals("-fx-background-color: #eee;") || style.equals("-fx-background-color: #e0e0e0;"),
            "Cover image style should be either the initial style or the fallback style"
        );
    }

    /**
     * Tests the setupTextContainer method of the PaneCarte class.
     * Verifies that it correctly configures the VBox and adds the right number of labels.
     */
    @Test
    void testSetupTextContainer() throws Exception {
        Field textContainerField = PaneCarte.class.getDeclaredField("textContainer");
        textContainerField.setAccessible(true);
        VBox textContainer = (VBox) textContainerField.get(paneCarteWithBook);

        assertEquals(8, textContainer.getSpacing(), "Text container spacing should be 8");
        assertEquals(javafx.geometry.Pos.CENTER_LEFT, textContainer.getAlignment(), "Text container alignment should be CENTER_LEFT");

        assertEquals(3, textContainer.getChildren().size(), "Text container should have 3 children");
        assertTrue(textContainer.getChildren().get(0) instanceof Label, "First child should be a Label");
        assertTrue(textContainer.getChildren().get(1) instanceof Label, "Second child should be a Label");
        assertTrue(textContainer.getChildren().get(2) instanceof Label, "Third child should be a Label");

        Label titleLabel = (Label) textContainer.getChildren().get(0);
        assertEquals(18, titleLabel.getFont().getSize(), "Title label font size should be 18");
        assertEquals("-fx-font-weight: bold; -fx-text-fill: #333;", titleLabel.getStyle(), "Title label style should be set correctly");

        Label authorLabel = (Label) textContainer.getChildren().get(1);
        assertEquals(14, authorLabel.getFont().getSize(), "Author label font size should be 14");

        Label genreLabel = (Label) textContainer.getChildren().get(2);
        assertEquals(12, genreLabel.getFont().getSize(), "Genre label font size should be 12");
        assertEquals("-fx-text-fill: #666;", genreLabel.getStyle(), "Genre label style should be set correctly");
    }

    /**
     * Tests the loadBook method of the PaneCarte class with a valid book.
     * Verifies that it correctly populates the UI components with book data.
     */
    @Test
    void testLoadBookWithValidBook() throws Exception {
        Field textContainerField = PaneCarte.class.getDeclaredField("textContainer");
        textContainerField.setAccessible(true);
        VBox textContainer = (VBox) textContainerField.get(paneCarteWithBook);

        Label titleLabel = (Label) textContainer.getChildren().get(0);
        assertEquals(TEST_TITLE, titleLabel.getText(), "Title label should display the book title");

        Label authorLabel = (Label) textContainer.getChildren().get(1);
        assertEquals("de " + TEST_AUTHOR, authorLabel.getText(), "Author label should display the book author");

        Label genreLabel = (Label) textContainer.getChildren().get(2);
        assertEquals(TEST_GENRE, genreLabel.getText(), "Genre label should display the book genre");
    }

    /**
     * Tests the loadBook method of the PaneCarte class with a null book.
     * Verifies that it handles null values correctly.
     */
    @Test
    void testLoadBookWithNullBook(FxRobot robot) {
        robot.interact(() -> {
            try {
                Field textContainerField = PaneCarte.class.getDeclaredField("textContainer");
                textContainerField.setAccessible(true);
                VBox textContainer = (VBox) textContainerField.get(paneCarteWithNullBook);

                // When book is null, the labels should not be populated
                // The method should not throw exceptions
                assertNotNull(textContainer, "Text container should not be null even with null book");
                assertEquals(3, textContainer.getChildren().size(), "Text container should still have 3 children");
            } catch (Exception e) {
                fail("Exception occurred: " + e.getMessage());
            }
        });
    }

    /**
     * Tests the loadBook method of the PaneCarte class with a book that has null fields.
     * Verifies that it handles null field values correctly.
     */
    @Test
    void testLoadBookWithNullFields(FxRobot robot) {
        robot.interact(() -> {
            try {
                // Create a book with null fields
                Book bookWithNullFields = new Book(TEST_ID, null, null, null, null, null);
                PaneCarte paneCarteWithNullFields = new PaneCarte(bookWithNullFields);

                Field textContainerField = PaneCarte.class.getDeclaredField("textContainer");
                textContainerField.setAccessible(true);
                VBox textContainer = (VBox) textContainerField.get(paneCarteWithNullFields);

                Label titleLabel = (Label) textContainer.getChildren().get(0);
                assertEquals("Titlu necunoscut", titleLabel.getText(), "Title label should display fallback text for null title");

                Label authorLabel = (Label) textContainer.getChildren().get(1);
                assertEquals("de Autor necunoscut", authorLabel.getText(), "Author label should display fallback text for null author");

                Label genreLabel = (Label) textContainer.getChildren().get(2);
                assertEquals("Gen necunoscut", genreLabel.getText(), "Genre label should display fallback text for null genre");
            } catch (Exception e) {
                fail("Exception occurred: " + e.getMessage());
            }
        });
    }
}
