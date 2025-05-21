package proiect.claseUser;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import java.net.URL;
import java.util.*;
import java.util.function.Consumer;

/**
 * Represents a custom scrollable panel designed for displaying a grid of book covers.
 * The panel uses a {@code GridPane} layout, where each cell represents a book cover.
 * It allows interaction with book covers and supports caching of images.
 */
public class ScrollPanel extends ScrollPane {
    private final int cellWidth = 170;
    private final int cellHeight = 250;
    private final int gap = 10;

    private GridPane gridPane;
    private List<String> coverUrls;
    private final int userId;
    private static final String DB_URL = "jdbc:mysql://localhost:3306/mydb";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root";
    private static final Image DEFAULT_COVER_IMAGE = createDefaultCoverImage();
    // hashmap care pastreaza imaginile
    private static final Map<String, Image> imageCache = new HashMap<>();

    private Consumer<String> onCoverClickHandler;
    private Consumer<String> onPlusButtonClickHandler;

    /**
     * Constructs a ScrollPanel instance and initializes the scroll panel for the home page.
     * The panel is designed to display content organized in a grid layout.
     *
     * @param userId the identifier of the user for whom the ScrollPanel is being created
     */
    public ScrollPanel(int userId) {
        this.userId = userId;
        initialize();
    }

    /**
     * Initializes the ScrollPanel by setting up its scroll policies, dimensions, and content.
     *
     * The method configures the horizontal and vertical scroll bars to never display, ensures the content
     * fits the panel's width, and calculates the preferred viewport height based on a set number of visible
     * rows. A {@code GridPane} layout is created and configured with gap spacing for grid items. It invokes
     * methods to load cover image URLs into a list and populate the grid with content based on those URLs.
     * The grid is then set as the content of the ScrollPanel.
     */
    private void initialize() {
        this.setHbarPolicy(ScrollBarPolicy.NEVER);
        this.setVbarPolicy(ScrollBarPolicy.NEVER);
        this.setFitToWidth(true);
        int maxVisibleRows = 3;
        this.setPrefViewportHeight(maxVisibleRows * (cellHeight + gap) + gap);

        gridPane = new GridPane();
        gridPane.setHgap(gap);
        gridPane.setVgap(gap);
        loadCoverUrls();
        generateGridContent();
        this.setContent(gridPane);
    }

    /**
     * Retrieves an image from the cache if available, otherwise loads and caches it.
     * If the URL is null or loading fails, a default placeholder image is returned.
     *
     * @param url the URL of the image to retrieve or load; may be null
     * @return the cached image, newly loaded image, or a default image if the URL is null or loading fails
     */
    public static Image getCachedImage(String url) {
        if (url == null) {
            return DEFAULT_COVER_IMAGE;
        }

        if (imageCache.containsKey(url)) {
            return imageCache.get(url);
        }

        try {
            Image image = new Image(url, true);
            imageCache.put(url, image);
            return image;
        } catch (Exception e) {
            System.err.println("Error loading image: " + url);
            return DEFAULT_COVER_IMAGE;
        }
    }

    /**
     * Creates a default cover image by loading it from the application's resources.
     * The image is expected to be located at "/proiect/css/white.jpg". If the image
     * cannot be found or an error occurs during the loading process, the method
     * returns null and logs an appropriate error message to the console.
     *
     * @return the default cover image if successfully loaded, or null if the image is
     *         not found or an error occurs.
     */
    private static Image createDefaultCoverImage() {
        try {
            URL imageUrl = ScrollPanel.class.getResource("/proiect/css/white.jpg");
            if (imageUrl != null) {
                return new Image(imageUrl.toExternalForm());
            } else {
                System.err.println("Default cover image not found in resources.");
            }
        } catch (Exception e) {
            System.err.println("Error loading default cover image: " + e.getMessage());
        }

        return null;
    }

    /**
     * Preloads a list of images by retrieving them from cache or loading them if not cached.
     * This method is useful for ensuring that all specified images are ready for display.
     *
     * @param urls a list of image URLs to be preloaded; each URL is processed to retrieve or load the corresponding image
     */
    public static void preloadImages(List<String> urls) {
        for (String url : urls) {
            getCachedImage(url);
        }
    }

