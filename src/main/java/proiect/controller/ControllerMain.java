package proiect.controller;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import javax.swing.*;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ControllerMain {
    @FXML
    public StackPane contentPane;
    private Pane Home;
    private  Pane Imreading;
    private  Pane LoginRegister;
    private  Pane Myreads;
    private  Pane Preferinte;
    private  Pane RegisterAuth;
    private  Pane Review;
    private  Pane Search;
    int usrId;
    private static final String DB_URL = "jdbc:mysql://localhost:3306/mydb";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "simone";
    Connection con;
    Statement stmt;
    ResultSet rs;
    private final String[] buttonIds = {
             "register", "login", "inchide", "submit","register1"
    };
    @FXML
    public void initialize() {
        try {
            // Load all panes with button handlers

//            Home = loadPane("/proiect/fxml/user/Home.fxml");
//            Imreading=loadPane("/proiect/fxml/user/Imreading.fxml");
            LoginRegister=loadPane("/proiect/fxml/LoginRegister.fxml");
//            Myreads=loadPane("/proiect/fxml/user/Myreads.fxml");
//            Preferinte = loadPane("/proiect/fxml/user/Preferinte.fxml");
            RegisterAuth=loadPane("/proiect/fxml/RegisterAuth.fxml");
//            Review=loadPane("/proiect/fxml/user/Review.fxml");
//            Search=loadPane("/proiect/fxml/user/Search.fxml");

            contentPane.getChildren().setAll(LoginRegister);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
    private Pane loadPane(String fxmlPath) throws IOException {
        Pane pane = FXMLLoader.load(getClass().getResource(fxmlPath));

        for (String buttonId : buttonIds) {
            Button button = (Button) pane.lookup("#" + buttonId);
            if (button != null) {
                switch (buttonId) {
                    case "register":
                        button.setOnAction(e ->register_reg());
                        break;
                    case "login":
                        button.setOnAction(e ->login());
                        break;
                    case "register1":
                        button.setOnAction(e ->register());
                                break;
                    case "inchide":

                        System.exit(0);
                        break;


                }
            }
        }
        return pane;
    }

    private void register() {
          contentPane.getChildren().setAll(RegisterAuth);
    }
    private void login() {
        TextField userField = (TextField) LoginRegister.lookup("#username_log");
        String username = userField.getText();
        PasswordField passField = (PasswordField) LoginRegister.lookup("#password_log");
        String password = String.valueOf(passField.getText());
        if (authenticateUser(username, password)) {
            showAlert("Login successful!");

        } else {
            showAlert("Invalid credentials.");
        }
    }
    private void register_reg() {
        TextField userField = (TextField) RegisterAuth.lookup("#username_reg");
        String username = userField.getText();
        PasswordField passField = (PasswordField) RegisterAuth.lookup("#password_reg");
        String password = String.valueOf(passField.getText());
        CheckBox librarian1 = (CheckBox) RegisterAuth.lookup("#librarian");
        boolean isLibrarian = librarian1.isSelected();
        int newUserId = registerUser(username, password, isLibrarian);
        if (newUserId != -1) {
            usrId = newUserId;
            showAlert("Registration successful!");
            System.out.println("User id = " + usrId);
        } else {
            showAlert("Registration failed. Username might already exist.");
        }
    }
    private boolean authenticateUser(String username, String password) {
        String query = "SELECT * FROM User WHERE userName = ? AND password = ?";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
private void showAlert(String message) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Pane Changed");
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
}
    private int registerUser(String username, String password, boolean isLibrarian) {
        int userId = -1;

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String checkQuery = "SELECT idUser FROM User WHERE userName = ?";
            try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {
                checkStmt.setString(1, username);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next()) {
                    return -1;
                }
            }
            String insertQuery = "INSERT INTO User (userName, password, librarian) VALUES (?, ?, ?)";
            try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
                insertStmt.setString(1, username);
                insertStmt.setString(2, password);
                insertStmt.setBoolean(3, isLibrarian);
                int affectedRows = insertStmt.executeUpdate();

                if (affectedRows == 0) {
                    return -1;
                }

                try (ResultSet generatedKeys = insertStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        userId = generatedKeys.getInt(1);
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return userId;
    }
}
