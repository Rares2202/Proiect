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
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ScrollPanel extends ScrollPane {
    private final int cols = 4;
    private final int rows = 15;
    private final int cellWidth = 170;
    private final int cellHeight = 250;
    private final int gap = 10;

    private GridPane gridPane;
    private List<String> coverUrls;
    private int userId;
    private static final String DB_URL = "jdbc:mysql://localhost:3306/mydb";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "simone";

    private Consumer<String> onCoverClickHandler;
    private Consumer<String> onPlusButtonClickHandler;

    public ScrollPanel(int userId) {
        this.userId = userId;

        initialize();
    }

    private void initialize() {
        // Configure scroll pane properties
        this.setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
        this.setVbarPolicy(ScrollBarPolicy.NEVER);
        this.setFitToWidth(true);
        gridPane = new GridPane();
        gridPane.setHgap(gap);
        gridPane.setVgap(gap);
        loadCoverUrls();
        generateGridContent();
        this.setContent(gridPane);
    }

    private void loadCoverUrls() {
        DBComands dbComands = new DBComands();
        String query = "SELECT coverCarte FROM carte WHERE coverCarte IS NOT NULL";
        List<Book> books = dbComands.SELECT_ALL_FROM_BOOKS(query, DB_URL, DB_USER, DB_PASSWORD);

        this.coverUrls = new ArrayList<>();
        for (Book book : books) {
            if (book.getCoverUrl() != null) {
                coverUrls.add(book.getCoverUrl());
            }
        }
    }

    private void generateGridContent() {
        Image plusImage = loadPlusIcon();
//        Pane leftSpacer = new Pane();
//        leftSpacer.setPrefWidth(30);
//        GridPane.setRowSpan(leftSpacer, rows);
//        gridPane.add(leftSpacer, 0, 0);
        int coverIndex = 0;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                StackPane cell = createCell(plusImage, coverIndex);
                gridPane.add(cell, col + 1, row);

                if (coverIndex < coverUrls.size()) {
                    coverIndex++;
                }
            }
        }
    }

    private StackPane createCell(Image plusImage, int coverIndex) {
        StackPane cell = new StackPane();
        cell.setPrefSize(cellWidth, cellHeight);
        Region background = createCellBackground(coverIndex);
        Button saveButton = createSaveButton(plusImage, coverIndex);
        cell.getChildren().addAll(background, saveButton);
        if (isValidCoverIndex(coverIndex)) {
            cell.setOnMouseClicked(event -> {
                if (onCoverClickHandler != null) {
                    onCoverClickHandler.accept(coverUrls.get(coverIndex));
                }
            });

            setupCellHoverEffects(cell);
        }

        return cell;
    }

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
                background.setStyle(createRandomColorStyle());
            }
        } else {
            background.setStyle(createRandomColorStyle());
        }

        return background;
    }

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
            saveButton.setOnAction(event -> {
                if (onPlusButtonClickHandler != null) {
                    onPlusButtonClickHandler.accept(coverUrls.get(coverIndex));
                }
            });
        }

        return saveButton;
    }

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

    private String createRandomColorStyle() {
        return String.format(
                "-fx-background-color: rgb(%d, %d, %d);",
                (int)(Math.random() * 255),
                (int)(Math.random() * 255),
                (int)(Math.random() * 255)
        );
    }
    public void setOnCoverClick(Consumer<String> handler) {
        this.onCoverClickHandler = handler;
    }

    public void setOnPlusButtonClick(Consumer<String> handler) {
        this.onPlusButtonClickHandler = handler;
    }
}