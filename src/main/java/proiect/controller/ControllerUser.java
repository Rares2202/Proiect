package proiect.controller;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
//import proiect.MainApp;
import javafx.geometry.Pos;
import proiect.Book;
import proiect.Star;

import java.util.*;
import java.io.IOException;
import java.sql.*;
import java.net.URL;

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
    public StackPane Userpane;
    int userId=-1;
    private static final String DB_URL = "jdbc:mysql://localhost:3306/mydb";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "simone";
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
    public void initialize() {
        try {

            Home = loadPane("/proiect/fxml/user/Home.fxml"); // Add Home pane
            Imreading = loadPane("/proiect/fxml/user/Imreading.fxml");
            Myreads = loadPane("/proiect/fxml/user/Myreads.fxml");
            Preferinte = loadPane("/proiect/fxml/user/Preferinte.fxml");
            Review = loadPane("/proiect/fxml/user/Review.fxml");
            Search = loadPane("/proiect/fxml/user/Search.fxml");
           initializeHomeGrid();
            initializeReviewPane();

            Userpane.getChildren().setAll(Home);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void setUserId(int userId) {
        this.userId = userId;
        System.out.println("Received user ID: " + userId);
        handleUserSpecificPanes();
    }

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
    private Pane loadPane(String fxmlPath) throws IOException {
        Pane pane = FXMLLoader.load(getClass().getResource(fxmlPath));

        for (String buttonId : buttonIds) {
            Button button = (Button) pane.lookup("#" + buttonId);
            if (button != null) {
                switch (buttonId) {
                    //in switch ul asta se vor adauga restul butoanelor pentru paneluri;
                    case "inchide":
                        button.setOnAction(e -> quit_app());

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


    @FXML
    public void quit_app() {
        System.exit(0);
    }
    public void setMainController(ControllerMain mainController) {
        this.mainController = mainController;
    }


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
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Pane Changed");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }



    List<Star> stars = new ArrayList<>();
    int currentRating = 0;
    // Add these methods to your ControllerUser class:

    private void initializeReviewPane() {
        // Get the review pane from your Search pane
        Pane reviewPane = (Pane) Search.lookup("#review1");

        // Clear any existing content
        reviewPane.getChildren().clear();

        // Create star container
        HBox starContainer = new HBox(5);
        starContainer.setAlignment(Pos.CENTER); // Center the stars

        // Create 5 stars
        for (int i = 0; i < 5; i++) {
            Star star = new Star(i + 1);
            star.setOnAction(event -> handleStarClick(star));
            stars.add(star);
            starContainer.getChildren().add(star);
        }

        // Add the stars to the review pane
        reviewPane.getChildren().add(starContainer);

    }

    private void handleStarClick(Star clickedStar) {
        currentRating = clickedStar.getRatingValue();
        updateStars();
        System.out.println("Selected rating: " + currentRating);
    }

    private void updateStars() {
        for (Star star : stars) {
            star.setFilled(star.getRatingValue() <= currentRating);
        }
    }



    private void initializeHomeGrid() {
        // Creează și configurează GridPane-ul
        GridPane homeGrid = new GridPane();
        Pane pane = new Pane();
        pane.setPrefSize(2000,30);
        int cols = 4;
        int rows = 20;
        int cellWidth = 160;
        int cellHeight = 220;


        // Populează grid-ul cu celule colorate
        generateRandomColors(homeGrid);

        // Calculează dimensiunea totală a gridului
        double totalWidth = cols * (cellWidth );
        double totalHeight = rows * (cellHeight )-1000;

        homeGrid.setPrefSize(totalWidth, totalHeight);

        // Găsește ScrollPane-ul din scenă
        ScrollPane scrollPane = (ScrollPane) Home.lookup("#scrollPane");

        // Setează gridul ca și conținut
        scrollPane.setContent(pane);
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
        int rows = 15;
        int gap = 10;
        int cellWidth = 160;
        int cellHeight = 220;
        gridPane.setHgap( gap);
        gridPane.setVgap(gap);
        Pane leftSpacer = new Pane();
        leftSpacer.setPrefWidth(30);
        GridPane.setRowSpan(leftSpacer, rows);
        List<Book> books = getBooksFromDatabase();

        // Obținem toate cover-urile din baza de date
        List<String> coverUrls = new ArrayList<>();
        String query = "SELECT coverCarte FROM carte WHERE coverCarte IS NOT NULL";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String url = rs.getString("coverCarte");
                if (url != null && !url.isEmpty()) {
                    coverUrls.add(url);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching cover URLs: " + e.getMessage());
        }

        // Încărcăm plus icon-ul
        Image plusImage = null;
        try {
            URL imageUrl = getClass().getResource("/proiect/css/plus.png");
            if (imageUrl != null) {
                plusImage = new Image(imageUrl.toExternalForm());
            }
        } catch (Exception e) {
            System.err.println("Error loading plus icon: " + e.getMessage());
        }

        gridPane.add(leftSpacer, 0, 0);

        int coverIndex = 0; // Index pentru a parcurge lista de cover-uri

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                StackPane cell = new StackPane();
                cell.setPrefSize(cellWidth, cellHeight);

                // Creăm background-ul
                Region background = new Region();

                // Setăm imaginea de fundal sau culoare aleatoare
                if (coverIndex < coverUrls.size()) {
                    try {
                        Image coverImage = new Image(coverUrls.get(coverIndex));
                        BackgroundImage bgImage = new BackgroundImage(
                                coverImage,
                                BackgroundRepeat.NO_REPEAT,
                                BackgroundRepeat.NO_REPEAT,
                                BackgroundPosition.CENTER,
                                new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true)
                        );
                        background.setBackground(new Background(bgImage));
                        coverIndex++; // Trecem la următorul cover
                    } catch (Exception e) {
                        System.err.println("Error loading cover image: " + e.getMessage());
                        // Fallback la culoare aleatoare dacă încărcarea imaginii eșuează
                        background.setStyle(String.format(
                                "-fx-background-color: rgb(%d, %d, %d);",
                                (int)(Math.random() * 255),
                                (int)(Math.random() * 255),
                                (int)(Math.random() * 255)
                        ));
                    }
                } else {
                    // Dacă nu mai avem cover-uri, folosim culoare aleatoare
                    background.setStyle(String.format(
                            "-fx-background-color: rgb(%d, %d, %d);",
                            (int)(Math.random() * 255),
                            (int)(Math.random() * 255),
                            (int)(Math.random() * 255)
                    ));
                }
                background.setPrefSize(cellWidth, cellHeight);

                // Creăm butonul cu plus
                Button saveButton = new Button();
                if (plusImage != null) {
                    ImageView plusIcon = new ImageView(plusImage);
                    plusIcon.setFitWidth(25);
                    plusIcon.setFitHeight(25);
                    saveButton.setGraphic(plusIcon);
                } else {
                    saveButton.setText("+"); // Fallback
                }
                saveButton.setStyle("-fx-background-color: transparent; -fx-padding: 5;");

                // Poziționăm butonul
                StackPane.setAlignment(saveButton, Pos.BOTTOM_RIGHT);
                saveButton.setTranslateX(-5);
                saveButton.setTranslateY(-5);

                saveButton.setOnAction(event -> {
                    System.out.println("This button was pushed");
                });

                cell.getChildren().addAll(background, saveButton);

                final int r = row;
                final int c = col;
                int finalCoverIndex = coverIndex;
                cell.setOnMouseClicked(event -> {

                    Userpane.getChildren().setAll(Search);
                    for (Book book : books) {
                        System.out.println(book.getAuthor() + " " + book.getTitle());
                    }
                        System.out.println("Click pe celula: (" + r + ", " + c + ")");

                });

                gridPane.add(cell, col + 1, row);
            }
        }
    }
    private List<Book> getBooksFromDatabase() {
        List<Book> books = new ArrayList<>();
        String query = "SELECT idCarte, titluCarti, autorCarte, descriere, genCarte, numarCarte, coverCarte FROM carte WHERE coverCarte IS NOT NULL";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Book book = new Book(
                        rs.getInt("idCarte"),
                        rs.getString("titluCarti"),
                        rs.getString("autorCarte"),
                        rs.getString("descriere"),
                        rs.getString("genCarte"),
                        rs.getInt("numarCarte"),
                        rs.getString("coverCarte")
                );
                books.add(book);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching books: " + e.getMessage());
        }
        System.out.println(books.size() + " books found");

     
        return books;
    }

}
