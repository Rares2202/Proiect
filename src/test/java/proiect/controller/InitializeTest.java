package proiect.controller;

import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the ControllerMain class.
 * 
 * This class uses TestFX to test the JavaFX components in the ControllerMain class.
 * It tests the initialize method to ensure it correctly sets up the UI components.
 */
@ExtendWith(ApplicationExtension.class)
class InitializeTest {

    private ControllerMain controller;
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
        this.controller = new ControllerMain();
        this.controller.contentPane = new StackPane();
        stage.setScene(new javafx.scene.Scene(this.controller.contentPane, 800, 600));
        stage.show();
    }

    /**
     * Tests the initialize method of the ControllerMain class.
     * 
     * This test verifies that the initialize method correctly:
     * - Maintains a non-null contentPane
     * - Loads the welcome screen into the contentPane
     * - Adds exactly one child (the welcome screen) to the contentPane
     * - Ensures the child is a Pane instance
     * - Completes without throwing any exceptions
     */
    @Test
    void testInitialize(FxRobot robot) {
        robot.interact(() -> controller.initialize());

        assertNotNull(controller.contentPane, "Content pane should not be null");
        assertFalse(controller.contentPane.getChildren().isEmpty(), "Content pane should have children");
        assertEquals(1, controller.contentPane.getChildren().size(), "Content pane should have exactly one child");
        assertTrue(controller.contentPane.getChildren().get(0) instanceof javafx.scene.layout.Pane, 
                "The child should be a Pane");
    }
}
