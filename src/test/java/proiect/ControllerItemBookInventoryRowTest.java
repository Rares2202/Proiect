package proiect;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcons;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the ControllerItemBookInventoryRow class.
 * 
 * This class tests the functionality of the ControllerItemBookInventoryRow class,
 * which manages the behavior and visual representation of a row in the book inventory list.
 */
@ExtendWith(ApplicationExtension.class)
class ControllerItemBookInventoryRowTest {

    private ControllerItemBookInventoryRow controller;
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
        this.controller = new ControllerItemBookInventoryRow();
        stage.show();
    }

    /**
     * Tests the initialize method of the ControllerItemBookInventoryRow class.
     * Verifies that it correctly initializes the selected flag to false.
     * 
     * Note: This test focuses only on the non-UI aspects of initialization
     * due to challenges with testing JavaFX UI components in a headless environment.
     */
    @Test
    void testInitialize() throws Exception {
        // Set the selected flag to true initially
        controller.selected = true;

        // Initialize all fields used in the initialize method and setDisabled method
        Field btnAddField = ControllerItemBookInventoryRow.class.getDeclaredField("btn_add");
        btnAddField.setAccessible(true);
        Button btnAdd = new Button();
        btnAddField.set(controller, btnAdd);

        Field btnAddIconField = ControllerItemBookInventoryRow.class.getDeclaredField("btn_add_icon");
        btnAddIconField.setAccessible(true);
        FontAwesomeIcon btnAddIcon = new FontAwesomeIcon();
        btnAddIconField.set(controller, btnAddIcon);

        Field backgroundField = ControllerItemBookInventoryRow.class.getDeclaredField("background");
        backgroundField.setAccessible(true);
        AnchorPane background = new AnchorPane();
        backgroundField.set(controller, background);

        // Initialize name field
        Field nameField = ControllerItemBookInventoryRow.class.getDeclaredField("name");
        nameField.setAccessible(true);
        Label nameLabel = new Label();
        nameField.set(controller, nameLabel);

        // Initialize author field
        Field authorField = ControllerItemBookInventoryRow.class.getDeclaredField("author");
        authorField.setAccessible(true);
        Label authorLabel = new Label();
        authorField.set(controller, authorLabel);

        // Initialize genre field
        Field genreField = ControllerItemBookInventoryRow.class.getDeclaredField("genre");
        genreField.setAccessible(true);
        Label genreLabel = new Label();
        genreField.set(controller, genreLabel);

        // Get the initialize method
        Method initializeMethod = ControllerItemBookInventoryRow.class.getDeclaredMethod("initialize");
        initializeMethod.setAccessible(true);

        // Call the initialize method directly
        initializeMethod.invoke(controller);

        // Verify that the selected flag is set to false
        assertFalse(controller.selected, "Selected flag should be false after initialization");
        assertEquals("-fx-background-color: transparent;", btnAdd.getStyle(), "Button style should be transparent");
    }

    /**
     * Tests the setData method of the ControllerItemBookInventoryRow class.
     * Verifies that it correctly sets the data for a book inventory row.
     */
    @Test
    void testSetData() throws Exception {
        // Set up the necessary fields
        Field nameField = ControllerItemBookInventoryRow.class.getDeclaredField("name");
        nameField.setAccessible(true);
        Label nameLabel = new Label();
        nameField.set(controller, nameLabel);

        Field authorField = ControllerItemBookInventoryRow.class.getDeclaredField("author");
        authorField.setAccessible(true);
        Label authorLabel = new Label();
        authorField.set(controller, authorLabel);

        Field genreField = ControllerItemBookInventoryRow.class.getDeclaredField("genre");
        genreField.setAccessible(true);
        Label genreLabel = new Label();
        genreField.set(controller, genreLabel);

        // Call the method
        controller.setData("123", "Test Book", "Test Author", "Test Genre");

        // Verify the data was set correctly
        assertEquals("123", controller.id, "ID should be set correctly");
        assertEquals("Test Book", nameLabel.getText(), "Name should be set correctly");
        assertEquals("Test Author", authorLabel.getText(), "Author should be set correctly");
        assertEquals("Test Genre", genreLabel.getText(), "Genre should be set correctly");
    }

    /**
     * Tests the OnAddButtonClicked method of the ControllerItemBookInventoryRow class.
     * Verifies that it correctly handles the add button click event when not selected.
     */
    @Test
    void testOnAddButtonClickedWhenNotSelected() throws Exception {
        // Set up the necessary fields
        controller.selected = false;

        Field mainControllerField = ControllerItemBookInventoryRow.class.getDeclaredField("mainController");
        mainControllerField.setAccessible(true);
        ControllerLibrarian mainController = new ControllerLibrarian();
        mainControllerField.set(controller, mainController);

        Field listSelectedInventoryBooksField = ControllerLibrarian.class.getDeclaredField("list_selected_inventory_books");
        listSelectedInventoryBooksField.setAccessible(true);
        List<ControllerItemBookInventoryRow> listSelectedInventoryBooks = new ArrayList<>();
        listSelectedInventoryBooksField.set(mainController, listSelectedInventoryBooks);

        Field btnEfectueazaField = ControllerLibrarian.class.getDeclaredField("btn_efectueaza");
        btnEfectueazaField.setAccessible(true);
        Button btnEfectueza = new Button();
        btnEfectueza.setDisable(true);
        btnEfectueazaField.set(mainController, btnEfectueza);

        // Initialize background field
        Field backgroundField = ControllerItemBookInventoryRow.class.getDeclaredField("background");
        backgroundField.setAccessible(true);
        AnchorPane background = new AnchorPane();
        backgroundField.set(controller, background);

        // Initialize name field
        Field nameField = ControllerItemBookInventoryRow.class.getDeclaredField("name");
        nameField.setAccessible(true);
        Label nameLabel = new Label();
        nameField.set(controller, nameLabel);

        // Initialize author field
        Field authorField = ControllerItemBookInventoryRow.class.getDeclaredField("author");
        authorField.setAccessible(true);
        Label authorLabel = new Label();
        authorField.set(controller, authorLabel);

        // Initialize genre field
        Field genreField = ControllerItemBookInventoryRow.class.getDeclaredField("genre");
        genreField.setAccessible(true);
        Label genreLabel = new Label();
        genreField.set(controller, genreLabel);

        // Initialize btn_add_icon field
        Field btnAddIconField = ControllerItemBookInventoryRow.class.getDeclaredField("btn_add_icon");
        btnAddIconField.setAccessible(true);
        FontAwesomeIcon btnAddIcon = new FontAwesomeIcon();
        btnAddIconField.set(controller, btnAddIcon);

        // Call the OnAddButtonClicked method
        final CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                controller.OnAddButtonClicked(null);
            } catch (Exception e) {
                fail("Exception occurred: " + e.getMessage());
            } finally {
                latch.countDown();
            }
        });

        // Wait for the operation to complete
        latch.await(5, TimeUnit.SECONDS);

        // Verify the results
        List<ControllerItemBookInventoryRow> selectedBooks = (List<ControllerItemBookInventoryRow>) listSelectedInventoryBooksField.get(mainController);
        assertTrue(selectedBooks.contains(controller), "Controller should be added to selected books list");
        assertFalse(btnEfectueza.isDisabled(), "Action button should be enabled");
    }

    /**
     * Tests the OnAddButtonClicked method of the ControllerItemBookInventoryRow class.
     * Verifies that it correctly handles the add button click event when already selected.
     */
    @Test
    void testOnAddButtonClickedWhenSelected() throws Exception {
        // Set up the necessary fields
        controller.selected = true;

        Field mainControllerField = ControllerItemBookInventoryRow.class.getDeclaredField("mainController");
        mainControllerField.setAccessible(true);
        ControllerLibrarian mainController = new ControllerLibrarian();
        mainControllerField.set(controller, mainController);

        Field listSelectedInventoryBooksField = ControllerLibrarian.class.getDeclaredField("list_selected_inventory_books");
        listSelectedInventoryBooksField.setAccessible(true);
        List<ControllerItemBookInventoryRow> listSelectedInventoryBooks = new ArrayList<>();
        listSelectedInventoryBooks.add(controller);
        listSelectedInventoryBooksField.set(mainController, listSelectedInventoryBooks);

        Field listSelectedReservedBooksField = ControllerLibrarian.class.getDeclaredField("list_selected_reserved_books");
        listSelectedReservedBooksField.setAccessible(true);
        List<Object> listSelectedReservedBooks = new ArrayList<>();
        listSelectedReservedBooksField.set(mainController, listSelectedReservedBooks);

        Field btnEfectueazaField = ControllerLibrarian.class.getDeclaredField("btn_efectueaza");
        btnEfectueazaField.setAccessible(true);
        Button btnEfectueza = new Button();
        btnEfectueza.setDisable(false);
        btnEfectueazaField.set(mainController, btnEfectueza);

        Field backgroundField = ControllerItemBookInventoryRow.class.getDeclaredField("background");
        backgroundField.setAccessible(true);
        AnchorPane background = new AnchorPane();
        backgroundField.set(controller, background);

        // Initialize name field
        Field nameField = ControllerItemBookInventoryRow.class.getDeclaredField("name");
        nameField.setAccessible(true);
        Label nameLabel = new Label();
        nameField.set(controller, nameLabel);

        // Initialize author field
        Field authorField = ControllerItemBookInventoryRow.class.getDeclaredField("author");
        authorField.setAccessible(true);
        Label authorLabel = new Label();
        authorField.set(controller, authorLabel);

        // Initialize genre field
        Field genreField = ControllerItemBookInventoryRow.class.getDeclaredField("genre");
        genreField.setAccessible(true);
        Label genreLabel = new Label();
        genreField.set(controller, genreLabel);

        Field btnAddIconField = ControllerItemBookInventoryRow.class.getDeclaredField("btn_add_icon");
        btnAddIconField.setAccessible(true);
        FontAwesomeIcon btnAddIcon = new FontAwesomeIcon();
        btnAddIconField.set(controller, btnAddIcon);

        // Call the OnAddButtonClicked method
        final CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                controller.OnAddButtonClicked(null);
            } catch (Exception e) {
                fail("Exception occurred: " + e.getMessage());
            } finally {
                latch.countDown();
            }
        });

        // Wait for the operation to complete
        latch.await(5, TimeUnit.SECONDS);

        // Verify the results
        List<ControllerItemBookInventoryRow> selectedBooks = (List<ControllerItemBookInventoryRow>) listSelectedInventoryBooksField.get(mainController);
        assertFalse(selectedBooks.contains(controller), "Controller should be removed from selected books list");
        assertTrue(btnEfectueza.isDisabled(), "Action button should be disabled when no books are selected");
        assertEquals("-fx-background-color: rgba(255,0,0,0.25)", background.getStyle(), "Background style should be updated");
        assertEquals(Color.WHITE, btnAddIcon.getFill(), "Button icon color should be white");
    }

    /**
     * Tests the OnMouseEntered method of the ControllerItemBookInventoryRow class.
     * Verifies that it correctly handles the mouse entered event when not selected.
     */
    @Test
    void testOnMouseEnteredWhenNotSelected() throws Exception {
        // Set up the necessary fields
        controller.selected = false;

        Field backgroundField = ControllerItemBookInventoryRow.class.getDeclaredField("background");
        backgroundField.setAccessible(true);
        AnchorPane background = new AnchorPane();
        backgroundField.set(controller, background);

        Field btnAddIconField = ControllerItemBookInventoryRow.class.getDeclaredField("btn_add_icon");
        btnAddIconField.setAccessible(true);
        FontAwesomeIcon btnAddIcon = new FontAwesomeIcon();
        btnAddIcon.setVisible(false);
        btnAddIconField.set(controller, btnAddIcon);

        // Call the OnMouseEntered method
        controller.OnMouseEntered(null);

        // Verify the results
        assertEquals("-fx-background-color: rgba(255,0,0,0.25)", background.getStyle(), "Background style should be updated");
        assertTrue(btnAddIcon.isVisible(), "Button icon should be visible");
    }

    /**
     * Tests the OnMouseEntered method of the ControllerItemBookInventoryRow class.
     * Verifies that it correctly handles the mouse entered event when already selected.
     */
    @Test
    void testOnMouseEnteredWhenSelected() throws Exception {
        // Set up the necessary fields
        controller.selected = true;

        Field backgroundField = ControllerItemBookInventoryRow.class.getDeclaredField("background");
        backgroundField.setAccessible(true);
        AnchorPane background = new AnchorPane();
        background.setStyle("-fx-background-color: red");
        backgroundField.set(controller, background);

        Field btnAddIconField = ControllerItemBookInventoryRow.class.getDeclaredField("btn_add_icon");
        btnAddIconField.setAccessible(true);
        FontAwesomeIcon btnAddIcon = new FontAwesomeIcon();
        btnAddIcon.setVisible(true);
        btnAddIconField.set(controller, btnAddIcon);

        // Call the OnMouseEntered method
        controller.OnMouseEntered(null);

        // Verify the results
        assertEquals("-fx-background-color: red", background.getStyle(), "Background style should not change");
        assertTrue(btnAddIcon.isVisible(), "Button icon should remain visible");
    }

    /**
     * Tests the OnMouseExited method of the ControllerItemBookInventoryRow class.
     * Verifies that it correctly handles the mouse exited event when not selected.
     */
    @Test
    void testOnMouseExitedWhenNotSelected() throws Exception {
        // Set up the necessary fields
        controller.selected = false;

        Field backgroundField = ControllerItemBookInventoryRow.class.getDeclaredField("background");
        backgroundField.setAccessible(true);
        AnchorPane background = new AnchorPane();
        background.setStyle("-fx-background-color: rgba(255,0,0,0.25)");
        backgroundField.set(controller, background);

        Field btnAddIconField = ControllerItemBookInventoryRow.class.getDeclaredField("btn_add_icon");
        btnAddIconField.setAccessible(true);
        FontAwesomeIcon btnAddIcon = new FontAwesomeIcon();
        btnAddIcon.setVisible(true);
        btnAddIconField.set(controller, btnAddIcon);

        // Call the OnMouseExited method
        controller.OnMouseExited(null);

        // Verify the results
        assertEquals("-fx-background-color: white", background.getStyle(), "Background style should be reset to white");
        assertFalse(btnAddIcon.isVisible(), "Button icon should not be visible");
    }

    /**
     * Tests the OnMouseExited method of the ControllerItemBookInventoryRow class.
     * Verifies that it correctly handles the mouse exited event when already selected.
     */
    @Test
    void testOnMouseExitedWhenSelected() throws Exception {
        // Set up the necessary fields
        controller.selected = true;

        Field backgroundField = ControllerItemBookInventoryRow.class.getDeclaredField("background");
        backgroundField.setAccessible(true);
        AnchorPane background = new AnchorPane();
        background.setStyle("-fx-background-color: red");
        backgroundField.set(controller, background);

        Field btnAddIconField = ControllerItemBookInventoryRow.class.getDeclaredField("btn_add_icon");
        btnAddIconField.setAccessible(true);
        FontAwesomeIcon btnAddIcon = new FontAwesomeIcon();
        btnAddIcon.setVisible(true);
        btnAddIconField.set(controller, btnAddIcon);

        // Call the OnMouseExited method
        controller.OnMouseExited(null);

        // Verify the results
        assertEquals("-fx-background-color: red", background.getStyle(), "Background style should not change");
        assertTrue(btnAddIcon.isVisible(), "Button icon should remain visible");
    }

    /**
     * Tests the setEnabled method of the ControllerItemBookInventoryRow class.
     * Verifies that it correctly updates the UI components to represent an enabled state.
     */
    @Test
    void testSetEnabled() throws Exception {
        // Set up the necessary fields
        Field backgroundField = ControllerItemBookInventoryRow.class.getDeclaredField("background");
        backgroundField.setAccessible(true);
        AnchorPane background = new AnchorPane();
        backgroundField.set(controller, background);

        Field nameField = ControllerItemBookInventoryRow.class.getDeclaredField("name");
        nameField.setAccessible(true);
        Label nameLabel = new Label();
        nameField.set(controller, nameLabel);

        Field authorField = ControllerItemBookInventoryRow.class.getDeclaredField("author");
        authorField.setAccessible(true);
        Label authorLabel = new Label();
        authorField.set(controller, authorLabel);

        Field genreField = ControllerItemBookInventoryRow.class.getDeclaredField("genre");
        genreField.setAccessible(true);
        Label genreLabel = new Label();
        genreField.set(controller, genreLabel);

        Field btnAddIconField = ControllerItemBookInventoryRow.class.getDeclaredField("btn_add_icon");
        btnAddIconField.setAccessible(true);
        FontAwesomeIcon btnAddIcon = new FontAwesomeIcon();
        btnAddIconField.set(controller, btnAddIcon);

        // Call the setEnabled method
        Method setEnabledMethod = ControllerItemBookInventoryRow.class.getDeclaredMethod("setEnabled");
        setEnabledMethod.setAccessible(true);
        setEnabledMethod.invoke(controller);

        // Verify the results
        assertEquals("-fx-background-color: red", background.getStyle(), "Background style should be red");
        assertEquals("-fx-text-fill: white", nameLabel.getStyle(), "Name label text should be white");
        assertEquals("-fx-text-fill: white", authorLabel.getStyle(), "Author label text should be white");
        assertEquals("-fx-text-fill: white", genreLabel.getStyle(), "Genre label text should be white");
        assertEquals(Color.WHITE, btnAddIcon.getFill(), "Button icon color should be white");
        assertEquals(String.valueOf(FontAwesomeIcons.PLUS), btnAddIcon.getGlyphName(), "Button icon should be plus");
        assertTrue(controller.selected, "Selected flag should be true");
    }

    /**
     * Tests the setDisabled method of the ControllerItemBookInventoryRow class.
     * Verifies that it correctly updates the UI components to represent a disabled state.
     */
    @Test
    void testSetDisabled() throws Exception {
        // Set up the necessary fields
        Field backgroundField = ControllerItemBookInventoryRow.class.getDeclaredField("background");
        backgroundField.setAccessible(true);
        AnchorPane background = new AnchorPane();
        backgroundField.set(controller, background);

        Field nameField = ControllerItemBookInventoryRow.class.getDeclaredField("name");
        nameField.setAccessible(true);
        Label nameLabel = new Label();
        nameField.set(controller, nameLabel);

        Field authorField = ControllerItemBookInventoryRow.class.getDeclaredField("author");
        authorField.setAccessible(true);
        Label authorLabel = new Label();
        authorField.set(controller, authorLabel);

        Field genreField = ControllerItemBookInventoryRow.class.getDeclaredField("genre");
        genreField.setAccessible(true);
        Label genreLabel = new Label();
        genreField.set(controller, genreLabel);

        Field btnAddIconField = ControllerItemBookInventoryRow.class.getDeclaredField("btn_add_icon");
        btnAddIconField.setAccessible(true);
        FontAwesomeIcon btnAddIcon = new FontAwesomeIcon();
        btnAddIconField.set(controller, btnAddIcon);

        // Call the setDisabled method
        Method setDisabledMethod = ControllerItemBookInventoryRow.class.getDeclaredMethod("setDisabled");
        setDisabledMethod.setAccessible(true);
        setDisabledMethod.invoke(controller);

        // Verify the results
        assertEquals("-fx-background-color: white", background.getStyle(), "Background style should be white");
        assertEquals("-fx-text-fill: black", nameLabel.getStyle(), "Name label text should be black");
        assertEquals("-fx-text-fill: black", authorLabel.getStyle(), "Author label text should be black");
        assertEquals("-fx-text-fill: black", genreLabel.getStyle(), "Genre label text should be black");
        assertEquals(Color.RED, btnAddIcon.getFill(), "Button icon color should be red");
        assertEquals(String.valueOf(FontAwesomeIcons.MINUS), btnAddIcon.getGlyphName(), "Button icon should be minus");
        assertFalse(controller.selected, "Selected flag should be false");
    }

    /**
     * Tests the OnAddButtonEntered method of the ControllerItemBookInventoryRow class.
     * Verifies that it correctly handles the mouse entered event on the add button.
     */
    @Test
    void testOnAddButtonEntered() throws Exception {
        // Set up the necessary fields
        Field btnAddField = ControllerItemBookInventoryRow.class.getDeclaredField("btn_add");
        btnAddField.setAccessible(true);
        Button btnAdd = new Button();
        btnAddField.set(controller, btnAdd);

        Field btnAddIconField = ControllerItemBookInventoryRow.class.getDeclaredField("btn_add_icon");
        btnAddIconField.setAccessible(true);
        FontAwesomeIcon btnAddIcon = new FontAwesomeIcon();
        btnAddIconField.set(controller, btnAddIcon);

        // Call the OnAddButtonEntered method
        controller.OnAddButtonEntered(null);

        // Verify the results
        assertEquals("-fx-background-color: rgb(75,0,0); -fx-cursor: hand;", btnAdd.getStyle(), "Button style should be updated");
        assertEquals(Color.WHITE, btnAddIcon.getFill(), "Button icon color should be white");
    }

    /**
     * Tests the OnAddButtonExited method of the ControllerItemBookInventoryRow class.
     * Verifies that it correctly handles the mouse exited event on the add button when not selected.
     */
    @Test
    void testOnAddButtonExitedWhenNotSelected() throws Exception {
        // Set up the necessary fields
        controller.selected = false;

        Field btnAddField = ControllerItemBookInventoryRow.class.getDeclaredField("btn_add");
        btnAddField.setAccessible(true);
        Button btnAdd = new Button();
        btnAddField.set(controller, btnAdd);

        Field btnAddIconField = ControllerItemBookInventoryRow.class.getDeclaredField("btn_add_icon");
        btnAddIconField.setAccessible(true);
        FontAwesomeIcon btnAddIcon = new FontAwesomeIcon();
        btnAddIconField.set(controller, btnAddIcon);

        // Call the OnAddButtonExited method
        controller.OnAddButtonExited(null);

        // Verify the results
        assertEquals("-fx-background-color: transparent; -fx-cursor: none;", btnAdd.getStyle(), "Button style should be reset");
        assertEquals(Color.RED, btnAddIcon.getFill(), "Button icon color should be red");
    }

    /**
     * Tests the OnAddButtonExited method of the ControllerItemBookInventoryRow class.
     * Verifies that it correctly handles the mouse exited event on the add button when already selected.
     */
    @Test
    void testOnAddButtonExitedWhenSelected() throws Exception {
        // Set up the necessary fields
        controller.selected = true;

        Field btnAddField = ControllerItemBookInventoryRow.class.getDeclaredField("btn_add");
        btnAddField.setAccessible(true);
        Button btnAdd = new Button();
        btnAddField.set(controller, btnAdd);

        Field btnAddIconField = ControllerItemBookInventoryRow.class.getDeclaredField("btn_add_icon");
        btnAddIconField.setAccessible(true);
        FontAwesomeIcon btnAddIcon = new FontAwesomeIcon();
        btnAddIcon.setFill(Color.WHITE);
        btnAddIconField.set(controller, btnAddIcon);

        // Call the OnAddButtonExited method
        controller.OnAddButtonExited(null);

        // Verify the results
        assertEquals("-fx-background-color: transparent; -fx-cursor: none;", btnAdd.getStyle(), "Button style should be reset");
        assertEquals(Color.WHITE, btnAddIcon.getFill(), "Button icon color should remain white");
    }
}
