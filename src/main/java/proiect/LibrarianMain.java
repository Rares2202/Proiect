package proiect;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

/**
 * The LibrarianMain class extends the JavaFX Application class to create and manage
 * a graphical user interface for a library application. It provides functionality
 * to set up, stylize, and display the main application window, as well as handle
 * basic user interactions such as window dragging.
 *
 * This application uses an undecorated Stage to provide a custom user interface.
 * An FXML file is loaded to define the GUI layout, and a CSS stylesheet is applied
 * to enhance the visual design.
 *
 * Key functionalities include:
 * - Loading an FXML layout file and configuring it as the root of the GUI scene.
 * - Applying a CSS stylesheet to the scene for styling purposes.
 * - Enabling window dragging by capturing mouse events and adjusting the Stage position.
 *
 * The main entry point for launching the application is the `main` method.
 *
 * Note:
 * The application does not use standard window decorations, requiring window movement
 * to be implemented manually via mouse event handlers.
 */
public class LibrarianMain extends Application {
    private double xOffset = 0;
    private double yOffset = 0;

    /**
     * Initializes and starts the JavaFX application by configuring the main stage,
     * loading the GUI from an FXML file, applying style definitions, and enabling
     * drag-and-drop functionality for moving the window.
     *
     * @param mainStage the primary stage for the JavaFX application, representing the main
     *                  top-level container for the GUI being displayed
     * @throws IOException if there is an error in loading the FXML file or applying style resources
     */
    @Override
    public void start(Stage mainStage) throws IOException {
        //load fxml file
        Parent mainAPP = FXMLLoader.load(getClass().getResource("fxml/LibraryGUI.fxml"));

        //create GUI
        Scene scene = new Scene(mainAPP);
        scene.getStylesheets().add(this.getClass().getResource("css/style.css").toExternalForm());

        //enable window moveing on screen
        mainAPP .setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        mainAPP .setOnMouseDragged(event -> {

            mainStage.setX(event.getScreenX() - xOffset);
            mainStage.setY(event.getScreenY() - yOffset);
        });

        //display app
        mainStage.getIcons().add(new Image(getClass().getResourceAsStream("/logo-app.png")));
        mainStage.setTitle("LibraryApp");
        mainStage.initStyle(StageStyle.UNDECORATED);
        mainStage.setScene(scene);
        mainStage.show();
    }

    /**
     * The main entry point of the application. This method is invoked when the program
     * is started and serves as the launcher for the JavaFX application.
     *
     * @param args command-line arguments passed to the application during execution
     */
    public static void main(String[] args) {
        System.out.println("Pornire aplicatie.");
        launch();
    }
}