    /**
     * Loads cover image URLs for books associated with a specific user into the `coverUrls` list.
     *
     * The method retrieves all books tied to the specified user by querying the database via
     * {@code DBComands.SELECT_ALL_FROM_USERPREF}. For each book, if a valid cover URL is available,
     * it is added to the `coverUrls` list. Additionally, a separate thread is started to preload
     * the images from the retrieved URLs for optimized display in the scroll panel.
     *
     * This method is internally used to populate the image data for the panel and ensure all
     * relevant images are preloaded.
     */
    private void loadCoverUrls() {
        DBComands dbComands = new DBComands();
        List<Book> books = dbComands.SELECT_ALL_FROM_USERPREF(DB_URL, DB_USER, DB_PASSWORD, userId);

        this.coverUrls = new ArrayList<>();
        for (Book book : books) {
            if (book.getCoverUrl() != null) {
                coverUrls.add(book.getCoverUrl());
            }
        }

        // se incarca imaginile in hashmap si apoi din hashmap se afiseaza in scrollPanel
        new Thread(() -> preloadImages(coverUrls)).start();
    }
    /**
     * <li>Se incarca fiecare Pane in GridPane</li>
     */
    private void generateGridContent() {
        gridPane.getChildren().clear();
        Image plusImage = loadPlusIcon();
        int coverIndex = 0;
        int totalBooks = coverUrls.size();
        int cols = 4;
        int requiredRows = (int) Math.ceil((double) totalBooks / cols);

        gridPane.setPrefWidth(cols * (cellWidth + gap) + gap);
        gridPane.setPrefHeight(requiredRows * (cellHeight + gap) + gap);

        for (int row = 0; row < requiredRows; row++) {
            for (int col = 0; col < cols; col++) {
                if (coverIndex < totalBooks) {
                    StackPane cell = createCell(plusImage, coverIndex);
                    gridPane.add(cell, col, row);
                    coverIndex++;
                }
            }
        }
    }

    /**
     * Creates a {@code StackPane} that represents a cell in the grid layout.
     * This cell contains a background region and a save button. If the specified
     * cover index is valid, the cell supports additional interactions, such as
     * mouse click and hover effects.
     *
     * @param plusImage   the image to use for the save button's "+" icon; if null, a text-based "+" button is used instead
     * @param coverIndex  the index of the cover image in the {@code coverUrls} list; determines the background and functionality
     * @return a {@code StackPane} containing the specified background and save button with optional interactive behaviors
     */
    private StackPane createCell(Image plusImage, int coverIndex) {
        StackPane cell = new StackPane();
        cell.setPrefSize(cellWidth, cellHeight);
        Region background = createCellBackground(coverIndex);
        Button saveButton = createSaveButton(plusImage, coverIndex);
        cell.getChildren().addAll(background, saveButton);

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
     * Creates a background region for a cell in the grid layout. The background can include
     * a cover image if the specified index is valid. If the index is invalid, a default
     * blank region is returned.
     *
     * @param coverIndex the index of the cover image in the {@code coverUrls} list;
     *                   determines the background image of the region
     * @return a {@code Region} representing the cell background, optionally with a cover image
     */
    private Region createCellBackground(int coverIndex) {
        Region background = new Region();
        background.setPrefSize(cellWidth, cellHeight);

        if (isValidCoverIndex(coverIndex)) {
            Image coverImage = getCachedImage(coverUrls.get(coverIndex));
            BackgroundImage bgImage = new BackgroundImage(
                    coverImage,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.CENTER,
                    new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true)
            );
            background.setBackground(new Background(bgImage));
        }

        return background;
    }

