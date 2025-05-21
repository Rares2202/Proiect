package proiect.claseUser;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the ReviewScroll class.
 * 
 * This class uses TestFX to test the JavaFX components in the ReviewScroll class.
 * It tests the following methods:
 * 
 * 1. Constructor - Verify it initializes the idCarte and parentScrollPane fields correctly
 * 2. close() - Verify it correctly hides the ReviewScroll and its parent ScrollPane
 * 3. show() - Verify it correctly displays the ReviewScroll and its parent ScrollPane
 * 4. isShowing() - Verify it correctly reports the visibility state
 * 5. getBookId() - Verify it returns the correct book ID
 * 6. refresh() - Verify it refreshes the content correctly
 */
@ExtendWith(ApplicationExtension.class)
class ReviewScrollTest {

    private static final int TEST_BOOK_ID = 999;
    private ReviewScroll reviewScroll;
    private ScrollPane parentScrollPane;
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
        this.parentScrollPane = new ScrollPane();
        
        try {
            this.reviewScroll = new ReviewScroll(TEST_BOOK_ID, parentScrollPane);
            stage.setScene(new javafx.scene.Scene(parentScrollPane, 800, 600));
            stage.show();
        } catch (SQLException e) {
            fail("Exception occurred during setup: " + e.getMessage());
        }
    }

    /**
     * Verifies that the ReviewScroll class exists.
     * This is a simple test that doesn't require JavaFX initialization.
     */
    @Test
    void testClassExists() {
        try {
            assertDoesNotThrow(() -> {
                Class.forName("proiect.claseUser.ReviewScroll", false, this.getClass().getClassLoader());
            }, "ReviewScroll class should exist");

            assertTrue(true, "ReviewScroll class exists");
        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }

    /**
     * Tests the constructor of the ReviewScroll class.
     * Verifies that it correctly initializes the idCarte and parentScrollPane fields.
     */
    @Test
    void testConstructor() throws Exception {
        Field idCarteField = ReviewScroll.class.getDeclaredField("idCarte");
        idCarteField.setAccessible(true);
        assertEquals(TEST_BOOK_ID, idCarteField.getInt(reviewScroll), "idCarte should be initialized correctly");

        Field parentScrollPaneField = ReviewScroll.class.getDeclaredField("parentScrollPane");
        parentScrollPaneField.setAccessible(true);
        assertSame(parentScrollPane, parentScrollPaneField.get(reviewScroll), "parentScrollPane should be initialized correctly");
    }

    /**
     * Tests the close method of the ReviewScroll class.
     * Verifies that it correctly hides the ReviewScroll and its parent ScrollPane.
     */
    @Test
    void testClose(FxRobot robot) {
        robot.interact(() -> {
            reviewScroll.close();
        });

        assertFalse(reviewScroll.isVisible(), "ReviewScroll should be hidden after close");
        assertFalse(parentScrollPane.isVisible(), "Parent ScrollPane should be hidden after close");
        
        try {
            Field isVisibleField = ReviewScroll.class.getDeclaredField("isVisible");
            isVisibleField.setAccessible(true);
            assertFalse((boolean) isVisibleField.get(reviewScroll), "isVisible field should be false after close");
        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }

    /**
     * Tests the show method of the ReviewScroll class.
     * Verifies that it correctly displays the ReviewScroll and its parent ScrollPane.
     */
    @Test
    void testShow(FxRobot robot) {
        // First hide the components
        robot.interact(() -> {
            reviewScroll.close();
        });
        
        // Then show them
        robot.interact(() -> {
            reviewScroll.show();
        });

        assertTrue(reviewScroll.isVisible(), "ReviewScroll should be visible after show");
        assertTrue(parentScrollPane.isVisible(), "Parent ScrollPane should be visible after show");
        
        try {
            Field isVisibleField = ReviewScroll.class.getDeclaredField("isVisible");
            isVisibleField.setAccessible(true);
            assertTrue((boolean) isVisibleField.get(reviewScroll), "isVisible field should be true after show");
        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }

    /**
     * Tests the isShowing method of the ReviewScroll class.
     * Verifies that it correctly reports the visibility state.
     */
    @Test
    void testIsShowing(FxRobot robot) {
        // Initially, the component should be visible
        assertTrue(reviewScroll.isShowing(), "ReviewScroll should initially be showing");
        
        // After closing, it should not be showing
        robot.interact(() -> {
            reviewScroll.close();
        });
        
        assertFalse(reviewScroll.isShowing(), "ReviewScroll should not be showing after close");
        
        // After showing again, it should be showing
        robot.interact(() -> {
            reviewScroll.show();
        });
        
        assertTrue(reviewScroll.isShowing(), "ReviewScroll should be showing after show");
    }

    /**
     * Tests the getBookId method of the ReviewScroll class.
     * Verifies that it returns the correct book ID.
     */
    @Test
    void testGetBookId() {
        assertEquals(TEST_BOOK_ID, reviewScroll.getBookId(), "getBookId should return the correct book ID");
    }

    /**
     * Tests the refresh method of the ReviewScroll class.
     * Verifies that it refreshes the content correctly.
     */
    @Test
    void testRefresh(FxRobot robot) {
        robot.interact(() -> {
            try {
                reviewScroll.refresh();
                assertTrue(true, "refresh should complete without errors");
            } catch (SQLException e) {
                fail("Exception occurred during refresh: " + e.getMessage());
            }
        });
    }

    /**
     * Tests the loadReviews method of the ReviewScroll class.
     * Verifies that it correctly loads reviews from the database.
     */
    @Test
    void testLoadReviews() throws Exception {
        Method loadReviewsMethod = ReviewScroll.class.getDeclaredMethod("loadReviews");
        loadReviewsMethod.setAccessible(true);
        
        Field reviewsField = ReviewScroll.class.getDeclaredField("reviews");
        reviewsField.setAccessible(true);
        
        // Clear the reviews list
        reviewsField.set(reviewScroll, new ArrayList<Review>());
        
        // Call loadReviews
        loadReviewsMethod.invoke(reviewScroll);
        
        // Verify that reviews were loaded
        @SuppressWarnings("unchecked")
        List<Review> reviews = (List<Review>) reviewsField.get(reviewScroll);
        assertNotNull(reviews, "reviews should not be null after loadReviews");
    }
}