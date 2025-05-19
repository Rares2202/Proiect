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
 * The ControllerItemBookInventoryRow class handles the behavior and visual representation
 * of a row in the book inventory list for a librarian system. It is responsible for managing
 * the interaction logic when books are selected or deselected, as well as dynamically updating
 * the user interface based on the interaction state.
 *
 * This controller is tied to an FXML file and uses JavaFX components. It interacts with the
 * main librarian controller to manage selected books and associated actions.
 */
public class ControllerItemBookInventoryRow {

    public ControllerLibrarian mainController;
    public boolean selected;
    public String id;
    @FXML AnchorPane background;
    @FXML Label name;
    @FXML Label author;
    @FXML Label genre;
    @FXML Button btn_add;
    @FXML FontAwesomeIcon btn_add_icon;


    /**
     * Initializes the controller's components and sets their initial states.
     * This method is automatically invoked after the FXML file has been loaded.
     *
     * Specifically:
     * - Sets a transparent background style for the add button.
     * - Invokes the {@code setDisabled()} method to apply default styling
     *   and deselected states to components.
     * - Hides the {@code btn_add_icon} by setting its visibility to false.
     * - Initializes the {@code selected} field to false to indicate that
     *   no item is selected initially.
     */
    @FXML void initialize() {
        btn_add.setStyle("-fx-background-color: transparent;");
        //btn_add.setStyle("-fx-background-radius: 0;");
        setDisabled();
        btn_add_icon.setVisible(false);
        selected = false;
    }

    /**
     * Sets the data for a book inventory row, updating the internal properties
     * and UI components associated with the book's details.
     *
     * @param id     the unique identifier of the book
     * @param name   the name or title of the book
     * @param author the author of the book
     * @param genre  the genre of the book
     */
    public void setData(String id, String name, String author, String genre) {
        this.id = id;
        this.name.setText(name);
        this.author.setText(author);
        this.genre.setText(genre);
    }

    /**
     * Handles the action taken when the 'Add' button is clicked. Depending on the
     * current selection state of the item, this method enables or disables the
     * item, updates its visual appearance, and modifies the list of selected items
     * maintained by the controller. Additionally, it updates the state of an
     * external button, enabling or disabling it based on the current selection of
     * items.
     *
     * @param mouseEvent the MouseEvent object containing details about the 'Add' button click.
     */
    public void OnAddButtonClicked(MouseEvent mouseEvent) {

        if(!selected)
        {
            setEnabled();
            mainController.list_selected_inventory_books.add(this);
            mainController.btn_efectueaza.setDisable(false);
        }
        else
        {
            setDisabled();
            background.setStyle("-fx-background-color: rgba(255,0,0,0.25)");
            btn_add_icon.setFill(Color.WHITE);
            mainController.list_selected_inventory_books.remove(this);
            if(mainController.list_selected_reserved_books.size()==0 && mainController.list_selected_inventory_books.size()==0)
                mainController.btn_efectueaza.setDisable(true);
        }
    }

    /**
     * Handles the event triggered when the mouse cursor enters the area
     * of this component. If the component is not selected, it changes
     * the background color and makes an additional button icon visible.
     *
     * @param mouseEvent the MouseEvent object containing details about the mouse action
     */
    public void OnMouseEntered(MouseEvent mouseEvent) {
        if(!selected)
        {
            background.setStyle("-fx-background-color: rgba(255,0,0,0.25)");
            btn_add_icon.setVisible(true);
        }

    }

    /**
     * Handles the event triggered when the mouse cursor exits the area
     * of this component. If the component is not selected, it resets
     * the background color to white and hides an additional button icon.
     *
     * @param mouseEvent the MouseEvent object containing details about the mouse action
     */
    public void OnMouseExited(MouseEvent mouseEvent) {
        if(!selected)
        {
            background.setStyle("-fx-background-color: white");
            btn_add_icon.setVisible(false);
        }
    }

    /**
     * Updates the visual styling and state of the book inventory row to represent
     * an enabled or selected state. This method performs the following actions:
     *
     * - Changes the background color to red to highlight the enabled state.
     * - Sets the text color for the name, author, and genre labels to white.
     * - Updates the icon in the add button to display a "plus" symbol, with white color.
     **/
    void setEnabled() {
        background.setStyle("-fx-background-color: red");
        name.setStyle("-fx-text-fill: white");
        author.setStyle("-fx-text-fill: white");
        genre.setStyle("-fx-text-fill: white");
        btn_add_icon.setFill(Color.WHITE);
        btn_add_icon.setGlyphName(String.valueOf(FontAwesomeIcons.PLUS));
        selected = true;
    }

    /**
     * Updates the visual styling and state of the book inventory row to represent
     * a disabled or deselected state. This method performs the following actions:
     *
     * - Sets the background color of the row to white.
     * - Changes the text color of the name, author, and genre labels to black.
     * - Updates the icon in the add button to display a "minus" symbol,
     *   with red color indicating removal or deselection.
     * - Updates the internal state of the row by setting the `selected` field to false.
     */
    void setDisabled() {
        background.setStyle("-fx-background-color: white");
        name.setStyle("-fx-text-fill: black");
        author.setStyle("-fx-text-fill: black");
        genre.setStyle("-fx-text-fill: black");
        btn_add_icon.setFill(Color.RED);
        btn_add_icon.setGlyphName(String.valueOf(FontAwesomeIcons.MINUS));
        selected = false;
    }

    /**
     * Handles the event triggered when the mouse cursor enters the 'Add' button area.
     * This method updates the visual appearance of the button to provide feedback
     * to the user. Specifically, it changes the background color of the button and
     * updates the color of the button's icon.
     *
     * @param mouseEvent the MouseEvent object containing details about the mouse action
     */
    public void OnAddButtonEntered(MouseEvent mouseEvent) {
        btn_add.setStyle("-fx-background-color: rgb(75,0,0); -fx-cursor: hand;");
        btn_add_icon.setFill(Color.WHITE);
    }

    /**
     * Handles the event triggered when the mouse cursor exits the 'Add' button area.
     * This method updates the visual appearance of the button to provide feedback
     * to the user. Specifically, it resets the background color to transparent and
     * hides the mouse cursor. If the component is not selected, it also changes the
     * color of the button icon to red.
     *
     * @param mouseEvent the MouseEvent object containing details about the mouse action
     */
    public void OnAddButtonExited(MouseEvent mouseEvent) {
        btn_add.setStyle("-fx-background-color: transparent; -fx-cursor: none;");
        if(!selected)
            btn_add_icon.setFill(Color.RED);
    }
}

