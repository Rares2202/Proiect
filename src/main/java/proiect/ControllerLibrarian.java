package proiect;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

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
    public List<AnchorPane> listMenu = new ArrayList<>();

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

    //Meniu Statistici
    @FXML BarChart bar_chart_book;

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

        //nu e deshis niciun meniu la deschiderea aplicatiei
        setOnlyMenu(null);

        //Crearea conexiune
        connection = new DatabaseConnection(url, user, pass, this);
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

                controller.label_books_reserved.setText(""+connection.getQueryCount("SELECT statusImprumut \n" +
                                                                                    "FROM cartiimprumutate\n" +
                                                                                    "WHERE User_idUser = " + userId + " AND UPPER(statusImprumut) = 'REZERVAT';"));
                controller.label_books_inventory.setText(""+connection.getQueryCount("SELECT statusImprumut \n" +
                                                                                    "FROM cartiimprumutate\n" +
                                                                                    "WHERE User_idUser = " + userId + " AND UPPER(statusImprumut) = 'INVENTAR';"));


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
    @FXML void OnCartiButtonClicked(MouseEvent mouseEvent) {
        setOnlyMenu(books_menu);
    }
    @FXML void OnStatisticiButtonClicked(MouseEvent mouseEvent) {
        initialize_statistics_menu();
    }

    // CLIENT_DETAILS MENU FUNCTIONS
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
                int nr_reserved_books = connection.getQueryCount("SELECT * FROM cartiimprumutate WHERE UPPER(statusImprumut)='REZERVAT' AND Carte_idCarte=" + book_id);
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
                            "SET statusImprumut = 'INVENTAR'\n" +
                            "WHERE statusImprumut = 'REZERVAT' AND\n" +
                            "User_idUser = " + transaction_user_id + " AND Carte_idCarte = " + book.id + ';');

                    //statement pentru stergerea cu o unitate a cartii din stoc
                    connection.executeUpdate("UPDATE carte\n" +
                            "SET numarCarte = numarCarte-1\n" +
                            "WHERE idCarte = " + book.id + ';');
                }

                //RETUR
                System.out.print("\n\nRetururi:\n");
                for(ControllerItemBookInventoryRow book : list_selected_inventory_books)
                {
                    //statement pentru adaugarea cu o unitate a cartii in stoc
                    connection.executeUpdate("UPDATE carte\n" +
                            "SET numarCarte = numarCarte+1\n" +
                            "WHERE idCarte = " + book.id + ';');

                    //statement pentru stergerea cartii din inventar
                    connection.executeUpdate("DELETE FROM cartiimprumutate\n" +
                            "WHERE statusImprumut = 'INVENTAR' AND\n" +
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
            connection.executeUpdate("INSERT INTO mydb.cartiimprumutate(dataImprumut, dataRetur, User_idUser, Carte_idCarte, statusImprumut)\n" +
                    "VALUES (CAST(sysdate() AS date), NULL, " + transaction_user_id + "," + add_book_id + ",\"REZERVAT\");");
            try {
                initialize_menu_client_detalii(transaction_user_id);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    @FXML void AreYouSureNo(MouseEvent mouseEvent) {
        popup_menu_validation.setVisible(false);
    }

    // MENU STATISTICS
    void initialize_statistics_menu()
    {
        setOnlyMenu(statistici_menu);
        bar_chart_book.getData().clear();

        XYChart.Series<Number, String> series = new XYChart.Series<>();
        XYChart.Data[] data = new XYChart.Data[10];
        series.setName("books");
        for(int i=0; i<10; i++)
        {
            data[i] = new XYChart.Data<>(i, ""+i);
            series.getData().add(data[i]);
        }
        bar_chart_book.getData().add(series);

        int index = 0;
        for(XYChart.Data bar : data)
        {
            Label label = new Label("book_"+index);
            label.setTextAlignment(TextAlignment.LEFT);
            label.setLayoutX(0);
            StackPane barNode = (StackPane) bar.getNode();
            barNode.getChildren().add(label);
        }
    }


}