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

    public ImReading(int userId, GridPane gridPane) {
        this.userId = userId;
        this.grid = gridPane;
        initialize();
        System.out.println(selectedBooks);
    }

    public void deleteBooks() {
        DBComands dbComands = new DBComands();
        dbComands.DELETE_FROM_IMREADING(DB_URL,DB_USER,DB_PASSWORD,userId,selectedBooks);
    }

    public void initialize() {
        grid.setHgap(5);
        grid.setVgap(60);
        loadCoverUrls();
        generateGridContent();
    }


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

    public void setOnCoverClick(Consumer<String> handler) {
        this.onCoverClickHandler = handler;
    }

    public void refresh() {
        loadCoverUrls();
        generateGridContent();
    }
    private void loadCoverUrls() {
        DBComands dbComands = new DBComands();
        books = dbComands.IS_IN_USE(DB_URL, DB_USER, DB_PASSWORD, userId);
    }
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