    /**
     * Creates a button, styled and optionally populated with an image or text, to act as a save button in a grid cell.
     * The button can include a "+" icon, or display a "+" text if no image is provided. When clicked,
     * it triggers a handler that processes a cover URL based on the given cover index, if valid.
     *
     * @param plusImage   the image to use for the save button's "+" icon; if null, a text-based "+" button is used instead
     * @param coverIndex  the index of the cover image in the coverUrls list; determines the save button's functionality and associated cover URL
     * @return a Button configured with the specified styles, icon, and click action
     */
    private Button createSaveButton(Image plusImage, int coverIndex) {
        Button saveButton = new Button();

        if (plusImage != null) {
            ImageView plusIcon = new ImageView(plusImage);
            plusIcon.setFitWidth(25);
            plusIcon.setFitHeight(25);
            saveButton.setGraphic(plusIcon);
        } else {
            saveButton.setText("+");
        }

        saveButton.setStyle("-fx-background-color: transparent; -fx-padding: 5;");
        StackPane.setAlignment(saveButton, Pos.BOTTOM_RIGHT);
        saveButton.setTranslateX(-5);
        saveButton.setTranslateY(-5);

        if (isValidCoverIndex(coverIndex)) {
            saveButton.setOnAction(e -> {
                if (onPlusButtonClickHandler != null) {
                    onPlusButtonClickHandler.accept(coverUrls.get(coverIndex));
                }
            });
        }

        return saveButton;
    }

    /**
     * Loads the "+" icon image from the application's resources.
     * This image is used for UI elements that require a plus icon, such as buttons.
     * If the image cannot be found or an error occurs during the loading process,
     * the method logs an error and returns null.
     *
     * @return the loaded "+" icon image if successfully retrieved, or null if the image cannot be found or an error occurs
     */
    private Image loadPlusIcon() {
        try {
            URL imageUrl = getClass().getResource("/proiect/css/plus.png");
            if (imageUrl != null) {
                return new Image(imageUrl.toExternalForm());
            }
        } catch (Exception e) {
            System.err.println("Error loading plus icon: " + e.getMessage());
        }
        return null;
    }

    /**
     * Checks if the provided index is a valid cover index within the {@code coverUrls} list.
     * A valid index must be less than the size of the list and must reference a non-null entry.
     *
     * @param index the index to validate in the {@code coverUrls} list
     * @return {@code true} if the index is valid and references a non-null entry, {@code false} otherwise
     */
    private boolean isValidCoverIndex(int index) {

        return index < coverUrls.size() && coverUrls.get(index) != null;
    }

    /**
     * Configures hover effects for a given cell in the grid layout.
     * When the cell is hovered over, a glowing effect is applied, which is removed upon exiting the hover.
     *
     * @param cell the {@code StackPane} representing the grid cell for which hover effects will be applied
     */
    private void setupCellHoverEffects(StackPane cell) {
        cell.setOnMouseEntered(e -> {
            DropShadow glow = new DropShadow();
            glow.setColor(Color.LIGHTBLUE); // Setează culoarea dorită
            glow.setSpread(0.9);           // Intensitatea glow-ului (0-1)
            glow.setRadius(15);            // Mărimea glow-ului
            cell.setEffect(glow);
        });

        cell.setOnMouseExited(e -> cell.setEffect(null));
    }

    /**
     * Sets a handler to be executed when a cover is clicked.
     * The handler processes the cover's URL, which is passed as a {@code String} argument.
     *
     * @param handler the {@code Consumer<String>} to handle cover click events.
     *                This consumer receives the clicked cover's URL as its parameter.
     */
    public void setOnCoverClick(Consumer<String> handler) {

        this.onCoverClickHandler = handler;
    }

    /**
     * Sets a handler to be executed when the "+" button is clicked.
     * The handler processes a string parameter, which may represent
     * an identifier or relevant data associated with the click action.
     *
     * @param handler the {@code Consumer<String>} to handle the "+" button click event.
     *                This consumer receives a string as its parameter, which can be
     *                used to perform specific actions based on the click context.
     */
    public void setOnPlusButtonClick(Consumer<String> handler) {

        this.onPlusButtonClickHandler = handler;
    }

    /**
     * Refreshes the content of the ScrollPanel by reloading cover image URLs and regenerating
     * the grid layout with updated data.
     *
     * This method first updates the list of cover URLs by invoking {@code loadCoverUrls()}.
     * Once the URLs have been reloaded, it uses the updated data to regenerate the
     * grid content via the {@code generateGridContent()} method. It is typically used
     * to ensure that the displayed data reflects any recent changes or updates.
     */
    public void refresh()
    {
        loadCoverUrls();
        generateGridContent();

    }

}