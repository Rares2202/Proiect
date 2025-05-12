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
import java.util.*;
import java.io.IOException;

public class ControllerUser {

    @FXML
   myReadsScroll myreads;
    private Pane Home;
    private Pane Imreading;
    private Pane SearchResults;
    private GridPane booksGrid;
    private Pane Myreads;
    Book book=new Book(0,null,null,null,null,0,null);
    Review review=new Review(null,0,0,0);
    MyReads myReads;
    SearchResultsScroll searchResults;
    ImReading imReading;
    private Pane Search;
    public StackPane Userpane;
    int userId=-1;
    private static final String DB_URL = "jdbc:mysql://localhost:3306/mydb";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "simone";
    private final DBComands dbComands=new DBComands();
    private final String[] buttonIds = {
            "myreads", "imreading", "inchide", "submit", "search","home","review","rezerva","search1"
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
            Home = loadPane("/proiect/fxml/user/Home.fxml");
            Imreading = loadPane("/proiect/fxml/user/Imreading.fxml");
            Myreads = loadPane("/proiect/fxml/user/Myreads.fxml");
            Pane preferinte1 = loadPane("/proiect/fxml/user/Preferinte.fxml");
            Search = loadPane("/proiect/fxml/user/Search.fxml");
            SearchResults = loadPane("/proiect/fxml/user/SearchResults.fxml");
            initializeScrollPanel();
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



    private void initializeScrollPanel() {

        ScrollPanel customScrollPanel = new ScrollPanel(userId);

        customScrollPanel.setOnCoverClick(coverUrl -> {
            try {
                Label author = (Label) Search.lookup("#autor");
                    Label title = (Label) Search.lookup("#titlu");
                    Label description = (Label) Search.lookup("#descriere");
                    Label genre = (Label) Search.lookup("#gen");
                    Pane pane=(Pane) Search.lookup("#imagine");
                    book=book.initializare(coverUrl);
                    System.out.println(coverUrl);
                    author.setText(book.getAuthor());
                    title.setText(book.getTitle());
                    description.setText(book.getDescription());
                    genre.setText(book.getGenre());
                    Image coverImage = new Image(coverUrl);
                    BackgroundImage bgImage = new BackgroundImage(
                            coverImage,
                            BackgroundRepeat.NO_REPEAT,
                            BackgroundRepeat.NO_REPEAT,
                            BackgroundPosition.CENTER,
                            new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true)
                    );
                    pane.setBackground(new Background(bgImage));

                    Platform.runLater(() ->Userpane.getChildren().setAll(Search));

            } catch (Exception e) {
                System.err.println("Error handling book click: " + e.getMessage());
                showAlert("Error loading book details");
            }
        });
        customScrollPanel.setOnPlusButtonClick(coverUrl -> {
            try {

                myReads = new MyReads(userId, coverUrl);
                myReads.addBook(myReads);

                Platform.runLater(() ->
                        showAlert("The book has been saved in MyReads section!")
                );
            } catch (Exception e) {
                System.err.println("Error saving to MyReads: " + e.getMessage());
                Platform.runLater(() ->
                        showAlert("Error saving book to MyReads")
                );
            }
        });

        ScrollPane homeScrollPane = (ScrollPane) Home.lookup("#scrollPane");
        if (homeScrollPane != null) {
            homeScrollPane.setContent(customScrollPanel);
            homeScrollPane.setFitToWidth(true);
            homeScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            homeScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        } else {
            ((Pane)Home).getChildren().add(customScrollPanel);
        }

        customScrollPanel.setPrefViewportWidth(Region.USE_COMPUTED_SIZE);
        customScrollPanel.setPrefViewportHeight(Region.USE_COMPUTED_SIZE);
    }

