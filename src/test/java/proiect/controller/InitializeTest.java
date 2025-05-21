package proiect.controller;

import javafx.application.Platform;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class InitializeTest {

    @BeforeAll
    static void initJfxRuntime() {
        Platform.startup(() -> {
        });
    }

    @Test
    void testInitialize() {
        // Given
        ControllerMain controller = new ControllerMain();

        // When/Then

        assertDoesNotThrow(controller::initialize, "Initialize method should not throw any exceptions");
        assertNotNull(controller, "Controller should be initialized");
    }

    @AfterAll
    static void cleanup() {
        Platform.exit();
    }
}