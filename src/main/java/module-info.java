module Biblioteca {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires java.desktop;
    requires java.sql;
    requires fontawesomefx;

    opens proiect to javafx.fxml;
    exports proiect;
}
