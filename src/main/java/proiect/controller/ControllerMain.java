package proiect.controller;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ControllerMain {
    @FXML
    public StackPane contentPane;
    private ControllerUser controllerUser;
    private ControllerLibrarian controllerLibrarian;
    private Pane LoginRegister;
    private Pane RegisterAuth;
    private Pane UserMain;
    private Pane LibrarianMain;

    int usrId;
    private static final String DB_URL = "jdbc:mysql://localhost:3306/mydb";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "simone";
    private final String[] buttonIds = {
            "register", "login", "inchide", "submit", "register1"
    };

    @FXML
    public void initialize() {
        try {


            LoginRegister = loadPane("/proiect/fxml/LoginRegister.fxml");
            RegisterAuth = loadPane("/proiect/fxml/RegisterAuth.fxml");
            UserMain=loadSpecialPane("/proiect/fxml/user/UserMain.fxml", ControllerUser.class);
            LibrarianMain=loadSpecialPane("/proiect/fxml/LibrarianMain.fxml", ControllerLibrarian.class);
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
                        button.setOnAction(e -> {
                            try {
                                register_reg();
                            } catch (SQLException ex) {
                                throw new RuntimeException(ex);
                            }
                        });
                        break;
                    case "login":
                        button.setOnAction(e -> login());
                        break;
                    case "register1":
                        button.setOnAction(e -> register());
                        break;
                    case "inchide":
                        button.setOnAction(e -> quit_app());

                        break;


                }
            }
        }
        return pane;
    }

    private Pane loadSpecialPane(String fxmlPath, Class<?> controllerClass) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Pane pane = loader.load();

        Object controller = loader.getController();
        if (controllerClass == ControllerUser.class) {
            controllerUser = loader.getController();
            controllerUser.setMainController(this);
        } else if (controllerClass == ControllerLibrarian.class) {
            controllerLibrarian = loader.getController();
            controllerLibrarian.setMainController(this);
        }

        return pane;
    }

    private void register() {
        contentPane.getChildren().setAll(RegisterAuth);
    }

    private void login() {
        int id=-1;
        TextField userField = (TextField) LoginRegister.lookup("#username_log");
        String username = userField.getText();
        PasswordField passField = (PasswordField) LoginRegister.lookup("#password_log");
        String password = String.valueOf(passField.getText());
        id=authenticateUser(username, password);
        if (id!=-1) {
            showAlert("Login successful!");

            contentPane.getChildren().setAll(UserMain);
           controllerUser.setUserId(id);



        } else {
            showAlert("Invalid credentials.");
        }

    }

    private void register_reg() throws SQLException {
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
        if(usrId!=-1)
        {
            contentPane.getChildren().setAll(UserMain);
            controllerUser.setUserId(newUserId);
        }
    }

    private int authenticateUser(String username, String password) {
        String query = "SELECT idUser FROM User WHERE userName = ? AND password = ?";
        int userId = -1;
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    userId = resultSet.getInt("idUser");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userId;
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
            try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery,Statement.RETURN_GENERATED_KEYS)) {
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

    public void quit_app() {
        System.exit(0);
    }


}
