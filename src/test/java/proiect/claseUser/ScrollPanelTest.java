package proiect.claseUser;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
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
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the ScrollPanel class.
 * 
 * This class uses TestFX to test the JavaFX components in the ScrollPanel class.
 * It tests the following methods:
 * 
 * 1. Constructor - Verify it initializes the userId field correctly
 * 2. getCachedImage() - Verify it correctly caches and retrieves images
 * 3. isValidCoverIndex() - Verify it correctly validates cover indices
 * 4. setupCellHoverEffects() - Verify it sets up hover effects correctly
 * 5. setOnCoverClick() - Verify it sets the handler correctly
 * 6. setOnPlusButtonClick() - Verify it sets the handler correctly
 * 7. refresh() - Verify it refreshes the content correctly
 */
@ExtendWith(ApplicationExtension.class)
class ScrollPanelTest {

    private static final int TEST_USER_ID = 999;
    private ScrollPanel scrollPanel;
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
        this.scrollPanel = new ScrollPanel(TEST_USER_ID);
        stage.setScene(new javafx.scene.Scene(scrollPanel, 800, 600));
        stage.show();
    }

    /**
     * Verifies that the ScrollPanel class exists.
     * This is a simple test that doesn't require JavaFX initialization.
     */
    @Test
    void testClassExists() {
        try {
            assertDoesNotThrow(() -> {
                Class.forName("proiect.claseUser.ScrollPanel", false, this.getClass().getClassLoader());
            }, "ScrollPanel class should exist");

            assertTrue(true, "ScrollPanel class exists");
        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }

    /**
     * Tests the constructor of the ScrollPanel class.
     * Verifies that it correctly initializes the userId field.
     */
    @Test
    void testConstructor() throws Exception {
        Field userIdField = ScrollPanel.class.getDeclaredField("userId");
        userIdField.setAccessible(true);
        assertEquals(TEST_USER_ID, userIdField.getInt(scrollPanel), "userId should be initialized correctly");
    }

    /**
     * Tests the getCachedImage method of the ScrollPanel class.
     * Verifies that it correctly caches and retrieves images.
     */
    @Test
    void testGetCachedImage() throws Exception {
        // Access the private imageCache field
        Field imageCacheField = ScrollPanel.class.getDeclaredField("imageCache");
        imageCacheField.setAccessible(true);

        @SuppressWarnings("unchecked")
        Map<String, Image> imageCache = (Map<String, Image>) imageCacheField.get(null);

        // Clear the cache to ensure a clean test
        imageCache.clear();

        // Test with a null URL
        Image defaultImage = ScrollPanel.getCachedImage(null);
        assertNotNull(defaultImage, "Default image should not be null when URL is null");

        // Test with a valid URL that should be cached
        String testUrl = "https://example.com/test.jpg";

        // This will likely return the default image since the URL is not valid,
        // but it should still be cached
        Image image1 = ScrollPanel.getCachedImage(testUrl);
        assertNotNull(image1, "Image should not be null");

        // The URL should now be in the cache
        assertTrue(imageCache.containsKey(testUrl), "URL should be cached after first call");

        // Get the image again, it should be retrieved from cache
        Image image2 = ScrollPanel.getCachedImage(testUrl);
        assertSame(image1, image2, "Second call should return the same image instance from cache");
    }

    /**
     * Tests the isValidCoverIndex method of the ScrollPanel class.
     * Verifies that it correctly validates cover indices.
     */
    @Test
    void testIsValidCoverIndex() throws Exception {
        // Access the private coverUrls field
        Field coverUrlsField = ScrollPanel.class.getDeclaredField("coverUrls");
        coverUrlsField.setAccessible(true);

        @SuppressWarnings("unchecked")
        List<String> coverUrls = (List<String>) coverUrlsField.get(scrollPanel);

        // Access the private isValidCoverIndex method
        Method isValidCoverIndexMethod = ScrollPanel.class.getDeclaredMethod("isValidCoverIndex", int.class);
        isValidCoverIndexMethod.setAccessible(true);

        // Test with an index that is out of bounds
        int outOfBoundsIndex = coverUrls.size() + 1;
        boolean outOfBoundsResult = (boolean) isValidCoverIndexMethod.invoke(scrollPanel, outOfBoundsIndex);
        assertFalse(outOfBoundsResult, "Index out of bounds should be invalid");

        // For negative index, we need to manually check since the method might throw an exception
        // A valid index should be non-negative and within bounds
        int negativeIndex = -1;
        try {
            boolean negativeResult = (boolean) isValidCoverIndexMethod.invoke(scrollPanel, negativeIndex);
            assertFalse(negativeResult, "Negative index should be invalid");
        } catch (Exception e) {
            // If an exception is thrown, that's also acceptable as negative indices are invalid
            assertTrue(e.getCause() instanceof IndexOutOfBoundsException, 
                    "Exception for negative index should be IndexOutOfBoundsException");
        }
    }

    /**
     * Tests the setupCellHoverEffects method of the ScrollPanel class.
     * Verifies that it correctly sets up hover effects.
     */
    @Test
    void testSetupCellHoverEffects(FxRobot robot) throws Exception {
        // Access the private setupCellHoverEffects method
        Method setupCellHoverEffectsMethod = ScrollPanel.class.getDeclaredMethod("setupCellHoverEffects", StackPane.class);
        setupCellHoverEffectsMethod.setAccessible(true);

        // Create a test cell
        StackPane testCell = new StackPane();

        // Set up hover effects
        setupCellHoverEffectsMethod.invoke(scrollPanel, testCell);

        // Initially, the cell should have no effect
        assertNull(testCell.getEffect(), "Cell should have no effect initially");

        // Move the mouse over the cell (simulating hover)
        robot.interact(() -> {
            testCell.fireEvent(new javafx.scene.input.MouseEvent(
                javafx.scene.input.MouseEvent.MOUSE_ENTERED, 0, 0, 0, 0,
                javafx.scene.input.MouseButton.NONE, 0, false, false, false, false,
                true, false, false, false, false, false, null
            ));
        });

        // Now the cell should have a DropShadow effect
        assertNotNull(testCell.getEffect(), "Cell should have an effect after mouse enter");
        assertTrue(testCell.getEffect() instanceof DropShadow, "Effect should be a DropShadow");

        // Move the mouse away from the cell
        robot.interact(() -> {
            testCell.fireEvent(new javafx.scene.input.MouseEvent(
                javafx.scene.input.MouseEvent.MOUSE_EXITED, 0, 0, 0, 0,
                javafx.scene.input.MouseButton.NONE, 0, false, false, false, false,
                true, false, false, false, false, false, null
            ));
        });

        // The effect should be removed
        assertNull(testCell.getEffect(), "Cell should have no effect after mouse exit");
    }

    /**
     * Tests the setOnCoverClick method of the ScrollPanel class.
     * Verifies that it correctly sets the handler.
     */
    @Test
    void testSetOnCoverClick() throws Exception {
        // Create a test handler
        Consumer<String> testHandler = url -> System.out.println("Test handler: " + url);

        // Set the handler
        scrollPanel.setOnCoverClick(testHandler);

        // Access the private onCoverClickHandler field
        Field handlerField = ScrollPanel.class.getDeclaredField("onCoverClickHandler");
        handlerField.setAccessible(true);

        // Verify that the handler was set correctly
        assertSame(testHandler, handlerField.get(scrollPanel), "Handler should be set correctly");
    }

    /**
     * Tests the setOnPlusButtonClick method of the ScrollPanel class.
     * Verifies that it correctly sets the handler.
     */
    @Test
    void testSetOnPlusButtonClick() throws Exception {
        // Create a test handler
        Consumer<String> testHandler = url -> System.out.println("Test handler: " + url);

        // Set the handler
        scrollPanel.setOnPlusButtonClick(testHandler);

        // Access the private onPlusButtonClickHandler field
        Field handlerField = ScrollPanel.class.getDeclaredField("onPlusButtonClickHandler");
        handlerField.setAccessible(true);

        // Verify that the handler was set correctly
        assertSame(testHandler, handlerField.get(scrollPanel), "Handler should be set correctly");
    }

    /**
     * Tests the refresh method of the ScrollPanel class.
     * Verifies that it correctly refreshes the content.
     */
    @Test
    void testRefresh(FxRobot robot) throws Exception {
        // Get the initial content
        Node initialContent = scrollPanel.getContent();

        // Call refresh on the JavaFX Application Thread
        robot.interact(() -> scrollPanel.refresh());

        // Get the refreshed content
        Node refreshedContent = scrollPanel.getContent();

        // The content should still be a GridPane
        assertTrue(refreshedContent instanceof GridPane, "Content should still be a GridPane after refresh");
    }
}
