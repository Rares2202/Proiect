package proiect;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcons;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

/**
 * The ControllerItemBookReservedRow class manages the behavior and interaction of a UI component
 * representing a reserved book row item in a librarian application. This controller handles the
 * visual appearance, user interaction, and data binding of the reserved book row.
 *
 * Functionalities include displaying book details, tracking the selection state, and managing
 * event-driven interactions like mouse clicks and hover effects.
 */
public class ControllerItemBookReservedRow {

    public ControllerLibrarian mainController;

    public AnchorPane background;
    public Label name;
    public Label author;
    public Label genre;
    public Button btn_add;
    public boolean selected;
    public FontAwesomeIcon btn_add_icon;

    public String id;

    /**
     * Initializes the user interface components and sets the initial state
     * of the associated controls for the reserved book row.
     * This method is automatically invoked after the FXML file is loaded.
     *
     * Functionality includes:
     * - Setting the style of the "Add" button to be transparent.
     * - Hiding the "Add" button icon by default.
     * - Setting the initial state of the row as not selected.
     * - Invoking the `setDisabled` method to configure the default disabled state.
     */
    @FXML
    void initialize() {
        btn_add.setStyle("-fx-background-color: transparent;");
        //btn_add.setStyle("-fx-background-radius: 0;");
        setDisabled();
        btn_add_icon.setVisible(false);
        selected = false;
    }

    /**
     * Updates the data of the reserved book row by setting the provided book details.
     *
     * @param id The unique identifier of the book.
     * @param name The name of the book.
     * @param author The author of the book.
     * @param genre The genre of the book.
     */
    public void setData(String id, String name, String author, String genre)
    {
        this.id = id;
        this.name.setText(name);
        this.author.setText(author);
        this.genre.setText(genre);
    }

    /**
     * Handles the click event for the "Add" button in a reserved book row.
     * Toggles the selection state of the row and updates its visual appearance.
     * If the row is selected, it is added to the list of selected reserved books
     * and the "Efectueaza" button is enabled. If unselected, it is removed from
     * the list and the button status is updated accordingly.
     *
     * @param mouseEvent The mouse event triggered when the "Add" button is clicked.
     */
    public void OnAddButtonClicked(MouseEvent mouseEvent) {

            if(!selected)
            {
                setEnabled();
                mainController.list_selected_reserved_books.add(this);
                mainController.btn_efectueaza.setDisable(false);
            }
            else
            {
                setDisabled();
                background.setStyle("-fx-background-color: rgba(0,255,0,0.25)");
                btn_add_icon.setFill(Color.WHITE);
                mainController.list_selected_reserved_books.remove(this);
                if(mainController.list_selected_reserved_books.size()==0 && mainController.list_selected_inventory_books.size()==0)
                    mainController.btn_efectueaza.setDisable(true);
            }
    }

    /**
     * Handles the event when the mouse pointer enters the reserved book row.
     * Changes the background style to visually indicate a hover state
     * and makes the "Add" button icon visible if the row is not currently selected.
     *
     * @param mouseEvent The mouse event triggered when the pointer enters the row.
     */
    public void OnMouseEntered(MouseEvent mouseEvent) {
        if(!selected)
        {
            background.setStyle("-fx-background-color: rgba(0,255,0,0.25)");
            btn_add_icon.setVisible(true);
        }

    }

    /**
     * Handles the event when the mouse pointer exits the reserved book row.
     * Changes the background style to its default state and hides the "Add" button icon
     * if the row is not currently selected.
     *
     * @param mouseEvent The mouse event triggered when the pointer exits the row.
     */
    public void OnMouseExited(MouseEvent mouseEvent) {
        if(!selected)
        {
            background.setStyle("-fx-background-color: white");
            btn_add_icon.setVisible(false);
        }
    }

    /**
     * Configures the reserved book row as enabled and updates its visual appearance
     * to represent this state. This involves changing the background color, text color
     * of associated labels, and the icon of the "Add" button. Additionally, the selection
     * state of the row is set to true.
     *
     * Functionality:
     * - Sets the background color to green to indicate the enabled state.
     * - Updates the text color of labels (name, author, genre) to white for*/
    void setEnabled()
    {
        background.setStyle("-fx-background-color: green");
        name.setStyle("-fx-text-fill: white");
        author.setStyle("-fx-text-fill: white");
        genre.setStyle("-fx-text-fill: white");
        btn_add_icon.setFill(Color.WHITE);
        btn_add_icon.setGlyphName(String.valueOf(FontAwesomeIcons.MINUS));
        selected = true;
    }

    /**
     * Updates the appearance and state of the reserved book row to represent a disabled state.
     * This involves resetting styles and colors to their default values and marking the
     * row as unselected. Specifically:
     * - Sets the background color to white.
     * - Updates the text color of associated labels (name, author, genre) to black.
     * - Changes the "Add" button icon to a green plus sign.
     * - Sets the selection state of the row to false.
     */
    void setDisabled()
    {
        background.setStyle("-fx-background-color: white");
        name.setStyle("-fx-text-fill: black");
        author.setStyle("-fx-text-fill: black");
        genre.setStyle("-fx-text-fill: black");
        btn_add_icon.setFill(Color.GREEN);
        btn_add_icon.setGlyphName(String.valueOf(FontAwesomeIcons.PLUS));
        selected = false;
    }

    /**
     * Handles the mouse enter event for the "Add" button in the reserved book row.
     * Updates the button's background color and changes the color of the icon fill
     * to indicate that the button is in a hover state.
     *
     * @param mouseEvent The mouse event triggered when the pointer enters the "Add" button area.
     */
    public void OnAddButtonEntered(MouseEvent mouseEvent) {
        btn_add.setStyle("-fx-background-color: rgb(0,75,0); -fx-cursor: hand;");
        btn_add_icon.setFill(Color.WHITE);
    }

    /**
     * Handles the event when the mouse pointer exits the "Add" button area
     * in the reserved book row. Resets the button's appearance to its default
     * state by making the background color transparent and removing the cursor style.
     * Additionally, changes the icon fill color of the button to green if the row
     * is not selected.
     *
     * @param mouseEvent The mouse event triggered when the pointer exits the "Add" button area.
     */
    public void OnAddButtonExited(MouseEvent mouseEvent) {
        btn_add.setStyle("-fx-background-color: transparent; -fx-cursor: none;");
        if(!selected)
            btn_add_icon.setFill(Color.GREEN);
    }
}
