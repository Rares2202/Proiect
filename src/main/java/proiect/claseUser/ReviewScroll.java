package proiect.claseUser;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.*;
import javafx.scene.control.TextArea;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * The ReviewScroll class is a custom extension of the ScrollPane that acts as
 * a container for displaying user reviews associated with a specific book.
 * It offers functionality to load, display, and manage reviews dynamically.
 */
public class ReviewScroll extends ScrollPane {
    private List<Review> reviews;
    private static final String DB_URL = "jdbc:mysql://localhost:3306/mydb";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "simone";
    private final int idCarte;
    private VBox mainContainer;
    private final ScrollPane parentScrollPane;
    private boolean isVisible = true;

    /**
     * Creates an instance of the ReviewScroll class, which is a customized ScrollPane designed
     * to display book reviews. It initializes its layout, functionality, loads reviews from the
     * database, and optionally integrates with a parent ScrollPane for visibility control.
     *
     * @param idCarte the unique identifier of the book whose reviews will be displayed.
     * @param parentScrollPane an optional parent ScrollPane that can be used to control the
     *                          visibility of this ReviewScroll instance. If provided, ReviewScroll
     *                          will adjust its position and size relative to the parent ScrollPane.
     * @throws SQLException if a database access error occurs while loading reviews or initializing
     *                      the container.
     */
    public ReviewScroll(int idCarte, ScrollPane parentScrollPane) throws SQLException {
        this.idCarte = idCarte;
        this.parentScrollPane = parentScrollPane;
        initialize();
    }

    /**
     * Initializes the layout and functionality of the ReviewScroll component.
     * This method configures the scroll pane settings, creates and styles a close button,
     * and sets up the container hierarchy with the top-aligned close button and a
     * vertically spaced main content container. It also integrates with the parent
     * scroll pane if provided, setting its content, visibility, and layout properties.
     *
     * Additionally, this method loads reviews from the database, populates the main
     * container with review panes, and sets the assembled content into the ReviewScroll.
     *
     * @throws SQLException if an error occurs while interacting with the database
     * to load reviews or populate the container.
     */
    private void initialize() throws SQLException {
        this.setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
        this.setVbarPolicy(ScrollBarPolicy.NEVER);
        this.setFitToWidth(true);

        // Initialize the close button
        Button closeButton = new Button("X");
        closeButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #888; -fx-font-weight: bold;");
        closeButton.setOnAction(e -> {
            // Close both this ReviewScroll and the parent ScrollPane
            this.setVisible(false);
            if (parentScrollPane != null) {
                parentScrollPane.setVisible(false);
            }
        });

        // Create a top-aligned container for the close button
        StackPane topContainer = new StackPane();
        topContainer.setAlignment(Pos.TOP_RIGHT);
        topContainer.getChildren().add(closeButton);
        StackPane.setMargin(closeButton, new Insets(5, 5, 0, 0));

        // Create the main content container
        mainContainer = new VBox();
        int verticalGap = 20;
        mainContainer.setSpacing(verticalGap);
        mainContainer.setPadding(new Insets(15));
        mainContainer.setFillWidth(true);

        VBox rootContainer = new VBox();
        rootContainer.getChildren().addAll(topContainer, mainContainer);

        loadReviews();
        populateContainer();

        this.setContent(rootContainer);

        if (parentScrollPane != null) {
            parentScrollPane.setContent(this);
            parentScrollPane.setVisible(true);
            parentScrollPane.setLayoutX(200);
            parentScrollPane.setLayoutY(100);
            parentScrollPane.setPrefWidth(550);
            parentScrollPane.setPrefHeight(400);
        }
    }

    /**
     * Populates the mainContainer in the ReviewScroll with review panes.
     *
     * This method clears the existing children of the main container and iterates through the
     * list of reviews. For each review, it retrieves the name of the user associated with the
     * review by querying the database. It then creates a visual representation of the review
     * using a StackPane and adds it to the main container.
     *
     * @throws SQLException if a database access error occurs while retrieving user information.
     */
    private void populateContainer() throws SQLException {
        mainContainer.getChildren().clear();
        String nume;

        for (Review review : reviews) {
            nume = review.SELECT_USER_FROM_REVIEWS(DB_URL, DB_USER, DB_PASSWORD, review.getUser_idUser());
            StackPane pane = createReviewPane(review, nume);
            mainContainer.getChildren().add(pane);
        }
    }

