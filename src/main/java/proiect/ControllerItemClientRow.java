package proiect;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.concurrent.Executor;

public class ControllerItemClientRow{
    @FXML
    Text clientName;

    public ControllerLibrarian mainController;
    @FXML
    void initialize() {

    }
    @FXML
    public void setName(String name)
    {
        clientName.setText(name);
    }

    @FXML
    public void showClientDetails(MouseEvent mouseEvent) {
        mainController.setOnlyMenu(mainController.client_menu_details);
        mainController.clienti_menu_clientName.setText(clientName.getText());
    }
}
