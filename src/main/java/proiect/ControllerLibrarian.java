package proiect;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;

import javax.naming.ldap.Control;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;
import java.util.concurrent.Executor;


public class ControllerLibrarian{

    //SQL
    String url = "jdbc:mysql://localhost:3306/mydb";
    String user = "root";
    String pass = "root";

    //Meniuri
    public List<AnchorPane> listMenu = new ArrayList<>();
    public AnchorPane connectionFailed_menu;
    public AnchorPane client_menu;
    public AnchorPane client_menu_details;
    public AnchorPane books_menu;
    public AnchorPane statistici_menu;
    public AnchorPane popup_menu_validation;

    //Meniu Clienti
    public VBox list_search_clients;
    public TextField textField_clientName;

    //Meniu Client_Detalii
    public String transaction_user_id = "";
    public Text menu_transaction_clientName;
    public VBox vbox_books_reserved;
    public VBox vbox_books_inventory;
    public Button btn_efectueaza;
    public Button btn_rezervari_all;
    public Button btn_rezervari_clear;
    public Button btn_inventar_all;
    public Button btn_inventar_clear;
    public ObservableList<ControllerItemBookReservedRow> list_books_reserved = FXCollections.observableArrayList();
    public ObservableList<ControllerItemBookInventoryRow> list_books_inventory = FXCollections.observableArrayList();
    public List<ControllerItemBookReservedRow> list_selected_reserved_books = new ArrayList<>();
    public List<ControllerItemBookInventoryRow> list_selected_inventory_books = new ArrayList<>();

    //Popup meniu de tranzactie
    public AnchorPane popup_menu_tranzactie;
    public TableView<ObservableList<Label>> tableView_transaction;
    public Button btn_confirma_tranzactie;
    public Button btn_anuleaza_tranzactie;

    //Meniu Carti


    //Meniu Statistici



    @FXML
    void initialize() {
        //initializare lista cu toate meniurile aplicatiei
        listMenu.add(connectionFailed_menu);
        listMenu.add(client_menu);
        listMenu.add(client_menu_details);
        listMenu.add(books_menu);
        listMenu.add(statistici_menu);
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
    }
    public void quit_app(MouseEvent mouseEvent) {
        System.exit(0);
    }
    public void setOnlyMenu(AnchorPane newMenu) {
        for(AnchorPane menu: listMenu)
            menu.setVisible(false);
        if (newMenu!=null)
            newMenu.setVisible(true);
    }

    // MAIN MENU BUTTONS FUNCTIONS

    public void OnClientiButtonClicked(MouseEvent mouseEvent){
        try
        {
            //Incearca o conexiune noua la db
            Connection connection = DriverManager.getConnection(url,user,pass);

            PreparedStatement statementUsers = connection.prepareStatement("SELECT idUser, userName FROM mydb.user ORDER BY userName;");
            ResultSet rs = statementUsers.executeQuery();
            ObservableList<ControllerItemClientRow> users = FXCollections.observableArrayList();
            list_search_clients.getChildren().clear();

            //Creaza+incarca lista cu useri (obiecte FXML)
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
                    controller.setName(String.format("%s (#%s)", _name, userId));

                    controller.mainController = this;

                    users.add(controller);
                    list_search_clients.getChildren().add(node);

                } catch (Exception e) {
                    System.out.print("Nu s-a putut genera client(FXML)");
                    //e.printStackTrace();
                }
            }
            connection.close();
            setOnlyMenu(client_menu);

