package proiect.claseUser;

import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import java.util.List;
import java.util.function.Consumer;

/**
 * Represents a custom scroll pane for displaying a list of books as a scrollable grid.
 * The grid dynamically populates with book information and provides interaction features.
 */
public class SearchResultsScroll extends ScrollPane {
    private GridPane gridPane;
    private final List<Book> books;
    private Consumer<String> onCoverClickHandler;

    /**
     * Constructs a SearchResultsScroll instance with a list of books.
     * The scroll pane displays the books in a grid layout, allowing users to scroll through
     * the provided list of books.
     *
     * @param books the list of Book objects to be displayed in the scrollable grid.
     */
    public SearchResultsScroll(List<Book> books) {
        this.books = books;
        initialize();
    }

    /**
     * Configures and initializes the scroll pane and its associated grid layout.
     * Sets the horizontal and vertical scrollbar policies, enables fitting the content
     * to the width of the scroll pane, and creates a styled grid for displaying content.
     * Populates the grid with the necessary content by invoking the content generation method.
     */
    private void initialize() {
        this.setHbarPolicy(ScrollBarPolicy.NEVER);
        this.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
        this.setFitToWidth(true);

        gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setStyle("-fx-padding: 10px;");

        generateGridContent();
        this.setContent(gridPane);
    }

    /**
     * Populates the grid pane with content based on the list of books.
     *
     * This method clears any existing content from the grid pane and dynamically
     * populates it with panes representing individual books. Each book pane includes
     * hover effects for visual feedback and a click handler to perform an action when
     * the pane is clicked. If the list of books is empty or null, a "Not found!" message
     * is displayed instead.
     *
     * The method includes:
     * - Clearing the current children of the grid pane.
     * - Adding styled panes for each book in the list with hover and click behaviors.
     * - Handling click events for book panes by invoking a consumer function
     *   with the cover URL of the clicked book.
     * - Displaying a placeholder message when the book list is empty.
     */
    private void generateGridContent() {
        gridPane.getChildren().clear();

        if (books != null && !books.isEmpty()) {
            for (int i = 0; i < books.size(); i++) {
                PaneCarte paneCarte = new PaneCarte(books.get(i));
                paneCarte.setPrefWidth(600);

                // Adaugă efecte la hover
                paneCarte.setOnMouseEntered(e -> paneCarte.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #bbb;"));
                paneCarte.setOnMouseExited(e -> paneCarte.setStyle("-fx-background-color: #f9f9f9; -fx-border-color: #ddd;"));

                // Handler pentru click
                int finalI = i;
                paneCarte.setOnMouseClicked(e -> {
                    if (onCoverClickHandler != null) {
                        onCoverClickHandler.accept(books.get(finalI).getCoverUrl());
                    }
                });

                gridPane.add(paneCarte, 0, i);
            }
        } else {
            Label noResults = new Label("Not found!");
            noResults.setStyle("-fx-font-size: 16px; -fx-text-fill: #666;");
            gridPane.add(noResults, 0, 0);
        }
    }

    /**
     * Sets the handler to be executed when a book cover is clicked.
     *
     * @param handler a Consumer that takes a String parameter representing the
     *                URL of the clicked book's cover image.
     */
    public void setOnCoverClick(Consumer<String> handler) {
        this.onCoverClickHandler = handler;
    }


}