module Biblioteca {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    requires javafx.swing;
    requires java.desktop;
    requires fontawesomefx;
    requires java.naming;

    opens proiect to javafx.fxml;
    exports proiect;
    exports proiect.controller;
    opens proiect.controller to javafx.fxml;
}
