package proiect;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.io.IOException;
import java.sql.*;
import java.util.*;


/**
 * The ControllerLibrarian class is responsible for managing the user interface and backend logic
 * of a library management system for a librarian role. It facilitates interactions with
 * various menus, popups, and functionalities such as client management, book inventory,
 * reservations, statistical analysis, transactions, and related UI components.
 *
 * This class handles the initialization of different components, updates of lists,
 * button actions, popup display, and execution of database-dependent operations
 * like searching, reserving, or updating book inventories.
 *
 * It provides methods to interact with reserved and inventory books, as well as to:
 * - Initialize and toggle main menus like client details, book management, and statistics.
 * - Handle user interactions such as adding or removing books, performing transactions,
 *   and validating the performed actions using popup confirmations.
 * - Generate and display statistical data related to library usage and inventory trends.
 *
 * The ControllerLibrarian ensures proper coordination between UI components and backend
 * operations, including database connectivity and error handling.
 */
public class ControllerLibrarian{

    //SQL
    public DatabaseConnection connection;
    String url = "jdbc:mysql://localhost:3306/mydb";
    /**
     * Represents the username for the database connection utilized by the librarian controller.
     * This variable stores the default username required for authentication within
     * the system and is primarily used to facilitate connections to the database.
     */
    String user = "root";
    /**
     * Represents the password used for authentication in the application.
     * It is initialized with the default value "root".
     */
    String pass = "simone";

    @FXML AnchorPane connectionFailed_menu;
    @FXML AnchorPane client_menu;
    @FXML AnchorPane client_menu_details;
    @FXML AnchorPane books_menu;
    @FXML AnchorPane statistici_menu;
    @FXML AnchorPane popup_menu_validation;
    @FXML AnchorPane popup_menu_add_book;
    @FXML AnchorPane popup_menu_add_carte;
    @FXML AnchorPane popup_error;
    @FXML AnchorPane menu_increase;
    @FXML AnchorPane menu_decrease;
    @FXML AnchorPane popup_info;
    @FXML AnchorPane popup_menu_review_user;
    public List<AnchorPane> listMenu = new ArrayList<>();

    //popups
    @FXML Label label_popup_info;
    @FXML Label label_popup_error;
    enum popup_info_case {ADD_BOOK, REMOVE_BOOK, NEW_BOOK, DELETE_BOOK, DELETE_USER, DELETE_REVIEWS, REMOVE_REVIEW};
    popup_info_case info_case;
    public String deleteContext = "";

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
    @FXML Button btn_delete_user;
    @FXML Button btn_review;
    public String transaction_user_id = "";
    public String add_book_id = "";
    public ObservableList<ControllerItemBookReservedRow> list_books_reserved = FXCollections.observableArrayList();
    public ObservableList<ControllerItemBookInventoryRow> list_books_inventory = FXCollections.observableArrayList();
    public List<ControllerItemBookReservedRow> list_selected_reserved_books = new ArrayList<>();
    public List<ControllerItemBookInventoryRow> list_selected_inventory_books = new ArrayList<>();

    //Popup meniu adaugare carti
    @FXML VBox list_search_books;
    @FXML TextField textField_add_book;

    //Popup meniu review
    @FXML VBox vbox_list_review;
    @FXML TextField review_search;
    @FXML Button btn_clear_review;
    @FXML Button btn_cancel_review;
    public String review_user_id = "";

    //Popup meniu de tranzactie
    @FXML AnchorPane popup_menu_tranzactie;
    @FXML TableView<ObservableList<Label>> tableView_transaction;
    @FXML Button btn_confirma_tranzactie;
    @FXML Button btn_anuleaza_tranzactie;

    //Meniu Carti
    @FXML VBox list_search_carti;
    @FXML TextField textField_bookTitle;
    @FXML Button addBase;
    public String book_id;
    public String book_name;
    @FXML Button btn_delete_book;

    //Popup adaugare cu success
    @FXML Button btn_error_OK;

    //Popup stergere cu success
    @FXML Button btn_info_OK;

    //Meniu adauga cantitate carte
    @FXML TextField numberUp;
    public Label title_book;
    public Button addIncrease;
    public Button cancelIncrease;

    //Meniu stergere cantitate/carte
    @FXML TextField numberDown;
    public Label title_book1;
    public Button removeDecrease;
    public Button cancelDecrease;


    //Popup meniu adaugare carte in baza de date
    @FXML TextField titleBar;
    @FXML TextField authorBar;
    @FXML TextField genreBar;
    @FXML TextField numberBar;
    @FXML TextField coverBar;
    @FXML TextField describeBar;
    @FXML Button bookAddBase;
    @FXML Button cancel;
//

    //Meniu Statistici
    @FXML VBox vbox_statistics_top_books;
    @FXML VBox vbox_statistics_clients;
    @FXML VBox vbox_statistics_books;
    @FXML PieChart piechart_statistics_genre;
    @FXML PieChart piechart_statistics_books1;
    @FXML PieChart piechart_statistics_clients;
    @FXML BarChart<Number, String> statistics_authors;
    @FXML Pane pane_top_genre;
    @FXML CategoryAxis CategoryAxis_statistics_authors;
    @FXML NumberAxis NumberAxis_statistics_authors;
    @FXML Pane pane_statistics_clients;
    @FXML Pane pane_statistics_books;
    Color color1 = Color.rgb(17,108,0);
    Color color2 = Color.rgb(40,243,40);
    Color color3 = Color.rgb(128,218,131);
    Color color4 = Color.rgb(183,180,180);
    Color color5 = Color.rgb(46,46,46);
    Color[] colors = {color1, color2, color3, color4, color5};

    //Mesaje pentru erori
    String err_connection_null = "Nu exista conexiune. (ControllerLibrarian.connection = null)";

    /**
     * Initializes the necessary components and configurations for the application.
     *
     * - Prepares and organizes the menu elements into the `listMenu` for visibility management.
     * - Configures the `TableView` component (`tableView_transaction`) to properly handle
     *   transaction records with custom column settings.
     * - Establishes the database connection using the provided credentials and URL.
     * - Ensures the initial state of the application by loading the statistics menu and
     *   making it visible, while hiding other menus.
     *
     * @throws SQLException if a database access error occurs during initialization.
     */
    @FXML void initialize() throws SQLException {
        //initializare lista cu toate meniurile aplicatiei
        listMenu.add(connectionFailed_menu);
        listMenu.add(client_menu);
        listMenu.add(client_menu_details);
        listMenu.add(books_menu);
        listMenu.add(statistici_menu);
        listMenu.add(popup_menu_add_book);
        listMenu.add(popup_menu_tranzactie);
        listMenu.add(popup_menu_validation);
        listMenu.add(popup_info);
        listMenu.add(popup_menu_add_carte);
        listMenu.add(popup_error);
        listMenu.add(menu_increase);
        listMenu.add(menu_decrease);
        listMenu.add(popup_menu_review_user);

        //initailizare TableView pentru tranzactionarea imprumuturilor/retururilor
        ObservableList<TableColumn<ObservableList<Label>, ?>> rawCols = tableView_transaction.getColumns();
        for (int i = 0; i < rawCols.size(); i++) {
            final int colIndex = i;
            TableColumn<ObservableList<Label>, ?> rawCol = rawCols.get(i);
            TableColumn<ObservableList<Label>, Label> col = (TableColumn<ObservableList<Label>, Label>) rawCol;
            col.setText("");
            col.setReorderable(false);
            col.setEditable(false);
            col.setCellValueFactory(data ->
                    new SimpleObjectProperty<>(data.getValue().get(colIndex))
            );
            col.setStyle("-fx-background-color: white;");
        }
        tableView_transaction.setSelectionModel(null);

        //Crearea conexiune
        connection = new DatabaseConnection(url, user, pass, this);

        //nu e deshis niciun meniu la deschiderea aplicatiei
        initialize_statistics_menu();
        setOnlyMenu(statistici_menu);
    }

