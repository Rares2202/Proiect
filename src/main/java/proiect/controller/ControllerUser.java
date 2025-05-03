package proiect.controller;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;

import java.util.*;
import java.io.IOException;
import java.sql.*;


/**
 * Tipul controller user.
 */
public class ControllerUser {

    @FXML
    private ScrollPane scrollPane;
    private Pane Home;
    private Pane Imreading;
    private ControllerMain mainController;
    private Pane Myreads;
    private Pane Preferinte;
    private Pane Review;
    private Pane Search;
    /**
     * The Userpane.
     */
    public StackPane Userpane;
    /**
     * The User id.
     */
    int userId=-1;
    private static final String DB_URL = "jdbc:mysql://localhost:3306/mydb";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root";
    private GridPane gridPane;
    private final String[] buttonIds = {
            "myreads", "imreading", "inchide", "submit", "search","home","review"
    };
    private final String[] checkboxIds = {
            "actionCheckBox","adventureCheckBox","biographyCheckBox","classicsCheckBox",
            "comicBooksCheckBox","cookbooksCheckBox","dramaCheckBox","economyCheckBox",
            "fantasyCheckBox","historicalCheckBox","horrorCheckBox","mysteryCheckBox",
            "philosophyCheckBox","poetryCheckBox", "psychologyCheckBox","religionCheckBox",
            "romanceCheckBox","scienceCheckBox","scienceFictionCheckBox","selfImprovementCheckBox"
    };
    private  List<String> preferinte = new ArrayList<>();

