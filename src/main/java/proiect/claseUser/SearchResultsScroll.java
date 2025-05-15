package proiect.claseUser;

import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import java.util.List;
import java.util.function.Consumer;

public class SearchResultsScroll extends ScrollPane {
    private GridPane gridPane;
    private List<Book> books;
    private Consumer<String> onCoverClickHandler;

    public SearchResultsScroll(List<Book> books) {
        this.books = books;
        initialize();
    }

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

    private void generateGridContent() {
        gridPane.getChildren().clear();

        if (books != null && !books.isEmpty()) {
            for (int i = 0; i < books.size(); i++) {
                PaneCarte paneCarte = new PaneCarte(books.get(i));
                paneCarte.setPrefWidth(600);

                // Adaugă efecte la hover
                paneCarte.setOnMouseEntered(e -> {
                    paneCarte.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #bbb;");
                });
                paneCarte.setOnMouseExited(e -> {
                    paneCarte.setStyle("-fx-background-color: #f9f9f9; -fx-border-color: #ddd;");
                });

                // Handler pentru click
                int finalI = i;
                paneCarte.setOnMouseClicked(event -> {
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

    public void setOnCoverClick(Consumer<String> handler) {
        this.onCoverClickHandler = handler;
    }


}