package proiect;

import javafx.application.Platform;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * A test implementation of ControllerLibrarian for testing purposes.
 * This class extends ControllerLibrarian and adds fields to track method calls.
 */
class TestControllerLibrarian extends ControllerLibrarian {
    boolean initializeMenuClientDetaliiCalled = false;
    String lastClientId = null;

    /**
     * Overrides the initialize_menu_client_detalii method to track calls.
     * 
     * @param id the client ID
     * @throws SQLException if a database access error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void initialize_menu_client_detalii(String id) throws SQLException, IOException {
        initializeMenuClientDetaliiCalled = true;
        lastClientId = id;
        // Don't call super implementation to avoid actual database operations
    }
}

/**
 * Test class for the ControllerItemClientRow class.
 * 
 * This class tests the functionality of the ControllerItemClientRow class,
 * which is responsible for managing a client row in the user interface.
 */
@ExtendWith(ApplicationExtension.class)
class ControllerItemClientRowTest {

    private ControllerItemClientRow controller;
    private Stage stage;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    /**
     * Sets up the test environment before each test.
     * This method is called by TestFX before each test method.
     *
     * @param stage the primary stage for the test
     */
    @Start
    private void start(Stage stage) {
        this.stage = stage;
        this.controller = new ControllerItemClientRow();
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
        stage.show();
    }

    /**
     * Cleans up the test environment after each test.
     */
    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    /**
     * Tests the initialize method of the ControllerItemClientRow class.
     * Verifies that the method exists and can be called without exceptions.
     */
    @Test
    void testInitialize() {
        assertDoesNotThrow(() -> controller.initialize(), 
            "initialize method should not throw exceptions");
    }

    /**
     * Tests the setName method of the ControllerItemClientRow class.
     * Verifies that it correctly sets the client name in the UI.
     */
    @Test
    void testSetName() throws Exception {
        // Set up the clientName field
        Text clientNameText = new Text();
        Field clientNameField = ControllerItemClientRow.class.getDeclaredField("clientName");
        clientNameField.setAccessible(true);
        clientNameField.set(controller, clientNameText);

        // Test setting the name
        final String testName = "Test Client";
        final CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            try {
                controller.setName(testName);
            } finally {
                latch.countDown();
            }
        });

        latch.await(5, TimeUnit.SECONDS);

        assertEquals(testName, clientNameText.getText(), 
            "Client name should be set correctly");
    }

    /**
     * Tests the showClientDetails method of the ControllerItemClientRow class.
     * Verifies that it correctly updates the main controller with client details.
     */
    @Test
    void testShowClientDetails() throws Exception {
        // Set up the required fields
        Text clientNameText = new Text("Test Client");
        Field clientNameField = ControllerItemClientRow.class.getDeclaredField("clientName");
        clientNameField.setAccessible(true);
        clientNameField.set(controller, clientNameText);

        controller.id = "123";

        // Create a test ControllerLibrarian
        TestControllerLibrarian testMainController = new TestControllerLibrarian();
        testMainController.menu_transaction_clientName = new Text();
        controller.mainController = testMainController;

        // Test showing client details
        final CountDownLatch latch = new CountDownLatch(1);
        final MouseEvent mouseEvent = new MouseEvent(MouseEvent.MOUSE_CLICKED, 0, 0, 0, 0, 
                                                    null, 0, false, false, false, false, 
                                                    false, false, false, false, false, false, null);

        Platform.runLater(() -> {
            try {
                controller.showClientDetails(mouseEvent);
            } catch (SQLException | IOException e) {
                fail("Exception occurred: " + e.getMessage());
            } finally {
                latch.countDown();
            }
        });

        latch.await(5, TimeUnit.SECONDS);

        // Verify interactions with the main controller
        assertEquals("123", testMainController.transaction_user_id, 
            "Transaction user ID should be set correctly");
        assertEquals("Test Client", testMainController.menu_transaction_clientName.getText(), 
            "Client name should be set correctly in the main controller");

        // Verify that initialize_menu_client_detalii was called with the correct ID
        assertTrue(testMainController.initializeMenuClientDetaliiCalled, 
            "initialize_menu_client_detalii should be called");
        assertEquals("123", testMainController.lastClientId, 
            "initialize_menu_client_detalii should be called with the correct ID");
    }
}
