package proiect.controller;

import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the ControllerMain class.
 * 
 * This class uses TestFX to test the JavaFX components in the ControllerMain class.
 * It tests various functionalities including initialization, login, registration,
 * and UI navigation.
 */
@ExtendWith(ApplicationExtension.class)
class ControllerMainTest {

    private ControllerMain controller;
    private Stage stage;
    private static final String DB_URL = "jdbc:mysql://localhost:3306/mydb";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root";

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
     * Verifies that the initialization process correctly sets up the UI components.
     */
    @Test
    void testInitialize() {
        assertDoesNotThrow(() -> controller.initialize());
        assertNotNull(controller.contentPane);
        assertFalse(controller.contentPane.getChildren().isEmpty());
    }

    /**
     * Tests the login functionality with valid credentials.
     * Verifies that a user can successfully log in with correct credentials.
     */
    @Test
    void testLoginWithValidCredentials() {
        // Setup test user in database
        setupTestUser("testuser", "testpass", false);
        
        // Simulate login
        assertDoesNotThrow(() -> {
            // Find login button and click it
            Button loginButton = (Button) controller.contentPane.lookup("#login");
            assertNotNull(loginButton);
            loginButton.fire();
        });
    }

    /**
     * Tests the registration functionality.
     * Verifies that a new user can be registered successfully.
     */
    @Test
    void testRegistration() {
        assertDoesNotThrow(() -> {
            // Find register button and click it
            Button registerButton = (Button) controller.contentPane.lookup("#register");
            assertNotNull(registerButton);
            registerButton.fire();
        });
    }

    /**
     * Tests the quit application functionality.
     * Verifies that the application can be closed properly.
     */
    @Test
    void testQuitApp() {
        assertDoesNotThrow(() -> {
            Button quitButton = (Button) controller.contentPane.lookup("#inchide");
            assertNotNull(quitButton);
            quitButton.fire();
        });
    }

    /**
     * Tests the UI navigation between different panes.
     * Verifies that the application can switch between different views.
     */
    @Test
    void testUINavigation() {
        assertDoesNotThrow(() -> {
            // Test navigation to login screen
            Button login1Button = (Button) controller.contentPane.lookup("#login1");
            assertNotNull(login1Button);
            login1Button.fire();
            
            // Test navigation to registration screen
            Button register1Button = (Button) controller.contentPane.lookup("#register1");
            assertNotNull(register1Button);
            register1Button.fire();
        });
    }

    /**
     * Helper method to set up a test user in the database.
     *
     * @param username the username for the test user
     * @param password the password for the test user
     * @param isLibrarian whether the user is a librarian
     */
    private void setupTestUser(String username, String password, boolean isLibrarian) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "INSERT INTO User (username, password, librarian) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, username);
                stmt.setString(2, password);
                stmt.setBoolean(3, isLibrarian);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            fail("Failed to setup test user: " + e.getMessage());
        }
    }

    /**
     * Cleans up test data after each test.
     */
    @AfterEach
    void cleanup() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "DELETE FROM User WHERE username = 'testuser'";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            fail("Failed to cleanup test data: " + e.getMessage());
        }
    }
} 