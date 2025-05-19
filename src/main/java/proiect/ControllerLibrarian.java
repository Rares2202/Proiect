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



public class ControllerLibrarian{

    //SQL
    public DatabaseConnection connection;
    String url = "jdbc:mysql://localhost:3306/mydb";
    String user = "root";
    String pass = "root";

    //Meniuri
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
    public List<AnchorPane> listMenu = new ArrayList<>();

    //popups
    @FXML Label label_popup_info;
    @FXML Label label_popup_error;
    enum popup_info_case {ADD_BOOK, REMOVE_BOOK, NEW_BOOK, DELETE_BOOK};
    popup_info_case info_case;

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

    //Popup meniu adaugare carti
    @FXML VBox list_search_books;
    @FXML TextField textField_add_book;

    //Popup meniu de tranzactie
    @FXML AnchorPane popup_menu_tranzactie;
    @FXML TableView<ObservableList<Label>> tableView_transaction;
    @FXML Button btn_confirma_tranzactie;
    @FXML Button btn_anuleaza_tranzactie;

    //Meniu Carti
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
    public void quit_app(MouseEvent mouseEvent) {
        connection.close();
        System.out.print("\nInchidere aplicatie.");
        System.exit(0);
    }
    public void setOnlyMenu(AnchorPane newMenu) {
        for(AnchorPane menu: listMenu)
            menu.setVisible(false);
        if (newMenu!=null)
            newMenu.setVisible(true);
    }

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
    @FXML void OnStatisticiButtonClicked(MouseEvent mouseEvent) throws SQLException {
        initialize_statistics_menu();
    }

    // POPUPS
    void show_popup_info(String message, popup_info_case newCase) {
        label_popup_info.setText(message);
        info_case = newCase;
        popup_info.setVisible(true);
    }
    void show_popup_error(String message){
        label_popup_error.setText(message);
        popup_error.setVisible(true);
    }

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
    @FXML void AddBookToRezervari(MouseEvent mouseEvent) throws SQLException {
        initialize_popup_menu_add_book();
    }

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

                Label avalabile_books = new Label(""+(nr_books-nr_reserved_books));
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
        @FXML void CancelAddBook(MouseEvent mouseEvent) {
            popup_menu_add_book.setVisible(false);
        }

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
        @FXML void ConfirmTransaction(MouseEvent mouseEvent) {
            popup_menu_validation.setVisible(true);
        }
        @FXML void CancelTransaction(MouseEvent mouseEvent) {
            popup_menu_tranzactie.setVisible(false);
        }

//
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

    // POPUP VALIDATION (Esti sigur?)
    @FXML void AreYouSureYes(MouseEvent mouseEvent) {
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

            if(title.isEmpty() || author.isEmpty() || genre.isEmpty() || numar==0  ){ // || cover.isEmpty()
                System.out.print("Trebuie ca toate campurile sa fie scrise");
            }

            try{
                PreparedStatement prep = connection.getConnection().prepareStatement("INSERT INTO carte(" +
                        "titluCarti, autorCarte, numarCarte, genCarte, coverCarte)" +
                        "VALUES (?,?,?,?,?)",Statement.RETURN_GENERATED_KEYS);
                prep.setString(1, title);
                prep.setString(2,author);
                prep.setInt(3,numar);
                prep.setString(4,genre);
                prep.setString(5,cover);

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

        popup_menu_validation.setVisible(false);
    }
    @FXML void AreYouSureNo(MouseEvent mouseEvent) {
        popup_menu_validation.setVisible(false);
    }

    //POPUP ERROR
    @FXML void popup_error_OK_clicked(MouseEvent mouseEvent) {
        popup_error.setVisible(false);
    }

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

    }

    // MENU STATISTICS
    void initialize_statistics_menu() throws SQLException {
        setOnlyMenu(statistici_menu);
        load_pane_statistics_clients();
        load_pane_statistics_books();
        load_pane_statistics_top_genres();
        load_pane_statistics_top_authors();
        load_pane_statistics_top_books();
    }
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
    private void load_pane_statistics_top_genres() throws SQLException {
        //Top genuri
        pane_top_genre.getChildren().clear();
        //date
        ResultSet rs_top_5_genres = connection.executeQuery("SELECT genCarte, COUNT(genCarte) AS nr_books\n" +
                "FROM cartiimprumutate borrow, carte book\n" +
                "WHERE book.idCarte = borrow.Carte_idCarte\n" +
                "GROUP BY genCarte\n" +
                "ORDER BY nr_books DESC LIMIT 5;");
        //legenda
        String[] labels_genres = new String[5];
        int[] nr_genres = new int[5];
        int index = 0;
        while(rs_top_5_genres.next())
        {
            String gen_carte = rs_top_5_genres.getString("genCarte");
            nr_genres[index] = rs_top_5_genres.getInt("nr_books");
            labels_genres[index] = String.format("%s - %d", gen_carte, nr_genres[index]);
            index++;
        }
        Color[] colors_genres  = {color1,color2,color3,color4,color5};
        statisticsBoxAddLegend(pane_top_genre,labels_genres,colors_genres,index);

        //piechart genres
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