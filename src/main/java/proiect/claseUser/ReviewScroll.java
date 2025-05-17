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

public class ReviewScroll extends ScrollPane {
    private List<Review> reviews;
    private static final String DB_URL = "jdbc:mysql://localhost:3306/mydb";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "simone";
    private final int idCarte;
    private VBox mainContainer;
    private final ScrollPane parentScrollPane;
    private boolean isVisible = true;

    public ReviewScroll(int idCarte, ScrollPane parentScrollPane) throws SQLException {
        this.idCarte = idCarte;
        this.parentScrollPane = parentScrollPane;
        initialize();
    }

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

    private void populateContainer() throws SQLException {
        mainContainer.getChildren().clear();
        String nume;

        for (Review review : reviews) {
            nume = review.SELECT_USER_FROM_REVIEWS(DB_URL, DB_USER, DB_PASSWORD, review.getUser_idUser());
            StackPane pane = createReviewPane(review, nume);
            mainContainer.getChildren().add(pane);
        }
    }

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

    private double calculateTextHeight(String text) {
        int lineCount = text.length() / 50;
        double lineHeight = 18;
        return Math.max(80, (lineCount + 1) * lineHeight);
    }

    private void loadReviews() {
        reviews = new ArrayList<>();
        DBComands dbComands = new DBComands();
        reviews = dbComands.SELECT_ALL_FROM_REVIEWS(DB_URL, DB_USER, DB_PASSWORD, idCarte);
    }


    public void close() {
        this.isVisible = false;
        this.setVisible(false);
        if (parentScrollPane != null) {
            parentScrollPane.setVisible(false);
        }
    }

    public void show() {
        this.isVisible = true;
        this.setVisible(true);
        if (parentScrollPane != null) {
            parentScrollPane.setVisible(true);
        }
    }

    public boolean isShowing() {
        return isVisible;
    }

    public int getBookId() {
        return this.idCarte;
    }
    public void refresh() throws SQLException {
        loadReviews();
        populateContainer();
    }
}