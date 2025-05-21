package proiect;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the ControllerItemReviewRow class.
 * 
 * This class tests the functionality of the ControllerItemReviewRow class,
 * which manages a UI component representing a book review row in the application.
 */
@ExtendWith(ApplicationExtension.class)
class ControllerItemReviewRowTest {

    private ControllerItemReviewRow controller;
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
        this.controller = new ControllerItemReviewRow();
        stage.show();
    }

    /**
     * Tests the initialize method of the ControllerItemReviewRow class.
     * Verifies that it correctly initializes the UI components.
     */
    @Test
    void testInitialize() {
        // The initialize method is empty, but we can test that it doesn't throw exceptions
        assertDoesNotThrow(() -> controller.initialize());
    }

    /**
     * Tests the setBook method of the ControllerItemReviewRow class.
     * Verifies that it correctly sets the book review text.
     */
    @Test
    void testSetBook() throws Exception {
        // Set up the necessary fields
        Field bookReviewField = ControllerItemReviewRow.class.getDeclaredField("book_review");
        bookReviewField.setAccessible(true);
        Text bookReview = new Text();
        bookReviewField.set(controller, bookReview);

        // Call the method
        final CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            controller.setBook("Test Book");
            latch.countDown();
        });
        latch.await(5, TimeUnit.SECONDS);

        // Verify the method behavior
        assertEquals("Test Book", bookReview.getText(), "Book review text should be set correctly");
    }

    /**
     * Tests the setUser method of the ControllerItemReviewRow class.
     * Verifies that it correctly sets the user review text.
     */
    @Test
    void testSetUser() throws Exception {
        // Set up the necessary fields
        Field userReviewField = ControllerItemReviewRow.class.getDeclaredField("user_review");
        userReviewField.setAccessible(true);
        Text userReview = new Text();
        userReviewField.set(controller, userReview);

        // Call the method
        final CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            controller.setUser("Test User");
            latch.countDown();
        });
        latch.await(5, TimeUnit.SECONDS);

        // Verify the method behavior
        assertEquals("Test User", userReview.getText(), "User review text should be set correctly");
    }

    /**
     * Tests the OnRemoveReview method of the ControllerItemReviewRow class.
     * Verifies that it correctly calls the main controller's remove_review method.
     */
    @Test
    void testOnRemoveReview() throws Exception {
        // Create a mock ControllerLibrarian
        class MockControllerLibrarian extends ControllerLibrarian {
            public boolean removeReviewCalled = false;
            public String removedReviewId = null;

            @Override
            public void remove_review(String reviewId) {
                removeReviewCalled = true;
                removedReviewId = reviewId;
            }
        }

        // Set up the necessary fields
        Field mainControllerField = ControllerItemReviewRow.class.getDeclaredField("mainController");
        mainControllerField.setAccessible(true);
        MockControllerLibrarian mainController = new MockControllerLibrarian();
        mainControllerField.set(controller, mainController);

        // Set the review_id
        controller.review_id = "123";

        // Call the method
        final CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                controller.OnRemoveReview(new MouseEvent(MouseEvent.MOUSE_CLICKED, 0, 0, 0, 0, 
                        null, 0, false, false, false, false, false, false, false, false, false, false, null));
                latch.countDown();
            } catch (SQLException e) {
                fail("SQLException occurred: " + e.getMessage());
            }
        });
        latch.await(5, TimeUnit.SECONDS);

        // Verify the method behavior
        assertTrue(mainController.removeReviewCalled, "remove_review method should be called");
        assertEquals("123", mainController.removedReviewId, "Review ID should be passed correctly");
        assertEquals("review", mainController.deleteContext, "deleteContext should be set to 'review'");
        assertEquals("123", mainController.review_user_id, "review_user_id should be set correctly");
    }

    /**
     * Tests the OnRemoveReview method when mainController is null.
     * Verifies that it handles the null case gracefully.
     */
    @Test
    void testOnRemoveReviewWithNullMainController() throws Exception {
        // Set mainController to null
        Field mainControllerField = ControllerItemReviewRow.class.getDeclaredField("mainController");
        mainControllerField.setAccessible(true);
        mainControllerField.set(controller, null);

        // Call the method
        assertDoesNotThrow(() -> {
            controller.OnRemoveReview(new MouseEvent(MouseEvent.MOUSE_CLICKED, 0, 0, 0, 0, 
                    null, 0, false, false, false, false, false, false, false, false, false, false, null));
        }, "OnRemoveReview should not throw an exception when mainController is null");
    }
}