package proiect.claseUser;

import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the Star class.
 * 
 * This class uses TestFX to test the JavaFX components in the Star class.
 * It tests the following methods:
 * 
 * 1. Constructor - Verify it initializes the ratingValue field correctly
 * 2. reset() - Verify it resets the star state to unfilled
 * 3. setFilled() - Verify it changes the star state as expected
 * 4. getRatingValue() - Verify it returns the correct rating value
 * 5. isFilled() - Verify it returns the correct filled state
 */
@ExtendWith(ApplicationExtension.class)
class StarTest {

    private static final int TEST_RATING_VALUE = 3;
    private Star star;
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
        this.star = new Star(TEST_RATING_VALUE);
        stage.setScene(new javafx.scene.Scene(star, 100, 100));
        stage.show();
    }

    /**
     * Verifies that the Star class exists.
     * This is a simple test that doesn't require JavaFX initialization.
     */
    @Test
    void testClassExists() {
        assertDoesNotThrow(() -> {
            Class.forName("proiect.claseUser.Star", false, this.getClass().getClassLoader());
        }, "Star class should exist");

        assertTrue(true, "Star class exists");
    }

    /**
     * Tests the constructor of the Star class.
     * Verifies that it correctly initializes the ratingValue field.
     */
    @Test
    void testConstructor() throws Exception {
        Field ratingValueField = Star.class.getDeclaredField("ratingValue");
        ratingValueField.setAccessible(true);
        assertEquals(TEST_RATING_VALUE, ratingValueField.getInt(star), "ratingValue should be initialized correctly");
        
        Field isFilledField = Star.class.getDeclaredField("isFilled");
        isFilledField.setAccessible(true);
        assertFalse((boolean) isFilledField.get(star), "isFilled should be initialized to false");
        
        assertNotNull(star.getShape(), "Shape should be initialized");
        assertTrue(star.getShape() instanceof SVGPath, "Shape should be an SVGPath");
    }

    /**
     * Tests the reset method of the Star class.
     * Verifies that it correctly resets the star state to unfilled.
     */
    @Test
    void testReset(FxRobot robot) throws Exception {
        // First set the star to filled
        robot.interact(() -> star.setFilled(true));
        
        Field isFilledField = Star.class.getDeclaredField("isFilled");
        isFilledField.setAccessible(true);
        assertTrue((boolean) isFilledField.get(star), "Star should be filled after setFilled(true)");
        
        // Then reset it
        robot.interact(() -> star.reset());
        
        assertFalse((boolean) isFilledField.get(star), "Star should be unfilled after reset");
        assertFalse(star.isFilled(), "isFilled() should return false after reset");
    }

    /**
     * Tests the setFilled method of the Star class.
     * Verifies that it correctly changes the star state.
     */
    @Test
    void testSetFilled(FxRobot robot) throws Exception {
        Field isFilledField = Star.class.getDeclaredField("isFilled");
        isFilledField.setAccessible(true);
        
        // Test setting to filled
        robot.interact(() -> star.setFilled(true));
        assertTrue((boolean) isFilledField.get(star), "isFilled field should be true after setFilled(true)");
        assertTrue(star.isFilled(), "isFilled() should return true after setFilled(true)");
        
        // Test setting to unfilled
        robot.interact(() -> star.setFilled(false));
        assertFalse((boolean) isFilledField.get(star), "isFilled field should be false after setFilled(false)");
        assertFalse(star.isFilled(), "isFilled() should return false after setFilled(false)");
    }

    /**
     * Tests the getRatingValue method of the Star class.
     * Verifies that it returns the correct rating value.
     */
    @Test
    void testGetRatingValue() {
        assertEquals(TEST_RATING_VALUE, star.getRatingValue(), "getRatingValue should return the correct rating value");
    }

    /**
     * Tests the isFilled method of the Star class.
     * Verifies that it returns the correct filled state.
     */
    @Test
    void testIsFilled(FxRobot robot) {
        // Initially, the star should be unfilled
        assertFalse(star.isFilled(), "Star should initially be unfilled");
        
        // After setting to filled, isFilled should return true
        robot.interact(() -> star.setFilled(true));
        assertTrue(star.isFilled(), "isFilled should return true after setFilled(true)");
        
        // After setting to unfilled, isFilled should return false
        robot.interact(() -> star.setFilled(false));
        assertFalse(star.isFilled(), "isFilled should return false after setFilled(false)");
    }
}