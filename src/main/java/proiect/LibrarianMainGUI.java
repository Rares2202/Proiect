package proiect;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class LibrarianMainGUI extends Application {
    private double xOffset = 0;
    private double yOffset = 0;

    @Override
    public void start(Stage mainStage) throws IOException {

        //load fxml file
        Parent window = FXMLLoader.load(getClass().getResource("fxml/librarian/LibrarianMain.fxml"));

        //create GUI
        Scene scene = new Scene(window);
        scene.getStylesheets().add(this.getClass().getResource("css/style.css").toExternalForm());

        //enable window moveing on screen
        window.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        window.setOnMouseDragged(event -> {

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