    /**
     * Handles the application quit event, ensuring proper resource cleanup and termination.
     * This method closes the active database connection, prints a shutdown message to
     * the console, and terminates the application.
     *
     * @param mouseEvent the MouseEvent that triggers the application quit action.
     */
    public void quit_app(MouseEvent mouseEvent) {
        connection.close();
        System.out.print("\nInchidere aplicatie.");
        System.exit(0);
    }

    /**
     * Sets the visibility of all menus in the application to false, except for the specified menu.
     * If the specified menu is not null, it will be made visible.
     *
     * @param newMenu the AnchorPane to be made visible. If null, all menus remain invisible.
     */
    public void setOnlyMenu(AnchorPane newMenu) {
        for(AnchorPane menu: listMenu)
            menu.setVisible(false);
        if (newMenu!=null)
            newMenu.setVisible(true);
    }

    /**
     * Handles the client's menu button click event.
     * Opens the client menu, verifies database connection, and configures the
     * functionality related to the client search text field. If the client menu
     * is already visible or if the database connection is null, the method terminates early.
     *
     * @param mouseEvent the MouseEvent that triggers the client menu button click action.
     * @throws SQLException if a database access error occurs.
     */
    // MAIN MENU BUTTONS FUNCTIONS
    @FXML void OnClientiButtonClicked(MouseEvent mouseEvent) throws SQLException {
        if(client_menu.isVisible())
            return;

        //verificam conexiunea pentru afisarea meniului cu clienti
        if(connection.getConnection()==null)
        {
            connection.setFailed(err_connection_null);
            return;
        }

        setOnlyMenu(client_menu);
        update_list_clients("");

        textField_clientName.setOnKeyPressed(event -> {
            if(event.getCode().equals(KeyCode.ENTER))
                try {
                    System.out.print("ENTER");
                    update_list_clients(textField_clientName.getText());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
        });
    }

    /**
     * Updates the list of clients displayed in the interface by fetching and filtering records
     * from the database based on the input text. Clears the current client list, fetches matching
     * results, loads the corresponding FXML components, and displays the updated list.
     *
     * The method uses the supplied search text to query the database for clients whose names or
     * IDs match the text (case-insensitive). It limits the number of results to 100.
     *
     * @param text the input search text used to filter client records from the database.
     * @throws SQLException if a database access error occurs during the query execution.
     */
    void update_list_clients(String text) throws SQLException {
        if(connection.getConnection()==null)
        {
            connection.setFailed(err_connection_null);
            return;
        }

        //initializare
        text = text.toUpperCase();
        list_search_clients.getChildren().clear();
        ResultSet rs = connection.executeQuery("SELECT idUser, userName\n" +
                                                "FROM user\n" +
                                                "WHERE UPPER(userName) LIKE '%" + text + "%' \n" +
                                                "OR UPPER(idUser) LIKE '%" + text + "%' \n" +
                                                "ORDER BY userName LIMIT 100;");

        while(rs.next())
        {
            String _name = rs.getString("userName");
            String userId = rs.getString("idUser");

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/ItemClientRow.fxml"));
                Node node = loader.load();
                ControllerItemClientRow controller = loader.getController();
                controller.id = userId;
                controller.name = _name;
                controller.label_books_inventory.setText("b");
                controller.label_books_reserved.setText("a");

                controller.label_books_reserved.setText(""+connection.getQueryCount("SELECT status \n" +
                                                                                    "FROM cartiimprumutate\n" +
                                                                                    "WHERE User_idUser = " + userId + " AND UPPER(status) = 'REZERVAT';"));
                controller.label_books_inventory.setText(""+connection.getQueryCount("SELECT status \n" +
                                                                                    "FROM cartiimprumutate\n" +
                                                                                    "WHERE User_idUser = " + userId + " AND UPPER(status) = 'INVENTAR';"));


                controller.setName(String.format("%s (#%s)", _name, userId));
                controller.mainController = this;
                list_search_clients.getChildren().add(node);
            }
            catch (Exception e) {
                System.err.print("\nNu s-a putut genera client(FXML)");
                e.printStackTrace();
            }
        }
    }

