package proiect.claseUser;

import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * The myReadsScroll class is a custom JavaFX ScrollPane designed to display a grid of book covers
 * with interactive functionality for selection and actions such as deletion or click-handling.
 * This class connects to a database to fetch book cover URLs and dynamically generates
 * a grid layout containing the retrieved book covers.
 */
public class myReadsScroll extends ScrollPane {
    private final int cols = 4;
    private int rows;
    private final int cellWidth = 170;
    private final int cellHeight = 250;
    private GridPane gridPane;
    private List<String> coverUrls;
    private final int userId;
    private static final String DB_URL = "jdbc:mysql://localhost:3306/mydb";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "simone";
    private Consumer<String> onCoverClickHandler;
    private final List<String> selectedBooks = new ArrayList<>();

    /**
     * Constructs an instance of the myReadsScroll class and initializes it.
     *
     * @param userId The ID of the user for whom the myReadsScroll instance is created.
     *               This ID is used to fetch and display content specific to the user.
     */
    public myReadsScroll(int userId) {
        this.userId = userId;
        initialize();
    }

    /**
     * Deletes books from the "My Reads" database for the current user.
     *
     * This method interacts with the database by invoking a command to
     * remove the books selected by the user. The user's unique identifier
     * and the list of selected books are utilized to execute the deletion.
     */
    public void deleteBooks()
    {
        DBComands dbComands = new DBComands();
        dbComands.DELETE_FROM_MYREADS(DB_URL,DB_USER,DB_PASSWORD,userId,selectedBooks);

    }

    /**
     * Initializes the scroll pane and its internal components, setting up visual and structural configurations.
     *
     * This method configures the horizontal and vertical scroll policies for the scroll pane, enabling or
     * disabling scrollbars based on the content dimensions. It ensures that the pane fits its content to
     * the available width. A new GridPane is created and configured with a specific horizontal and vertical
     * gap between cells.
     *
     * The method also invokes the necessary steps to populate the grid with content:
     *  - Loads cover URLs for the books associated with the user.
     *  - Generates the structure and content of the grid based on the retrieved cover URLs.
     *
     * Finally, the generated GridPane is set as the content of the scroll pane.
     */
    public void initialize() {
        this.setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
        this.setVbarPolicy(ScrollBarPolicy.NEVER);
        this.setFitToWidth(true);

        gridPane = new GridPane();
        int gap = 10;
        gridPane.setHgap(gap);
        gridPane.setVgap(gap);

        loadCoverUrls();
        generateGridContent();
        this.setContent(gridPane);

    }

    /**
     * Loads the cover URLs for the books associated with the current user.
     *
     * This method interacts with the database to fetch the cover image URLs of books
     * from the "My Reads" collection that belongs to the specified user. The retrieved
     * cover URLs are stored in the corresponding field for further processing.
     *
     * The method calculates the required number of rows to display the covers in a grid
     * format based on the total number of cover URLs and the predefined number of columns.
     * If no URLs are retrieved, it ensures at least one row is created.
     */
    private void loadCoverUrls() {
        DBComands dbComands = new DBComands();
        coverUrls = dbComands.SELECT_COVER_FROM_MYREADS(DB_URL, DB_USER, DB_PASSWORD, userId);
        rows = (int) Math.ceil((double) coverUrls.size() / cols);
        if (rows == 0) rows = 1;
    }