            //filtrarea listei in functie de textField
            FilteredList<ControllerItemClientRow> filteredData = new FilteredList<>(users, pred -> true);
            textField_clientName.textProperty().addListener((observable, oldValue, newValue) -> {

                filteredData.setPredicate(pred -> {
                    if (newValue == null || newValue.isEmpty()) {
                        return true;
                    }
                    return pred.clientName.getText().toUpperCase().contains(newValue.toUpperCase());
                });
                list_search_clients.getChildren().clear();

                for (ControllerItemClientRow user : filteredData) {
                    try {

                        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/ItemClientRow.fxml"));
                        Node node = loader.load();
                        ControllerItemClientRow controller = loader.getController();
                        controller.name = user.name;
                        controller.id = user.id;
                        controller.setName(String.format("%s (#%s)", controller.name, controller.id));
                        controller.mainController = this;
                        list_search_clients.getChildren().add(node);

                    } catch (Exception e) {
                        System.out.print("Nu s-a putut genera client(FXML)");
                        //e.printStackTrace();
                    }
                }
            });
        }
        catch (SQLException e)
        {
            //afiseaza meniul pentru conexiune esuata
            setOnlyMenu(connectionFailed_menu);
            e.printStackTrace();
        }


    }
    public void OnCartiButtonClicked(MouseEvent mouseEvent) {
        setOnlyMenu(books_menu);
    }
    public void OnStatisticiButtonClicked(MouseEvent mouseEvent) {
        setOnlyMenu(statistici_menu);
    }

    // CLIENT_DETAILS MENU FUNCTIONS

    public void SelectAllFromRezervari(MouseEvent mouseEvent) {
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
    public void SelectNoneFromRezervari(MouseEvent mouseEvent) {
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
    public void SelectAllFromInventar(MouseEvent mouseEvent) {
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
    public void SelectNoneFromInventar(MouseEvent mouseEvent) {
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

    // POPUP TRANSACTION

    public void ShowPopupTransaction(MouseEvent mouseEvent) {
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
    public void ConfirmTransaction(MouseEvent mouseEvent) {
        popup_menu_validation.setVisible(true);
    }
    public void CancelTransaction(MouseEvent mouseEvent) {
        popup_menu_tranzactie.setVisible(false);
    }

    // POPUP VALIDATION (Esti sigur?)

    public void AreYouSureYes(MouseEvent mouseEvent) {
        //confirmare tranzactie imprumut/retur
        if(popup_menu_tranzactie.isVisible()) {
            try
            {
                Connection connection = DriverManager.getConnection(url,user,pass);

                //IMPRUMUT
                for(ControllerItemBookReservedRow book : list_selected_reserved_books)
                {
                    //statement pentru schimbarea statusului REZERVAT -> IMPRUMUTAT
                    connection.createStatement().executeUpdate("UPDATE cartiimprumutate\n" +
                                                                "SET statusImprumut = 'INVENTAR'\n" +
                                                                "WHERE statusImprumut = 'REZERVAT' AND\n" +
                                                                "User_idUser = " + transaction_user_id + " AND Carte_idCarte = " + book.id + ';');

                    //statement pentru stergerea cu o unitate a cartii din stoc
                    connection.createStatement().executeUpdate("UPDATE carte\n" +
                                                                    "SET numarCarte = numarCarte-1\n" +
                                                                    "WHERE idCarte = " + book.id + ';');
                }

                //RETUR
                for(ControllerItemBookInventoryRow book : list_selected_inventory_books)
                {
                    //statement pentru adaugarea cu o unitate a cartii in stoc
                    connection.createStatement().executeUpdate("UPDATE carte\n" +
                                                                    "SET numarCarte = numarCarte+1\n" +
                                                                    "WHERE idCarte = " + book.id + ';');

                    //statement pentru stergerea cartii din inventar
                    connection.createStatement().executeUpdate("DELETE FROM cartiimprumutate\n" +
                            "WHERE statusImprumut = 'INVENTAR' AND\n" +
                            "User_idUser = " + transaction_user_id + " AND Carte_idCarte = " + book.id + ';');


                }

                //actualizam meniul
                initialize_menu_client_detalii(transaction_user_id);

                System.out.print("\nTranzactie efectuata!");
                connection.close();

            } catch (SQLException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            popup_menu_validation.setVisible(false);
            popup_menu_tranzactie.setVisible(false);

            System.out.print("\nProces finalizat!");
        }
    }
    public void AreYouSureNo(MouseEvent mouseEvent) {
        popup_menu_validation.setVisible(false);
    }

    // FUNCTII de actualizare

    void initialize_menu_client_detalii(String userID) throws SQLException, IOException {
        System.out.print("\ninitialize_menu_client_detalii(): userID: " + userID);
        setOnlyMenu(client_menu_details);
        btn_efectueaza.setDisable(true);

        //resetarea datelor
        vbox_books_reserved.getChildren().clear();
        list_books_reserved.clear();
        vbox_books_inventory.getChildren().clear();
        list_books_inventory.clear();

        //Conexiune noua la db
        Connection connection = DriverManager.getConnection(url,user, pass);
        String statement = "SELECT carte.idCarte, carte.titluCarti, carte.autorCarte, carte.genCarte, borrow.statusImprumut\n" +
                "FROM mydb.carte carte, mydb.cartiimprumutate borrow\n" +
                "WHERE carte.idCarte = borrow.Carte_idCarte AND borrow.User_idUser = " + userID;

        PreparedStatement preparedStatement = connection.prepareStatement(statement);
        ResultSet rs = preparedStatement.executeQuery();
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
        connection.close();
    }


}