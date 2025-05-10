package proiect;

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
import javafx.stage.Stage;

import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;
import java.util.concurrent.Executor;


/**
 * Clasa ce implementeaza Controllerul pentru Librarian.
 */
public class ControllerLibrarian{

    /**
     * Meniu client.
     */
    public AnchorPane client_menu;
    public VBox list_search_clients;
    public ScrollPane ScrollPane;

    /**
     * Detalii meniu client.
     */
    public AnchorPane client_menu_details;
    public Text clienti_menu_clientName;
    public Label clienti_menu_phone;
    public Label clienti_menu_mail;

    /**
     * Meniu carti.
     */
    public AnchorPane books_menu;

    /**
     * Meniu statistici.
     */
    public AnchorPane statistici_menu;


    @FXML
    void initialize()
    {
        System.out.print("Main GUI initializat.");
        loadData();
        setOnlyMenu(null);
    }

    public void quit_app(MouseEvent mouseEvent) {
        System.exit(0);
    }


    public void OnClientiButtonClicked(MouseEvent mouseEvent){
        setOnlyMenu(client_menu);
    }

    public void OnCartiButtonClicked(MouseEvent mouseEvent) {
        setOnlyMenu(books_menu);
    }

    public void OnStatisticiButtonClicked(MouseEvent mouseEvent) {
        setOnlyMenu(statistici_menu);
    }

    public static String randomName() {
        List<String> NUME = Arrays.asList("Popescu", "Ionescu", "Georgescu", "Vasile", "Stoica", "Radu");
        List<String> PRENUME = Arrays.asList("Ion", "Maria", "Andrei", "Elena", "Vasile", "Ioana");

        Random rand = new Random();
        String nume = NUME.get(rand.nextInt(NUME.size())); // Selectează aleator un nume
        String prenume = PRENUME.get(rand.nextInt(PRENUME.size())); // Selectează aleator un prenume

        return prenume + " " + nume; // Returnează "prenume nume"
    }

    public void setOnlyMenu(AnchorPane newMenu)
    {
        for (Field field : this.getClass().getDeclaredFields()) {
            if (field.getType() == AnchorPane.class) {
                try
                {
                    AnchorPane menu = (AnchorPane) field.get(this);
                    menu.setVisible(false);
                }
                catch (Exception e)
                {
                    System.out.print("Nu s-a putut accesa meniul.");
                }
            }
        }
        if(newMenu!=null)
            newMenu.setVisible(true);
    }



    private void loadData()
    {
        //initializare lista de clienti
        int nr_items = 100;
        for (int i=0; i<nr_items; i++) {
            try {

                FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/ItemClientRow.fxml"));
                Node node = loader.load();

                ControllerItemClientRow controller = loader.getController();
                controller.setName(randomName());
                controller.mainController = this;

                list_search_clients.getChildren().add(node);

            } catch (Exception e) {
                System.out.print("Nu s-a putut genera client(FXML)");
                //e.printStackTrace();
            }
        }
    }


}