    /**
     * Creates a StackPane that visually represents a review. The pane includes
     * user information, a star rating visualization, and the review text.
     *
     * @param review the Review object containing review text and rating information
     * @param usr the name or identifier of the user who wrote the review
     * @return a StackPane object displaying the review details
     */
    private StackPane createReviewPane(Review review, String usr) {
        StackPane pane = new StackPane();
        int cellWidth = 500;
        pane.setPrefWidth(cellWidth);

        VBox contentBox = new VBox(10);
        contentBox.setPadding(new Insets(15));
        contentBox.setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #ddd; -fx-border-radius: 5;");
        contentBox.setPrefWidth(cellWidth - 30);

        // Header with user info and stars
        HBox headerBox = new HBox(10);
        headerBox.setAlignment(Pos.CENTER_LEFT);

        Label userNameLabel = new Label("User: " + usr);
        userNameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");

        HBox starContainer = new HBox(5);
        starContainer.setAlignment(Pos.CENTER);
        List<Star> stars = new ArrayList<>();
        for (int i = 0; i < review.getReviewRating(); i++) {
            Star star = new Star(i + 1);
            star.setFilled(star.getRatingValue() <= review.getReviewRating());
            stars.add(star);
            starContainer.getChildren().add(star);
        }

        headerBox.getChildren().addAll(userNameLabel, starContainer);

        // Review text
        TextArea textArea = new TextArea(review.getReviewText());
        textArea.setWrapText(true);
        textArea.setEditable(false);
        textArea.setStyle("-fx-background-color: white; -fx-border-color: #ddd;");
        textArea.setPrefHeight(calculateTextHeight(review.getReviewText()));

        contentBox.getChildren().addAll(headerBox, textArea);
        pane.getChildren().add(contentBox);

        return pane;
    }

    /**
     * Calculates the height required to display a given block of text, considering
     * a fixed character width and line height. Ensures the height does not go below a
     * predefined minimum value.
     *
     * @param text the text to calculate the height for, with the assumption of a fixed
     *             number of characters per line.
     * @return the calculated height of the text block.
     */
    private double calculateTextHeight(String text) {
        int lineCount = text.length() / 50;
        double lineHeight = 18;
        return Math.max(80, (lineCount + 1) * lineHeight);
    }

    /**
     * Loads reviews from the database into the `reviews` list.
     *
     * This method initializes the `reviews` field by querying all reviews
     * related to the book identified by `idCarte` from the database. Data
     * retrieval is handled by the `DBComands` class, which performs the
     * SQL query using the database connection parameters `DB_URL`,
     * `DB_USER`, and `DB_PASSWORD`.
     */
    private void loadReviews() {
        reviews = new ArrayList<>();
        DBComands dbComands = new DBComands();
        reviews = dbComands.SELECT_ALL_FROM_REVIEWS(DB_URL, DB_USER, DB_PASSWORD, idCarte);
    }

    /**
     * Closes the ReviewScroll component and optionally its associated parent ScrollPane.
     *
     * This method sets the visibility of the ReviewScroll instance to false, effectively
     * hiding it from view. Additionally, if a parent ScrollPane is integrated with this
     * instance, its visibility is also set to false. This allows for coordinated hiding
     * of both the child and parent scroll panes, ensuring that the ReviewScroll's
     * functionality aligns with the broader application layout.
     */
    public void close() {
        this.isVisible = false;
        this.setVisible(false);
        if (parentScrollPane != null) {
            parentScrollPane.setVisible(false);
        }
    }

    /**
     * Displays the ReviewScroll component and optionally its associated parent ScrollPane.
     *
     * This method sets the visibility of the ReviewScroll instance to true, making it
     * visible in the user interface. It also ensures that the associated parent
     * ScrollPane, if one is provided, is also made visible. This functionality
     * allows for coordinated visibility control between the ReviewScroll component
     * and its parent ScrollPane, ensuring consistency in the application's layout.
     */
    public void show() {
        this.isVisible = true;
        this.setVisible(true);
        if (parentScrollPane != null) {
            parentScrollPane.setVisible(true);
        }
    }

    /**
     * Determines whether the ReviewScroll component is currently visible.
     *
     * @return true if the ReviewScroll is visible; false otherwise.
     */
    public boolean isShowing() {
        return isVisible;
    }

    /**
     * Retrieves the unique identifier of the book associated with this instance.
     *
     * @return the unique identifier of the book as an integer.
     */
    public int getBookId() {
        return this.idCarte;
    }

    /**
     * Refreshes the content of the ReviewScroll component by reloading reviews
     * from the database and repopulating the main container with the updated
     * review data.
     *
     * This method calls `loadReviews` to fetch the latest reviews for the*/
    public void refresh() throws SQLException {
        loadReviews();
        populateContainer();
    }
}