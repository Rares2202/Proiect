package proiect.controller;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import proiect.ControllerLibrarian;
import proiect.LibrarianMain;

import javax.swing.*;
import java.io.IOException;
import java.net.ConnectException;
import java.sql.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class serves as the main controller for managing the application's user interface and functionality.
 * It provides methods for loading FXML views, handling user interactions such as login and registration,
 * and managing the switch between different panes based on user actions and roles.
 *
 */
public class ControllerMain {
    @FXML
    public StackPane contentPane;
    private ControllerUser controllerUser;
    private ControllerLibrarian controllerLibrarian;
    private Pane LoginRegister;
    private Pane RegisterAuth;
    private Pane UserMain;


    int usrId;
    private static final String DB_URL = "jdbc:mysql://localhost:3306/mydb";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "simone";
    private final String[] buttonIds = {
            "register", "login", "inchide", "submit", "register1","login1"
    };
    private static final String regex = "^[A-Za-z0-9]+$";

    /**
     * Initializes the main controller and loads the initial UI panes.
     *
     * This method is triggered automatically during the controller's lifecycle to set up the user interface.
     * It loads multiple FXML-based UI components and assigns them to the corresponding fields for later use.
     * The method also sets the initial content pane to display the welcome screen.
     *
     * Functionality:
     * - Loads FXML files for various panes, including the welcome screen, login/register screen, user main screen, and librarian main screen.
     * - Uses the `loadPane` and `loadSpecialPane` helper methods for FXML loading, ensuring the correct controllers are assigned where needed.
     * - Initializes the `contentPane` with the welcome screen*/
    @FXML
    public void initialize() {
        try{
            Pane wellcome = loadPane("/proiect/fxml/Wellcome.fxml");
            LoginRegister = loadPane("/proiect/fxml/LoginRegister.fxml");
            RegisterAuth = loadPane("/proiect/fxml/RegisterAuth.fxml");
            UserMain=loadSpecialPane("/proiect/fxml/user/UserMain.fxml", ControllerUser.class);

            contentPane.getChildren().setAll(wellcome);


        } catch (IOException e) {
            System.out.println("Error loading FXML files");
            e.printStackTrace();
        }


    }

    /**
     * Loads an FXML layout and returns the corresponding pane.
     * Additionally, this method assigns actions to specific buttons within the loaded pane based
     * on their predefined identifiers.
     *
     * Buttons handled include:
     * - "register" - Handles user registration and processes associated actions.
     * - "login" - Handles user login operations.
     * - "register1" - Switches the UI content to the registration pane.
     * - "inchide" - Quits the application.
     * - "login1" - Switches the UI content to the login pane.
     *
     * @param fxmlPath The relative path to the FXML file to be loaded.
     * @return The loaded Pane containing the UI layout defined in the specified FXML file.
     * @throws IOException If an I/O error occurs when loading the FXML file.
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
     * Loads a specific FXML pane and configures its controller based on the provided class type.
     *
     * This method is used to dynamically load an FXML file, initialize the corresponding pane
     * and associate it with the specified controller class. Depending on the controller type
     * (e.g., `ControllerUser`, `ControllerLibrarian`), it sets the main controller context
     * for the loaded controller.
     *
     * @param fxmlPath The file path of the FXML resource to be loaded.
     * @param controllerClass The class type of the controller associated with the FXML resource.
     * @return The loaded Pane containing the user interface layout defined in the specified FXML file.
     * @throws IOException If an I/O error occurs during the loading of the FXML file.
     */
    private Pane loadSpecialPane(String fxmlPath, Class<?> controllerClass) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Pane pane = loader.load();

        Object controller = loader.getController();
        if (controllerClass == ControllerUser.class) {
            controllerUser = loader.getController();
            controllerUser.setMainController(this);
        }

