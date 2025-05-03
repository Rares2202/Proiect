package proiect.controller;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.awt.event.MouseEvent;
import java.io.IOException;
import java.sql.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>Tipul controller main.</p>
 */
public class ControllerMain {
    /**
     * Content pane-ul.
     */
    @FXML
    public StackPane contentPane;
    private ControllerUser controllerUser;

    private Pane LoginRegister;
    private Pane RegisterAuth;
    private Pane UserMain;
    private Pane LibrarianMain;
    private Pane Wellcome;

    /**
     * <p>Id-ul userului curent.</p>
     */
    int usrId;
    /**
     * <p>URL-ul la baza de date.</p>
     */
    private static final String DB_URL = "jdbc:mysql://localhost:3306/mydb";
    /**
     * <p>Username-ul vostru de acces la baza de date.</p>
     */
    private static final String DB_USER = "root";
    /**
     * <p>Parola pentru baza de date, aici introduceti fiecare parola voastra pe care v-ati configurat-o la baza voastra de date.</p>
     */
    private static final String DB_PASSWORD = "root";
    /**
     * <p>Aici sunt definite id-urile butoanelor folosite in interfete.</p>
     */
    private final String[] buttonIds = {
            "register", "login", "inchide", "submit", "register1","login1"
    };
    /**
     * Regex pentru parola.
     */
    private static final String regex = "^[A-Za-z0-9]+$";


    /**
     * Initialize.
     */
    @FXML
    public void initialize() {
        try {

            Wellcome=loadPane("/proiect/fxml/Wellcome.fxml");
            LoginRegister = loadPane("/proiect/fxml/LoginRegister.fxml");
            RegisterAuth = loadPane("/proiect/fxml/RegisterAuth.fxml");
            UserMain=loadSpecialPane("/proiect/fxml/user/UserMain.fxml", ControllerUser.class);
            contentPane.getChildren().setAll(Wellcome);

        }
        catch (IOException e) {
            e.printStackTrace();
        }


    }

    /**
     * <p>
     * Adauga functionalitati la butoane.
     * </p>
     * @param fxmlPath
     * @return Pane-ul cu butoanele functionale.
     * @throws IOException
     */
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
                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }
                        });
                        break;
                    case "login":
                        button.setOnAction(e -> {
                            try {
                                login();
                            }
                            catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }
                        }
                        );
                        break;
                    case "register1":
                        button.setOnAction(e -> register());
                        break;
                    case "inchide":
                        button.setOnMouseClicked(e -> {
                            quit_app();
                        });
                        break;
                    case "login1":
                        button.setOnAction(e -> login1());
                        break;


                }
            }
        }
        return pane;
    }

    /**
     * <p>Initializarea pane-ului si initializeaza butoanele.</p>
     * @param fxmlPath path-ul catre fereastra
     * @param controllerClass tip de controller
     * @return pane-ul incarcat.
     * @throws IOException
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
     * <p>Optiunea de inregistrare la launch.</p>
     */
    private void register() {
        contentPane.getChildren().setAll(RegisterAuth);
    }
    /**
     *
     * <p>Optiunea de logare la launch.</p>
     */
    @FXML
    private void login1() {
        contentPane.getChildren().setAll(LoginRegister);
    }

    /**
     * <p>Metoda pentru logarea utilizatorului in aplicatie.</p>
     * @throws IOException
     */
    private void login() throws IOException {
        int id=-1;

        TextField userField = (TextField) LoginRegister.lookup("#username_log");
        String username = userField.getText();
        PasswordField passField = (PasswordField) LoginRegister.lookup("#password_log");
        String password = String.valueOf(passField.getText());
         id = authenticateUser(username, password);


        if (id != -1) {
            showAlert("Login successful!");
            boolean isLib = isUserLibrarian(id);// Implement this method to check DB
            System.out.println("isLib: " + isLib);
            if (isLib) {
                contentPane.getChildren().setAll(LibrarianMain);
            } else {
                contentPane.getChildren().setAll(UserMain);
                controllerUser.setUserId(id);
            }
        } else {
            showAlert("Invalid credentials.");
        }
    }


    /**
     * <p>Inregistrarea userului in baza de date.</p>
     * @throws SQLException
     * @throws IOException
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

        }
        if(!matcher.matches())
        {
            newUserId=-1;
            showAlert("Invalid password.");
            return;
        }


        if (newUserId != -1) {
            usrId = newUserId;
            showAlert("Registration successful!");

            System.out.println("User id = " + usrId);
        } else {
            showAlert("Registration failed. Username might already exist.");
        }
        if (newUserId != -1) {
            usrId = newUserId;
            showAlert("Registration successful!");
            if (isLibrarian) {
                contentPane.getChildren().setAll(LibrarianMain);
            } else {
                contentPane.getChildren().setAll(UserMain);
                controllerUser.setUserId(newUserId);
            }
        } else {
            showAlert("Registration failed.");
        }
    }

    /**
     * <p>Logare user in aplicatie.<p>
     * @param username Numele de utilizator unic fiecarui user.
     * @param password Parola utilizatorului.
     * @return Id-ul unic al userului.
     */
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
            showAlert("Fetch from database failed.");
        }
        return userId;
    }

    /**
     * <p>Pop-up eroare.</p>
     * @param message Mesaj eroare.
     */
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Pane Changed");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * <p>Metoda inregistrare user in baza de date.</p>
     * @param username  Username care trebuie sa fie unic.
     * @param password  Parola userului.
     * @param isLibrarian   Default 0, parametru care face userul librarian.
     * @return id user unic.
     */
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
            showAlert("Insert to database failed.");
        }

        return userId;
    }

    /**
     * <p>Verifica daca userul este librarian</p>
     * @param userId id user unic
     * @return boolean default false
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
     * Quit app.
     */
    public void quit_app() {
        System.exit(0);
    }


}
