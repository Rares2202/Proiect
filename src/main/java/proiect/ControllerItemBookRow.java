package proiect;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

import java.sql.*;

/**
 * ControllerItemBookRow is a controller class designed to manage the behavior
 * and interactions of individual book items represented as rows in the user interface.
 * It enables interaction with book attributes such as title, author, genre, quantity,
 * and availability. Additionally, it provides functionality to add or remove books
 * through associated buttons.
 *
 * The class assumes a connection with a parent controller, ControllerLibrarian,
 * to handle library operations such as increasing or decreasing the number of books.
 */
public class ControllerItemBookRow extends Book {
    public Label bookTitle;
    public Label authorBook;
    public Label genreBook;
    public Label quantityBook;
    public Label availableBook;
    public Button addBook;
    public Button removeBook;
    public ControllerLibrarian mainController;

    /**
     * Initializes the state of the current ControllerItemBookRow instance.
     * This method is automatically invoked by the JavaFX framework after the
     * associated FXML file has been loaded.
     *
     * Specifically:
     * - Hides the "Add Book" button to prevent it from being visible by default.
     * - Hides the "Remove Book" button to prevent it from being visible by default.
     */
    @FXML
    void initialize() {
        addBook.setVisible(false);
        removeBook.setVisible(false);
    }

    /**
     * Handles the action of adding a book to the system. When the associated
     * button is clicked, this method updates relevant UI elements and configures
     * the "increase" functionality in the parent controller. Specifically,
     * it displays the menu for increasing the book quantity, updates the
     * book title in the main controller, and initializes the button used
     * for this operation.
     *
     * @param event the ActionEvent triggered by the user interaction with the "Add Book" button
     * @throws SQLException if a database access error occurs while initializing the increase button
     */
    @FXML
    public void handleAddBook(ActionEvent event) throws SQLException {
        if (mainController != null) {
            String currentBookId = id;
            String currentBookTitle = book_name;
            mainController.title_book.setText(currentBookTitle);
            mainController.menu_increase.setVisible(true);
            mainController.initialize_increase_button(currentBookId);
        }
    }

    /**
     * Handles the removal of a book from the system when the user interacts with the associated UI button.
     * Updates the parent controller to reflect the removal by setting relevant fields, making the decrease menu visible,
     * and initializing the decrease functionality.
     *
     * @param event the event that triggers this handler, typically an action event from the UI
     * @throws SQLException if a database access error occurs during the execution of the decrease operation
     */
    @FXML
    public void handleRemoveBook(ActionEvent event) throws SQLException {
        if(mainController != null) {
            String currentBookId = id;
            String currentBookTitle = book_name;
            mainController.title_book1.setText(currentBookTitle);
            mainController.menu_decrease.setVisible(true);
            mainController.initialize_decrease_button(currentBookId);
            mainController.book_id = currentBookId;
        }
    }

    /**
     * Handles the event triggered when the mouse pointer enters the associated UI element.
     * This method ensures that the "Add Book" and "Remove Book" buttons*/
    public void onMouseEntered(MouseEvent mouseEvent) {
        addBook.setVisible(true);
        removeBook.setVisible(true);
    }

    /**
     * Handles the event triggered when the mouse pointer exits the associated UI element.
     * This method ensures that the "Add Book" and "Remove Book" buttons are hidden.
     *
     * @param mouseEvent the MouseEvent triggered when the mouse pointer leaves the UI element
     */
    public void onMouseExited(MouseEvent mouseEvent) {
        addBook.setVisible(false);
        removeBook.setVisible(false);
    }
}
