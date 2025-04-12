package proiect.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.List;
import java.util.Random;

public class ControllerLibrarian {
    private ControllerMain mainController;
    private Pane background;
    private Pane Clients;
    private Pane Carti;
    private Pane Statistici;
    String[] buttonIds={"exit","clienti","carti","statistici"};
    @FXML
    private StackPane LibrarianPane;
    private final List<String> firstNames = List.of("Ion", "Maria", "Andrei");
    private final List<String> lastNames = List.of("Popescu", "Ionescu", "Georgescu");
    private final Random random = new Random();
    public void initialize() throws IOException {
       // background = loadPane("/proiect/fxml/librarian/Background.fxml");
        Clients = loadPane("/proiect/fxml/librarian/Clients.fxml");
        Carti=loadPane("/proiect/fxml/librarian/Carti.fxml");
        Statistici = loadPane("/proiect/fxml/librarian/Statistici.fxml");





    }



    //seteaza ControllerLibrarian ca Main
    public void setMainController(ControllerMain mainController) {
        this.mainController = mainController;
    }
    //metoda de incarcat panel uri
    Pane loadPane(String fxmlPath) throws IOException{

         Pane   pane = FXMLLoader.load(getClass().getResource(fxmlPath));

        for (String buttonId : buttonIds) {
            Button button = (Button) pane.lookup("#" + buttonId);
            if (button != null) {
                switch (buttonId) {
                    case "exit":
                        button.setOnAction(e -> quit_app());
                        break;
                        case "clienti":
                            button.setOnAction(e -> ClientShow());
                            break;
                            case "carti":
                                button.setOnAction(e -> BookShow());
                                break;

                    case "statistici":
                        button.setOnAction(e -> StatisticsShow());
                        break;

                }
            }
        }

        return pane;
    }
    private void populateClientList() {
        VBox clientListContainer = (VBox) Clients.lookup("#list_search_clients");
        if (clientListContainer == null) {
            System.err.println("Error: Could not find #list_search_clients VBox!");
            return;
        }

        clientListContainer.getChildren().clear();
        System.out.println("Generating client list...");

        for (int i = 0; i < 100; i++) {
            // Create new row manually
            Pane clientRow = new Pane();
            clientRow.setPrefSize(434, 40);
            clientRow.getStyleClass().add("list_item");

            Text nameText = new Text(generateRandomName());
            nameText.setLayoutX(9);
            nameText.setLayoutY(24);
            nameText.getStyleClass().add("client-text");

            clientRow.getChildren().add(nameText);

            clientRow.setOnMouseClicked(e ->
                    showClientDetails(nameText.getText())
            );

            clientListContainer.getChildren().add(clientRow);
        }
    }

    private String generateRandomName() {
        String firstName = firstNames.get(random.nextInt(firstNames.size()));
        String lastName = lastNames.get(random.nextInt(lastNames.size()));
        return firstName + " " + lastName;
    }

    private void showClientDetails(String clientName) {
        // Implement client details display logic here
        System.out.println("Selected client: " + clientName);
    }

    @FXML
    public void quit_app() {
        System.exit(0);
    }
    public void ClientShow()
    {
        populateClientList();
        LibrarianPane.getChildren().setAll(Clients);


    }
    public void BookShow()
    {
        LibrarianPane.getChildren().setAll(Carti);
    }
    public void StatisticsShow()
    {
        LibrarianPane.getChildren().setAll(Statistici);
    }





}
