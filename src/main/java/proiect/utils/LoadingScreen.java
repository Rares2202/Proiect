package proiect.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class LoadingScreen {

    private Stage stage;

    public LoadingScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/proiect/view/loading.fxml"));
            Parent root = loader.load();
            stage = new Stage();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void show() {
        if (stage != null) {
            stage.show();
        }
    }
}
