package proiect;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class LibrarianMainGUI extends Application {

    @Override
    public void start(Stage stage) throws IOException {

        //load fxml files
        Parent root = FXMLLoader.load(getClass().getResource("fxml/LibrarianMain.fxml"));

        //create GUIs
        Scene mainGUI = new Scene(root);

        //set main GUI
        stage.setTitle("LibraryApp");
        stage.setScene(mainGUI);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}