    /**
     * Generates and populates the grid content of a GridPane with book cover data or placeholders.
     *
     * The method clears the current content of the GridPane and sets its background color to white.
     * It then iterates through the rows and columns defined by `rows` and `cols`, populating each grid cell:
     * - If there is a corresponding cover URL available in the `coverUrls` list, it generates a populated cell
     *   using the `createCell(int coverIndex)` method and places it in the appropriate position.
     * - If no cover URL is available for a grid cell, it creates and places an empty placeholder cell
     *   using the `createEmptyCell()` method.
     *
     * The method ensures that the grid content dynamically adapts to the number of available cover URLs.
     */
    private void generateGridContent() {
        gridPane.getChildren().clear();
        gridPane.setStyle("-fx-background-color: WHITE;");
        // adauga cărțile in GridPane
        int coverIndex = 0;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (coverIndex < coverUrls.size()) {
                    StackPane cell = createCell(coverIndex);
                    gridPane.add(cell, col + 1, row);
                    coverIndex++;
                } else {
                    StackPane emptyCell = createEmptyCell();
                    gridPane.add(emptyCell, col + 1, row);
                }
            }
        }
    }

    /**
     * Creates and configures a cell represented as a StackPane, populated with a background,
     * a CheckBox for selection, and event listeners for interactions.
     *
     * @param coverIndex the index of the cover URL within the coverUrls list, used to populate the cell.
     * @return a configured StackPane instance representing the cell.
     */
    private StackPane createCell(int coverIndex) {
        StackPane cell = new StackPane();
        cell.setPrefSize(cellWidth, cellHeight);

        Region background = createCellBackground(coverIndex);
        cell.getChildren().add(background);
        CheckBox checkBox = new CheckBox();
        checkBox.setStyle("-fx-background-color: transparent; -fx-opacity: 0.5;");
        StackPane.setAlignment(checkBox, Pos.BOTTOM_RIGHT);
        cell.getChildren().add(checkBox);

        checkBox.selectedProperty().addListener((observableValue, b, isNowSelected) -> {
            if (isNowSelected) {
                this.selectedBooks.add(coverUrls.get(coverIndex));


            } else {
                this.selectedBooks.remove(coverUrls.get(coverIndex));
            }
            System.out.println("Selection changed. List size: " + selectedBooks.size());
        });

        if (isValidCoverIndex(coverIndex)) {
            cell.setOnMouseClicked(e -> {
                if (onCoverClickHandler != null) {
                    onCoverClickHandler.accept(coverUrls.get(coverIndex));
                }
            });
            setupCellHoverEffects(cell);
        }

        return cell;
    }

    /**
     * Creates an empty cell represented as a StackPane with predefined dimensions and a transparent background.
     *
     * @return a StackPane instance configured as an empty cell with a transparent background and specified dimensions.
     */
    private StackPane createEmptyCell() {
        StackPane cell = new StackPane();
        cell.setPrefSize(cellWidth, cellHeight);
        cell.setStyle("-fx-background-color: transparent;");
        return cell;
    }

    /**
     * Creates and configures a background for a cell as a Region object.
     *
     * The background is set to specific dimensions and optionally styled with an image or fallback color.
     * If the provided cover index is valid, an image is loaded from the cover URLs list and applied as the background.
     * In case of an invalid index or an error while loading the image, a default style is applied.
     *
     * @param coverIndex the index of the cover URL within the coverUrls list, used to determine the background image.
     * @return a configured Region instance representing the cell background.
     */
    private Region createCellBackground(int coverIndex) {
        Region background = new Region();
        background.setPrefSize(cellWidth, cellHeight);

        if (isValidCoverIndex(coverIndex)) {
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
            } catch (Exception e) {
                System.err.println("Error loading cover image: " + e.getMessage());
                background.setStyle("-fx-background-color: #f0f0f0;");
            }
        }

        return background;
    }

    /**
     * Checks if a given index corresponds to a valid cover URL in the coverUrls list.
     *
     * A valid cover index must meet the following conditions:
     * - The index is within the bounds of the `coverUrls` list.
     * - The URL at the specified index is not null.
     * - The URL at the specified index is not an empty string.
     *
     * @param index the index to validate within the coverUrls list.
     * @return true if the index corresponds to a valid cover URL, false otherwise.
     */
    private boolean isValidCoverIndex(int index) {
        return index < coverUrls.size() && coverUrls.get(index) != null && !coverUrls.get(index).isEmpty();
    }

    /**
     * Adds mouse hover effects to a given StackPane cell.
     *
     * This method configures the provided cell to display a `DropShadow` effect
     * with a light blue color, increased spread, and radius when the mouse enters
     * the cell. The effect is removed when the mouse exits the cell.
     *
     * @param cell the StackPane instance to which hover effects will be applied
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

    /**
     * Sets the handler to be triggered when a book cover is clicked.
     *
     * This method allows registering a consumer that processes a string parameter,
     * which could represent the identifier, URL, or some attribute related to the clicked book cover.
     *
     * @param handler a {@code Consumer<String>} that will be executed when a book cover is clicked,
     *                receiving a string argument corresponding to the cover's identifier or related data.
     */
    public void setOnCoverClick(Consumer<String> handler) {
        this.onCoverClickHandler = handler;
    }

    /**
     * Refreshes the content displayed within the scroll pane and updates the grid layout.
     *
     * This method is responsible for reloading the data and regenerating the user interface
     * elements in the grid. It performs the following steps:
     *
     * 1. Invokes {@code loadCoverUrls()} to retrieve updated book cover URLs for the current user.
     * 2. Executes {@code generateGridContent()} to redraw the grid with the latest data.
     *
     * This ensures that any changes to the user's "My Reads" collection are reflected in the grid
     * without requiring a full application restart.
     */
    public void refresh() {
        loadCoverUrls();
        generateGridContent();
    }
}