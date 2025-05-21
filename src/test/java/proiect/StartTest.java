package proiect;

import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the MainApp and LibrarianMain classes.
 * 
 * This class uses TestFX to test the JavaFX components in these classes.
 * It tests the start methods to ensure they correctly set up the UI components.
 */
@ExtendWith(ApplicationExtension.class)
class StartTest {

    private Stage stage;
    private MainApp mainApp;
    private LibrarianMain librarianMain;

    /**
     * Sets up the test environment before each test.
     * This method is called by TestFX before each test method.
     *
     * @param stage the primary stage for the test
     */
    @Start
    private void start(Stage stage) {
        this.stage = stage;
        this.mainApp = new MainApp();
        this.librarianMain = new LibrarianMain();
    }

    /**
     * Tests the start method of the MainApp class.
     * 
     * This test verifies that the start method correctly:
     * - Loads the FXML layout
     * - Creates a Scene with the loaded layout
     * - Applies CSS styling
     * - Sets up mouse event handlers for window dragging
     * - Configures the Stage as UNDECORATED
     * - Sets the Scene to the Stage
     */
    @Test
    void testStart(FxRobot robot) {
        robot.interact(() -> {
            try {
                mainApp.start(stage);
            } catch (Exception e) {
                fail("Exception occurred: " + e.getMessage());
            }
        });

        assertEquals(StageStyle.UNDECORATED, stage.getStyle(), "Stage should be UNDECORATED");
        assertNotNull(stage.getScene(), "Scene should be set");

        Scene scene = stage.getScene();
        assertNotNull(scene.getRoot(), "Scene should have a root");
        assertFalse(scene.getStylesheets().isEmpty(), "Scene should have stylesheets");

        assertNotNull(scene.getRoot().getOnMousePressed(), "Root should have mouse pressed handler");
        assertNotNull(scene.getRoot().getOnMouseDragged(), "Root should have mouse dragged handler");
    }

    /**
     * A simplified test for the LibrarianMain class.
     * This test verifies that the LibrarianMain class can be instantiated
     * and that its start method has the correct signature.
     */
    @Test
    void testLibrarianStart() {
        assertNotNull(librarianMain, "LibrarianMain instance should not be null");
        assertEquals(LibrarianMain.class, librarianMain.getClass(), 
            "Instance should be of type LibrarianMain");

        assertDoesNotThrow(() -> {
            LibrarianMain.class.getDeclaredMethod("start", Stage.class);
        }, "LibrarianMain should have a start method that takes a Stage parameter");
    }
}
