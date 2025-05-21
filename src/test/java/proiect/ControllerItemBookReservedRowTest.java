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
 * Test class for the ControllerItemBookReservedRow class.
 * 
 * This class tests the functionality of the ControllerItemBookReservedRow class,
 * which manages a UI component representing a reserved book row in a librarian application.
 */
@ExtendWith(ApplicationExtension.class)
class ControllerItemBookReservedRowTest {

    private ControllerItemBookReservedRow controller;
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
        this.controller = new ControllerItemBookReservedRow();
        stage.show();
    }

    /**
     * Tests the initialize method of the ControllerItemBookReservedRow class.
     * Verifies that it correctly initializes the UI components.
     */
    @Test
    void testInitialize() throws Exception {
        // Set up the necessary fields
        Field btnAddField = ControllerItemBookReservedRow.class.getDeclaredField("btn_add");
        btnAddField.setAccessible(true);
        Button btnAdd = new Button();
        btnAddField.set(controller, btnAdd);

        Field btnAddIconField = ControllerItemBookReservedRow.class.getDeclaredField("btn_add_icon");
        btnAddIconField.setAccessible(true);
        FontAwesomeIcon btnAddIcon = new FontAwesomeIcon();
        btnAddIconField.set(controller, btnAddIcon);

        // Set up background field needed by setDisabled
        Field backgroundField = ControllerItemBookReservedRow.class.getDeclaredField("background");
        backgroundField.setAccessible(true);
        AnchorPane background = new AnchorPane();
        backgroundField.set(controller, background);

        // Set up name, author, genre fields needed by setDisabled
        Field nameField = ControllerItemBookReservedRow.class.getDeclaredField("name");
        nameField.setAccessible(true);
        Label nameLabel = new Label();
        nameField.set(controller, nameLabel);

        Field authorField = ControllerItemBookReservedRow.class.getDeclaredField("author");
        authorField.setAccessible(true);
        Label authorLabel = new Label();
        authorField.set(controller, authorLabel);

        Field genreField = ControllerItemBookReservedRow.class.getDeclaredField("genre");
        genreField.setAccessible(true);
        Label genreLabel = new Label();
        genreField.set(controller, genreLabel);

        // Get the initialize method
        Method initializeMethod = ControllerItemBookReservedRow.class.getDeclaredMethod("initialize");
        initializeMethod.setAccessible(true);

        // Call the method
        initializeMethod.invoke(controller);

        // Verify the initialization
        assertEquals("-fx-background-color: transparent;", btnAdd.getStyle(), "Button style should be transparent");
        assertFalse(btnAddIcon.isVisible(), "Button icon should not be visible");
        assertFalse(controller.selected, "Selected state should be false");
    }

    /**
     * Tests the setData method of the ControllerItemBookReservedRow class.
     * Verifies that it correctly sets the book data.
     */
    @Test
    void testSetData() throws Exception {
        // Set up the necessary fields
        Field nameField = ControllerItemBookReservedRow.class.getDeclaredField("name");
        nameField.setAccessible(true);
        Label nameLabel = new Label();
        nameField.set(controller, nameLabel);

        Field authorField = ControllerItemBookReservedRow.class.getDeclaredField("author");
        authorField.setAccessible(true);
        Label authorLabel = new Label();
        authorField.set(controller, authorLabel);

        Field genreField = ControllerItemBookReservedRow.class.getDeclaredField("genre");
        genreField.setAccessible(true);
        Label genreLabel = new Label();
        genreField.set(controller, genreLabel);

        // Call the setData method
        controller.setData("123", "Test Book", "Test Author", "Test Genre");

        // Verify the data was set correctly
        assertEquals("123", controller.id, "ID should be set correctly");
        assertEquals("Test Book", nameLabel.getText(), "Name should be set correctly");
        assertEquals("Test Author", authorLabel.getText(), "Author should be set correctly");
        assertEquals("Test Genre", genreLabel.getText(), "Genre should be set correctly");
    }

    /**
     * Tests the OnAddButtonClicked method of the ControllerItemBookReservedRow class.
     * Verifies that it correctly toggles the selection state.
     */
    @Test
    void testOnAddButtonClicked() throws Exception {
        // Set up the necessary fields
        Field backgroundField = ControllerItemBookReservedRow.class.getDeclaredField("background");
        backgroundField.setAccessible(true);
        AnchorPane background = new AnchorPane();
        backgroundField.set(controller, background);

        Field btnAddIconField = ControllerItemBookReservedRow.class.getDeclaredField("btn_add_icon");
        btnAddIconField.setAccessible(true);
        FontAwesomeIcon btnAddIcon = new FontAwesomeIcon();
        btnAddIconField.set(controller, btnAddIcon);

        // Set up name, author, genre fields needed by setEnabled/setDisabled
        Field nameField = ControllerItemBookReservedRow.class.getDeclaredField("name");
        nameField.setAccessible(true);
        Label nameLabel = new Label();
        nameField.set(controller, nameLabel);

        Field authorField = ControllerItemBookReservedRow.class.getDeclaredField("author");
        authorField.setAccessible(true);
        Label authorLabel = new Label();
        authorField.set(controller, authorLabel);

        Field genreField = ControllerItemBookReservedRow.class.getDeclaredField("genre");
        genreField.setAccessible(true);
        Label genreLabel = new Label();
        genreField.set(controller, genreLabel);

        Field mainControllerField = ControllerItemBookReservedRow.class.getDeclaredField("mainController");
        mainControllerField.setAccessible(true);
        ControllerLibrarian mainController = new ControllerLibrarian();
        mainControllerField.set(controller, mainController);

        // Set up the mainController fields
        Field listSelectedReservedBooksField = ControllerLibrarian.class.getDeclaredField("list_selected_reserved_books");
        listSelectedReservedBooksField.setAccessible(true);
        List<ControllerItemBookReservedRow> listSelectedReservedBooks = new ArrayList<>();
        listSelectedReservedBooksField.set(mainController, listSelectedReservedBooks);

        Field btnEfectueazaField = ControllerLibrarian.class.getDeclaredField("btn_efectueaza");
        btnEfectueazaField.setAccessible(true);
        Button btnEfectueza = new Button();
        btnEfectueazaField.set(mainController, btnEfectueza);

        // Test selecting (when not selected)
        controller.selected = false;
        final CountDownLatch latch1 = new CountDownLatch(1);
        Platform.runLater(() -> {
            controller.OnAddButtonClicked(null);
            latch1.countDown();
        });
        latch1.await(5, TimeUnit.SECONDS);

        assertTrue(controller.selected, "Row should be selected after clicking");
        assertTrue(listSelectedReservedBooks.contains(controller), "Controller should be added to selected list");
        assertFalse(btnEfectueza.isDisabled(), "Action button should be enabled");

        // Test deselecting (when selected)
        final CountDownLatch latch2 = new CountDownLatch(1);
        Platform.runLater(() -> {
            controller.OnAddButtonClicked(null);
            latch2.countDown();
        });
        latch2.await(5, TimeUnit.SECONDS);

        assertFalse(controller.selected, "Row should be deselected after clicking again");
        assertFalse(listSelectedReservedBooks.contains(controller), "Controller should be removed from selected list");
    }

    /**
     * Tests the OnMouseEntered method of the ControllerItemBookReservedRow class.
     * Verifies that it correctly updates the UI when the mouse enters the row.
     */
    @Test
    void testOnMouseEntered() throws Exception {
        // Set up the necessary fields
        Field backgroundField = ControllerItemBookReservedRow.class.getDeclaredField("background");
        backgroundField.setAccessible(true);
        AnchorPane background = new AnchorPane();
        backgroundField.set(controller, background);

        Field btnAddIconField = ControllerItemBookReservedRow.class.getDeclaredField("btn_add_icon");
        btnAddIconField.setAccessible(true);
        FontAwesomeIcon btnAddIcon = new FontAwesomeIcon();
        btnAddIconField.set(controller, btnAddIcon);

        // Test when not selected
        controller.selected = false;
        controller.OnMouseEntered(null);

        assertEquals("-fx-background-color: rgba(0,255,0,0.25)", background.getStyle(), "Background style should change on mouse enter");
        assertTrue(btnAddIcon.isVisible(), "Button icon should be visible on mouse enter");

        // Test when selected
        controller.selected = true;
        background.setStyle("");
        btnAddIcon.setVisible(false);
        controller.OnMouseEntered(null);

        assertNotEquals("-fx-background-color: rgba(0,255,0,0.25)", background.getStyle(), "Background style should not change when selected");
        assertFalse(btnAddIcon.isVisible(), "Button icon should not change visibility when selected");
    }

    /**
     * Tests the OnMouseExited method of the ControllerItemBookReservedRow class.
     * Verifies that it correctly updates the UI when the mouse exits the row.
     */
    @Test
    void testOnMouseExited() throws Exception {
        // Set up the necessary fields
        Field backgroundField = ControllerItemBookReservedRow.class.getDeclaredField("background");
        backgroundField.setAccessible(true);
        AnchorPane background = new AnchorPane();
        backgroundField.set(controller, background);

        Field btnAddIconField = ControllerItemBookReservedRow.class.getDeclaredField("btn_add_icon");
        btnAddIconField.setAccessible(true);
        FontAwesomeIcon btnAddIcon = new FontAwesomeIcon();
        btnAddIcon.setVisible(true);
        btnAddIconField.set(controller, btnAddIcon);

        // Test when not selected
        controller.selected = false;
        controller.OnMouseExited(null);

        assertEquals("-fx-background-color: white", background.getStyle(), "Background style should reset on mouse exit");
        assertFalse(btnAddIcon.isVisible(), "Button icon should be hidden on mouse exit");

        // Test when selected
        controller.selected = true;
        background.setStyle("");
        btnAddIcon.setVisible(true);
        controller.OnMouseExited(null);

        assertNotEquals("-fx-background-color: white", background.getStyle(), "Background style should not change when selected");
        assertTrue(btnAddIcon.isVisible(), "Button icon should not change visibility when selected");
    }

    /**
     * Tests the setEnabled method of the ControllerItemBookReservedRow class.
     * Verifies that it correctly updates the UI to represent an enabled state.
     */
    @Test
    void testSetEnabled() throws Exception {
        // Set up the necessary fields
        Field backgroundField = ControllerItemBookReservedRow.class.getDeclaredField("background");
        backgroundField.setAccessible(true);
        AnchorPane background = new AnchorPane();
        backgroundField.set(controller, background);

        Field nameField = ControllerItemBookReservedRow.class.getDeclaredField("name");
        nameField.setAccessible(true);
        Label nameLabel = new Label();
        nameField.set(controller, nameLabel);

        Field authorField = ControllerItemBookReservedRow.class.getDeclaredField("author");
        authorField.setAccessible(true);
        Label authorLabel = new Label();
        authorField.set(controller, authorLabel);

        Field genreField = ControllerItemBookReservedRow.class.getDeclaredField("genre");
        genreField.setAccessible(true);
        Label genreLabel = new Label();
        genreField.set(controller, genreLabel);

        Field btnAddIconField = ControllerItemBookReservedRow.class.getDeclaredField("btn_add_icon");
        btnAddIconField.setAccessible(true);
        FontAwesomeIcon btnAddIcon = new FontAwesomeIcon();
        btnAddIconField.set(controller, btnAddIcon);

        // Get the setEnabled method
        Method setEnabledMethod = ControllerItemBookReservedRow.class.getDeclaredMethod("setEnabled");
        setEnabledMethod.setAccessible(true);

        // Call the method
        setEnabledMethod.invoke(controller);

        // Verify the UI updates
        assertEquals("-fx-background-color: green", background.getStyle(), "Background should be green");
        assertEquals("-fx-text-fill: white", nameLabel.getStyle(), "Name text should be white");
        assertEquals("-fx-text-fill: white", authorLabel.getStyle(), "Author text should be white");
        assertEquals("-fx-text-fill: white", genreLabel.getStyle(), "Genre text should be white");
        assertEquals(Color.WHITE, btnAddIcon.getFill(), "Button icon should be white");
        assertEquals(String.valueOf(FontAwesomeIcons.MINUS), btnAddIcon.getGlyphName(), "Button icon should be minus");
        assertTrue(controller.selected, "Selected state should be true");
    }

    /**
     * Tests the setDisabled method of the ControllerItemBookReservedRow class.
     * Verifies that it correctly updates the UI to represent a disabled state.
     */
    @Test
    void testSetDisabled() throws Exception {
        // Set up the necessary fields
        Field backgroundField = ControllerItemBookReservedRow.class.getDeclaredField("background");
        backgroundField.setAccessible(true);
        AnchorPane background = new AnchorPane();
        backgroundField.set(controller, background);

        Field nameField = ControllerItemBookReservedRow.class.getDeclaredField("name");
        nameField.setAccessible(true);
        Label nameLabel = new Label();
        nameField.set(controller, nameLabel);

        Field authorField = ControllerItemBookReservedRow.class.getDeclaredField("author");
        authorField.setAccessible(true);
        Label authorLabel = new Label();
        authorField.set(controller, authorLabel);

        Field genreField = ControllerItemBookReservedRow.class.getDeclaredField("genre");
        genreField.setAccessible(true);
        Label genreLabel = new Label();
        genreField.set(controller, genreLabel);

        Field btnAddIconField = ControllerItemBookReservedRow.class.getDeclaredField("btn_add_icon");
        btnAddIconField.setAccessible(true);
        FontAwesomeIcon btnAddIcon = new FontAwesomeIcon();
        btnAddIconField.set(controller, btnAddIcon);

        // Get the setDisabled method
        Method setDisabledMethod = ControllerItemBookReservedRow.class.getDeclaredMethod("setDisabled");
        setDisabledMethod.setAccessible(true);

        // Call the method
        setDisabledMethod.invoke(controller);

        // Verify the UI updates
        assertEquals("-fx-background-color: white", background.getStyle(), "Background should be white");
        assertEquals("-fx-text-fill: black", nameLabel.getStyle(), "Name text should be black");
        assertEquals("-fx-text-fill: black", authorLabel.getStyle(), "Author text should be black");
        assertEquals("-fx-text-fill: black", genreLabel.getStyle(), "Genre text should be black");
        assertEquals(Color.GREEN, btnAddIcon.getFill(), "Button icon should be green");
        assertEquals(String.valueOf(FontAwesomeIcons.PLUS), btnAddIcon.getGlyphName(), "Button icon should be plus");
        assertFalse(controller.selected, "Selected state should be false");
    }

    /**
     * Tests the OnAddButtonEntered method of the ControllerItemBookReservedRow class.
     * Verifies that it correctly updates the UI when the mouse enters the Add button.
     */
    @Test
    void testOnAddButtonEntered() throws Exception {
        // Set up the necessary fields
        Field btnAddField = ControllerItemBookReservedRow.class.getDeclaredField("btn_add");
        btnAddField.setAccessible(true);
        Button btnAdd = new Button();
        btnAddField.set(controller, btnAdd);

        Field btnAddIconField = ControllerItemBookReservedRow.class.getDeclaredField("btn_add_icon");
        btnAddIconField.setAccessible(true);
        FontAwesomeIcon btnAddIcon = new FontAwesomeIcon();
        btnAddIconField.set(controller, btnAddIcon);

        // Call the method
        controller.OnAddButtonEntered(null);

        // Verify the UI updates
        assertEquals("-fx-background-color: rgb(0,75,0); -fx-cursor: hand;", btnAdd.getStyle(), "Button style should change on mouse enter");
        assertEquals(Color.WHITE, btnAddIcon.getFill(), "Button icon should be white on mouse enter");
    }

    /**
     * Tests the OnAddButtonExited method of the ControllerItemBookReservedRow class.
     * Verifies that it correctly updates the UI when the mouse exits the Add button.
     */
    @Test
    void testOnAddButtonExited() throws Exception {
        // Set up the necessary fields
        Field btnAddField = ControllerItemBookReservedRow.class.getDeclaredField("btn_add");
        btnAddField.setAccessible(true);
        Button btnAdd = new Button();
        btnAddField.set(controller, btnAdd);

        Field btnAddIconField = ControllerItemBookReservedRow.class.getDeclaredField("btn_add_icon");
        btnAddIconField.setAccessible(true);
        FontAwesomeIcon btnAddIcon = new FontAwesomeIcon();
        btnAddIconField.set(controller, btnAddIcon);

        // Test when not selected
        controller.selected = false;
        controller.OnAddButtonExited(null);

        assertEquals("-fx-background-color: transparent; -fx-cursor: none;", btnAdd.getStyle(), "Button style should reset on mouse exit");
        assertEquals(Color.GREEN, btnAddIcon.getFill(), "Button icon should be green when not selected");

        // Test when selected
        controller.selected = true;
        // Set the fill to a non-green color to verify it doesn't change to green
        btnAddIcon.setFill(Color.WHITE);
        controller.OnAddButtonExited(null);

        assertEquals("-fx-background-color: transparent; -fx-cursor: none;", btnAdd.getStyle(), "Button style should reset on mouse exit");
        assertEquals(Color.WHITE, btnAddIcon.getFill(), "Button icon color should not change when selected");
    }
}
