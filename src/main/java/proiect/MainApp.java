package proiect;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


/**
 * The MainApp class extends the JavaFX Application class to serve as the main
 * entry point for launching a graphical user interface application. It initializes
 * and manages the primary stage of the application by loading an FXML layout,
 * applying styles, and enabling custom window behavior.
 *
 * This application uses an undecorated Stage to allow for a customized user interface.
 * The FXML file is used to define the structure of the GUI, and a CSS stylesheet
 * is applied for styling purposes. The class also implements functionality to
 * handle custom window dragging by capturing mouse events.
 *
 * Key functionalities include:
 * - Loading an FXML layout file to define the application's GUI.
 * - Applying a CSS stylesheet to enhance the application's appearance.
 * - Implementing custom window dragging to enable movement of the undecorated window.
 *
 * Note:
 * Since the Stage is undecorated, standard system controls such as the close,
 * minimize, and maximize buttons are not provided. Custom implementations
 * are required for such features, if needed.
 */
public class MainApp extends Application {


    private double xOffset =0;
    private double yOffset = 0;

    /**
     * Initializes and starts the JavaFX application by configuring the main stage,
     * loading the GUI from an FXML file, applying style definitions, and enabling
     * drag-and-drop functionality for moving the window.
     *
     * @param mainStage the primary stage for the JavaFX application, representing the main
     *                  top-level container for the GUI being displayed
     * @throws Exception if there is an error in loading the FXML file or applying style resources
     */
    @Override
    public void start(Stage mainStage) throws Exception {
        Parent window = FXMLLoader.load(getClass().getResource("fxml/MainApp.fxml"));
        Scene scene = new Scene(window);
        scene.getStylesheets().add(this.getClass().getResource("css/style.css").toExternalForm());


        window.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        window.setOnMouseDragged(event -> {

            mainStage.setX(event.getScreenX() - xOffset);
            mainStage.setY(event.getScreenY() - yOffset);
        });

        mainStage.getIcons().add(new Image(getClass().getResourceAsStream("/logo-app.png")));
        mainStage.setTitle("LibraryApp");
        mainStage.initStyle(StageStyle.UNDECORATED);
        mainStage.setScene(scene);
        mainStage.show();

    }


    /**
     * The main entry point for launching the JavaFX application. This method initializes
     * the application by invoking the JavaFX runtime to start the application's lifecycle.
     *
     * @param args command-line arguments passed to the application during execution
     */
    public static void main(String[] args) {
        //showImage("/proiect/css/plus.png","Title", 600, 600);
        launch(args);
    }
}
