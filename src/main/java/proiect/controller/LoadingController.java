package proiect.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

public class LoadingController {

    @FXML
    private void handleLoginButton(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/proiect/view/login.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
