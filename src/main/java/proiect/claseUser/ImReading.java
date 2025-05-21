package proiect.claseUser;

import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.CheckBox;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
 import java.util.function.Consumer;
import java.util.ArrayList;
import java.util.List;

/**
 * The ImReading class is responsible for managing and displaying a list of books
 * currently associated with a user. It provides functionality to load, refresh,
 * and manage interactions with books, leveraging a graphical GridPane component.
 * This class organizes books into categories based on their status and allows
 * users to interact with visual representations of book covers in a grid format.
 */
public class ImReading {
    int userId;
    List<BookInfo> books = new ArrayList<>();
    GridPane grid;
    private static final String DB_URL = "jdbc:mysql://localhost:3306/mydb";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "simone";
    private final int cellWidth = 125;
    private final int cellHeight = 130;
    private Consumer<String> onCoverClickHandler;
    private final List<Book> selectedBooks = new ArrayList<>();

    /**
     * Constructs an instance of the ImReading class with the specified user ID and GridPane.
     * Initializes the grid with the associated content and prints the selected books.
     *
     * @param userId the ID of the user associated with this instance
     * @param gridPane the graphical GridPane UI component used to display information
     */
    public ImReading(int userId, GridPane gridPane) {
        this.userId = userId;
        this.grid = gridPane;
        initialize();
        System.out.println(selectedBooks);
    }

    /**
     * Deletes the selected books of the current user from the "I'm Reading" section.
     * Utilizes database commands to remove the entries associated with the user's ID
     * and the list of selected books.
     */
    public void deleteBooks() {
        DBComands dbComands = new DBComands();
        dbComands.DELETE_FROM_IMREADING(DB_URL,DB_USER,DB_PASSWORD,userId,selectedBooks);
    }

    /**
     * Initializes the grid layout and populates it with content based on the user's reading list.
     * This method sets up grid spacing, fetches the required cover URLs from the database,
     * and generates the content to display within the grid.
     *
     * Tasks performed by this method:
     * 1. Sets horizontal and vertical gaps for the grid layout to ensure proper spacing.
     * 2. Loads the URLs of book covers for the books the user is currently reading.
     * 3. Generates and adds content to the grid by creating the appropriate layout for books.
     */
    public void initialize() {
        grid.setHgap(5);
        grid.setVgap(60);
        loadCoverUrls();
        generateGridContent();
    }

    /**
     * Generates and populates the content of the grid with books categorized by their status.
     *
     * This method is responsible for:
     * - Clearing the current contents of the grid.
     * - Classifying books into two groups based on their status: "Rezervat" and others.
     * - Adding books to the grid, grouping "Rezervat" books and others into separate rows.
     * - Adjusting row sizes in the grid according to the number of books in each group.
     *
     * Key operations performed:
     * 1. Ensures that only books with a non-null, non-empty cover URL are included.
     * 2. Divides the books into "Rezervat" and "Inventar" groups based on their status.
     * 3. Invokes helper methods to populate the grid with the categorized books.
     *    - Uses {@link #addBooksToRow(List, int)} to add books to specific grid rows.
     *    - Calls {@link #adjustRowSizes(int, int)} to modify grid row constraints based on the book count.
     * 4. Outputs debug information for the count of books in each category.
     */
        private void generateGridContent() {
            grid.getChildren().clear();

            // Separă cărțile pe status și verifică URL-urile
            List<BookInfo> inventarBooks = new ArrayList<>();
            List<BookInfo> rezervatBooks = new ArrayList<>();

            for (BookInfo b : books) {
                if (b.getBook() != null && b.getBook().getCoverUrl() != null && !b.getBook().getCoverUrl().isEmpty()) {
                    if ("Rezervat".equalsIgnoreCase(b.getStatus())) {
                        rezervatBooks.add(b);
                    } else {
                        inventarBooks.add(b);
                    }
                }
            }

            // Debug output
            System.out.println("Inventar books: " + inventarBooks.size());
            System.out.println("Rezervat books: " + rezervatBooks.size());

            // Adaugă cărțile în grid
            addBooksToRow(inventarBooks, 1); // Primul rând
            addBooksToRow(rezervatBooks, 0); // Al doilea rând

            // Ajustează dimensiunile rândurilor
            adjustRowSizes(inventarBooks.size(), rezervatBooks.size());
        }

    /**
     * Adds a list of books to a specific row in the grid. Each book is represented
     * visually by a cell containing the book's cover image and a checkbox for selection.
     *
     * The method ensures that no more than six books are added to a row.
     * Clicking on a cell invokes the cover click handler, if set.
     * Selecting or deselecting the checkbox updates the list of selected books.
     *
     * @param books the list of {@link BookInfo} objects to be added to the row
     * @param row the index of the row in the grid where the books will be added
     */
        private void addBooksToRow(List<BookInfo> books, int row) {
            for (int col = 0; col < books.size(); col++) {
                if (col >= 6) break; // Limitează la 6 coloane

                BookInfo book = books.get(col);
                StackPane cell = createCell(book.getBook().getCoverUrl());
                CheckBox checkBox = new CheckBox();
               // checkBox.setGraphic(cell);


                checkBox.setStyle("-fx-background-color: transparent; -fx-opacity: 0.5;");
                StackPane.setAlignment(checkBox, Pos.BOTTOM_RIGHT);
                cell.setOnMouseClicked(e -> {
                    if (onCoverClickHandler != null) {
                        onCoverClickHandler.accept(book.getBook().getCoverUrl());
                    }

                });
                cell.getChildren().add(checkBox);
                checkBox.selectedProperty().addListener((o, b, isNowSelected) -> {
                    if (isNowSelected) {
                        selectedBooks.add(book.getBook());
                    } else {
                        selectedBooks.remove(book.getBook());
                    }
                });


                grid.add(cell, col, row);
            }
        }

