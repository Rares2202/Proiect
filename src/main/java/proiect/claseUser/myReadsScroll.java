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
    public myReadsScroll(int userId) {
        this.userId = userId;
        initialize();
    }
    public void deleteBooks()
    {
        DBComands dbComands = new DBComands();
        dbComands.DELETE_FROM_MYREADS(DB_URL,DB_USER,DB_PASSWORD,userId,selectedBooks);

    }
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

    private void loadCoverUrls() {
        DBComands dbComands = new DBComands();
        coverUrls = dbComands.SELECT_COVER_FROM_MYREADS(DB_URL, DB_USER, DB_PASSWORD, userId);
        rows = (int) Math.ceil((double) coverUrls.size() / cols);
        if (rows == 0) rows = 1;
    }

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

    private StackPane createEmptyCell() {
        StackPane cell = new StackPane();
        cell.setPrefSize(cellWidth, cellHeight);
        cell.setStyle("-fx-background-color: transparent;");
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
                System.err.println("Error loading cover image: " + e.getMessage());
                background.setStyle("-fx-background-color: #f0f0f0;");
            }
        }

        return background;
    }

    private boolean isValidCoverIndex(int index) {
        return index < coverUrls.size() && coverUrls.get(index) != null && !coverUrls.get(index).isEmpty();
    }

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

    public void setOnCoverClick(Consumer<String> handler) {
        this.onCoverClickHandler = handler;
    }

    public void refresh() {
        loadCoverUrls();
        generateGridContent();
    }
}