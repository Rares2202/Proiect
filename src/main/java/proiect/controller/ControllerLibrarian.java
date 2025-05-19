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

/**
 * The ControllerLibrarian class is responsible for managing the librarian's GUI functionality,
 * including navigating between panes, displaying client information, handling button interactions,
 * and managing the main Controller.
 */
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

    /**
     * Initializes the main UI components for the librarian controller.
     * Loads and assigns the necessary panes for the librarian dashboard, including
     * clients, books, and statistics sections.
     *
     * @throws IOException if loading the FXML files for the panes fails
     */
    public void initialize() throws IOException {
       // background = loadPane("/proiect/fxml/librarian/Background.fxml");
        Clients = loadPane("/proiect/fxml/librarian/Clients.fxml");
        Carti=loadPane("/proiect/fxml/librarian/Carti.fxml");
        Statistici = loadPane("/proiect/fxml/librarian/Statistici.fxml");





    }


    /**
     * Sets the main controller for the librarian controller.
     *
     * @param mainController the instance of the main controller to be set
     */
    public void setMainController(ControllerMain mainController) {
        this.mainController = mainController;
    }


    /**
     * Loads an FXML file and initializes its associated UI elements.
     * Based on predefined button IDs available in the class, sets specific actions
     * for buttons if they are found in the loaded pane.
     *
     * @param fxmlPath the path to the FXML file to be loaded
     * @return the loaded Pane object containing the UI elements
     * @throws IOException if the FXML file cannot be loaded
     */
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

    /**
     * Populates the client list UI component with dynamically generated client entries.
     *
     * This method searches for a VBox element with the ID `#list_search_clients` and clears its children
     * before generating new client rows. Each row represents a client and is configured with a random name
     * and a click event listener that triggers client detail display.
     *
     * The method uses the `generateRandomName` function to create random client names and the `showClientDetails`
     * function to handle the action when a client row is clicked.
     *
     * If the VBox with the specified ID is not found, an error message is printed, and the method exits.
     *
     * Key operations include:
     * - Clearing the existing child nodes of the target VBox.
     * - Creating a new row for each client with proper styling and text content.
     * - Adding click listeners to each row for displaying client-specific details.
     */
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

    /**
     * Generates a random full name by selecting a random first name and last name
     * from predefined lists.
     *
     * @return a randomly generated full name in the format "FirstName LastName"
     */
    private String generateRandomName() {
        String firstName = firstNames.get(random.nextInt(firstNames.size()));
        String lastName = lastNames.get(random.nextInt(lastNames.size()));
        return firstName + " " + lastName;
    }

    /**
     * Displays the details of a given client.
     *
     * This method is responsible for showing detailed information about the client
     * specified by the provided client name. The implementation may include logic
     * to fetch and display additional details associated with the client.
     *
     * @param clientName the name of the client whose details are to be displayed
     */
    private void showClientDetails(String clientName) {
        // Implement client details display logic here
        System.out.println("Selected client: " + clientName);
    }

    /**
     * Exits the application.
     *
     * This method is bound to an FXML element and invoked when an associated GUI action is triggered,
     * such as clicking a "Quit" or "Exit" button. It terminates the Java application by calling
     * {@link System#exit(int)} with an exit status of 0, indicating normal termination.
     */
    @FXML
    public void quit_app() {
        System.exit(0);
    }

    /**
     * Displays the clients section within the librarian pane.
     *
     * This method is responsible for switching the visible content in
     * the `LibrarianPane` to show the clients section. It performs
     * the following operations:
     *
     * - Calls the `populateClientList` method to populate and update
     *   the client list UI with dynamically generated or pre-existing
     *   client entries.
     * - Updates the children of `LibrarianPane` to exclusively display
     *   the clients section.
     */
    public void ClientShow()
    {
        populateClientList();
        LibrarianPane.getChildren().setAll(Clients);
    }

    /**
     * Displays the books section within the librarian pane.
     *
     * This method switches the content displayed in the `LibrarianPane` to show*/
    public void BookShow()
    {
        LibrarianPane.getChildren().setAll(Carti);
    }

    /**
     * Displays the statistics section within the librarian pane.
     *
     * This method is responsible for updating the content displayed in the
     * `LibrarianPane` to show the statistics section. It replaces the current
     * children of the pane with the `Statistici` pane.
     */
    public void StatisticsShow()
    {
        LibrarianPane.getChildren().setAll(Statistici);
    }





}
