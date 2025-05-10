package proiect.clase;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

public class PaneCarte extends HBox { // Folosim HBox pentru layout orizontal
    private final Book book;
    private final ImageView coverImageView;
    private final VBox textContainer;

    public PaneCarte(Book book) {
        this.book = book;
        this.coverImageView = new ImageView();
        this.textContainer = new VBox();

        initialize();
    }

    private void initialize() {
        // Configurare layout principal
        this.setAlignment(Pos.CENTER_LEFT);
        this.setSpacing(20);
        this.setPadding(new Insets(15));
        this.setStyle("-fx-background-color: #f9f9f9; -fx-border-color: #ddd; -fx-border-width: 1px;");
        this.setPrefHeight(180); // Înălțime fixă pentru fiecare element

        // Configurează ImageView pentru copertă
        setupCoverImage();

        // Configurează containerul pentru text
        setupTextContainer();

        this.getChildren().addAll(coverImageView, textContainer);
        loadBook();
    }

    private void setupCoverImage() {
        coverImageView.setFitWidth(120);
        coverImageView.setFitHeight(180);
        coverImageView.setPreserveRatio(false);
        coverImageView.setStyle("-fx-background-color: #eee;");
    }

    private void setupTextContainer() {
        textContainer.setSpacing(8);
        textContainer.setAlignment(Pos.CENTER_LEFT);

        Label titleLabel = new Label();
        titleLabel.setFont(new Font(18));
        titleLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #333;");

        Label authorLabel = new Label();
        authorLabel.setFont(new Font(14));

        Label genreLabel = new Label();
        genreLabel.setFont(new Font(12));
        genreLabel.setStyle("-fx-text-fill: #666;");

        textContainer.getChildren().addAll(titleLabel, authorLabel, genreLabel);
    }

    private void loadBook() {
        if (book != null) {
            // Setează textul
            ((Label)textContainer.getChildren().get(0)).setText(
                    book.getTitle() != null ? book.getTitle() : "Titlu necunoscut");
            ((Label)textContainer.getChildren().get(1)).setText(
                    "de " + (book.getAuthor() != null ? book.getAuthor() : "Autor necunoscut"));
            ((Label)textContainer.getChildren().get(2)).setText(
                    book.getGenre() != null ? book.getGenre() : "Gen necunoscut");

            // Încarcă coperta
            if (book.getCoverUrl() != null && !book.getCoverUrl().isEmpty()) {
                try {
                    Image image = new Image(book.getCoverUrl(), 120, 180, false, true);
                    coverImageView.setImage(image);
                } catch (Exception e) {
                    System.err.println("Error loading cover: " + e.getMessage());
                    coverImageView.setStyle("-fx-background-color: #e0e0e0;");
                }
            }
        }
    }
}