package proiect.controller;

import javafx.scene.input.MouseEvent;


public class ControllerLibrarian {

    private ControllerMain mainController;

    public void quit_app(MouseEvent mouseEvent) {
        System.exit(0);
    }

    public void setMainController(ControllerMain mainController) {
        this.mainController = mainController;
    }

}