package proiect;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * The ControllerClient class extends the ControllerLibrarian class and is responsible
 * for managing client-related menus and operations within the library management system.
 * This class handles displaying and interacting with client reservation and inventory
 * data, along with providing functionality for book selection and management.
 */
public class ControllerClient extends ControllerLibrarian{
    //Meniu Clienti
    @FXML VBox list_search_clients;
    @FXML TextField textField_clientName;

    //Meniu Client_Detalii
    @FXML Text menu_transaction_clientName;
    @FXML VBox vbox_books_reserved;
    @FXML VBox vbox_books_inventory;
    @FXML Button btn_efectueaza;
    @FXML Button btn_rezervari_all;
    @FXML Button btn_rezervari_clear;
    @FXML Button btn_inventar_all;
    @FXML Button btn_inventar_clear;
    @FXML Button btn_add_book;
    public String transaction_user_id = "";
    public String add_book_id = "";
    public ObservableList<ControllerItemBookReservedRow> list_books_reserved = FXCollections.observableArrayList();
    public ObservableList<ControllerItemBookInventoryRow> list_books_inventory = FXCollections.observableArrayList();
    public List<ControllerItemBookReservedRow> list_selected_reserved_books = new ArrayList<>();
    public List<ControllerItemBookInventoryRow> list_selected_inventory_books = new ArrayList<>();

    /**
     * Initializes the client details menu by resetting the data, checking database connection,
     * and populating the reserved and inventory book lists based on the user ID provided.
     *
     * @param userID The unique identifier of the client whose data is being initialized
     * @throws SQLException if a database access error occurs
     * @throws IOException if an I/O error occurs during loading FXML resources
     */
    void initialize_menu_client_detalii(String userID) throws SQLException, IOException {
        setOnlyMenu(client_menu_details);
        btn_efectueaza.setDisable(true);

        //resetarea datelor
        vbox_books_reserved.getChildren().clear();
        list_books_reserved.clear();
        list_selected_reserved_books.clear();
        vbox_books_inventory.getChildren().clear();
        list_books_inventory.clear();
        list_selected_inventory_books.clear();
        //tableView_transaction.getItems().clear();

        //Verificare conexiune
        if(connection.getConnection()==null) {
            connection.setFailed(err_connection_null);
            return;
        }

        ResultSet rs = connection.executeQuery("SELECT carte.idCarte, carte.titluCarti, carte.autorCarte, carte.genCarte, borrow.statusImprumut\n" +
                "FROM mydb.carte carte, mydb.cartiimprumutate borrow\n" +
                "WHERE carte.idCarte = borrow.Carte_idCarte AND borrow.User_idUser = " + userID + " LIMIT 100;");
        while(rs.next())
        {
            String bookId = rs.getString("idCarte");
            String bookTitle = rs.getString("titluCarti");
            String bookAuthor = rs.getString("autorCarte");
            String bookGenre = rs.getString("genCarte");
            String borrowStatus = rs.getString("statusImprumut");

            if(borrowStatus.toUpperCase().equals("REZERVAT"))
            {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/ItemBookReservedRow.fxml"));
                Node node = loader.load();
                ControllerItemBookReservedRow controller = loader.getController();
                controller.mainController = this;
                controller.setData(bookId, bookTitle, bookAuthor, bookGenre);
                vbox_books_reserved.getChildren().add(node);
                list_books_reserved.add(controller);
            }

            if(borrowStatus.toUpperCase().equals("INVENTAR"))
            {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/ItemBookInventoryRow.fxml"));
                Node node = loader.load();
                ControllerItemBookInventoryRow controller = loader.getController();
                controller.mainController = this;
                controller.setData(bookId, bookTitle, bookAuthor, bookGenre);
                vbox_books_inventory.getChildren().add(node);
                list_books_inventory.add(controller);
            }

        }
    }

