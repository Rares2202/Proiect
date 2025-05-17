package proiect.controller;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.geometry.Pos;
import javafx.stage.Modality;
import javafx.stage.Stage;
import proiect.claseUser.*;
import java.sql.SQLException;
import java.util.*;
import java.io.IOException;

public class ControllerUser {

    @FXML
   myReadsScroll myreads;
    private Pane Home;
    private Pane Imreading;
    private Pane SearchResults;

    private Pane Myreads;
 
    Book book=new Book(0,null,null,null,null,null);
    Review review=new Review(null,0,0,0);
    MyReads myReads;
    private static String coverImagine;
    SearchResultsScroll searchResults;
    ReviewScroll reviewScroll;
    private Pane Search;
    public StackPane Userpane;
    private boolean isInitialized = false;
    int userId=-1;
    private static final String DB_URL = "jdbc:mysql://localhost:3306/mydb";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "simone";
    private final DBComands dbComands=new DBComands();
    private final String[] buttonIds = {
            "myreads", "imreading", "inchide", "submit", "search","home","review","rezerva","search1","plus","reviews","search2"
    };
    private final String[] checkboxIds = {
            "actionCheckBox","adventureCheckBox","biographyCheckBox","classicsCheckBox",
            "comicBooksCheckBox","cookbooksCheckBox","dramaCheckBox","economyCheckBox",
            "fantasyCheckBox","historicalCheckBox","horrorCheckBox","mysteryCheckBox",
            "philosophyCheckBox","poetryCheckBox", "psychologyCheckBox","religionCheckBox",
            "romanceCheckBox","scienceCheckBox","scienceFictionCheckBox","selfImprovementCheckBox"
    };
    private  List<String> preferinte = new ArrayList<>();
    private ScrollPanel homeScrollPanel;
    /**
     * <li>Initializeaza controller User</li>
     */
    public void initialize() {
        try {
            Home = loadPane("/proiect/fxml/user/Home.fxml");
            Imreading = loadPane("/proiect/fxml/user/Imreading.fxml");
            Myreads = loadPane("/proiect/fxml/user/Myreads.fxml");
            Pane preferinte1 = loadPane("/proiect/fxml/user/Preferinte.fxml");
            Search = loadPane("/proiect/fxml/user/Search.fxml");
            SearchResults = loadPane("/proiect/fxml/user/SearchResults.fxml");

            initializeScrollPanel();
            initializeReviewPane();

            Userpane.getChildren().add(Home);
        } catch (IOException e) {
            System.err.println("Initialization error: " + e.getMessage());
            e.printStackTrace();
            showAlert("Critical error: Could not load application resources");
            Platform.exit();
        }
    }

    public void setUserId(int userId) {
        this.userId = userId;
        if (!isInitialized) {
            initialize();
            isInitialized = true;
        }
        handleUserSpecificPanes();
    }

    private void initializeScrollPanel() {
        homeScrollPanel = new ScrollPanel(userId);
        ScrollPane homeScrollPane = (ScrollPane) Home.lookup("#scrollPane");

        if (homeScrollPane != null) {
            homeScrollPane.setContent(homeScrollPanel);
            homeScrollPane.setFitToWidth(true);
            homeScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            homeScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        } else {
            ((Pane) Home).getChildren().add(homeScrollPanel);
        }

        homeScrollPanel.setPrefViewportWidth(Region.USE_COMPUTED_SIZE);
        homeScrollPanel.setPrefViewportHeight(Region.USE_COMPUTED_SIZE);

        homeScrollPanel.setOnCoverClick(coverUrl -> {
            try {
                book = book.initializare(coverUrl);
                coverImagine = coverUrl;
                updateBookDetails(book);
                navigateTo(Search);
            } catch (Exception e) {
                System.err.println("Error handling book click: " + e.getMessage());
                showAlert("Error loading book details");
            }
        });

        homeScrollPanel.setOnPlusButtonClick(coverUrl -> {
            try {
                myReads = new MyReads(userId, coverUrl);
                myReads.addBook(myReads);
                showAlert("The book has been saved in MyReads section!");

                int genre = dbComands.SEARCH_BY_COVER(DB_URL, DB_USER, DB_PASSWORD, coverUrl);
                dbComands.INSERT_INTO_USERPREF_GEN(DB_URL, DB_USER, DB_PASSWORD, 2, userId, genre);

                homeScrollPanel.refresh();
            } catch (Exception e) {
                System.err.println("Error saving to MyReads: " + e.getMessage());
                showAlert("Error saving book to MyReads");
            }
        });
    }

