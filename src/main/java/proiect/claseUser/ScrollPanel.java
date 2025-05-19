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
 * The type Scroll panel.
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
     * Instantiates a new Scroll panel.
     *
     * @param userId the user id
     */
    public ScrollPanel(int userId) {
        this.userId = userId;
        initialize();
    }
    /**
     * <li>Initializeaza scrollPanel ul din pagina de Home</li>
     * <li>E un scrollPane ce este sectionat printr un GridPane si ca elemente avem Pane-uri adica imaginile cartilor</li>
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
     * Gets cached image.
     *
     * @param url the url
     * @return the cached image
     */
// adauga in hashmap
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
     * <li>Initializeaza imagine</li>
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
     * Preload images.
     *
     * @param urls the urls
     */
    public static void preloadImages(List<String> urls) {
        for (String url : urls) {
            getCachedImage(url);
        }
    }
    /**
     * <li>Incarca imaginile intr o lista de url</li>
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
     * <li>Se creaza Panel pentru carte</li>
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
     * <li>Adauga background-ul la Panel</li>
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
     * <li>Butonul de plus  din Carte,care daca este apasat,cartea va fi adauggata in myReads</li>
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
     * <li>Incarca imaginea butonului de +</li>
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
     * <li>Verifica daca imaginea cu indexul dat se afla in lista de imagini</li>
     */
    private boolean isValidCoverIndex(int index) {
        return index < coverUrls.size() && coverUrls.get(index) != null;
    }

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
     * <li>Handler pentru apasare pe imagine</li>
     *
     * @param handler the handler
     */
    public void setOnCoverClick(Consumer<String> handler) {
        this.onCoverClickHandler = handler;
    }

    /**
     * <li>Handler pentru apasare +</li>
     *
     * @param handler the handler
     */
    public void setOnPlusButtonClick(Consumer<String> handler) {
        this.onPlusButtonClickHandler = handler;
    }

    /**
     * <li>Da refresh la homeScrollPane</li>
     */
    public void refresh()
    {
        loadCoverUrls();
        generateGridContent();

    }

}