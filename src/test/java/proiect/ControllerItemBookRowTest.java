package proiect;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the ControllerItemBookRow class.
 * 
 * This class tests the functionality of the ControllerItemBookRow class,
 * which manages a UI component representing a book row in a librarian application.
 */
@ExtendWith(ApplicationExtension.class)
class ControllerItemBookRowTest {

    private ControllerItemBookRow controller;
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
        this.controller = new ControllerItemBookRow();
        stage.show();
    }

    /**
     * Tests the initialize method of the ControllerItemBookRow class.
     * Verifies that it correctly initializes the UI components.
     */
    @Test
    void testInitialize() throws Exception {
        // Set up the necessary fields
        Field addBookField = ControllerItemBookRow.class.getDeclaredField("addBook");
        addBookField.setAccessible(true);
        Button addBook = new Button();
        addBookField.set(controller, addBook);

        Field removeBookField = ControllerItemBookRow.class.getDeclaredField("removeBook");
        removeBookField.setAccessible(true);
        Button removeBook = new Button();
        removeBookField.set(controller, removeBook);

        // Get the initialize method
        Method initializeMethod = ControllerItemBookRow.class.getDeclaredMethod("initialize");
        initializeMethod.setAccessible(true);

        // Call the method
        initializeMethod.invoke(controller);

        // Verify the initialization
        assertFalse(addBook.isVisible(), "Add book button should not be visible");
        assertFalse(removeBook.isVisible(), "Remove book button should not be visible");
    }

    /**
     * Tests the handleAddBook method of the ControllerItemBookRow class.
     * Verifies that it correctly updates the UI and calls the main controller.
     */
    // Custom subclass of ControllerLibrarian that overrides the initialize methods to do nothing
    private static class TestControllerLibrarian extends ControllerLibrarian {
        @Override
        public void initialize_increase_button(String bookId) {
            // Do nothing
        }

        @Override
        public void initialize_decrease_button(String bookId) {
            // Do nothing
        }
    }

    @Test
    void testHandleAddBook() throws Exception {
        // Set up the necessary fields
        Field mainControllerField = ControllerItemBookRow.class.getDeclaredField("mainController");
        mainControllerField.setAccessible(true);
        TestControllerLibrarian mainController = new TestControllerLibrarian();
        mainControllerField.set(controller, mainController);

        // Set up the title_book field in the main controller
        Field titleBookField = ControllerLibrarian.class.getDeclaredField("title_book");
        titleBookField.setAccessible(true);
        Label titleBook = new Label();
        titleBookField.set(mainController, titleBook);

        // Set up the menu_increase field in the main controller
        Field menuIncreaseField = ControllerLibrarian.class.getDeclaredField("menu_increase");
        menuIncreaseField.setAccessible(true);
        javafx.scene.layout.AnchorPane menuIncrease = new javafx.scene.layout.AnchorPane();
        menuIncreaseField.set(mainController, menuIncrease);

        // Set up the book fields in the controller
        controller.id = "123";
        controller.book_name = "Test Book";

        // Call the method
        final CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                controller.handleAddBook(new ActionEvent());
                latch.countDown();
            } catch (SQLException e) {
                fail("SQLException occurred: " + e.getMessage());
            }
        });
        latch.await(5, TimeUnit.SECONDS);

        // Verify the method behavior
        assertEquals("Test Book", titleBook.getText(), "Book title should be set correctly");
        assertTrue(menuIncrease.isVisible(), "Menu increase should be visible");
    }

    /**
     * Tests the handleRemoveBook method of the ControllerItemBookRow class.
     * Verifies that it correctly updates the UI and calls the main controller.
     */
    @Test
    void testHandleRemoveBook() throws Exception {
        // Set up the necessary fields
        Field mainControllerField = ControllerItemBookRow.class.getDeclaredField("mainController");
        mainControllerField.setAccessible(true);
        TestControllerLibrarian mainController = new TestControllerLibrarian();
        mainControllerField.set(controller, mainController);

        // Set up the title_book1 field in the main controller
        Field titleBook1Field = ControllerLibrarian.class.getDeclaredField("title_book1");
        titleBook1Field.setAccessible(true);
        Label titleBook1 = new Label();
        titleBook1Field.set(mainController, titleBook1);

        // Set up the menu_decrease field in the main controller
        Field menuDecreaseField = ControllerLibrarian.class.getDeclaredField("menu_decrease");
        menuDecreaseField.setAccessible(true);
        javafx.scene.layout.AnchorPane menuDecrease = new javafx.scene.layout.AnchorPane();
        menuDecreaseField.set(mainController, menuDecrease);

        // Set up the book fields in the controller
        controller.id = "123";
        controller.book_name = "Test Book";

        // Call the method
        final CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                controller.handleRemoveBook(new ActionEvent());
                latch.countDown();
            } catch (SQLException e) {
                fail("SQLException occurred: " + e.getMessage());
            }
        });
        latch.await(5, TimeUnit.SECONDS);

        // Verify the method behavior
        assertEquals("Test Book", titleBook1.getText(), "Book title should be set correctly");
        assertTrue(menuDecrease.isVisible(), "Menu decrease should be visible");
        assertEquals("123", mainController.book_id, "Book ID should be set correctly");
    }

    /**
     * Tests the onMouseEntered method of the ControllerItemBookRow class.
     * Verifies that it correctly shows the add and remove buttons.
     */
    @Test
    void testOnMouseEntered() throws Exception {
        // Set up the necessary fields
        Field addBookField = ControllerItemBookRow.class.getDeclaredField("addBook");
        addBookField.setAccessible(true);
        Button addBook = new Button();
        addBookField.set(controller, addBook);

        Field removeBookField = ControllerItemBookRow.class.getDeclaredField("removeBook");
        removeBookField.setAccessible(true);
        Button removeBook = new Button();
        removeBookField.set(controller, removeBook);

        // Call the method
        controller.onMouseEntered(null);

        // Verify the method behavior
        assertTrue(addBook.isVisible(), "Add book button should be visible");
        assertTrue(removeBook.isVisible(), "Remove book button should be visible");
    }

    /**
     * Tests the onMouseExited method of the ControllerItemBookRow class.
     * Verifies that it correctly hides the add and remove buttons.
     */
    @Test
    void testOnMouseExited() throws Exception {
        // Set up the necessary fields
        Field addBookField = ControllerItemBookRow.class.getDeclaredField("addBook");
        addBookField.setAccessible(true);
        Button addBook = new Button();
        addBook.setVisible(true);
        addBookField.set(controller, addBook);

        Field removeBookField = ControllerItemBookRow.class.getDeclaredField("removeBook");
        removeBookField.setAccessible(true);
        Button removeBook = new Button();
        removeBook.setVisible(true);
        removeBookField.set(controller, removeBook);

        // Call the method
        controller.onMouseExited(null);

        // Verify the method behavior
        assertFalse(addBook.isVisible(), "Add book button should not be visible");
        assertFalse(removeBook.isVisible(), "Remove book button should not be visible");
    }
}