    /**
     * Handles the click event for the books menu button and manages the visibility and functionality of the books menu.
     *
     * This method performs the following actions:
     * - Checks if the books menu (`books_menu`) is already visible. If yes, it exits early.
     * - Verifies the database connection. If the connection is null, triggers a failure action through `connection.setFailed`.
     * - Makes the books menu visible by calling `setOnlyMenu` and initializes the book list display.
     * - Configures key press events on the books search text field (`textField_bookTitle`)
     *   to update the list of books when the Enter key is pressed.
     * - Configures mouse click events for adding a new book by initializing the popup menu for adding books.
     *
     * @param mouseEvent the MouseEvent that triggers the books menu button click action.
     * @throws SQLException if a database access error occurs during menu initialization or book updates.
     */
    @FXML void OnCartiButtonClicked(MouseEvent mouseEvent) throws SQLException {
        if(books_menu.isVisible())
            return;

        //verificam conexiunea pentru afisarea meniului cu carti
        if(connection.getConnection()==null)
        {
            connection.setFailed(err_connection_null);
            return;
        }
        setOnlyMenu(books_menu);
        update_list_books("");

        textField_bookTitle.setOnKeyPressed(event -> {
            if(event.getCode().equals(KeyCode.ENTER))
                try{
                    System.out.print("ENTER");
                    update_list_books(textField_bookTitle.getText());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
        });

        addBase.setOnMouseClicked(event -> {
            try {
                initialize_popup_menu_carte();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Updates the list of books displayed in the interface by fetching and filtering records
     * from the database based on the input text. This method clears the current book list,
     * executes a case-insensitive query for books whose titles, authors, or IDs match the
     * provided text, and limits the result set to a maximum of 100 records.
     *
     * For each result, it initializes an FXML component for the book row, populates
     * its details using the corresponding database record, and adds it to the display list.
     *
     * If the database connection is null, the method sets a failure state and stops execution.
     *
     * @param text the input search text used to filter the book records from the database.
     *             This value is automatically converted to uppercase for case-insensitive matching.
     * @throws SQLException if a database access error occurs during query execution.
     */
    void update_list_books(String text) throws SQLException {
        if(connection.getConnection()==null)
        {
            connection.setFailed(err_connection_null);
            return;
        }

        text=text.toUpperCase();
        list_search_carti.getChildren().clear();
        ResultSet rs = connection.executeQuery("SELECT idCarte\n, titluCarti\n, autorCarte\n, numarCarte\n, genCarte\n" +
                "FROM carte\n" +
                "WHERE UPPER(titluCarti) LIKE '%" + text + "%'\n" +
                "OR UPPER(autorCarte) LIKE '%" + text + "%' \n" +
                "OR UPPER(idCarte) LIKE '%" + text + "%' \n" +
                "ORDER BY idCarte LIMIT 100;");
        while(rs.next()){
            int id = rs.getInt("idCarte");
            String _titlu = rs.getString("titluCarti");
            String _author = rs.getString("autorCarte");
            String _genre = rs.getString("genCarte");
            String _num = rs.getString("numarCarte");
            int numar = Integer.parseInt(_num);
            int reserved = connection.getQueryCount("SELECT * FROM cartiimprumutate WHERE Carte_idCarte = '" + id + "'");
            int stoc = numar - reserved;

            try{
                FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/ItemBookRow.fxml"));
                Node node = loader.load();
                ControllerItemBookRow controller = loader.getController();

                controller.id = String.valueOf(id);
                controller.bookTitle.setText(_titlu);
                controller.authorBook.setText(_author);
                controller.genreBook.setText(_genre);
                controller.quantityBook.setText(_num);
                controller.availableBook.setText(String.valueOf(stoc));

                controller.book_name = _titlu;

                controller.mainController = this;
                list_search_carti.getChildren().add(node);
            }
            catch (Exception e) {
                System.err.print("\nNu s-a putut genera book(FXML)");
                e.printStackTrace();
            }
        }
    }

    /**
     * Handles the click event for the "Statistici" button and initializes the statistics menu.
     *
     * This method performs the following actions:
     * - Calls `initialize_statistics_menu` to configure and load various panes
     *   (such as clients, books, genres, authors, and top books) in the statistics menu.
     * - Makes the statistics menu visible while hiding other menus.
     *
     * @param mouseEvent the MouseEvent that triggers the "Statistici" button click action.
     * @throws SQLException if a database access error occurs during the initialization process.
     */
    @FXML void OnStatisticiButtonClicked(MouseEvent mouseEvent) throws SQLException {
        initialize_statistics_menu();
    }

    /**
     * Displays a popup with an informational message and sets the context for the information type.
     *
     * This method updates the popup label with the provided message, configures the specific
     * informational context using the given enum value, and makes the popup visible to the user.
     *
     * @param message the message to be displayed in the popup.
     * @param newCase the context or type of information, specified as a value of the {@code popup_info_case} enum.
     */
    // POPUPS
    void show_popup_info(String message, popup_info_case newCase) {
        label_popup_info.setText(message);
        info_case = newCase;
        popup_info.setVisible(true);
    }

    /**
     * Displays a popup with an error message.
     *
     * This method updates the popup's label with the provided error message,
     * and makes the error popup visible to the user.
     *
     * @param message the error message to be displayed in the popup.
     */
    void show_popup_error(String message){
        label_popup_error.setText(message);
        popup_error.setVisible(true);
    }

    /**
     * Initializes the "Client Details" menu by setting up the UI components, clearing previous data,
     * fetching the relevant book details for the user, and populating the reserved and inventory sections
     * of the menu with the retrieved data.
     *
     * @param userID The unique identifier of the user whose book data is to be displayed.
     * @throws SQLException If a database access error occurs while querying book details.
     * @throws IOException If there is an error loading UI components or FXML files.
     */
    // CLIENT_DETAILS MENU FUNCTIONS
    void initialize_menu_client_detalii(String userID) throws SQLException, IOException {
        setOnlyMenu(client_menu_details);
        btn_efectueaza.setDisable(true);
        btn_efectueaza.setOnMouseClicked(mouseEvent -> {
            popup_menu_validation.setVisible(true);
        });

        //resetarea datelor
        vbox_books_reserved.getChildren().clear();
        list_books_reserved.clear();
        list_selected_reserved_books.clear();
        vbox_books_inventory.getChildren().clear();
        list_books_inventory.clear();
        list_selected_inventory_books.clear();

        //Verificare conexiune
        if(connection.getConnection()==null) {
            connection.setFailed(err_connection_null);
            return;
        }

        ResultSet rs = connection.executeQuery("SELECT carte.idCarte, carte.titluCarti, carte.autorCarte, carte.genCarte, borrow.status\n" +
                "FROM mydb.carte carte, mydb.cartiimprumutate borrow\n" +
                "WHERE carte.idCarte = borrow.Carte_idCarte AND borrow.User_idUser = " + userID + " LIMIT 100;");
        while(rs.next())
        {
            String bookId = rs.getString("idCarte");
            String bookTitle = rs.getString("titluCarti");
            String bookAuthor = rs.getString("autorCarte");
            String bookGenre = rs.getString("genCarte");
            String borrowStatus = rs.getString("status");

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
     * Handles the UI interaction to select all items in the reservation list when triggered by a mouse event.
     * Each reserved item in the list becomes enabled, and its corresponding "add" icon is made visible.
     * Items that are not already included in the selected reserved books list are added to it.
     * Additionally, enables the action button if there are selected items in either
     * the reserved books list or inventory books list.
     *
     * @param mouseEvent the MouseEvent that triggers this method, typically a click event
     */
    @FXML
    void SelectAllFromRezervari(MouseEvent mouseEvent) {
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
     * Deselects all items from the list of reserved books and disables the associated UI elements.
     * This method hides the add button for each reserved book, removes the book from the selection list,
     * and disables the selection functionality for these books. If no books are selected in both
     * the inventory and reserved lists, the confirm button is disabled.
     *
     * @param mouseEvent The MouseEvent triggered by interacting with the UI element that calls this method.
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
     * Handles the selection of all items from the inventory when a mouse event occurs.
     * Updates the visibility and state of the respective inventory items and adjusts
     * the enabled state of related UI components accordingly.
     *
     * @param mouseEvent the MouseEvent triggered by the user's interaction
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
     * Deselects all items in the inventory by hiding their associated 'add' icons,
     * removing them from the selected inventory books list, and disabling them.
     * If no items remain selected in both the inventory and reserved lists,
     * a button action is disabled.
     *
     * @param mouseEvent the mouse event that triggers the deselection operation
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
     * Handles the event of adding a book to the reservations list.
     * Initializes the popup menu associated with adding a book.
     *
     * @param mouseEvent the mouse event that triggered this method
     * @throws SQLException if a database access error occurs
     */
    @FXML void AddBookToRezervari(MouseEvent mouseEvent) throws SQLException {
        initialize_popup_menu_add_book();
    }
    @FXML void DeleteUser(MouseEvent mouseEvent) throws SQLException {
        popup_menu_validation.setVisible(true);
        ResultSet rs = connection.executeQuery("SELECT u.idUser, c.Carte_idCarte " +
                "FROM user u, cartiimprumutate c " +
                "WHERE u.idUser = " + transaction_user_id);
        try{
            String book_id = null;
            if(rs.next()){
                book_id = rs.getString("c.Carte_idCarte");
            }
            int reserved_book = connection.getQueryCount("SELECT * FROM cartiimprumutate WHERE User_idUser = " + transaction_user_id + " AND UPPER(status)='INVENTAR'");
            if(reserved_book!=0){
                show_popup_error("Utilizatorul are o carte rezervata!");
            }
            else{
                connection.executeUpdate("DELETE FROM cartiimprumutate WHERE Carte_idCarte = " + book_id + " AND User_idUser = " + transaction_user_id);
                connection.executeUpdate("DELETE FROM review WHERE User_idUser = " + transaction_user_id);
                connection.executeUpdate("DELETE FROM user WHERE idUser = " + transaction_user_id);
                update_list_clients("");
                show_popup_info("Utilizatorul a fost sters cu succes!",popup_info_case.DELETE_USER);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        /*
        ALTER TABLE cartiimprumutate
        DROP FOREIGN KEY fk_CartiImprumutate_User;

        ALTER TABLE cartiimprumutate
        ADD CONSTRAINT fk_CartiImprumutate_User
        FOREIGN KEY (User_idUser) REFERENCES user(idUser)
        ON DELETE CASCADE;
         */
    }

    //Meniu reviews
    void initialize_menu_user_review(String userID) throws SQLException {
        setOnlyMenu(popup_menu_review_user);

        if(connection.getConnection()==null){
            connection.setFailed(err_connection_null);
            return;
        }
        vbox_list_review.getChildren().clear();
        userID = transaction_user_id;
        ResultSet rs = connection.executeQuery("SELECT r.idReview, r.reviewText, r.reviewRating, r.User_idUser, r.Carte_idCarte, c.idCarte, c.titluCarti\n" +
                " FROM review r, carte c\n" +
                " WHERE r.User_idUser = " + userID + " AND c.idCarte = r.Carte_idCarte;");
        while(rs.next()){
            String id_review = rs.getString("r.idReview");
            String reviewText = rs.getString("reviewText");
            String reviewRating = rs.getString("r.reviewRating");
            String bookTitle = rs.getString("c.titluCarti");
            String bookId = rs.getString("r.Carte_idCarte");

            try{
                FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/ItemReviewRow.fxml"));
                Node node = loader.load();
                ControllerItemReviewRow controller = loader.getController();

                System.out.println("Controller: " + controller);

                controller.review_id = id_review;
                controller.review_rating = reviewRating;
                controller.book_id = bookId;

                controller.setBook(String.format("#%s", id_review));
                controller.setUser(String.format("%s(#%s) : %s/5", bookTitle, bookId, reviewRating));
                controller.txt_review.setText(reviewText);

                controller.mainController = this;
                vbox_list_review.getChildren().add(node);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }
    @FXML void ViewReviews(MouseEvent mouseEvent) throws SQLException {
        if(popup_menu_review_user.isVisible()){
            return;
        }

        if(connection.getConnection()==null){
            connection.setFailed(err_connection_null);
            return;
        }
       initialize_menu_user_review(transaction_user_id);
        popup_menu_review_user.setVisible(true);
        review_search.setOnKeyPressed(event -> {
            if(event.getCode().equals(KeyCode.ENTER))
                try{
                    System.out.print("ENTER");
                    update_user_review(review_search.getText());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
        });
        btn_cancel_review.setOnMouseClicked(event -> {
            popup_menu_review_user.setVisible(false);
            client_menu_details.setVisible(true);
        });

    }
    void update_user_review(String text) throws SQLException {
        if(connection.getConnection()==null)
        {
            connection.setFailed(err_connection_null);
            return;
        }
        text=text.toUpperCase();
        vbox_list_review.getChildren().clear();
        String query = "SELECT r.idReview, r.reviewText, r.reviewRating, r.User_idUser, r.Carte_idCarte, c.idCarte, c.titluCarti " +
                "FROM review r JOIN carte c ON r.Carte_idCarte = c.idCarte " +
                "WHERE r.User_idUser = " + transaction_user_id;

        if (!text.isEmpty()) {
            query += " AND (UPPER(c.titluCarti) LIKE '%" + text + "%' OR UPPER(r.idReview) LIKE '%" + text + "%')";
        }

        query += " ORDER BY r.idReview LIMIT 100;";

        ResultSet rs = connection.executeQuery(query);

        while(rs.next()){
            String id_review = rs.getString("r.idReview");
            String reviewText = rs.getString("reviewText");
            String reviewRating = rs.getString("r.reviewRating");
            String bookTitle = rs.getString("c.titluCarti");
            String bookId = rs.getString("r.Carte_idCarte");

            try{
                FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/ItemReviewRow.fxml"));
                Node node = loader.load();
                ControllerItemReviewRow controller = loader.getController();

                controller.review_id = id_review;
                controller.review_rating = reviewRating;
                controller.book_id = bookId;

                controller.setBook(String.format("#%s", id_review));
                controller.setUser(String.format("%s(#%s) : %s/5", bookTitle, bookId, reviewRating));

                controller.txt_review.setText(reviewText);

                controller.mainController = this;
                vbox_list_review.getChildren().add(node);
            } catch (Exception e) {
                System.err.print("\nNu s-a putut genera client(FXML)");
                e.printStackTrace();
            }
        }

    }
    @FXML void ClearAllReviews(MouseEvent mouseEvent) throws SQLException {
        deleteContext = "all_reviews";
        popup_menu_validation.setVisible(true);
    }
    public void remove_review(String id_review) throws SQLException {
        deleteContext = "review";
        popup_menu_validation.setVisible(true);
    }

    /**
     * Initializes the popup menu for adding books.
     *
     * This method sets up the functionality for a popup menu that allows users
     * to search for and add books. It checks the database connection, sets up
     * the search functionality to react on a 'Enter' key press, and makes the
     * popup menu visible.
     *
     * @throws SQLException if a database access error occurs while updating the book list.
     */
        // POPUP ADD BOOK
        @FXML void initialize_popup_menu_add_book() throws SQLException {
            //verificam conexiunea
            if(connection.getConnection()==null)
            {
                connection.setFailed(err_connection_null);
                return;
            }
            update_list_add_book("");
            textField_add_book.setOnKeyPressed(keyEvent -> {
                if(keyEvent.getCode() == KeyCode.ENTER)
                    try {
                        update_list_add_book(textField_add_book.getText());
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
            });

            popup_menu_add_book.setVisible(true);
        }

    /**
     * Updates the list of books displayed in the UI based on the search text provided.
     * The method retrieves book data from the database, filters it by the provided text,
     * and dynamically updates a visual VBox element with matching books along with their availability status.
     *
     * @param text A string containing the search keyword. The search is case-insensitive
     *             and matches book titles, authors, and genres that contain the keyword.
     * @throws SQLException If a database access error occurs while executing queries.
     */
    void update_list_add_book(String text) throws SQLException {
            //initializare
            list_search_books.getChildren().clear();
            text = text.toUpperCase();
            ResultSet rs = connection.executeQuery("SELECT idCarte, titluCarti, autorCarte, genCarte, numarCarte\n" +
                    "FROM carte\n" +
                    "WHERE UPPER(titluCarti) LIKE '%" + text + "%' \n" +
                    "OR UPPER(autorCarte) LIKE '%" + text + "%'\n" +
                    "OR UPPER(genCarte) LIKE '%" + text + "%'\n" +
                    "ORDER BY titluCarti LIMIT 100;");
            //adaugam in VBox cartile valabile
            while(rs.next())
            {
                String book_id = rs.getString("idCarte");
                int nr_books = rs.getInt("numarCarte");
                int nr_reserved_books = connection.getQueryCount("SELECT * FROM cartiimprumutate WHERE UPPER(status)='REZERVAT' AND Carte_idCarte=" + book_id);
                int nr_user_books = connection.getQueryCount("SELECT * FROM cartiimprumutate WHERE User_idUser=" + transaction_user_id + " AND Carte_idCarte=" + book_id);
                int nr_avalabile_books = nr_books - nr_reserved_books;
                String style = "-fx-text-fill: black; -fx-font-size: 13; ";

                AnchorPane item = new AnchorPane();
                //adauga carte (daca nu exista la rezervari sau in inventar)
                if(nr_avalabile_books>0 && nr_user_books==0) {
                    item.getStyleClass().setAll("row-valid");
                    //click pe rand
                    item.setOnMouseClicked(mouseEvent -> {
                        add_book_id = book_id;
                        popup_menu_validation.setVisible(true);
                    });
                }
                else {
                    item.getStyleClass().setAll("row-invalid");
                    style = "-fx-text-fill: #b50000; -fx-font-size: 13; ";
                }


                Label title = new Label('\"' + rs.getString("titluCarti") + '\"');
                title.setStyle(style);
                title.setLayoutX(5);

                Label author = new Label(rs.getString("autorCarte"));
                author.setStyle(style);
                author.setLayoutX(200);

                Label genre = new Label(rs.getString("genCarte"));
                genre.setStyle(style);
                genre.setLayoutX(400);

                Label avalabile_books = new Label(""+(nr_avalabile_books));
                if(nr_user_books!=0)
                    avalabile_books.setText("exista deja");

                avalabile_books.setStyle(style + "-fx-font-weight: bold");
                avalabile_books.setLayoutX(500);

                item.getChildren().add(title);
                item.getChildren().add(author);
                item.getChildren().add(genre);
                item.getChildren().add(avalabile_books);
                list_search_books.getChildren().add(item);
            }
        }

    /**
     * Cancels the process of adding*/
    @FXML void CancelAddBook(MouseEvent mouseEvent) {
            popup_menu_add_book.setVisible(false);
        }

    /**
     * Displays the transaction popup with details of borrowed and returned books.
     * The method generates a structured list of data for borrowed books (IMPRUMUTURI)
     * and returned books (RETURURI), then populates the table view and makes the
     * popup visible.
     *
     * @param mouseEvent the MouseEvent object that triggers the popup display,
     *                   typically triggered by a mouse click or interaction.
     */
        // POPUP TRANSACTION
        @FXML void ShowPopupTransaction(MouseEvent mouseEvent) {
            ObservableList<ObservableList<Label>> data = FXCollections.observableArrayList();

            Label title = new Label("IMPRUMUTURI:");
            title.setStyle("-fx-font-weight: bold");
            data.add(FXCollections.observableArrayList(title, new Label(""), new Label("")));

            for(ControllerItemBookReservedRow item : list_selected_reserved_books)
            {
                Label name = new Label('\t'+item.name.getText());
                Label author = new Label('\t'+item.author.getText());
                Label genre = new Label('\t'+item.genre.getText());
                data.add(FXCollections.observableArrayList(name, author, genre));
            }

            data.add(FXCollections.observableArrayList(new Label(""), new Label(""), new Label("")));

            title = new Label("RETURURI:");
            title.setStyle("-fx-font-weight: bold");
            data.add(FXCollections.observableArrayList(title, new Label(""), new Label("")));

            for(ControllerItemBookInventoryRow item : list_selected_inventory_books)
            {
                Label name = new Label('\t'+item.name.getText());
                Label author = new Label('\t'+item.author.getText());
                Label genre = new Label('\t'+item.genre.getText());
                data.add(FXCollections.observableArrayList(name, author, genre));
            }

            tableView_transaction.setItems(data);
            popup_menu_tranzactie.setVisible(true);
        }

    /**
     * Handles the confirmation of a transaction when the associated event is triggered by the user.
     * This method makes a validation pop-up menu visible.
     *
     * @param mouseEvent the MouseEvent triggered by the user's interaction, such as a mouse click
     */
    @FXML void ConfirmTransaction(MouseEvent mouseEvent) {
            popup_menu_validation.setVisible(true);
        }

    /**
     * Cancels the transaction and hides the transaction popup menu.
     *
     * @param mouseEvent the MouseEvent instance that triggered the cancel operation
     */
    @FXML void CancelTransaction(MouseEvent mouseEvent) {
            popup_menu_tranzactie.setVisible(false);
        }

    /**
     * Initializes the popup menu for adding books in the application.
     * This method sets the visibility of the popup menu and manages user interactions
     * within the menu, including cancel and submission actions.
     *
     * @throws SQLException if there is an error while verifying the database connection.
     *
     * Functionalities:
     * 1. Validates the database connection before processing.
     * 2. Displays the popup menu for adding a book and hides the main books menu.
     * 3. Resets input fields and restores main menu visibility upon cancel action.
     * 4. Displays a validation popup when attempting to add a book.
     */
// BOOKS MENU FUNCTIONS
@FXML void initialize_popup_menu_carte() throws SQLException {
    //Verificam conexiunea
    if(connection.getConnection()==null)
    {
        connection.setFailed(err_connection_null);
        return;
    }
    popup_menu_add_carte.setVisible(true);
    books_menu.setVisible(false);
    cancel.setOnMouseClicked(event -> {
        popup_menu_add_carte.setVisible(false);
        books_menu.setVisible(true);
        titleBar.setText("");
        authorBar.setText("");
        genreBar.setText("");
        numberBar.setText("");
        coverBar.setText("");
    });
    bookAddBase.setOnMouseClicked(event -> {
        popup_menu_validation.setVisible(true);
    });
}

    /**
     * Initializes the increase button and sets the appropriate event handlers for
     * the provided book ID. The method handles the behavior for increasing a
     * value associated with the book and resets the UI state when the action is cancelled.
     *
     * @param book_id the identifier of the book for which the increase button is being initialized
     * @throws SQLException if a database access error occurs during the increase operation
     */
    public void initialize_increase_button(String book_id) throws SQLException {
        // Șterge handler-ul vechi, pentru a evita acumularea de acțiuni multiple
        addIncrease.setOnMouseClicked(null);
        cancelIncrease.setOnMouseClicked(null);

        if (addIncrease != null && addIncrease.getOnMouseClicked() == null) {
            addIncrease.setOnMouseClicked(actionEvent -> {
                String numarText = numberUp.getText();
                if (numarText.isEmpty()) return;

                int number;
                try {
                    number = Integer.parseInt(numarText);
                    if (number <= 0) return;
                } catch (NumberFormatException e) {
                    return;
                }

                try {
                    increase(book_id, number);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
        }

        if (cancelIncrease != null && cancelIncrease.getOnMouseClicked() == null) {
            cancelIncrease.setOnMouseClicked(actionEvent -> {
                menu_increase.setVisible(false);
                books_menu.setVisible(true);
                numberUp.setText("");
            });
        }
    }

    /**
     * Increases the quantity of a specific book in the database by a given number.
     *
     * @param bookID the unique identifier of the book whose quantity is to be increased
     * @param number the amount to add to the current quantity of the book
     * @throws SQLException if a database access error occurs or the SQL query fails
     */
    @FXML void increase(String bookID, int number) throws SQLException {
        if(connection.getConnection()==null){
            connection.setFailed(err_connection_null);
            return;
        }

        book_id = bookID;

        // Verificam daca exista book id
        if(book_id == null || book_id.isEmpty()) {
            System.out.println("Eroare: ID carte neconfigurat!");
            return;
        }

        try {
            // Obtinem cantitatea curenta
            ResultSet rs = connection.executeQuery("SELECT numarCarte FROM carte WHERE idCarte = " + book_id);

            if(rs.next()){
                int cantitate = rs.getInt("numarCarte");
                cantitate += number;

                // Actualizam cantitatea in baza de date
                connection.executeUpdate("UPDATE carte SET numarCarte = numarCarte + " + number + " WHERE idCarte = " + book_id);

                // Verificam daca actualizarea a avut loc
                ResultSet checkRs = connection.executeQuery("SELECT numarCarte FROM carte WHERE idCarte = " + book_id);
                if(checkRs.next() && checkRs.getInt("numarCarte")==cantitate){
                    update_list_books("");
                    show_popup_info("Cartea a fost adaugata cu succes!", popup_info_case.ADD_BOOK);
                    //menu_increase.setVisible(false);
                    /*
                    btn_info_OK.setOnMouseClicked(event1 -> {
                        popup_info.setVisible(false);
                        books_menu.setVisible(true);
                        numberUp.setText("");
                    });

                     */
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initializes the decrease button's functionality, setting up event handlers for
     * user actions such as decreasing a book count or cancelling the decrease action.
     * This method ensures old event handlers are removed to prevent accumulation of redundant actions.
     *
     * @param book_id The unique identifier of the book whose count is to be decreased.
     * @throws SQLException If a database access error occurs during the decrease operation.
     */
    public void initialize_decrease_button(String book_id) throws SQLException {
        // Sterge handle-uri vechi, pentru a evita acumularea de actiuni multiple
        //removeDecrease.setOnMouseClicked(null);
        cancelDecrease.setOnMouseClicked(null);
        btn_delete_book.setOnMouseClicked(mouseEvent -> {
            if(connection.getConnection()==null){
                connection.setFailed(err_connection_null);
                return;
            }
            popup_menu_validation.setVisible(true);
        });

        if (removeDecrease != null && removeDecrease.getOnMouseClicked() == null) {
            removeDecrease.setOnMouseClicked(actionEvent -> {
                String numarText = numberDown.getText();
                if (numarText.isEmpty()) return;

                int number;

                try{
                    number = Integer.parseInt(numarText);
                    if (number <= 0) return;
                } catch (NumberFormatException e) {
                    show_popup_error("Numar invalid!");
                    return;
                }
                try{
                    decrease(book_id,number);
                } catch (SQLException e){
                    e.printStackTrace();
                }
            });
        }

        if (cancelDecrease != null && cancelDecrease.getOnMouseClicked() == null) {
            cancelDecrease.setOnMouseClicked(actionEvent -> {
                menu_decrease.setVisible(false);
                books_menu.setVisible(true);
                numberDown.setText("");
            });
        }
    }

    /**
     * Decreases the quantity of a specific book in the database by the specified amount.
     *
     * @param bookID the unique identifier of the book whose quantity is to be decreased
     * @param number the number to decrease the quantity of the specified book by
     * @throws SQLException if a database access error occurs during the operation
     */
    @FXML void decrease(String bookID, int number) throws SQLException {

        System.out.println("number: "+number);
        if (connection.getConnection() == null) {
            connection.setFailed(err_connection_null);
            return;
        }

        book_id = bookID;

        // Verificam daca exista book id
        if (book_id == null || book_id.isEmpty()) {
            System.out.println("Eroare: ID carte neconfigurat!");
            return;
        }

        try {
            // Obtinem cantitatea curenta
            ResultSet rs = connection.executeQuery("SELECT numarCarte FROM carte WHERE idCarte = " + book_id);

            if (rs.next()) {
                int cantitate = rs.getInt("numarCarte");
                cantitate = cantitate - number;

                if (cantitate >= 0) {
                    // Actualizam cantitatea in baza de date
                    connection.executeUpdate("UPDATE carte SET numarCarte = numarCarte - " + number + " WHERE idCarte = " + book_id);

                    // Verificam daca actualizarea a avut loc
                    ResultSet checkRs = connection.executeQuery("SELECT numarCarte FROM carte WHERE idCarte = " + book_id);
                    if (checkRs.next() && checkRs.getInt("numarCarte") == cantitate) {

                        show_popup_info("Cartea a fost stearsa cu succes!", popup_info_case.REMOVE_BOOK);
                        //menu_decrease.setVisible(false);
                        /*
                        btn_info_OK.setOnMouseClicked(event1 -> {
                            try {
                                update_list_books("");
                                popup_info.setVisible(false);
                                books_menu.setVisible(true);
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                        });

                         */
                    }
                } else {
                    show_popup_error("Cantitate prea mare!");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles user confirmation for various popup validation scenarios.
     * This method performs the following actions based on different popup menus:
     * - Confirms borrowing or returning books.
     * - Confirms adding a new book to inventory.
     * - Confirms deleting a book from stock.
     * - Confirms adding a new book record to the library database.
     *
     * @param mouseEvent The mouse event triggered by interacting with the confirmation button.
     */
    // POPUP VALIDATION (Esti sigur?)
    @FXML void AreYouSureYes(MouseEvent mouseEvent) throws SQLException {
        //confirmare tranzactie imprumut/retur
        if(popup_menu_tranzactie.isVisible()) {
            try
            {
                System.out.print(String.format("\nSTART TRANZACTIE -> user(#%s)", transaction_user_id));
                //IMPRUMUT
                System.out.print("\n\nImprumuturi:\n");
                for(ControllerItemBookReservedRow book : list_selected_reserved_books)
                {
                    //statement pentru schimbarea statusului REZERVAT -> IMPRUMUTAT
                    connection.executeUpdate("UPDATE cartiimprumutate\n" +
                            "SET status = 'INVENTAR'\n" +
                            "WHERE status = 'REZERVAT' AND\n" +
                            "User_idUser = " + transaction_user_id + " AND Carte_idCarte = " + book.id + ';');

                    //statement pentru stergerea cu o unitate a cartii din stoc
                    /*
                    connection.executeUpdate("UPDATE carte\n" +
                            "SET numarCarte = numarCarte-1\n" +
                            "WHERE idCarte = " + book.id + ';');

                     */
                }

                //RETUR
                System.out.print("\n\nRetururi:\n");
                for(ControllerItemBookInventoryRow book : list_selected_inventory_books)
                {
                    //statement pentru adaugarea cu o unitate a cartii in stoc
                    /*
                    connection.executeUpdate("UPDATE carte\n" +
                            "SET numarCarte = numarCarte+1\n" +
                            "WHERE idCarte = " + book.id + ';');
                     */

                    //statement pentru stergerea cartii din inventar
                    connection.executeUpdate("DELETE FROM cartiimprumutate\n" +
                            "WHERE status = 'INVENTAR' AND\n" +
                            "User_idUser = " + transaction_user_id + " AND Carte_idCarte = " + book.id + ';');
                }

                //actualizam meniul
                initialize_menu_client_detalii(transaction_user_id);
                System.out.print("\nTranzactie finalizata!");

            }
            catch (SQLException e) {
                throw new RuntimeException(e);
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }

            popup_menu_validation.setVisible(false);
            popup_menu_tranzactie.setVisible(false);
        }
        //confirmare adaugare carte in inventar
        if(popup_menu_add_book.isVisible()) {
            connection.executeUpdate("INSERT INTO mydb.cartiimprumutate(dataImprumut, dataRetur, User_idUser, Carte_idCarte, status)\n" +
                    "VALUES (CAST(sysdate() AS date), NULL, " + transaction_user_id + "," + add_book_id + ",\"REZERVAT\");");
            try {
                initialize_menu_client_detalii(transaction_user_id);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        //confirmare stergere carte din stoc
        if(menu_decrease.isVisible()) {
            try{
                int nr_borrowed_books = connection.getQueryCount("SELECT * FROM cartiimprumutate\n" +
                        "WHERE UPPER(status)='INVENTAR' AND Carte_idCarte = " + book_id);
                if(nr_borrowed_books > 0){
                    show_popup_error("Unii utilizatori detin cartea!");
                }
                else {
                    connection.executeUpdate("DELETE FROM cartiimprumutate WHERE Carte_idCarte = " + book_id);
                    connection.executeUpdate("DELETE FROM review WHERE Carte_idCarte = " + book_id);
                    connection.executeUpdate("DELETE FROM carte WHERE idCarte = " + book_id);
                    update_list_books("");
                    show_popup_info("Stoc sters cu succes!", popup_info_case.DELETE_BOOK);
                    /*
                    btn_info_OK.setOnMouseClicked(event2 -> {
                        popup_info.setVisible(false);
                        books_menu.setVisible(true);
                        numberDown.setText("");
                    });
                     */
                }

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        //confirmare adaugare carte noua in stoc
        if(popup_menu_add_carte.isVisible()) {
            String title = titleBar.getText();
            String author = authorBar.getText();
            String genre = genreBar.getText();
            String number = numberBar.getText();
            int numar = Integer.parseInt(number);
            String cover = coverBar.getText();
            String descriere = describeBar.getText();

            if(title.isEmpty() || author.isEmpty() || genre.isEmpty() || numar==0 || descriere.isEmpty()){ // || cover.isEmpty()
                System.out.print("Trebuie ca toate campurile sa fie scrise");
            }

            try{
                PreparedStatement prep = connection.getConnection().prepareStatement("INSERT INTO carte(" +
                        "titluCarti, autorCarte, numarCarte, genCarte, coverCarte, descriere)" +
                        "VALUES (?,?,?,?,?,?)",Statement.RETURN_GENERATED_KEYS);
                prep.setString(1, title);
                prep.setString(2,author);
                prep.setInt(3,numar);
                prep.setString(4,genre);
                prep.setString(5,cover);
                prep.setString(6,descriere);

                int insertrow = prep.executeUpdate();
                if (insertrow > 0) {
                    ResultSet key = prep.getGeneratedKeys();
                    if(key.next()){
                        int generatedId = key.getInt(1);
                        show_popup_info("Ai introdus o noua carte!", popup_info_case.NEW_BOOK);
                        //popup_menu_add_carte.setVisible(false);
                        /*
                        btn_info_OK.setOnMouseClicked(event1 -> {
                            try {
                                update_list_books("");
                                books_menu.setVisible(true);
                                popup_info.setVisible(false);
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                        });
                         */
                    }
                }
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
        //confrimare stergere review-uri
        if(popup_menu_review_user.isVisible()){
            if (deleteContext.equals("all_reviews")){
                ResultSet rs = connection.executeQuery("SELECT * FROM review WHERE user_idUser = " + transaction_user_id);
                try{
                    while(rs.next()){
                        connection.executeUpdate("DELETE FROM review WHERE user_idUser = " + transaction_user_id);
                        update_user_review("");
                        show_popup_info("Review-urile au fost sterse cu succes!", popup_info_case.DELETE_REVIEWS);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            else if(deleteContext.equals("review")){
                ResultSet rs = connection.executeQuery("SELECT * FROM review WHERE user_idUser = " + transaction_user_id
                                                    + " AND idReview = " + review_user_id);
                try {
                    if(rs.next()){
                        connection.executeUpdate("DELETE FROM review WHERE idReview = " + review_user_id);
                        update_user_review("");
                        show_popup_info("Review-ul a fost sters cu succes!", popup_info_case.REMOVE_REVIEW);
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        deleteContext = "";
        }

        popup_menu_validation.setVisible(false);
    }

    /**
     * Handles the event triggered when the "No" option is selected in a confirmation popup menu.
     * This method hides the popup menu when the "No" button is clicked.
     *
     * @param mouseEvent The MouseEvent triggered by clicking the "No" option.
     */
    @FXML void AreYouSureNo(MouseEvent mouseEvent) {
        popup_menu_validation.setVisible(false);
    }

    /**
     * Handles the action event when the "OK" button is clicked on an error popup.
     * This method hides the error popup by setting its visibility to false.
     *
     * @param mouseEvent The MouseEvent instance containing details about the clicked event.
     */
    //POPUP ERROR
    @FXML void popup_error_OK_clicked(MouseEvent mouseEvent) {
        popup_error.setVisible(false);
    }

    /**
     * Handles the click event on the "OK" button in the popup information dialog.
     * The method performs different actions based on the current context (info_case)
     * such as updating the book list, closing menus, or resetting input fields.
     *
     * @param mouseEvent The mouse event triggered by clicking the "OK" button in the popup dialog.
     */
    //POPUP INFO
    @FXML void popup_info_OK_clicked(MouseEvent mouseEvent) {
        popup_info.setVisible(false);

        //mesaj confirmare adaugare carte
        if(info_case==popup_info_case.NEW_BOOK) {
            System.out.println("inchide meniu hatz");
            try {
                update_list_books("");
                popup_menu_add_carte.setVisible(false);
                books_menu.setVisible(true);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            popup_menu_add_carte.setVisible(false);
        }
        //mesaj confirmare stergere cantitate carti
        if(info_case==popup_info_case.REMOVE_BOOK){
            try {
                update_list_books("");
                menu_decrease.setVisible(false);
                books_menu.setVisible(true);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        //mesaj confirmare adaugare cantitate carti
        if(info_case==popup_info_case.ADD_BOOK){
            books_menu.setVisible(true);
            menu_increase.setVisible(false);
            numberUp.setText("");
        }
        //mesaj confirmare eliminare carte din stoc
        if(info_case==popup_info_case.DELETE_BOOK){
            books_menu.setVisible(true);
            menu_decrease.setVisible(false);
            numberDown.setText("");
        }
        //mesaj confirmare stergere user
        if(info_case==popup_info_case.DELETE_USER){
            client_menu.setVisible(true);
            client_menu_details.setVisible(false);
        }
        //mesaj confirmare stergere review-uri
        if(info_case==popup_info_case.DELETE_REVIEWS){
            popup_menu_review_user.setVisible(false);
            client_menu_details.setVisible(false);
            client_menu.setVisible(true);
        }
        //mesaj confirmare stergere review
        if(info_case==popup_info_case.REMOVE_REVIEW){
            popup_menu_review_user.setVisible(false);
            client_menu_details.setVisible(false);
            client_menu.setVisible(true);
        }

    }

    /**
     * Initializes the statistics menu by setting it as the active menu and loading the different
     * statistical panes.*/
    // MENU STATISTICS
    void initialize_statistics_menu() throws SQLException {
        setOnlyMenu(statistici_menu);
        load_pane_statistics_clients();
        load_pane_statistics_books();
        load_pane_statistics_top_genres();
        load_pane_statistics_top_authors();
        load_pane_statistics_top_books();
    }

    /**
     * Loads and displays client statistics in a graphical user interface pane.
     *
     * This method fetches data from the database to populate a pie chart and a legend
     * in the `pane_statistics_clients` container, indicating the proportion of active
     * and inactive clients relative to the total number of clients. The method modifies
     * and clears existing content in `pane_statistics_clients` before adding new components.
     *
     * The data includes:
     * - Total number of clients from the "user" database table.
     * - Number of active clients from the "*/
    private void load_pane_statistics_clients() throws SQLException {
        //Clienti
        pane_statistics_clients.getChildren().clear();
        //date
        int nr_total_clients = connection.getQueryCount("SELECT DISTINCT idUser FROM user;");
        int nr_active_clients = connection.getQueryCount("SELECT DISTINCT User_idUser FROM cartiimprumutate;");

        //legenda
        String[] labels_clients = {String.format("Activi:     %d/%d", nr_active_clients, nr_total_clients),
                String.format("Inactivi:   %d/%d", nr_total_clients-nr_active_clients, nr_total_clients)};
        Color[] colors_clients = {color2, color5};
        statisticsBoxAddLegend(pane_statistics_clients, labels_clients, colors_clients, 2);

        //piechart
        ObservableList<PieChart.Data> data_clients = FXCollections.observableArrayList();
        data_clients.add(new PieChart.Data("", nr_active_clients));
        data_clients.add(new PieChart.Data("", nr_total_clients));
        piechart_statistics_clients.setData(data_clients);
        for (int i = 0; i < 2; i++) {
            PieChart.Data slice = data_clients.get(i);
            switch(i)
            {
                case 0 -> slice.getNode().setStyle("-fx-pie-color: #1fcd00;");
                case 1 -> slice.getNode().setStyle("-fx-pie-color: #2E2E2EFF;");
            }
        }
    }

    /**
     * Loads and updates the pane containing statistics related to books.
     *
     * This method clears the existing content of the statistics pane, retrieves and calculates
     * the necessary data for total books, reserved books, borrowed books, and available books
     * in stock from the database queries. The data is then used to populate a pie chart and its
     * corresponding legend to visually represent the book statistics.
     *
     * @throws SQLException if a database access error occurs or the SQL queries fail.
     */
    private void load_pane_statistics_books() throws SQLException {
        //Carti
        pane_statistics_books.getChildren().clear();
        //date
        int nr_books_total = connection.getQuerrySum("SELECT SUM(numarCarte) FROM carte;");
        int nr_books_reserved = connection.getQueryCount("SELECT * FROM cartiimprumutate WHERE UPPER(status)='REZERVAT';");
        int nr_books_borrowed = connection.getQueryCount("SELECT * FROM cartiimprumutate WHERE UPPER(status)='INVENTAR';");
        int nr_books_stock = nr_books_total - nr_books_reserved - nr_books_borrowed;
        String books_stock = "In stoc";
        String books_reserved = "Rezervate";
        String books_borrowed = "Imprumutate";

        //legenda
        String[] labels_books = {String.format("In stoc:       %d/%d",nr_books_stock,nr_books_total),
                String.format("Rezervate:     %d/%d",nr_books_reserved,nr_books_total),
                String.format("Imprumutate:   %d/%d",nr_books_borrowed,nr_books_total)};
        Color[] colors_books = {color2, color4, color5};
        statisticsBoxAddLegend(pane_statistics_books, labels_books, colors_books, 3);

        //piechart
        ObservableList<PieChart.Data> data_books = FXCollections.observableArrayList();
        data_books.add(new PieChart.Data("", nr_books_stock));
        data_books.add(new PieChart.Data("", nr_books_reserved));
        data_books.add(new PieChart.Data("", nr_books_borrowed));
        piechart_statistics_books1.setData(data_books);
        for (int i = 0; i < 3; i++) {
            PieChart.Data slice = data_books.get(i);
            switch(i)
            {
                case 0 -> slice.getNode().setStyle("-fx-pie-color: #1fcd00;");
                case 1 -> slice.getNode().setStyle("-fx-pie-color: #b8b5b5;");
                case 2 -> slice.getNode().setStyle("-fx-pie-color: #2e2e2e;");
            }
        }
    }

    /**
     * Loads and displays statistics for the top genres based on borrowed books into the pane.
     *
     * This method retrieves data about the top 5 most borrowed book genres from the database,
     * and visualizes the information using a pie chart and a legend. The retrieved data consists
     * of the genre names and the corresponding number of borrowed books. Each genre is assigned
     * a specific color for visual distinction.
     *
     * @throws SQLException if there is an error in executing the database query or
     *                      processing the result set*/
    private void load_pane_statistics_top_genres() throws SQLException {
        //Top genuri
        pane_top_genre.getChildren().clear();
        //date
        ResultSet rs_top_5_genres = connection.executeQuery("SELECT g.genuri, SUM(up.number) AS numar\n" +
                "FROM userpref up\n" +
                "         JOIN genuri g ON up.preferinte_idpreferinte = g.idpreferinte\n" +
                "GROUP BY g.genuri\n" +
                "ORDER BY numar DESC\n" +
                "LIMIT 5;");
        //legenda
        String[] labels_genres = new String[5];
        int[] nr_genres = new int[5];
        int index = 0;
        while(rs_top_5_genres.next())
        {
            String gen_carte = rs_top_5_genres.getString("genuri");
            nr_genres[index] = rs_top_5_genres.getInt("numar");
            labels_genres[index] = String.format("%s - %d", gen_carte, nr_genres[index]);
            index++;
        }
        Color[] colors_genres  = {color1,color2,color3,color4,color5};
        statisticsBoxAddLegend(pane_top_genre,labels_genres,colors_genres,index);

        //pie-chart genres
        ObservableList<PieChart.Data> data_genres = FXCollections.observableArrayList();
        for(int i=0; i<index; i++)
            data_genres.add(new PieChart.Data("", nr_genres[i]));
        piechart_statistics_genre.setData(data_genres);
        for (int i = 0; i < index; i++) {
            PieChart.Data slice = data_genres.get(i);
            switch(i)
            {
                case 0 -> slice.getNode().setStyle("-fx-pie-color: #116c00;");
                case 1 -> slice.getNode().setStyle("-fx-pie-color: #28f328;");
                case 2 -> slice.getNode().setStyle("-fx-pie-color: #80da83;");
                case 3 -> slice.getNode().setStyle("-fx-pie-color: #b7b4b4;");
                case 4 -> slice.getNode().setStyle("-fx-pie-color: #2e2e2e;");
            }
        }
    }

    /**
     * Loads and displays statistics for the top authors based on the number of borrowed books,
     * retrieving data from the database and rendering it in a bar chart using JavaFX components.
     *
     * This method retrieves data for the top five authors who have the highest number of borrowed books.
     * The result is displayed in a bar chart with the number of borrowed books on the horizontal axis
     * and the authors' names on the vertical axis. If there are no borrowed books in the*/
    private void load_pane_statistics_top_authors() throws SQLException{
        //data
        ResultSet rs = connection.executeQuery("SELECT autorCarte, COUNT(autorCarte) AS nr_author\n" +
                                                "FROM cartiimprumutate borrow, carte book\n" +
                                                "WHERE book.idCarte = borrow.Carte_idCarte\n" +
                                                "GROUP BY autorCarte\n" +
                                                "ORDER BY nr_author LIMIT 5;");
        String[] labels_authors = new String[5];
        int[] nr_authors = new int[5];
        int index = 0;
        while(rs.next()) {
            labels_authors[index] = rs.getString("autorCarte");
            nr_authors[index] = rs.getInt("nr_author");
            index++;
        }

        //barchart
        CategoryAxis_statistics_authors.getCategories().clear();
        XYChart.Series<Number, String> series1 = new XYChart.Series<>();
        series1.getData().clear();
        int i=0;
        for(i=0; i<index; i++)
        {
            System.err.println(String.format("%d: series1: <%d, %s>", i, nr_authors[i], labels_authors[i]));
            series1.getData().add(new XYChart.Data<>(nr_authors[i], labels_authors[i]+"   ---"));
        }

        if(index>0)
        {
            NumberAxis_statistics_authors.setUpperBound(nr_authors[index-1]);
            NumberAxis_statistics_authors.setTickUnit(nr_authors[index-1] < 5 ? 1 : nr_authors[index-1]/index);
        }
        else
        {
            NumberAxis_statistics_authors.setUpperBound(10);
            NumberAxis_statistics_authors.setTickUnit(1);
        }
        NumberAxis_statistics_authors.setTickLabelFill(Paint.valueOf("black"));
        NumberAxis_statistics_authors.setTickLabelFont(Font.font("Arial", FontWeight.BOLD, 11));

        statistics_authors.getData().clear();
        statistics_authors.getData().setAll(series1);
        cssBarChart(statistics_authors, index);
        CategoryAxis_statistics_authors.setTickLabelFont(Font.font("Arial", FontWeight.BOLD, 12));
        CategoryAxis_statistics_authors.setTickLabelFill(Paint.valueOf("black"));
        CategoryAxis_statistics_authors.lookup(".axis-tick-mark").setStyle("-fx-stroke: transparent;");
    }

    /**
     * Loads and displays the top borrowed books in a statistics pane.
     * The method retrieves data from the database to find the top 8*/
    private void load_pane_statistics_top_books() throws SQLException {
        vbox_statistics_top_books.getChildren().clear();
        ResultSet rs = connection.executeQuery("SELECT titluCarti, idCarte, COUNT(titluCarti) AS nr_books\n" +
                                                "FROM cartiimprumutate borrow, carte book\n" +
                                                "WHERE book.idCarte = borrow.Carte_idCarte\n" +
                                                "GROUP BY titluCarti, idCarte\n" +
                                                "ORDER BY nr_books DESC LIMIT 8;");
        int cnt = 0;
        while(rs.next()) {
            String title = rs.getString("titluCarti");
            String idCarte = rs.getString("idCarte");
            int nb_books = rs.getInt("nr_books");
            Label label = new Label(String.format("   %s (#%s)   ->   (%d)", title, idCarte, nb_books));
            label.setPrefWidth(vbox_statistics_top_books.getPrefWidth());    label.setPrefHeight(35);
            String style = " -fx-border-width: 1px; -fx-border-color: white; -fx-font-size: 13; -fx-font-weight: bold;";
            if(cnt%2==0)
                style+=" -fx-background-color: #abff9c; ";
            else
                style+=" -fx-background-color: #ececec; ";
            label.setStyle(style);
            vbox_statistics_top_books.getChildren().add(label);
            cnt++;
        }
    }

    /**
     * Adds a legend to the provided pane, where each entry consists of a colored circle and a label
     * describing an item. The number of items in the legend is determined by the nr_items parameter.
     *
     * @param paneLegend The pane*/
    private void statisticsBoxAddLegend(Pane paneLegend, String[] string_arr, Color[] color_arr, int nr_items){
        int height = 20;
        for(int i=0; i<nr_items; i++)
        {
            Pane box = new Pane();
            box.setPrefWidth(paneLegend.getPrefWidth());
            box.setPrefHeight(height);
            box.setLayoutY(i*height);

            Circle circle = new Circle();
            circle.setRadius(5);
            circle.setFill(color_arr[i]);
            circle.setLayoutY(height/2);
            circle.setLayoutX(height/2);

            Label label = new Label(string_arr[i]);
            label.setLayoutY(2);
            label.setLayoutX(height);
            label.setStyle("fx-text-fill: black; -fx-font-size: 12; -fx-font-weight: bold");
            box.getChildren().setAll(circle, label);
            paneLegend.getChildren().add(box);
        }
    }

    /**
     * Applies CSS styling to the bars of a given BarChart based on their position index.
     * Each bar in the chart is colored differently according to a predefined set of colors.
     * The number of bars to style is determined by the specified size parameter.
     *
     * @param barChart the BarChart object whose bars will be styled
     * @param size the number of bars to style in the chart
     */
    private void cssBarChart(BarChart barChart, int size) {
        XYChart.Series<String, Number> series = (XYChart.Series<String, Number>) barChart.getData().get(0);
            for (int i = 0; i < size; i++) {
                XYChart.Data<String, Number> data = series.getData().get(i);
                if (data.getNode() != null) {
                    switch (i) {
                        case 0 -> data.getNode().setStyle("-fx-bar-fill: #8aff75;");
                        case 1 -> data.getNode().setStyle("-fx-bar-fill: #00ff00;");
                        case 2 -> data.getNode().setStyle("-fx-bar-fill: #28a609;");
                        case 3 -> data.getNode().setStyle("-fx-bar-fill: #135100;");
                        case 4 -> data.getNode().setStyle("-fx-bar-fill: #003300;");
                    }
                }
            }
    }
}