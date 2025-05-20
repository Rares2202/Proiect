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
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;

public class ControllerItemReviewRow {
    public String review_id;
    public String review_rating;
    public String book_id;
    @FXML Text book_review;
    @FXML Text user_review;
    public Label txt_review;
    @FXML Button btn_remove_review;
    public ControllerLibrarian mainController;

    void initialize() {

    }

    @FXML
    public void setBook(String book) {
        book_review.setText(book);
    }

    @FXML
    public void setUser(String user) {
        user_review.setText(user);
    }

    @FXML
    public void OnRemoveReview(MouseEvent mouseEvent) throws SQLException {
        if(mainController!=null){
            mainController.deleteContext = "review";
            mainController.review_user_id = review_id;
            mainController.remove_review(review_id);
        }


    }
}
