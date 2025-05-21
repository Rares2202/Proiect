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
    /**
     * Represents the scroll panel in the "MyReads" section of the user interface.
     * This variable is linked to the corresponding FXML component and is used for
     * displaying or managing the user's currently selected or previously read books.
     *
     * It is dynamically interacted with during the application lifecycle to update
     * content or handle user-specific actions tied to the "MyReads" feature.
     * The scroll panel may also be influenced by user data or database queries to
     * reflect personalized content.
     */
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
    private static final String DB_PASSWORD = "root";
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
     * Initializes the primary user interface by loading various FXML UI components
     * and performing setup routines for application functionality.
     *
     * This method is responsible for:
     * - Loading multiple FXML panes such as Home, Imreading, Myreads, Preferences,
     *   Search, and SearchResults into memory.
     * - Initializing scroll panels and UI components such as the review pane.
     * - Adding the Home pane as the initial view in the application's main container.
     *
     * Error handling is in place to manage initialization issues such as missing
     * resources or failed UI component loading. If an exception occurs during
     * initialization, an error message is logged, an alert is displayed to the user,
     * and the application exits.
     *
     * Designed to be invoked when the application's main UI is being prepared.
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
            showAlert("Critical error: Could not load application resources");
            e.printStackTrace();
            Platform.exit();
        }
    }

    /**
     * Sets the user ID for the current session, initializes components if required,
     * and handles user-specific panes based on preferences.
     *
     * When the user ID is set, the method checks if the instance has been initialized.
     * If it hasn’t, it triggers the initialization of the necessary components.
     * Additionally, it determines appropriate panes to display for the user
     * depending on the presence of user preferences.
     *
     * @param userId the unique identifier of the user being set
     */
    public void setUserId(int userId) {
        this.userId = userId;
        if (!isInitialized) {
            initialize();
            isInitialized = true;
        }
        handleUserSpecificPanes();
    }

    /**
     * Initializes the scroll panel for the home view by setting its content, layout, and event handlers.
     *
     * This method performs the following tasks:
     * - Creates a new scroll panel associated with the current user.
     * - Attempts to locate an existing ScrollPane in the home view. If found, sets up the ScrollPane with the newly created scroll panel.
     * - If a ScrollPane is not found, adds the scroll panel directly to the home view container.
     * - Configures viewport size preferences for the scroll panel.
     * - Defines and attaches event handlers to the scroll panel for specific user interactions, such as:
     *   - Clicking on a book cover: Loads and displays the book details, navigates to the search page, handles exceptions, and displays error alerts if necessary.
     *   - Clicking the plus button on a book: Adds the book to the "MyReads" section, saves user preferences to the database, updates genres, refreshes the scroll panel, and handles
     *  exceptions with appropriate alerts.
     */
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

    /**
     * Navigates to the specified UI pane, managing the pane stack to ensure proper navigation flow.
     * If the pane already exists in the stack, it removes all subsequent panes. If the pane does not exist,
     * it adds the new pane to the stack. Additionally, if the target pane is the home view, it initializes
     * the home scroll panel.
     *
     * @param newPane the Pane object to navigate to
     */
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

    /**
     * Handles the initialization and display of user-specific panes based on their preferences.
     *
     * This method performs the following actions:
     * - Executes a database query to determine if the current user has stored preferences.
     * - If the user does not have preferences and a valid user ID is provided, it loads
     *   a specific pane (e.g., a preferences form) to allow the user to set their preferences.
     * - Navigates to the loaded pane if applicable.
     *
     * Error handling is incorporated to manage potential issues during the loading and navigation
     * of panes, such as input/output exceptions.
     */
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

    /**
     * Loads an FXML file and returns its corresponding Pane instance.
     * This method also sets up event handlers for buttons within the pane
     * based on their IDs, implementing specific functionality for each button.
     *
     * @param fxmlPath the relative path to the FXML file to be loaded.
     *                 It must not be null and should correspond to a valid resource.
     * @return the Pane instance loaded from the specified FXML file.
     * @throws IOException if an error occurs during the loading of the FXML resource.
     */
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

    /**
     * Creates and configures an ImReading instance for managing books within a specified GridPane.
     * The method sets up an event handler for cover click events, allowing the application
     * to load and display book details, navigate to the search page, handle exceptions,
     * and display error alerts if necessary.
     *
     * @param booksGrid the GridPane containing the books to be managed by the ImReading instance.
     *                  This parameter must not be null.
     * @return an ImReading instance configured with the provided GridPane and necessary event handlers.
     */
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

    /**
     * Initializes the "My Reads" section of the application, including the setup and configuration
     * of the scroll panel, book cover click event handling, and associated UI updates.
     *
     * This method performs the following tasks:
     * - Creates and initializes a new instance of "myReadsScroll" using the current user's ID.
     * - Attaches an event handler to handle interactions when a book cover is clicked.
     *   - On cover click: Initializes the selected book's details, updates the book display,
     *     navigates to the Search pane, and handles exceptions with appropriate alerts.
     * - Attempts to locate and configure the scroll panel associated with the "My Reads" section.
     *   - If a matching ScrollPane is found, it sets its content to "myReadsScroll" and applies layout adjustments.
     *   - If the ScrollPane is not found, adds the "myReadsScroll" directly to the "My Reads" pane container.
     * - Configures the horizontal and vertical scroll policies for the scroll pane.
     *
     * Error handling is implemented to address potential issues such as invalid book data or
     * missing UI components. Relevant error messages are logged, and alerts are shown to notify users of any problems.
     */
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

    /**
     * Processes a search operation and displays the results within the given pane.
     *
     * This method performs the following:
     * - Retrieves the search text from a specified text field within the provided pane.
     * - Executes a query to obtain a list of books based on the search term.
     * - Processes the search results and preloads book cover images.
     * - Displays the search results in a designated scroll pane.
     * - Handles user interactions such as clicking on a book cover to load book details.
     * - Displays appropriate alerts in case of errors, missing input, or no search results.
     *
     * @param pane the Pane object containing the search components and results display area.
     *             Must include a TextField with the ID "#searchField" and a ScrollPane with
     *             the ID "#resultsScrollPane" for this method to function as expected.
     */
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

    /**
     * Updates the details of the book provided by setting properties of UI elements such as labels and image panes
     * based on the given Book object's properties.
     *
     * @param book the Book object containing the details to be updated in the UI, including author, title, description,
     *             genre, and cover image URL. Null fields in the Book object default to "N/A" for text fields and a
     *             generic background for the cover image pane.
     */
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

    /**
     * Reserves an item for the user by inserting a record into the `Imprumuturi` database table.
     *
     * This method retrieves the title of the item to be reserved by accessing a UI label with the
     * ID "#titlu". It then invokes a database command to store the reservation details, associating
     * the current user ID with the selected item's title.
     *
     * Precondition:
     * - The UI element "#titlu" must exist and contain valid text representing the title of the item.
     * - Database connection details (`DB_URL`, `DB_USER`, and `DB_PASSWORD`) must be correctly initialized.
     * - The `dbComands` instance must provide a functional implementation of the `insertIntoImprumuturi` method.
     *
     * Postcondition:
     * - A new entry recording the reservation is added to the `Imprumuturi` table in the database.
     *
     * Error Handling:
     * - No specific error handling is implemented within this method. Potential issues such as null
     *   pointers (missing UI elements) or database access errors will need to be managed by surrounding methods
     *   or a global error-handling mechanism.
     */
    private void REZERVA() {
        Label titlu = (Label) Search.lookup("#titlu");
        dbComands.insertIntoImprumuturi(DB_URL, DB_USER, DB_PASSWORD, userId, titlu.getText());
    }

    /**
     * Retrieves a list of selected genres based on the state of checkboxes
     * within the provided pane. Each checkbox corresponds to a specific genre.
     * Only those checkboxes that are checked (selected) are considered.
     *
     * @param pane the container pane that holds the checkboxes to check
     * @return a list of strings representing the genres corresponding to the selected checkboxes
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

    /**
     * Processes the preferences selected by the user, validates them against the genres available in the database,
     * and inserts the valid preferences into the user preferences table. If there are no preferences or any invalid
     * preferences are found, appropriate alerts are shown.
     *
     * @param userId the unique identifier of the user whose preferences are being processed
     */
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

    /**
     * Terminates the application.
     */
    @FXML
    public void quit_app() {
        System.exit(0);
    }

    /**
     * Displays an informational alert dialog with the given message.
     *
     * @param message the message to be displayed in the alert dialog
     */
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Attention!");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }



    List<Star> stars = new ArrayList<>();
    int currentRating = 0;

    /**
     * Initializes the review pane by setting up a user interface component
     * that allows users to provide star-based ratings. This method retrieves
     * the pane with the ID "#review1", clears its existing content, and
     * then dynamically creates a horizontal layout of five interactive star
     * components to facilitate user input.
     *
     * The stars are added to a horizontal box (HBox) with a spacing of 5 units
     * and are centrally aligned. Each star is assigned an action handler which
     * invokes the associated logic when clicked. The initialized star components
     * are stored in a collection for further reference.
     *
     * The container for the stars is finally added to the review pane for display.
     */
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

    /**
     * Handles the click action on a star element in a rating system.
     *
     * @param clickedStar the star that was clicked, representing the selected rating value
     */
    private void handleStarClick(Star clickedStar) {
        currentRating = clickedStar.getRatingValue();
        updateStars();
        showReviewPopup();
       resetAllStars();
    }

    /**
     * Updates the state of all stars in the collection based on the current rating.
     * Each star's filled status is set to true if its rating value is less than
     * or equal to the currentRating. Otherwise, the filled status is set to false.
     *
     * This method iterates through all the stars and modifies their visual appearance
     * by setting their filled state based on the comparison with the currentRating value.
     */
    private void updateStars() {
        for (Star star : stars) {
            star.setFilled(star.getRatingValue() <= currentRating);
        }
    }

    /**
     * Resets the state of all stars in the collection and sets the current rating to zero.
     *
     * This method iterates through each star in the stars collection and invokes their reset method.
     * The currentRating variable is also updated to indicate that no stars are active.
     */
    public void resetAllStars() {
        currentRating = 0;
        for (Star star : stars) {
            star.reset();
        }
    }

    /**
     * Displays a popup window for submitting a review.
     * The popup contains a text area for entering the review and a submit button.
     * When the submit button is clicked, the entered review is processed and stored.
     *
     * The popup is modal, which means it blocks interaction
     * with other application windows until it is closed.
     *
     * This method initializes and decorates the popup window with:
     * - A title "Review".
     * - A label with instructions.
     * - A text area for user input with a placeholder and predefined size.
     * - A submit button that handles the review submission process,
     *   including creating a Review object, sending it to a storage mechanism,
     *   refreshing the review display, and closing the popup.
     *
     * Exceptions during the database refresh process are wrapped and rethrown as runtime exceptions.
     */
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

    /**
     * Sets the main controller for the application.
     *
     * @param controllerMain the main controller to set
     */
    public void setMainController(ControllerMain controllerMain) {
    }

    /**
     * Initializes and sets up the display for reviews associated with the currently selected book.
     * This method performs validation to ensure a book is selected before populating the review container.
     * If the review container is already set up for the selected book, it ensures the review scroll view is visible.
     *
     * @throws SQLException if there is an error fetching reviews from the database
     *
     * The method performs the following steps:
     * 1. Checks if a book is selected (book is not null and has a valid ID). If not, displays an alert and exits.
     * 2. Looks up the ScrollPane for displaying reviews using a specific ID. If not found, exits.
     * 3. If the review scroll is already initialized with the current book ID, ensures it is visible and returns.
     * 4. Otherwise, creates and initializes a new ReviewScroll instance for the selected book, configures the ScrollPane, and displays the reviews.
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