    /**
     * Adjusts the row sizes of the grid based on the count of "Inventar" and "Rezervat" books.
     * This method calculates the required number of rows, clears existing row constraints,
     * and updates the grid's layout to accommodate the books in their respective categories.
     *
     * @param inventarCount the number of books categorized under "Inventar"
     * @param rezervatCount the number of books categorized under "Rezervat"
     */
        private void adjustRowSizes(int inventarCount, int rezervatCount) {
            // Calculează numărul de rânduri necesare
            int rowsNeeded = (inventarCount > 0 && rezervatCount > 0) ? 2 : 1;

            // Actualizează constrângerile rândurilor
            grid.getRowConstraints().clear();

            for (int i = 0; i < rowsNeeded; i++) {
                RowConstraints rc = new RowConstraints();
                rc.setVgrow(Priority.ALWAYS);
                rc.setValignment(VPos.CENTER);
                rc.setPrefHeight(200);
                grid.getRowConstraints().add(rc);
            }

            // Ajustează dimensiunea GridPane-ului
            grid.setPrefHeight(rowsNeeded * 250); // 200 pentru conținut + 50 pentru padding
        }

    /**
     * Creates a cell represented as a StackPane, containing a background region
     * initialized with the cover image specified by the provided URL. The cell is
     * configured to handle mouse click events and hover effects.
     *
     * @param URL the URL of the cover image used to style the cell's background
     * @return a StackPane representing the cell, with configured background, click handler,
     *         and hover effects
     */
    private StackPane createCell(String URL) {
        StackPane cell = new StackPane();
        cell.setPrefSize(cellWidth, cellHeight);

        Region background = createCellBackground(URL);
        cell.getChildren().add(background);


            cell.setOnMouseClicked(e -> {
                if (onCoverClickHandler != null) {
                    onCoverClickHandler.accept(URL);
                }
            });
            setupCellHoverEffects(cell);


        return cell;
    }

    /**
     * Sets a handler that will be invoked when a book cover is clicked.
     * The provided handler accepts a string parameter, which typically
     * represents the URL or identifier of the clicked cover.
     *
     * @param handler a Consumer function that processes a string argument corresponding
     *                to the clicked cover's identifier or URL
     */
    public void setOnCoverClick(Consumer<String> handler) {
        this.onCoverClickHandler = handler;
    }

    /**
     * Refreshes the state of the grid and its content by reloading the cover URLs
     * and regenerating the grid's structure and data. This method ensures that
     * the latest updates from the database and user interactions are reflected in
     * the grid view.
     *
     * Key operations performed:
     * 1. Invokes {@code loadCoverUrls()} to load the appropriate book cover URLs
     *    from the database, ensuring up-to-date images and metadata.
     * 2. Calls {@code generateGridContent()} to rebuild and populate the grid
     *    with books categorized and displayed in the appropriate format.
     */
    public void refresh() {
        loadCoverUrls();
        generateGridContent();
    }

    /**
     * Loads the URLs of book covers for the books associated with the current user's reading list.
     *
     * This method retrieves book information from the database using specific credentials and the user's ID.
     * The resulting data is assigned to the `books` field for further operations, such as populating the grid
     * with corresponding book covers.
     *
     * Key operations:
     * - Establishes a database connection using predefined URL, user credentials, and user ID.
     * - Fetches book data currently marked as in use for the specified user.
     * - Updates the `books` field with the retrieved list of books and their associated details.
     */
    private void loadCoverUrls() {
        DBComands dbComands = new DBComands();
        books = dbComands.IS_IN_USE(DB_URL, DB_USER, DB_PASSWORD, userId);
    }

    /**
     * Creates a background region styled with an image specified by the provided URL.
     * If the URL is invalid or the image loading fails, the background defaults to a
     * light gray color.
     *
     * @param URL the URL of the image used as the background
     * @return a Region styled with the specified background image or a default style if the image is unavailable
     */
    private Region createCellBackground(String URL) {
    Region background = new Region();
    background.setPrefSize(cellWidth, cellHeight);


        try {
            Image coverImage = new Image(URL);
            BackgroundImage bgImage = new BackgroundImage(
                    coverImage,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.CENTER,
                    new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true)
            );
            background.setBackground(new Background(bgImage));
        } catch (Exception e) {
            System.err.println("Error loading cover image: " + e.getMessage());
            background.setStyle("-fx-background-color: #f0f0f0;");
        }


    return background;
}

    /**
     * Configures hover effects for a given StackPane cell. When the mouse enters the cell,
     * a light blue glow effect is applied. The glow effect is removed when the mouse exits the cell.
     *
     * @param cell the StackPane to which the hover effects should be applied
     */
    private void setupCellHoverEffects(StackPane cell) {
        cell.setOnMouseEntered(e -> {
            DropShadow glow = new DropShadow();
            glow.setColor(Color.LIGHTBLUE);
            glow.setSpread(0.7);
            glow.setRadius(15);
            cell.setEffect(glow);
        });

        cell.setOnMouseExited(e -> cell.setEffect(null));
    }

}
