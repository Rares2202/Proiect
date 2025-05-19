package proiect.controller;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import java.io.IOException;
import java.sql.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Controller main.
 */
public class ControllerMain {
    /**
     * Content pane.
     */
    @FXML
    public StackPane contentPane;
    private ControllerUser controllerUser;
//    private ControllerLibrarian controllerLibrarian;
    private Pane LoginRegister;
    private Pane RegisterAuth;
    private Pane UserMain;
    private Pane LibrarianMain;

    /**
     *  usr id.
     */
    int usrId;
    private static final String DB_URL = "jdbc:mysql://localhost:3306/mydb";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root";
    private final String[] buttonIds = {
            "register", "login", "inchide", "submit", "register1","login1"
    };
    private static final String regex = "^[A-Za-z0-9]+$";


    /**
     * Initialize.
     */
    @FXML
    public void initialize() {
        try {

            Pane wellcome = loadPane("/proiect/fxml/Wellcome.fxml");
            LoginRegister = loadPane("/proiect/fxml/LoginRegister.fxml");
            RegisterAuth = loadPane("/proiect/fxml/RegisterAuth.fxml");
            UserMain=loadSpecialPane("/proiect/fxml/user/UserMain.fxml", ControllerUser.class);
//            LibrarianMain=loadSpecialPane("/proiect/fxml/librarian/LibrarianMain.fxml", ControllerLibrarian.class);
            contentPane.getChildren().setAll(wellcome);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    /**
     * Loads and returns a Pane from the specified FXML file path. This method also
     * sets up action event handlers for specific buttons within the loaded Pane
     * based on their IDs.
     *
     * @param fxmlPath the path to the FXML file to load
     * @return the loaded Pane object
     * @throws IOException if there is an error loading the FXML file
     */
    private Pane loadPane(String fxmlPath) throws IOException {
        Pane pane = FXMLLoader.load(getClass().getResource(fxmlPath));

        for (String buttonId : buttonIds) {
            Button button = (Button) pane.lookup("#" + buttonId);
            if (button != null) {
                switch (buttonId) {
                    case "register":
                        button.setOnAction(_ -> {
                            try {
                                register_reg();
                            } catch (SQLException ex) {
                                throw new RuntimeException(ex);
                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }
                        });
                        break;
                    case "login":
                        button.setOnAction(_ -> {
                            try {
                                login();
                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }
                        });
                        break;
                    case "register1":
                        button.setOnAction(_ -> register());
                        break;
                    case "inchide":
                        button.setOnAction(_ -> quit_app());

                        break;
                    case "login1":
                        button.setOnAction(_ -> login1());
                        break;


                }
            }
        }
        return pane;
    }

    /**
     * Loads a Pane from the specified FXML file path and associates it with
     * the given controller class. Depending on the controller type, it initializes
     * the appropriate controller and sets a reference back to the main controller.
     *
     * @param fxmlPath the path to the FXML file to be loaded
     * @param controllerClass the class of the controller associated with the FXML
     *                        file, used to determine the type of controller to initialize
     * @return the loaded Pane
     * @throws IOException if an error occurs while loading the FXML file
     */
    private Pane loadSpecialPane(String fxmlPath, Class<?> controllerClass) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Pane pane = loader.load();

        Object controller = loader.getController();
        if (controllerClass == ControllerUser.class) {
            controllerUser = loader.getController();
            controllerUser.setMainController(this);
        }
//        else if (controllerClass == ControllerLibrarian.class) {
//            controllerLibrarian = loader.getController();
//            controllerLibrarian.setMainController(this);
//        }

        return pane;
    }

    /**
     * Updates the contents of the application's main display to show the registration authentication screen.
     *
     * This method replaces all child nodes of the contentPane with the RegisterAuth Pane,
     * effectively navigating the UI to the registration interface.
     */
    private void register() {
        contentPane.getChildren().setAll(RegisterAuth);
    }
    @FXML
    private void login1() {
        contentPane.getChildren().setAll(LoginRegister);
    }
    private void login() throws IOException {
        int id=-1;
        TextField userField = (TextField) LoginRegister.lookup("#username_log");
        String username = userField.getText();
        PasswordField passField = (PasswordField) LoginRegister.lookup("#password_log");
        String password = String.valueOf(passField.getText());
         id = authenticateUser(username, password);
        if (id != -1) {
            showAlert("Login successful!");
            initializeUserController(id);
            contentPane.getChildren().setAll(UserMain);
        }
    }
    private void register_reg() throws SQLException, IOException {
        TextField userField = (TextField) RegisterAuth.lookup("#username_reg");
        String username = userField.getText();
        PasswordField passField = (PasswordField) RegisterAuth.lookup("#password_reg");
        String password = String.valueOf(passField.getText());
        Matcher matcher=null;
        Pattern pattern = Pattern.compile(regex);
        matcher = pattern.matcher(password);
        CheckBox librarian1 = (CheckBox) RegisterAuth.lookup("#librarian1");
        boolean isLibrarian = librarian1.isSelected();
        int newUserId = registerUser(username, password, isLibrarian);
        if(username.length()<6||password.length()<6)
        {
            showAlert("Username or password too short.");
            newUserId=-1;
        }
        if (newUserId != -1) {
            usrId = newUserId;
            showAlert("Registration successful!");
        } else {
            showAlert("Registration failed. Username might already exist.");
        }
        if (newUserId != -1) {
            usrId = newUserId;
            showAlert("Registration successful!");
            initializeUserController(newUserId);
            contentPane.getChildren().setAll(UserMain);
        }
    }

    private int authenticateUser(String username, String password) {
        String query = "SELECT idUser, librarian FROM User WHERE userName = ? AND password = ?";
        int userId = -1;
        boolean isLibrarian = false;
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    userId = resultSet.getInt("idUser");
                    isLibrarian = resultSet.getBoolean("librarian");
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
    private boolean isUserLibrarian(int userId) {
        String query = "SELECT librarian FROM User WHERE idUser = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getBoolean("librarian");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * Quit app.
     */
    public void quit_app() {
        System.exit(0);
    }
    private void initializeUserController(int userId) {
        try {
            if (controllerUser == null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/proiect/fxml/user/UserMain.fxml"));
                UserMain = loader.load();
                controllerUser = loader.getController();
                controllerUser.setMainController(this);
            }
            controllerUser.setUserId(userId);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
