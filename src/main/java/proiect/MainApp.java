package proiect;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


import java.util.Date;

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

    public static void main(String[] args) {
        launch(args);
    }
}