        return pane;
    }

    /**
     * Switches the main application content to the registration pane.
     *
     * This method replaces the current contents of the `contentPane` with the
     * `RegisterAuth` pane, which contains the user interface elements for registration.
     *
     * Usage Context:
     * This method is used when transitioning the user interface to display
     * the registration form. It updates the main view by setting the entire
     * content area to the designated registration pane.
     */
    private void register() {
        contentPane.getChildren().setAll(RegisterAuth);
    }

    /**
     * Switches the current content pane of the main application to the login/register screen.
     *
     * This method updates the `contentPane` of the main UI to display the login/register interface
     * stored in the `LoginRegister` pane. It is typically triggered when the user needs to navigate
     * to the login screen from another section of the application.
     *
     * Functionality:
     * - Clears the existing children of `contentPane`.
     * - Loads and sets the `LoginRegister` pane as the new content in the `contentPane`.
     */
    @FXML
    private void login1() {

        contentPane.getChildren().setAll(LoginRegister);
    }


    /**
     * Handles the user login process by verifying credentials and initializing the user interface upon successful authentication.
     *
     * This method retrieves the username and password entered in the corresponding input fields within the login interface.
     * The credentials are validated using the `authenticateUser` method. If the authentication is successful (i.e., a valid user ID is returned),
     * the user interface is updated to display the user dashboard, and the user controller is initialized. If the authentication fails,
     * no changes occur.
     *
     * @throws IOException if an error occurs while loading the user interface resources
     */
    protected void login() throws IOException {
        int id=-1;
        TextField userField = (TextField) LoginRegister.lookup("#username_log");
        String username = userField.getText();
        PasswordField passField = (PasswordField) LoginRegister.lookup("#password_log");
        String password = String.valueOf(passField.getText());
         id = authenticateUser(username, password);

        if (id != -1) {
            if(isUserLibrarian(id))
            {
                Stage librarianStage = new Stage();
                Stage currentStage = (Stage) contentPane.getScene().getWindow();
                new LibrarianMain().start(librarianStage);
                currentStage.close();
            }
            else {
               // showAlert("Login successful!");
                initializeUserController(id);
                contentPane.getChildren().setAll(UserMain);
            }
        }
        else {
            showAlert("Login failed. Wrong username or password.");
            return;
        }

    }


    /**
     * Handles the registration process for a new user.
     *
     * This method retrieves the user's input from the registration interface, including the username, password,
     * and librarian status. The method validates the inputs to ensure they meet specific criteria, such as
     * minimum length and password pattern compliance. Upon successful validation and registration via the
     * `registerUser` method, the user ID is stored, and the registration success message is displayed.
     * Otherwise, appropriate error messages are shown to the user.
     *
     * Functionality includes:
     * - Extracting user registration information from the interface.
     * - Validating the username and password for length and compliance with a regex pattern.
     * - Attempting to register the user, returning a unique user ID upon success.
     * - Handling success or failure events, along with necessary error messages or UI updates.
     * - Initializing the user controller and updating the main content pane upon registration success.
     *
     * @throws SQLException if a database access error occurs during user registration.
     * @throws IOException if an error occurs during UI-related operations or loading resources.
     */
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
            return;

        }
        if (newUserId != -1) {
            usrId = newUserId;
            showAlert("Registration successful!");
        } else {
            showAlert("Registration failed. Username might already exist.");
            return;
        }
        if (newUserId != -1) {
            if(isLibrarian){
                    Stage librarianStage = new Stage();
                Stage currentStage = (Stage) contentPane.getScene().getWindow();
                new LibrarianMain().start(librarianStage);
                currentStage.close();
            }
            else {
                usrId = newUserId;
                showAlert("Registration successful!");
                initializeUserController(newUserId);
                contentPane.getChildren().setAll(UserMain);
            }
        }
    }

    /**
     * Authenticates a user by verifying the provided username and password
     * against the database and retrieves the user's ID if authentication is successful.
     *
     * @param username The username of the user attempting to authenticate.
     * @param password The password of the user attempting to authenticate.
     * @return The user ID if authentication is successful; -1 if authentication fails or an*/
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
            showAlert("Nu exista conexiune la baza de date");
            e.printStackTrace();
        }
        return userId;
    }

    /**
     * Displays an informational alert with a specified message.
     *
     * The alert will have the title "Pane Changed" and will show the given message
     * in the content area. The header area of the alert is intentionally left
     * blank. Once displayed, the alert requires the user to acknowledge it before
     * proceeding.
     */
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("ERROR");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Registers a new user in the system by storing their credentials and role in the database.
     * If a user with the same username already exists, the registration fails.
     *
     * @param username The username of the new user to be registered.
     * @param password The password of the new user to be registered.
     * @param isLibrarian A boolean value indicating if the user is registering as a librarian.
     * @return The unique ID of the newly registered user if successful, or -1 if the registration fails
     *         (e.g., username already exists or database operation errors occur).
     */
    private int registerUser(String username, String password, boolean isLibrarian) {
        int userId = -1;

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String checkQuery = "SELECT idUser FROM User WHERE userName = ?";
            try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery,Statement.RETURN_GENERATED_KEYS)) {
                checkStmt.setString(1, username);
                ResultSet rs = checkStmt.executeQuery();
                if (rs!=null) {
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

    /**
     * Determines whether a user is a librarian based on their ID.
     * This method queries the database to check if the user with the given ID
     * has the librarian role. If the user is found and has the librarian role,
     * it returns true; otherwise, it returns false. If an exception occurs during
     * database interaction, it returns false as a default.
     *
     * @param userId The unique identifier of the user to be checked.
     * @return true if the user is a librarian, false otherwise.
     */
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
     * Terminates the application.
     *
     * This method is invoked to exit the application by calling the `System.exit(0)` method,
     * which closes the running program and terminates all associated processes.
     * The exit status is set to 0, indicating a normal termination without errors.
     *
     * Usage Context:
     * Typically triggered by user actions, such as pressing an "exit" or "quit" button, or
     * in scenarios where the program needs to close in response to specific conditions.
     */
    public void quit_app() {
        System.exit(0);
    }

    /**
     * Initializes the user controller by loading the corresponding FXML layout
     * and setting up the controller instance with necessary configurations.
     *
     * This method ensures that the user interface and associated logic for the user
     * controller are properly loaded and prepared for interaction. If the user
     * controller has not been initialized yet, it loads the FXML file, assigns the
     * controller, and configures it with the provided user ID.
     *
     * @param userId The unique identifier of the user for which the controller is being initialized.
     */
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
