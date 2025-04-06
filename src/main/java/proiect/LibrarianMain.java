package proiect;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class LibrarianMain extends Application {
    private double xOffset = 0;
    private double yOffset = 0;

    @Override
    public void start(Stage mainStage) throws IOException {
        //load fxml file
        Parent mainAPP = FXMLLoader.load(getClass().getResource("fxml/idk.fxml"));

        //create GUI
        Scene scene = new Scene(mainAPP );
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
        mainStage.initStyle(StageStyle.UNDECORATED);
        mainStage.setScene(scene);
        mainStage.show();



    }

    public static void main(String[] args) {
        launch();
    }
}