    private void handleUserSpecificPanes() {
        String query = "SELECT * FROM userpref WHERE user_idUser = ?";
        Boolean havePreferences=dbComands.USER_ARE_PREF(query,DB_URL,DB_USER,DB_PASSWORD,this.userId);
        try {
            if(this.userId != -1&&!havePreferences) {
                Pane preferintePane = loadPane("/proiect/fxml/user/Preferinte.fxml");
                Userpane.getChildren().setAll(preferintePane);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private Pane loadPane(String fxmlPath) throws IOException {
        Pane pane = FXMLLoader.load(Objects.requireNonNull(Objects.requireNonNull(getClass().getResource(fxmlPath))));

        for (String buttonId : buttonIds) {
            Button button = (Button) pane.lookup("#" + buttonId);
            if (button != null) {
                switch (buttonId) {
                    //in switch ul asta se vor adauga restul butoanelor pentru paneluri;
                    case "inchide":
                        button.setOnAction(_ -> quit_app());

                        break;
                    case "submit":
                        button.setOnAction(_ -> {
                            preferinte = getSelectedGenres(pane);
                            trimitePreferinte(userId);
                            Userpane.getChildren().setAll(Home);
                        });
                        break;
                        case "home":
                            button.setOnAction(_ -> Userpane.getChildren().setAll(Home));

                        break;
                    case "search":
                        button.setOnAction(_ -> {
                            ReturnResults(Home);
                        });
                        break;
                    case "search1":
                        button.setOnAction(_ -> {
                            ReturnResults(SearchResults);
                        });
                        break;
                        case "myreads":
                            button.setOnAction(_ -> {
                                //adauga functie
                                initializeMyReads();


                                ImageView trashMyReads = (ImageView) Myreads.lookup("#trashMyReads");
                                trashMyReads.setOnMouseClicked(e -> {
                                    myreads.deleteBooks();
                                    myreads.refresh();

                                    });
                                Userpane.getChildren().setAll(Myreads);
                            });

                        break;
                        case "imreading":
                            button.setOnAction(_ ->{
                                GridPane booksGrid = (GridPane) Imreading.lookup("#booksGrid");
                               ImReading imReading1= new ImReading(userId, booksGrid);
                               imReading1.setOnCoverClick(coverUrl -> {
                                   try {
                                       book = book.initializare(coverUrl);
                                       updateBookDetails(book);
                                       Userpane.getChildren().setAll(Search);
                                   } catch (Exception e) {
                                       System.err.println("Error loading book details: " + e.getMessage());
                                       showAlert("Error loading book details");
                                   }
                               });
                                ImageView trashImReading = (ImageView) Imreading.lookup("#trashImReading");
                                trashImReading.setOnMouseClicked(e -> {imReading1.deleteBooks();
                                                              imReading1.refresh();  });
                                Userpane.getChildren().setAll(Imreading);
                            } );
                        break;
                    case "rezerva":
                        button.setOnAction(_ -> {
                            REZERVA();

                        });
                        break;


                }
           }
       }
        return pane;
    }

//    private void displayBooksInGrid(GridPane booksGrid, int userId) {
//        DBComands dbComands = new DBComands();
//        List <BookInfo> books=new ArrayList<>();
//        books=dbComands.IS_IN_USE(DB_URL,DB_USER,DB_PASSWORD,userId);
//
//    }

    private void REZERVA(){
        Label titlu =(Label) Search.lookup("#titlu");
        DBComands dbComands=new DBComands();
        dbComands.insertIntoImprumuturi(DB_URL,DB_USER,DB_PASSWORD,userId,titlu.getText());

    }

    private void ReturnResults(Pane pane) {
        TextField searchField = (TextField) pane.lookup("#searchField");

        if (searchField != null) {
            String searchText = searchField.getText().trim(); // Elimină spațiile albe

            // Verifică dacă searchText este gol
            if (searchText.isEmpty()) {
                showAlert("Please enter a search term");
                return; // Ieșim din metodă fără a efectua căutarea
            }

            List<Book> results = dbComands.REZULTATE(DB_URL, DB_USER, DB_PASSWORD, searchText, searchText);

            // Verifică dacă s-au găsit rezultate
            if (results == null || results.isEmpty()) {
                showAlert("No results found for: " + searchText);
                return;
            }

            // Creează un nou SearchResultsScroll cu rezultatele obținute
            searchResults = new SearchResultsScroll(results);

            // Setează handler pentru click pe copertă
            searchResults.setOnCoverClick(coverUrl -> {
                try {
                    book = book.initializare(coverUrl);
                    updateBookDetails(book);
                    Userpane.getChildren().setAll(Search);
                } catch (Exception e) {
                    System.err.println("Error loading book details: " + e.getMessage());
                    showAlert("Error loading book details");
                }
            });

            // Afișează rezultatele
            ScrollPane resultsScrollPane = (ScrollPane) SearchResults.lookup("#resultsScrollPane");
            if (resultsScrollPane != null) {
                resultsScrollPane.setContent(searchResults);
            }

            Userpane.getChildren().setAll(SearchResults);
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
                Image coverImage = new Image(book.getCoverUrl());
                BackgroundImage bgImage = new BackgroundImage(
                        coverImage,
                        BackgroundRepeat.NO_REPEAT,
                        BackgroundRepeat.NO_REPEAT,
                        BackgroundPosition.CENTER,
                        new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true)
                );
                pane.setBackground(new Background(bgImage));
            }
        } catch (Exception e) {
            System.err.println("Error updating book details: " + e.getMessage());
        }
    }
    private void initializeMyReads() {
        myreads = new myReadsScroll(userId);
        myreads.setOnCoverClick(coverUrl -> {
            try {
                Label author = (Label) Search.lookup("#autor");
                Label title = (Label) Search.lookup("#titlu");
                Label description = (Label) Search.lookup("#descriere");
                Label genre = (Label) Search.lookup("#gen");
                Pane pane = (Pane) Search.lookup("#imagine");

                book = book.initializare(coverUrl);
                author.setText(book.getAuthor());
                title.setText(book.getTitle());
                description.setText(book.getDescription());
                genre.setText(book.getGenre());

                Image coverImage = new Image(coverUrl);
                BackgroundImage bgImage = new BackgroundImage(
                        coverImage,
                        BackgroundRepeat.NO_REPEAT,
                        BackgroundRepeat.NO_REPEAT,
                        BackgroundPosition.CENTER,
                        new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true)
                );
                pane.setBackground(new Background(bgImage));

                Platform.runLater(() -> Userpane.getChildren().setAll(Search));
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
            ((Pane)Myreads).getChildren().add(myreads);
        }
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
        Map<String, Integer> validGenres = dbComands.SELECT_FROM_GENURI(query,DB_URL,DB_USER,DB_PASSWORD);
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
         query = "INSERT INTO userpref(number, user_idUser, preferinte_idpreferinte) VALUES(?, ?, ?)";
        dbComands.INSERT_INTO_USERPREF(query,DB_URL,DB_USER,DB_PASSWORD,userId,validGenreIds);

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
            star.setOnAction(_ -> handleStarClick(star));
            stars.add(star);
            starContainer.getChildren().add(star);
        }
        reviewPane.getChildren().add(starContainer);

    }

    private void handleStarClick(Star clickedStar) {
        currentRating = clickedStar.getRatingValue();
        updateStars();
        System.out.println("Selected rating: " + currentRating);
        showReviewPopup();

       resetAllStars();
        System.out.println("Selected rating: " + currentRating);
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
        submitButton.setOnAction(_ -> {
            String reviewText = reviewTextArea.getText();
            review=new Review(reviewText,currentRating,userId,book.getId());
            //System.out.println("Review submitted: " + review.getReviewText()+" "+review.getUser_idUser()+" "+review.getCarte_idCarte()+" "+review.getReviewRating());
            review.trimiteReview(review);
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

}
