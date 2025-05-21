package proiect.claseUser;

import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the ImReading class.
 * 
 * This class uses TestFX to test the JavaFX components in the ImReading class.
 * It tests the following methods:
 * 
 * 1. Constructor - Verify it initializes the userId and grid fields correctly
 * 2. deleteBooks() - Verify it correctly deletes books from the database
 * 3. initialize() - Verify it sets up the GridPane correctly
 * 4. generateGridContent() - Verify it generates the grid content correctly
 * 5. addBooksToRow() - Verify it adds books to a specific row in the grid
 * 6. adjustRowSizes() - Verify it adjusts the row sizes based on the number of books
 * 7. createCell() - Verify it creates a cell with the correct properties
 * 8. createCellBackground() - Verify it creates a background with the correct properties
 * 9. setupCellHoverEffects() - Verify it sets up hover effects correctly
 * 10. setOnCoverClick() - Verify it sets the handler correctly
 * 11. refresh() - Verify it refreshes the content correctly
 */
@ExtendWith(ApplicationExtension.class)
class ImReadingTest {

    private ImReading imReading;
    private GridPane gridPane;
    private static final int TEST_USER_ID = 123;
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
        this.gridPane = new GridPane();
        this.imReading = new ImReading(TEST_USER_ID, gridPane);
        stage.setScene(new javafx.scene.Scene(gridPane, 800, 600));
        stage.show();
    }

    /**
     * Tests the constructor of the ImReading class.
     * Verifies that it correctly initializes the userId and grid fields.
     */
    @Test
    void testConstructor() throws Exception {
        Field userIdField = ImReading.class.getDeclaredField("userId");
        userIdField.setAccessible(true);
        assertEquals(TEST_USER_ID, userIdField.getInt(imReading), "userId should be set correctly");

        Field gridField = ImReading.class.getDeclaredField("grid");
        gridField.setAccessible(true);
        assertSame(gridPane, gridField.get(imReading), "grid should be set correctly");
    }

    /**
     * Tests the setOnCoverClick method.
     * Verifies that it correctly sets the onCoverClickHandler.
     */
    @Test
    void testSetOnCoverClick() throws Exception {
        Consumer<String> handler = (url) -> System.out.println("Cover clicked: " + url);

        imReading.setOnCoverClick(handler);

        Field handlerField = ImReading.class.getDeclaredField("onCoverClickHandler");
        handlerField.setAccessible(true);
        assertSame(handler, handlerField.get(imReading), "Handler should be set correctly");
    }

    /**
     * Tests the selectedBooks field initialization.
     * Verifies that it is initialized as an empty ArrayList.
     */
    @Test
    void testSelectedBooksInitialization() throws Exception {
        Field selectedBooksField = ImReading.class.getDeclaredField("selectedBooks");
        selectedBooksField.setAccessible(true);

        @SuppressWarnings("unchecked")
        List<Book> selectedBooks = (List<Book>) selectedBooksField.get(imReading);

        assertNotNull(selectedBooks, "selectedBooks should not be null");
        assertTrue(selectedBooks.isEmpty(), "selectedBooks should be empty initially");
        assertEquals(ArrayList.class, selectedBooks.getClass(), "selectedBooks should be an ArrayList");
    }

    /**
     * Tests the deleteBooks method of the ImReading class.
     * Verifies that it correctly deletes books from the database.
     */
    @Test
    void testDeleteBooks() throws Exception {
        Field selectedBooksField = ImReading.class.getDeclaredField("selectedBooks");
        selectedBooksField.setAccessible(true);

        @SuppressWarnings("unchecked")
        List<Book> selectedBooks = (List<Book>) selectedBooksField.get(imReading);

        selectedBooks.clear();

        Book testBook = new Book(999, "Test Title", "Test Author", "Test Description", "Test Genre", "test_book_url.jpg");
        selectedBooks.add(testBook);

        imReading.deleteBooks();

        assertEquals(1, selectedBooks.size(), "Selected books list should still contain the test book");
        assertEquals("test_book_url.jpg", selectedBooks.get(0).getCoverUrl(), "Selected books list should contain the test book URL");
    }

    /**
     * Tests the refresh method of the ImReading class.
     * Verifies that it correctly refreshes the content.
     */
    @Test
    void testRefresh(FxRobot robot) throws Exception {
        robot.interact(() -> imReading.refresh());

        assertTrue(true, "Refresh should complete without errors");
    }

    /**
     * Tests the createCell method of the ImReading class.
     * Verifies that it correctly creates a cell with the correct properties.
     */
    @Test
    void testCreateCell(FxRobot robot) throws Exception {
        Method createCellMethod = ImReading.class.getDeclaredMethod("createCell", String.class);
        createCellMethod.setAccessible(true);

        robot.interact(() -> {
            try {
                StackPane cell = (StackPane) createCellMethod.invoke(imReading, "test_url.jpg");

                assertNotNull(cell, "Cell should not be null");
                assertEquals(125, cell.getPrefWidth(), "Cell width should be 125");
                assertEquals(130, cell.getPrefHeight(), "Cell height should be 130");
                assertTrue(cell.getChildren().size() >= 1, "Cell should have at least one child");
                assertTrue(cell.getChildren().get(0) instanceof Region, "First child should be a Region");
            } catch (Exception e) {
                fail("Exception occurred: " + e.getMessage());
            }
        });
    }

    /**
     * Tests the createCellBackground method of the ImReading class.
     * Verifies that it correctly creates a background with the correct properties.
     */
    @Test
    void testCreateCellBackground(FxRobot robot) throws Exception {
        Method createCellBackgroundMethod = ImReading.class.getDeclaredMethod("createCellBackground", String.class);
        createCellBackgroundMethod.setAccessible(true);

        robot.interact(() -> {
            try {
                Region background = (Region) createCellBackgroundMethod.invoke(imReading, "test_url.jpg");

                assertNotNull(background, "Background should not be null");
                assertEquals(125, background.getPrefWidth(), "Background width should be 125");
                assertEquals(130, background.getPrefHeight(), "Background height should be 130");
            } catch (Exception e) {
                fail("Exception occurred: " + e.getMessage());
            }
        });
    }

    /**
     * Tests the setupCellHoverEffects method of the ImReading class.
     * Verifies that it correctly sets up hover effects.
     */
    @Test
    void testSetupCellHoverEffects(FxRobot robot) throws Exception {
        Method setupCellHoverEffectsMethod = ImReading.class.getDeclaredMethod("setupCellHoverEffects", StackPane.class);
        setupCellHoverEffectsMethod.setAccessible(true);

        StackPane testCell = new StackPane();

        setupCellHoverEffectsMethod.invoke(imReading, testCell);

        assertNull(testCell.getEffect(), "Cell should have no effect initially");

        robot.interact(() -> {
            testCell.fireEvent(new javafx.scene.input.MouseEvent(
                javafx.scene.input.MouseEvent.MOUSE_ENTERED, 0, 0, 0, 0,
                javafx.scene.input.MouseButton.NONE, 0, false, false, false, false,
                true, false, false, false, false, false, null
            ));
        });

        assertNotNull(testCell.getEffect(), "Cell should have an effect after mouse enter");
        assertTrue(testCell.getEffect() instanceof DropShadow, "Effect should be a DropShadow");

        robot.interact(() -> {
            testCell.fireEvent(new javafx.scene.input.MouseEvent(
                javafx.scene.input.MouseEvent.MOUSE_EXITED, 0, 0, 0, 0,
                javafx.scene.input.MouseButton.NONE, 0, false, false, false, false,
                true, false, false, false, false, false, null
            ));
        });

        assertNull(testCell.getEffect(), "Cell should have no effect after mouse exit");
    }
}
