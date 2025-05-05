package proiect;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


import java.util.Date;

/**
 * The type Main app.
 */
public class MainApp extends Application {


    private double xOffset = 0;
    private double yOffset = 0;
    /**
     * Initializeaza si afiseaza; fereastra principala a aplicatiei.
     * <p>
     * Aceasta metoda realizeaza urmatoarele actiuni principale:
     * <ul>
     *   <li>Incarca layout-ul din fisierul FXML.</li>
     *   <li>Adauga stylesheet-ul CSS.</li>
     *   <li>Implementeaza functionalitate de drag pentru ferestra.</li>
     *   <li>Configureaza fereastra sa fie fara bordura standard.</li>
     * </ul>
     *
     * @param mainStage Stage-ul primar al aplicatiei JavaFX.
     * @throws Exception Dac&#x103; apare o eroare la Incarcarea resurselor FXML sau CSS.
     * @author Simone.
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


        mainStage.initStyle(StageStyle.UNDECORATED);
        mainStage.setScene(scene);
        mainStage.show();

    }

    /**
     * The entry point of application.
     * <p> Metoda care lanseaza aplicatia </p>
     * @author George
     * @param args Argumente input
     */
    public static void main(String[] args) {

        launch(args);
    }
}
