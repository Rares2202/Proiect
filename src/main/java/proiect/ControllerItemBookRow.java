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
import javafx.scene.control.Button;
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

public class ControllerItemBookRow extends book{
    public Label bookTitle;
    public Label authorBook;
    public Label genreBook;
    public Label quantityBook;
    public Label availableBook;
    public Button addBook;
    public Button removeBook;
    public ControllerLibrarian mainController;


    void initialize() {}
}