    /**
     * Initialize.
     */
    public void initialize() {
        try {

            Home = loadPane("/proiect/fxml/user/Home.fxml"); // Add Home pane
            Imreading = loadPane("/proiect/fxml/user/Imreading.fxml");
            Myreads = loadPane("/proiect/fxml/user/Myreads.fxml");
            Preferinte = loadPane("/proiect/fxml/user/Preferinte.fxml");
            Review = loadPane("/proiect/fxml/user/Review.fxml");
            Search = loadPane("/proiect/fxml/user/Search.fxml");
           initializeHomeGrid();




            // Adaugă conținut în GridPane

            // Configurează ScrollPane




            Userpane.getChildren().setAll(Home);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * <p>Sets user id.</p>
     *
     * @param userId Id user.
     */
    public void setUserId(int userId) {
        this.userId = userId;
        System.out.println("Received user ID: " + userId);
        handleUserSpecificPanes();
    }

    /**
     * <p>Creeaza pane-ul specific userului curent.
     * </p>
     */
    private void handleUserSpecificPanes() {
        try {
            if(this.userId != -1&&!havePreferences(this.userId)) {
                Pane preferintePane = loadPane("/proiect/fxml/user/Preferinte.fxml");
                Userpane.getChildren().setAll(preferintePane);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * <p>
     *     Functionalitatile butoanelor.
     * </p>
     * @param fxmlPath Path catre pane-ul ce trebuie incarcat.
     * @return pane-ul incarcat.
     * @throws IOException
     */
    private Pane loadPane(String fxmlPath) throws IOException {
        Pane pane = FXMLLoader.load(getClass().getResource(fxmlPath));

        for (String buttonId : buttonIds) {
            Button button = (Button) pane.lookup("#" + buttonId);
            if (button != null) {
                switch (buttonId) {
                    //in switch ul asta se vor adauga restul butoanelor pentru paneluri;
                    case "inchide":
                        button.setOnMouseClicked(e -> {
                            quit_app();
                        });

                        break;
                    case "submit":
                        button.setOnAction(e -> {
                            preferinte = getSelectedGenres(pane);
                            trimitePreferinte(userId);
                            Userpane.getChildren().setAll(Home);
                        });
                        break;
                        case "home":
                            button.setOnAction(e -> {
                                Userpane.getChildren().setAll(Home);
                            });

                        break;
                        case "search":
                            button.setOnAction(e -> {
                                Userpane.getChildren().setAll(Search);

                            });
                        break;
                        case "myreads":
                            button.setOnAction(e -> {
                                Userpane.getChildren().setAll(Myreads);

                            });
                        break;
                        case "imreading":
                            button.setOnAction(e -> {
                                Userpane.getChildren().setAll(Imreading);

                            });
                        break;
                        case "review":
                            button.setOnAction(e -> {
                                Userpane.getChildren().setAll(Review);

                            });
                        break;

                }
           }
       }
        return pane;
    }

    /**
     * <p>
     *     Pane-ul de selectare preferinte.
     * </p>
     * @param pane Pane-ul in care se vor pune genurile ca si checkmark.
     * @return pane-ul incarcat
     */
    private List<String> getSelectedGenres(Pane pane) {
        List<String> selectedGenres = new ArrayList<>();
        Map<String, String> genreMap = Map.ofEntries(
                Map.entry("actionCheckBox", "Action"),
                Map.entry("adventureCheckBox", "Adventure"),
                Map.entry("biographyCheckBox", "Biography"),
                Map.entry("classicsCheckBox", "Classics"),
                Map.entry("comicBooksCheckBox", "Comic Books"),
                Map.entry("cookbooksCheckBox", "Cookbooks"),
                Map.entry("dramaCheckBox", "Drama"),
                Map.entry("economyCheckBox", "Economy"),
                Map.entry("fantasyCheckBox", "Fantasy"),
                Map.entry("historicalCheckBox", "Historical"),
                Map.entry("horrorCheckBox", "Horror"),
                Map.entry("mysteryCheckBox", "Mystery"),
                Map.entry("philosophyCheckBox", "Philosophy"),
                Map.entry("poetryCheckBox", "Poetry"),
                Map.entry("psychologyCheckBox", "Psychology"),
                Map.entry("religionCheckBox", "Religion"),
                Map.entry("romanceCheckBox", "Romance"),
                Map.entry("scienceCheckBox", "Science"),
                Map.entry("scienceFictionCheckBox", "Science Fiction"),
                Map.entry("selfImprovementCheckBox", "Self Improvement")
        );

        for (String checkboxId : checkboxIds) {
            CheckBox checkBox = (CheckBox) pane.lookup("#" + checkboxId);
            if (checkBox != null && checkBox.isSelected()) {
                selectedGenres.add(genreMap.get(checkboxId));
            }
        }
        return selectedGenres;
    }
    private void trimitePreferinte(int userId) {
        if (preferinte.isEmpty()) {
            showAlert("No genres selected");
            return;
        }


        Map<String, Integer> validGenres = getValidGenres();
        List<String> invalidGenres = new ArrayList<>();
        List<Integer> validGenreIds = new ArrayList<>();

        for (String genre : preferinte) {
            if (validGenres.containsKey(genre)) {
                validGenreIds.add(validGenres.get(genre));
            } else {
                invalidGenres.add(genre);
            }
        }

        if (!invalidGenres.isEmpty()) {
            showAlert("Invalid genres: " + String.join(", ", invalidGenres));
            return;
        }


        String query = "INSERT INTO userpref(number, user_idUser, preferinte_idpreferinte) VALUES(?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = connection.prepareStatement(query)) {


            for (Integer genreId : validGenreIds) {
                pstmt.setInt(1, 1);
                pstmt.setInt(2, userId);
                pstmt.setInt(3, genreId);
                pstmt.addBatch();
            }

            int[] results = pstmt.executeBatch();
            showAlert("Successfully saved " + Arrays.stream(results).sum() + " preferences!");

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error saving preferences: " + e.getMessage());
        }
    }

    private Map<String, Integer> getValidGenres() {
        Map<String, Integer> genreMap = new HashMap<>();
        String query = "SELECT idpreferinte, genuri FROM genuri";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                genreMap.put(rs.getString("genuri"), rs.getInt("idpreferinte"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return genreMap;
    }


    /**
     * Quit app.
     */
    @FXML
    public void quit_app() {
        System.exit(0);
    }

    /**
     * Sets main controller.
     *
     * @param mainController the main controller
     */
    public void setMainController(ControllerMain mainController) {
        this.mainController = mainController;
    }


    /**
     * Have preferences boolean.
     *
     * @param id the id
     * @return the boolean
     * @throws SQLException the sql exception
     */
    Boolean havePreferences(int id) throws SQLException {

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT * FROM userpref WHERE user_idUser = ?";
            try (PreparedStatement checkStmt = connection.prepareStatement(query)) {
                checkStmt.setInt(1, id);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next()) {
                    return true;
                }
            }

        }
        return false;
    }

    /**
     * <p>
     *     Alerta eroare.
     * </p>
     * @param message
     */
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Pane Changed");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * <p>
     *     Creare home.
     * </p>
     */
    private void initializeHomeGrid() {
        // Creează și configurează GridPane-ul
        GridPane homeGrid = new GridPane();
        int cols = 4;
        int rows = 20;
        int cellWidth = 130;
        int cellHeight = 150;


        // Populează grid-ul cu celule colorate
        generateRandomColors(homeGrid);

        // Calculează dimensiunea totală a gridului
        double totalWidth = cols * (cellWidth );
        double totalHeight = rows * (cellHeight )-600;

        homeGrid.setPrefSize(totalWidth, totalHeight);

        // Găsește ScrollPane-ul din scenă
        ScrollPane scrollPane = (ScrollPane) Home.lookup("#scrollPane");

        // Setează gridul ca și conținut
        scrollPane.setContent(homeGrid);


        // Ajustează dimensiunea ScrollPane-ului la dimensiunea gridului
        scrollPane.setPrefViewportWidth(totalWidth);
        scrollPane.setPrefViewportHeight(totalHeight);

        // Dezactivează scrollul vertical
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        // Viteză personalizată pentru scroll (dacă e reactivat)
        scrollPane.setOnScroll(event -> {
            double deltaY = event.getDeltaY() * 0.005;
            scrollPane.setVvalue(scrollPane.getVvalue() - deltaY);
        });
    }

        private void generateRandomColors(GridPane gridPane) {
        int cols = 4;
        int rows = 15; // 20 rânduri, forțează scroll-ul
        int gap=10;
        int cellWidth = 130;
        int cellHeight = 150;
        gridPane.setHgap(3*gap);
        gridPane.setVgap(gap);
        Region region = new Region();
        region.setPrefSize(70, 2250);
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                Region cell = new Region();
                cell.setPrefSize(cellWidth, cellHeight);
                double red = Math.random();
                double green = Math.random();
                double blue = Math.random();

                cell.setStyle(String.format(
                        "-fx-background-color: rgb(%d, %d, %d);",
                        (int)(red * 255),
                        (int)(green * 255),
                        (int)(blue * 255)
                ));

                final int r = row;
                final int c = col;

                cell.setOnMouseClicked(event -> {
                    double newRed = Math.random();
                    double newGreen = Math.random();
                    double newBlue = Math.random();

                    cell.setStyle(String.format(
                            "-fx-background-color: rgb(%d, %d, %d);",
                            (int)(newRed * 255),
                            (int)(newGreen * 255),
                            (int)(newBlue * 255)
                    ));

                    System.out.println("Click pe celula: (" + r + ", " + c + ")");
                });

                gridPane.add(cell, col, row);
            }
        }
    }
}
