package proiect;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


import java.util.Date;
import java.util.Objects;

import static proiect.MainApp.ImageUtils.showImage;

/**
 * The type Main app.
 */
public class MainApp extends Application {


    private double xOffset = 0;
    private double yOffset = 0;

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
     * <li>Metoda pentru vizualizare imagine </li>
     */
    public class ImageUtils {

        /**
         * Show image.
         *
         * @param imagePath   the image path
         * @param windowTitle the window title
         * @param width       the width
         * @param height      the height
         */
        public static void showImage(String imagePath, String windowTitle, int width, int height) {
            javafx.application.Platform.runLater(() -> {
                try {
                    // Folosim clasa ImageUtils pentru încărcare
                    javafx.scene.image.Image image = new javafx.scene.image.Image(
                            ImageUtils.class.getResourceAsStream(imagePath) // <- CORECT
                    );

                    // Restul codului rămâne la fel
                    javafx.scene.image.ImageView imageView = new javafx.scene.image.ImageView(image);
                    imageView.setPreserveRatio(true);
                    imageView.setFitWidth(width - 20);

                    javafx.stage.Stage stage = new javafx.stage.Stage();
                    stage.setTitle(windowTitle);
                    stage.setScene(new javafx.scene.Scene(
                            new javafx.scene.layout.StackPane(imageView), width, height));
                    stage.show();

                } catch (Exception e) {
                    System.err.println("Eroare: " + e.getMessage());
                }
            });
        }
    }

    /**
     * Aici lansez aplicatia
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        //showImage("/proiect/css/plus.png","Title", 600, 600);
        launch(args);
    }
}