    private void navigateTo(Pane newPane) {
        int existingIndex = Userpane.getChildren().indexOf(newPane);
        if (existingIndex >= 0) {
            while (Userpane.getChildren().size() > existingIndex + 1) {
                Userpane.getChildren().remove(Userpane.getChildren().size() - 1);
            }
        } else {

            Userpane.getChildren().add(newPane);

        }


        if (newPane == Home) {
            initializeScrollPanel();
        }
    }

    private void handleUserSpecificPanes() {
        String query = "SELECT * FROM userpref WHERE user_idUser = ?";
        Boolean havePreferences = dbComands.USER_ARE_PREF(query, DB_URL, DB_USER, DB_PASSWORD, this.userId);
        try {
            if (this.userId != -1 && !havePreferences) {
                Pane preferintePane = loadPane("/proiect/fxml/user/Preferinte.fxml");
                navigateTo(preferintePane);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Pane loadPane(String fxmlPath) throws IOException {
        Pane pane = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(fxmlPath)));

        for (String buttonId : buttonIds) {
            Button button = (Button) pane.lookup("#" + buttonId);
            if (button != null) {
                switch (buttonId) {
                    case "inchide":
                        button.setOnAction(e -> quit_app());
                        break;

                    case "submit":
                        button.setOnAction(e -> {
                            preferinte = getSelectedGenres(pane);
                            trimitePreferinte(userId);
                            navigateTo(Home);
                        });
                        break;

                    case "home":
                        button.setOnAction(e -> {
                            if (Userpane.getChildren().size() > 1) {
                                Userpane.getChildren().remove(Userpane.getChildren().size() - 1);
                            }
                        });
                        break;

                    case "search":
                        button.setOnAction(e -> ReturnResults(Home));
                        break;

                    case "search1":
                        button.setOnAction(e -> ReturnResults(SearchResults));
                        break;

                    case "search2":
                        button.setOnAction(e -> ReturnResults(Search));
                        break;

                    case "myreads":
                        button.setOnAction(e -> {
                            initializeMyReads();
                            ImageView trashMyReads = (ImageView) Myreads.lookup("#trashMyReads");
                            trashMyReads.setOnMouseClicked(j -> {
                                myreads.deleteBooks();
                                myreads.refresh();
                            });
                            navigateTo(Myreads);
                        });
                        break;

                    case "imreading":
                        button.setOnAction(e -> {
                            GridPane booksGrid = (GridPane) Imreading.lookup("#booksGrid");
                            ImReading imReading1 = getImReading(booksGrid);
                            ImageView trashImReading = (ImageView) Imreading.lookup("#trashImReading");
                            trashImReading.setOnMouseClicked(j -> {
                                imReading1.deleteBooks();
                                imReading1.refresh();
                            });
                            navigateTo(Imreading);
                        });
                        break;

                    case "rezerva":
                        button.setOnAction(e -> REZERVA());
                        break;

                    case "plus":
                        button.setOnAction(j -> {
                            try {
                                myReads = new MyReads(userId, coverImagine);
                                myReads.addBook(myReads);
                                showAlert("The book has been saved in MyReads section!");

                                int genre = dbComands.SEARCH_BY_COVER(DB_URL, DB_USER, DB_PASSWORD, coverImagine);
                                dbComands.INSERT_INTO_USERPREF_GEN(DB_URL, DB_USER, DB_PASSWORD, 2, userId, genre);
                            } catch (Exception e) {
                                System.err.println("Error saving to MyReads: " + e.getMessage());
                                showAlert("Error saving book to MyReads");
                            }
                        });
                        break;

                    case "reviews":
                        button.setOnAction(j -> {
                            try {

                                    book.initializare(coverImagine);
                                    initializeReviewsDisplay();
                                    reviewScroll.show();


                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                        });
                        break;
                }
            }
        }
        return pane;
    }

    private ImReading getImReading(GridPane booksGrid) {
        ImReading imReading1 = new ImReading(userId, booksGrid);
        imReading1.setOnCoverClick(coverUrl -> {
            try {
                book = book.initializare(coverUrl);
                updateBookDetails(book);
                navigateTo(Search);
            } catch (Exception e) {
                System.err.println("Error loading book details: " + e.getMessage());
                showAlert("Error loading book details");
            }
        });
        return imReading1;
    }


    private void initializeMyReads() {
        myreads = new myReadsScroll(userId);
        myreads.setOnCoverClick(coverUrl -> {
            try {
                book = book.initializare(coverUrl);
                updateBookDetails(book);
                navigateTo(Search);
            } catch (Exception e) {
                System.err.println("Error handling book click: " + e.getMessage());
                showAlert("Error loading book details");
            }
        });

        ScrollPane myReadsScrollPane = (ScrollPane) Myreads.lookup("#myReadsPane");
        if (myReadsScrollPane != null) {
            myReadsScrollPane.setContent(myreads);
            myReadsScrollPane.setFitToWidth(true);
            myReadsScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            myReadsScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        } else {
            ((Pane) Myreads).getChildren().add(myreads);
        }
    }

    private void ReturnResults(Pane pane) {
        TextField searchField = (TextField) pane.lookup("#searchField");

        if (searchField != null) {
            String searchText = searchField.getText().trim();
            if (searchText.isEmpty()) {
                showAlert("Please enter a search term");
                return;
            }

            List<Book> results = dbComands.REZULTATE(DB_URL, DB_USER, DB_PASSWORD, searchText, searchText);
            if (results == null || results.isEmpty()) {
                showAlert("No results found for: " + searchText);
                return;
            }

            new Thread(() -> {
                List<String> urls = new ArrayList<>();
                for (Book book : results) {
                    if (book.getCoverUrl() != null) {
                        urls.add(book.getCoverUrl());
                    }
                }
                ScrollPanel.preloadImages(urls);
            }).start();

            searchResults = new SearchResultsScroll(results);
            searchResults.setOnCoverClick(coverUrl -> {
                try {
                    book = book.initializare(coverUrl);
                    coverImagine = coverUrl;
                    updateBookDetails(book);
                    navigateTo(Search);
                } catch (Exception e) {
                    System.err.println("Error loading book details: " + e.getMessage());
                    showAlert("Error loading book details");
                }
            });

            ScrollPane resultsScrollPane = (ScrollPane) SearchResults.lookup("#resultsScrollPane");
            if (resultsScrollPane != null) {
                resultsScrollPane.setContent(searchResults);
            }

            navigateTo(SearchResults);
        } else {
            showAlert("Search field not found");
        }
    }

    private void updateBookDetails(Book book) {
        try {
            Label author = (Label) Search.lookup("#autor");
            Label title = (Label) Search.lookup("#titlu");
            Label description = (Label) Search.lookup("#descriere");
            Label genre = (Label) Search.lookup("#gen");
            Pane pane = (Pane) Search.lookup("#imagine");

            if (author != null) author.setText(book.getAuthor() != null ? book.getAuthor() : "N/A");
            if (title != null) title.setText(book.getTitle() != null ? book.getTitle() : "N/A");
            if (description != null) description.setText(book.getDescription() != null ? book.getDescription() : "N/A");
            if (genre != null) genre.setText(book.getGenre() != null ? book.getGenre() : "N/A");

            if (pane != null && book.getCoverUrl() != null) {
                try {
                    Image coverImage = new Image(book.getCoverUrl(), true);
                    BackgroundImage bgImage = new BackgroundImage(
                            coverImage,
                            BackgroundRepeat.NO_REPEAT,
                            BackgroundRepeat.NO_REPEAT,
                            BackgroundPosition.CENTER,
                            new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true)
                    );
                    pane.setBackground(new Background(bgImage));
                } catch (Exception e) {
                    System.err.println("Error loading cover: " + e.getMessage());
                    pane.setStyle("-fx-background-color: #f0f0f0;");
                }
            }
        } catch (Exception e) {
            System.err.println("Error updating book details: " + e.getMessage());
        }
    }

    private void REZERVA() {
        Label titlu = (Label) Search.lookup("#titlu");
        dbComands.insertIntoImprumuturi(DB_URL, DB_USER, DB_PASSWORD, userId, titlu.getText());
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

        String query = "SELECT idpreferinte, genuri FROM genuri";
        Map<String, Integer> validGenres = dbComands.SELECT_FROM_GENURI(query, DB_URL, DB_USER, DB_PASSWORD);
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
            showAlert("Invalid genre");
            return;
        }

        query = "INSERT INTO userpref(number, user_idUser, preferinte_idpreferinte) VALUES(?, ?, ?)";
        dbComands.INSERT_INTO_USERPREF(query, DB_URL, DB_USER, DB_PASSWORD, userId, validGenreIds);
    }

