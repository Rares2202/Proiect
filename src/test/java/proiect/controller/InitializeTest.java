package proiect.controller;

import javafx.application.Platform;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InitializeTest {
    @Test
    void TestInitialize() {
        Platform.startup(() -> {
        }); // Initialize JavaFX toolkit
        ControllerMain controller = new ControllerMain();
        assertDoesNotThrow(controller::initialize);
    }
}