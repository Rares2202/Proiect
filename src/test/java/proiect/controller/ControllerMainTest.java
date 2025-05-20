package proiect.controller;

import javafx.scene.layout.Pane;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class ControllerMainTest {


   @Test
    void testInitialize_shouldSetInitialPane() {
        ControllerMain controller = new ControllerMain();

        // Simulează apelul metodei initialize
        controller.initialize();

        // Verifică dacă panoul inițial este setat
        assertNotNull(controller.contentPane, "Content pane should be initialized.");
        // Alte verificări, dacă sunt necesare
    }
}