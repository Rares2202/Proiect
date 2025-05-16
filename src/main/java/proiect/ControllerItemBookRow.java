package proiect;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

import java.sql.*;

public class ControllerItemBookRow extends book {
    public Label bookTitle;
    public Label authorBook;
    public Label genreBook;
    public Label quantityBook;
    public Label availableBook;
    public Button addBook;
    public Button removeBook;
    public ControllerLibrarian mainController;

    @FXML
    void initialize() {
    }

    @FXML
    public void handleAddBook(ActionEvent event) throws SQLException {
        if (mainController != null) {
            String currentBookId = id;
            String currentBookTitle = book_name;
            mainController.title_book.setText(currentBookTitle);
            mainController.menu_increase.setVisible(true);
            mainController.books_menu.setVisible(false);
            mainController.initialize_increase_button(currentBookId);
        }
    }

    @FXML
    public void handleRemoveBook(ActionEvent event) throws SQLException {
        if(mainController != null) {
            String currentBookId = id;
            String currentBookTitle = book_name;
            mainController.title_book1.setText(currentBookTitle);
            mainController.menu_decrease.setVisible(true);
            mainController.books_menu.setVisible(false);
            mainController.initialize_decrease_button(currentBookId);
        }
    }

}
