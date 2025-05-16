package proiect;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

import java.io.IOException;

public abstract class book {
    String id;
    public String book_name;
    public String author_name;
    public String gen_name;
    public String number;

    public String getBookId() {
        return id;
    }

    public String getBookName() {
        return book_name;
    }

    void f() {}

}
