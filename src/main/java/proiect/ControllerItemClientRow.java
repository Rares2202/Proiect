package proiect;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcons;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
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
import java.util.concurrent.ForkJoinPool;

/**
 * The ControllerItemClientRow class represents a JavaFX controller for managing
 * a client row in the user interface. It handles the display and interaction
 * logic for client-related data within a given row.
 */
public class ControllerItemClientRow {
    /**
     * Represents a JavaFX Text element used to display the name of a client.
     * This field is part of the user interface managed by the ControllerItemClientRow class.
     * It can display and update the client's name dynamically in the application.
     */
    @FXML
    Text clientName;
    public String id;
    public String name;
    public Label label_books_reserved;
    public Label label_books_inventory;
    public ControllerLibrarian mainController;

    /**
     * Initializes the current controller instance and sets up necessary configurations
     * or bindings for the associated user interface. This method is automatically invoked
     * by the JavaFX framework after the FXML file has been loaded and all @FXML-annotated
     * fields have been injected.
     */
    @FXML
    void initialize() {
    }

    /**
     * Sets the name of the client and updates the corresponding user interface element.
     *
     * @param name the name of the client to be displayed in the user interface
     */
    @FXML
    public void setName(String name) {
        clientName.setText(name);
    }

    /**
     * Displays the details of a client in the user interface by updating relevant
     * elements in the main controller. This method is triggered by a mouse event
     * and interacts with the main controller to set up client-specific data.
     *
     * @param mouseEvent the mouse event that triggers this method; typically associated
     *                   with a user action such as clicking on a client row
     * @throws SQLException if a database access error occurs
     * @throws IOException if an I/O error occurs during processing
     */
    @FXML
    public void showClientDetails(MouseEvent mouseEvent) throws SQLException, IOException {
        mainController.transaction_user_id = id;
        mainController.menu_transaction_clientName.setText(clientName.getText());
        mainController.initialize_menu_client_detalii(id);
    }
}