    @FXML
    public void quit_app() {
        System.exit(0);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Attention!");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }



    List<Star> stars = new ArrayList<>();
    int currentRating = 0;

    private void initializeReviewPane() {

        Pane reviewPane = (Pane) Search.lookup("#review1");
        reviewPane.getChildren().clear();

        HBox starContainer = new HBox(5);
        starContainer.setAlignment(Pos.CENTER);

        for (int i = 0; i < 5; i++) {
            Star star = new Star(i + 1);
            star.setOnAction(e -> handleStarClick(star));
            stars.add(star);
            starContainer.getChildren().add(star);
        }
        reviewPane.getChildren().add(starContainer);

    }

    private void handleStarClick(Star clickedStar) {
        currentRating = clickedStar.getRatingValue();
        updateStars();
        showReviewPopup();
       resetAllStars();
    }
    private void updateStars() {
        for (Star star : stars) {
            star.setFilled(star.getRatingValue() <= currentRating);
        }
    }
    public void resetAllStars() {
        currentRating = 0;
        for (Star star : stars) {
            star.reset();
        }
    }
    private void showReviewPopup() {
        // Create a new stage for the popup
        Stage popupStage = new Stage();
        popupStage.setTitle("Review");

        // Block events to other windows
        popupStage.initModality(Modality.APPLICATION_MODAL);

        // Create review components
        Label instructionLabel = new Label("Please write your review below:");
        TextArea reviewTextArea = new TextArea();
        reviewTextArea.setPromptText("Enter your review here...");
        reviewTextArea.setPrefRowCount(5);

        Button submitButton = new Button("Submit Review");
        submitButton.setOnAction(j -> {
            String reviewText = reviewTextArea.getText();
            review=new Review(reviewText,currentRating,userId,book.getId());
            //System.out.println("Review submitted: " + review.getReviewText()+" "+review.getUser_idUser()+" "+review.getCarte_idCarte()+" "+review.getReviewRating());
            review.trimiteReview(review);
            try {
                reviewScroll.refresh();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            popupStage.close();
        });

        // Layout for the popup
        VBox popupLayout = new VBox(10, instructionLabel, reviewTextArea, submitButton);
        popupLayout.setPadding(new Insets(15));
        popupLayout.setAlignment(Pos.CENTER);
        popupStage.setResizable(false);


        // Set the scene for the popup
        Scene popupScene = new Scene(popupLayout, 500, 400);
        popupStage.setScene(popupScene);

        // Show the popup
        popupStage.showAndWait();
    }
    public void setMainController(ControllerMain controllerMain) {
    }
    /**
     * <li>Initializeaza scrollPanel ul din pagina Review.fxml care contine toate review-urile</li>
     */
    private void initializeReviewsDisplay() throws SQLException {
        if (book == null || book.getId() == 0) {
            System.err.println("No book selected for reviews");
            showAlert("Please select a book first");
            return;
        }

        ScrollPane reviewsContainer = (ScrollPane) Search.lookup("#reviews1");
        if (reviewsContainer == null) {
            System.err.println("Reviews container not found");
            return;
        }

        if (reviewScroll != null && reviewScroll.getBookId() == book.getId()) {
            if (!reviewScroll.isShowing()) {
                reviewScroll.show();
            }
            return;
        }

        reviewScroll = new ReviewScroll(book.getId(), reviewsContainer);
        reviewsContainer.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        reviewsContainer.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        reviewsContainer.setFitToWidth(true);
        reviewsContainer.setVisible(true);
        reviewScroll.show();
    }

}
