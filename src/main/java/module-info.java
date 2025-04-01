module Biblioteca {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    opens proiect to javafx.fxml;
    exports proiect;
}