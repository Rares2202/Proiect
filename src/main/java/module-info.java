module Biblioteca {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    requires javafx.swing;
    requires java.desktop;
    requires fontawesomefx;

    opens proiect to javafx.fxml;
    exports proiect;
}
