package proiect.claseUser;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

/**
 * PaneCarte is a custom UI component that displays information about a book
 * in a visually structured horizontal layout. It extends the HBox class from
 * JavaFX and includes the book's cover image, title, author, and genre.
 *
 * The layout consists of two main sections:
 * - An ImageView for displaying the book's cover image.
 * - A VBox containing Labels for displaying textual information
 *   about the book such as its title, author, and genre.
 */
public class PaneCarte extends HBox { // Folosim HBox pentru layout orizontal
    private final Book book;
    private final ImageView coverImageView;
    private final VBox textContainer;

    /**
     * Constructs a PaneCarte instance, setting up UI elements such as an image view for the book cover
     * and a text container to display book details. Initializes the layout and loads the book data.
     *
     * @param book the Book instance containing data such as title, author, genre, description, and cover URL.
     *             This data is used to populate the UI elements of the PaneCarte.
     */
    public PaneCarte(Book book) {
        this.book = book;
        this.coverImageView = new ImageView();
        this.textContainer = new VBox();

        initialize();
    }

    /**
     * Initializes the layout and content of the PaneCarte instance.
     * Sets the alignment, spacing, padding, style, and preferred height of the layout container.
     * Configures and adds the child components, including the cover image and text container.
     * Loads the book data into the respective UI elements.
     * Calls helper methods to configure specific components: setupCoverImage, setupTextContainer, and loadBook.
     */
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

    /**
     * Configures the visual properties of the cover image view by setting its dimensions,
     * aspect ratio preservation, and background style. This setup ensures the cover image
     * is displayed with consistent sizing and appearance in the UI.
     */
    private void setupCoverImage() {
        coverImageView.setFitWidth(120);
        coverImageView.setFitHeight(180);
        coverImageView.setPreserveRatio(false);
        coverImageView.setStyle("-fx-background-color: #eee;");
    }

    /**
     * Configures the text container in the PaneCarte instance by setting its layout properties,
     * such as spacing and alignment, and adding labels for displaying book details.
     *
     * This method creates and styles three labels:
     * - A title label with a bold font and larger size for emphasizing the book's title.
     * - An author label with a smaller font for displaying the author's name.
     * - A genre label with a smaller font and muted text color for displaying the book's genre.
     *
     * The labels are then added to the text container, which organizes and displays the book's details.
     */
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

    /**
     * Loads the details of a Book object and populates the UI components of PaneCarte with
     * the respective book information such as title, author, genre, and cover image.
     *
     * This method performs the following actions:
     * - If the title of the book is null, it sets the title label to "Titlu necunoscut".
     * - If the author's name is null, it sets the author label to "Autor necunoscut".
     * - If the genre of the book is null, it sets the genre label to "Gen necunoscut".
     * - If a valid cover URL is available, it attempts to load the cover as an image to
     *   the coverImageView. If the loading fails, it applies a default background style.
     *
     * It updates the corresponding labels in the text container of the PaneCarte
     * and loads the cover image view appropriately.
     *
     * Handles exceptions during the cover image loading process by displaying an error
     * message to the console and applying a default style for the cover image view.
     */
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