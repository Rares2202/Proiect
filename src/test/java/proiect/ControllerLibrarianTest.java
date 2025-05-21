package proiect;

import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the ControllerLibrarian class.
 * 
 * This class tests the method signatures and basic functionality of the ControllerLibrarian class.
 * It tests the following methods:
 * 
 * 1. setOnlyMenu() - Verify the method signature
 * 2. SelectAllFromRezervari() - Verify the method signature
 * 3. SelectNoneFromRezervari() - Verify the method signature
 * 4. SelectAllFromInventar() - Verify the method signature
 * 5. SelectNoneFromInventar() - Verify the method signature
 * 6. show_popup_info() - Verify the method signature
 */
@ExtendWith(ApplicationExtension.class)
class ControllerLibrarianTest {

    private ControllerLibrarian controller;
    private Stage stage;

    /**
     * Sets up the test environment before each test.
     * This method is called by TestFX before each test method.
     *
     * @param stage the primary stage for the test
     */
    @Start
    private void start(Stage stage) throws Exception {
        this.stage = stage;
        this.controller = new ControllerLibrarian();
        stage.show();
    }

    /**
     * Tests the setOnlyMenu method of the ControllerLibrarian class.
     * Verifies that the method has the correct signature.
     */
    @Test
    void testSetOnlyMenu() throws Exception {
        // Get the setOnlyMenu method
        Method setOnlyMenuMethod = ControllerLibrarian.class.getDeclaredMethod("setOnlyMenu", AnchorPane.class);

        // Verify the method signature
        assertNotNull(setOnlyMenuMethod, "setOnlyMenu method should exist");
        assertEquals(void.class, setOnlyMenuMethod.getReturnType(), "setOnlyMenu method should return void");
        assertEquals(1, setOnlyMenuMethod.getParameterCount(), "setOnlyMenu method should have one parameter");
        assertEquals(AnchorPane.class, setOnlyMenuMethod.getParameterTypes()[0], "setOnlyMenu method parameter should be an AnchorPane");
    }

    /**
     * Tests the SelectAllFromRezervari method of the ControllerLibrarian class.
     * Verifies that the method has the correct signature.
     */
    @Test
    void testSelectAllFromRezervari() throws Exception {
        // Get the SelectAllFromRezervari method
        Method selectAllFromRezervariMethod = ControllerLibrarian.class.getDeclaredMethod("SelectAllFromRezervari", MouseEvent.class);

        // Verify the method signature
        assertNotNull(selectAllFromRezervariMethod, "SelectAllFromRezervari method should exist");
        assertEquals(void.class, selectAllFromRezervariMethod.getReturnType(), "SelectAllFromRezervari method should return void");
        assertEquals(1, selectAllFromRezervariMethod.getParameterCount(), "SelectAllFromRezervari method should have one parameter");
        assertEquals(MouseEvent.class, selectAllFromRezervariMethod.getParameterTypes()[0], "SelectAllFromRezervari method parameter should be a MouseEvent");
    }

    /**
     * Tests the SelectNoneFromRezervari method of the ControllerLibrarian class.
     * Verifies that the method has the correct signature.
     */
    @Test
    void testSelectNoneFromRezervari() throws Exception {
        // Get the SelectNoneFromRezervari method
        Method selectNoneFromRezervariMethod = ControllerLibrarian.class.getDeclaredMethod("SelectNoneFromRezervari", MouseEvent.class);

        // Verify the method signature
        assertNotNull(selectNoneFromRezervariMethod, "SelectNoneFromRezervari method should exist");
        assertEquals(void.class, selectNoneFromRezervariMethod.getReturnType(), "SelectNoneFromRezervari method should return void");
        assertEquals(1, selectNoneFromRezervariMethod.getParameterCount(), "SelectNoneFromRezervari method should have one parameter");
        assertEquals(MouseEvent.class, selectNoneFromRezervariMethod.getParameterTypes()[0], "SelectNoneFromRezervari method parameter should be a MouseEvent");
    }

    /**
     * Tests the SelectAllFromInventar method of the ControllerLibrarian class.
     * Verifies that the method has the correct signature.
     */
    @Test
    void testSelectAllFromInventar() throws Exception {
        // Get the SelectAllFromInventar method
        Method selectAllFromInventarMethod = ControllerLibrarian.class.getDeclaredMethod("SelectAllFromInventar", MouseEvent.class);

        // Verify the method signature
        assertNotNull(selectAllFromInventarMethod, "SelectAllFromInventar method should exist");
        assertEquals(void.class, selectAllFromInventarMethod.getReturnType(), "SelectAllFromInventar method should return void");
        assertEquals(1, selectAllFromInventarMethod.getParameterCount(), "SelectAllFromInventar method should have one parameter");
        assertEquals(MouseEvent.class, selectAllFromInventarMethod.getParameterTypes()[0], "SelectAllFromInventar method parameter should be a MouseEvent");
    }

    /**
     * Tests the SelectNoneFromInventar method of the ControllerLibrarian class.
     * Verifies that the method has the correct signature.
     */
    @Test
    void testSelectNoneFromInventar() throws Exception {
        // Get the SelectNoneFromInventar method
        Method selectNoneFromInventarMethod = ControllerLibrarian.class.getDeclaredMethod("SelectNoneFromInventar", MouseEvent.class);

        // Verify the method signature
        assertNotNull(selectNoneFromInventarMethod, "SelectNoneFromInventar method should exist");
        assertEquals(void.class, selectNoneFromInventarMethod.getReturnType(), "SelectNoneFromInventar method should return void");
        assertEquals(1, selectNoneFromInventarMethod.getParameterCount(), "SelectNoneFromInventar method should have one parameter");
        assertEquals(MouseEvent.class, selectNoneFromInventarMethod.getParameterTypes()[0], "SelectNoneFromInventar method parameter should be a MouseEvent");
    }

    /**
     * Tests the show_popup_info method of the ControllerLibrarian class.
     * Verifies that the method has the correct signature.
     */
    @Test
    void testShowPopupInfo() throws Exception {
        // Get the show_popup_info method
        Method showPopupInfoMethod = ControllerLibrarian.class.getDeclaredMethod("show_popup_info", String.class, ControllerLibrarian.popup_info_case.class);

        // Verify the method signature
        assertNotNull(showPopupInfoMethod, "show_popup_info method should exist");
        assertEquals(void.class, showPopupInfoMethod.getReturnType(), "show_popup_info method should return void");
        assertEquals(2, showPopupInfoMethod.getParameterCount(), "show_popup_info method should have two parameters");
        assertEquals(String.class, showPopupInfoMethod.getParameterTypes()[0], "First parameter of show_popup_info should be a String");
        assertEquals(ControllerLibrarian.popup_info_case.class, showPopupInfoMethod.getParameterTypes()[1], "Second parameter of show_popup_info should be a popup_info_case enum");
    }

}
