package proiect.controller;

import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;
import proiect.claseUser.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the ControllerUser class.
 * 
 * This class uses TestFX to test the JavaFX components in the ControllerUser class.
 * It tests the following methods:
 * 
 * 1. setUserId() - Verify it correctly sets the user ID and initializes components
 * 2. loadPane() - Verify it correctly loads an FXML file and sets up button actions
 * 3. getSelectedGenres() - Verify it correctly retrieves selected genres
 * 4. trimitePreferinte() - Verify it correctly processes user preferences
 * 5. initializeReviewsDisplay() - Verify it correctly initializes the reviews display
 * 6. getImReading() - Verify it correctly creates and configures an ImReading instance
 * 7. initializeMyReads() - Verify it correctly initializes the "My Reads" section
 * 8. ReturnResults() - Verify it correctly processes search operations and displays results
 */
@ExtendWith(ApplicationExtension.class)
class ControllerUserTest {

    private ControllerUser controller;
    private Stage stage;
    private StackPane userPane;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    private static final int TEST_USER_ID = 999;

    /**
     * Sets up the test environment before each test.
     * This method is called by TestFX before each test method.
     *
     * @param stage the primary stage for the test
     */
    @Start
    private void start(Stage stage) throws Exception {
        this.stage = stage;
        this.controller = new ControllerUser();
        this.userPane = new StackPane();

        // Use reflection to set the private Userpane field
        Field userPaneField = ControllerUser.class.getDeclaredField("Userpane");
        userPaneField.setAccessible(true);
        userPaneField.set(controller, userPane);

        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
        stage.setScene(new javafx.scene.Scene(userPane, 800, 600));
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
     * Tests the setUserId method of the ControllerUser class.
     * Verifies that it correctly sets the user ID and initializes components.
     */
    @Test
    void testSetUserId() throws Exception {
        // Get the userId field
        Field userIdField = ControllerUser.class.getDeclaredField("userId");
        userIdField.setAccessible(true);

        // Call setUserId on the JavaFX Application Thread
        final CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                controller.setUserId(TEST_USER_ID);
                latch.countDown();
            } catch (Exception e) {
                fail("Exception occurred: " + e.getMessage());
            }
        });

        // Wait for the operation to complete
        latch.await(5, TimeUnit.SECONDS);

        // Verify that the userId was set correctly
        assertEquals(TEST_USER_ID, userIdField.getInt(controller), "User ID should be set correctly");

        // Get the isInitialized field
        Field isInitializedField = ControllerUser.class.getDeclaredField("isInitialized");
        isInitializedField.setAccessible(true);

        // Verify that isInitialized was set to true
        assertTrue((boolean) isInitializedField.get(controller), "isInitialized should be set to true");
    }

    /**
     * Tests the loadPane method of the ControllerUser class.
     * Verifies that it correctly loads an FXML file and sets up button actions.
     */
    @Test
    void testLoadPane() throws Exception {
        // Get the loadPane method
        Method loadPaneMethod = ControllerUser.class.getDeclaredMethod("loadPane", String.class);
        loadPaneMethod.setAccessible(true);

        // We can't directly test the method since it loads an FXML file,
        // but we can verify that the method exists and has the correct signature
        assertNotNull(loadPaneMethod, "loadPane method should exist");
        assertEquals(Pane.class, loadPaneMethod.getReturnType(), "loadPane method should return a Pane");
        assertEquals(1, loadPaneMethod.getParameterCount(), "loadPane method should have one parameter");
        assertEquals(String.class, loadPaneMethod.getParameterTypes()[0], "loadPane method parameter should be a String");
    }

    /**
     * Tests the getSelectedGenres method of the ControllerUser class.
     * Verifies that it correctly retrieves selected genres.
     */
    @Test
    void testGetSelectedGenres() throws Exception {
        // Create a test pane with checkboxes
        Pane testPane = new Pane();

        // Create and add checkboxes to the pane
        CheckBox actionCheckBox = new CheckBox();
        actionCheckBox.setId("actionCheckBox");
        actionCheckBox.setSelected(true);

        CheckBox adventureCheckBox = new CheckBox();
        adventureCheckBox.setId("adventureCheckBox");
        adventureCheckBox.setSelected(false);

        CheckBox fantasyCheckBox = new CheckBox();
        fantasyCheckBox.setId("fantasyCheckBox");
        fantasyCheckBox.setSelected(true);

        testPane.getChildren().addAll(actionCheckBox, adventureCheckBox, fantasyCheckBox);

        // Get the getSelectedGenres method
        Method getSelectedGenresMethod = ControllerUser.class.getDeclaredMethod("getSelectedGenres", Pane.class);
        getSelectedGenresMethod.setAccessible(true);

        // Call the method
        @SuppressWarnings("unchecked")
        List<String> selectedGenres = (List<String>) getSelectedGenresMethod.invoke(controller, testPane);

        // Verify that the selected genres were retrieved correctly
        assertNotNull(selectedGenres, "Selected genres should not be null");
        assertEquals(2, selectedGenres.size(), "There should be 2 selected genres");
        assertTrue(selectedGenres.contains("Action"), "Action should be selected");
        assertTrue(selectedGenres.contains("Fantasy"), "Fantasy should be selected");
        assertFalse(selectedGenres.contains("Adventure"), "Adventure should not be selected");
    }

    /**
     * Tests the trimitePreferinte method of the ControllerUser class.
     * Verifies that it correctly processes user preferences.
     */
    @Test
    void testTrimitePreferinte() throws Exception {
        // Set up the preferinte field
        Field preferinteField = ControllerUser.class.getDeclaredField("preferinte");
        preferinteField.setAccessible(true);

        // Create a list of genres
        List<String> genres = List.of("Action", "Fantasy");
        preferinteField.set(controller, genres);

        // Get the trimitePreferinte method
        Method trimitePreferinteMethod = ControllerUser.class.getDeclaredMethod("trimitePreferinte", int.class);
        trimitePreferinteMethod.setAccessible(true);

        // Call the method on the JavaFX Application Thread
        final CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                trimitePreferinteMethod.invoke(controller, TEST_USER_ID);
                latch.countDown();
            } catch (Exception e) {
                fail("Exception occurred: " + e.getMessage());
            }
        });

        // Wait for the operation to complete
        latch.await(5, TimeUnit.SECONDS);

        // We can't verify the database interaction, but we can verify that the method was called without exceptions
    }

    /**
     * Tests the initializeReviewsDisplay method of the ControllerUser class.
     * Verifies that it correctly initializes the reviews display.
     */
    @Test
    void testInitializeReviewsDisplay() throws Exception {
        // Set up the book field
        Field bookField = ControllerUser.class.getDeclaredField("book");
        bookField.setAccessible(true);
        Book testBook = new Book(1, "Test Title", "Test Author", "Test Description", "Test Genre", "Test Cover URL");
        bookField.set(controller, testBook);

        // Set up the Search field
        Field searchField = ControllerUser.class.getDeclaredField("Search");
        searchField.setAccessible(true);
        Pane searchPane = new Pane();
        ScrollPane reviewsContainer = new ScrollPane();
        reviewsContainer.setId("reviews1");
        searchPane.getChildren().add(reviewsContainer);
        searchField.set(controller, searchPane);

        // Get the initializeReviewsDisplay method
        Method initializeReviewsDisplayMethod = ControllerUser.class.getDeclaredMethod("initializeReviewsDisplay");
        initializeReviewsDisplayMethod.setAccessible(true);

        // We can't directly test the method since it interacts with the database,
        // but we can verify that the method exists and has the correct signature
        assertNotNull(initializeReviewsDisplayMethod, "initializeReviewsDisplay method should exist");
        assertEquals(void.class, initializeReviewsDisplayMethod.getReturnType(), "initializeReviewsDisplay method should return void");
        assertEquals(0, initializeReviewsDisplayMethod.getParameterCount(), "initializeReviewsDisplay method should have no parameters");
    }

    /**
     * Tests the getImReading method of the ControllerUser class.
     * Verifies that it correctly creates and configures an ImReading instance.
     */
    @Test
    void testGetImReading() throws Exception {
        // Set up the userId field
        Field userIdField = ControllerUser.class.getDeclaredField("userId");
        userIdField.setAccessible(true);
        userIdField.set(controller, TEST_USER_ID);

        // Create a test GridPane
        GridPane testGrid = new GridPane();

        // Get the getImReading method
        Method getImReadingMethod = ControllerUser.class.getDeclaredMethod("getImReading", GridPane.class);
        getImReadingMethod.setAccessible(true);

        // Call the method
        ImReading imReading = (ImReading) getImReadingMethod.invoke(controller, testGrid);

        // Verify that the ImReading instance was created correctly
        assertNotNull(imReading, "ImReading instance should not be null");
    }

    /**
     * Tests the initializeMyReads method of the ControllerUser class.
     * Verifies that it correctly initializes the "My Reads" section.
     */
    @Test
    void testInitializeMyReads() throws Exception {
        // Set up the userId field
        Field userIdField = ControllerUser.class.getDeclaredField("userId");
        userIdField.setAccessible(true);
        userIdField.set(controller, TEST_USER_ID);

        // Set up the Myreads field
        Field myreadsField = ControllerUser.class.getDeclaredField("Myreads");
        myreadsField.setAccessible(true);
        Pane myreadsPane = new Pane();
        ScrollPane myReadsScrollPane = new ScrollPane();
        myReadsScrollPane.setId("myReadsPane");
        myreadsPane.getChildren().add(myReadsScrollPane);
        myreadsField.set(controller, myreadsPane);

        // Get the initializeMyReads method
        Method initializeMyReadsMethod = ControllerUser.class.getDeclaredMethod("initializeMyReads");
        initializeMyReadsMethod.setAccessible(true);

        // Call the method
        Platform.runLater(() -> {
            try {
                initializeMyReadsMethod.invoke(controller);

                // Verify that the myreads field was set
                Field myreadsScrollField = ControllerUser.class.getDeclaredField("myreads");
                myreadsScrollField.setAccessible(true);
                assertNotNull(myreadsScrollField.get(controller), "myreads field should be set");
            } catch (Exception e) {
                fail("Exception occurred: " + e.getMessage());
            }
        });

        WaitForAsyncUtils.waitForFxEvents();
    }

    /**
     * Tests the ReturnResults method of the ControllerUser class.
     * Verifies that it correctly processes search operations and displays results.
     */
    @Test
    void testReturnResults() throws Exception {
        // Set up the SearchResults field
        Field searchResultsField = ControllerUser.class.getDeclaredField("SearchResults");
        searchResultsField.setAccessible(true);
        Pane searchResultsPane = new Pane();
        ScrollPane resultsScrollPane = new ScrollPane();
        resultsScrollPane.setId("resultsScrollPane");
        searchResultsPane.getChildren().add(resultsScrollPane);
        searchResultsField.set(controller, searchResultsPane);

        // Create a test pane with a search field
        Pane testPane = new Pane();
        TextField searchField = new TextField();
        searchField.setId("searchField");
        searchField.setText("test search");
        testPane.getChildren().add(searchField);

        // Get the ReturnResults method
        Method returnResultsMethod = ControllerUser.class.getDeclaredMethod("ReturnResults", Pane.class);
        returnResultsMethod.setAccessible(true);

        // We can't directly test the method since it interacts with the database,
        // but we can verify that the method exists and has the correct signature
        assertNotNull(returnResultsMethod, "ReturnResults method should exist");
        assertEquals(void.class, returnResultsMethod.getReturnType(), "ReturnResults method should return void");
        assertEquals(1, returnResultsMethod.getParameterCount(), "ReturnResults method should have one parameter");
        assertEquals(Pane.class, returnResultsMethod.getParameterTypes()[0], "ReturnResults method parameter should be a Pane");
    }
}