    /**
     * Handles the selection of all reserved books by toggling their visibility and enabling associated
     * UI actions. This method iterates through all reserved book items, sets their button icon visibility
     * to true, adds them to the selected list if not already present, and updates their status to enabled.
     * Additionally, it ensures that the action button is enabled if there are any selected items in either
     * the reserved or inventory lists.
     *
     * @param mouseEvent The mouse event that triggered the selection of all reserved books.
     */
    @FXML void SelectAllFromRezervari(MouseEvent mouseEvent) {
        for(ControllerItemBookReservedRow reserved : list_books_reserved)
        {
            reserved.btn_add_icon.setVisible(true);
            if(list_selected_reserved_books.contains(reserved)==false)
                list_selected_reserved_books.add(reserved);
            reserved.setEnabled();
        }
        if(list_selected_reserved_books.size()!=0 || list_selected_inventory_books.size()!=0)
            btn_efectueaza.setDisable(false);
    }

    /**
     * Deselects all reserved books in the reservation list by hiding associated icons,
     * updating their status to disabled, and removing them from the list of selected books.
     * If no books remain selected in either the reserved list or the inventory list,
     * the action button is disabled.
     *
     * @param mouseEvent The mouse event that triggered the deselection of reserved books.
     */
    @FXML void SelectNoneFromRezervari(MouseEvent mouseEvent) {
        for(ControllerItemBookReservedRow reserved : list_books_reserved)
        {

            reserved.btn_add_icon.setVisible(false);
            if(list_selected_reserved_books.contains(reserved)==true)
                list_selected_reserved_books.remove(reserved);
            reserved.setDisabled();
        }
        if(list_selected_inventory_books.size()==0 && list_selected_reserved_books.size()==0)
            btn_efectueaza.setDisable(true);
    }

    /**
     * Handles the selection of all items from the inventory list. This method iterates
     * through all book items in the inventory, makes their add button icon visible,
     * adds them to the selected inventory books list if they are not already present,
     * and updates their status to enabled. Additionally, the action button is enabled
     * if any books are selected in either the reserved or inventory lists.
     *
     * @param mouseEvent The mouse event that triggered the selection of all inventory books.
     */
    @FXML void SelectAllFromInventar(MouseEvent mouseEvent) {
        for(ControllerItemBookInventoryRow borrowed : list_books_inventory)
        {
            borrowed.btn_add_icon.setVisible(true);
            if(list_selected_inventory_books.contains(borrowed)==false)
                list_selected_inventory_books.add(borrowed);
            borrowed.setEnabled();
        }
        if(list_selected_reserved_books.size()!=0 || list_selected_inventory_books.size()!=0)
            btn_efectueaza.setDisable(false);
    }

    /**
     * Deselects all items in the inventory list by hiding associated icons, updating their status
     * to disabled, and removing them from the list of selected inventory books. If no books remain
     * selected in either the reserved or inventory lists, the action button is disabled.
     *
     * @param mouseEvent The mouse event that triggered the deselection of inventory items.
     */
    @FXML void SelectNoneFromInventar(MouseEvent mouseEvent) {
        for(ControllerItemBookInventoryRow borrowed : list_books_inventory)
        {
            borrowed.btn_add_icon.setVisible(false);
            if(list_selected_inventory_books.contains(borrowed)==true)
                list_selected_inventory_books.remove(borrowed);
            borrowed.setDisabled();
        }
        if(list_selected_inventory_books.size()==0 && list_selected_reserved_books.size()==0)
            btn_efectueaza.setDisable(true);
    }

    /**
     * Adds a book to the reservation list by initializing and displaying
     * the popup menu for adding books. This method checks the database
     * connection before allowing the user to search and select books to add.
     *
     * @param mouseEvent The mouse event that triggers the action of
     *                   adding a book to the reservation list.
     * @throws SQLException If a database access error occurs during the
     *                      initialization of the popup menu or book update.
     */
    @FXML void AddBookToRezervari(MouseEvent mouseEvent) throws SQLException {
        initialize_popup_menu_add_book();
    }
}
