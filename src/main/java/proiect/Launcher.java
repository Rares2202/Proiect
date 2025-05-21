package proiect;
import javafx.application.Application;

/**
 * Launcher class that serves as the entry point for the application.
 * This class is responsible for launching the main JavaFX application.
 */
public class Launcher {
    /**
     * The main method that starts the application.
     * 
     * @param args command-line arguments passed to the application
     */
    public static void main(String[] args) {
        try {
            System.out.println("Pornire aplicatie.");
            Application.launch(MainApp.class, args);
        } catch (Exception e) {
            System.err.println("Error launching application: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
