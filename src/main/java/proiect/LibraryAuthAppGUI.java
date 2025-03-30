package proiect;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;

public class LibraryAuthAppGUI {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/mydb";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "thatwasfun";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LibraryAuthAppGUI::new);
    }

    public LibraryAuthAppGUI() {
        JFrame frame = new JFrame("Library Application");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLayout(new GridLayout(3, 1));

        JButton loginButton = new JButton("Log-in");
        JButton registerButton = new JButton("Register");

        frame.add(new JLabel("Welcome to the Library System", SwingConstants.CENTER));
        frame.add(loginButton);
        frame.add(registerButton);

        loginButton.addActionListener(e -> openLoginWindow());
        registerButton.addActionListener(e -> openRegisterWindow());

        frame.setVisible(true);
    }

    private void openLoginWindow() {
        JFrame loginFrame = new JFrame("Log-in");
        loginFrame.setSize(400, 200);
        loginFrame.setLayout(new GridLayout(3, 2));

        JLabel userLabel = new JLabel("Username:");
        JTextField userField = new JTextField();
        JLabel passLabel = new JLabel("Password:");
        JPasswordField passField = new JPasswordField();
        JButton loginButton = new JButton("Log-in");

        loginFrame.add(userLabel);
        loginFrame.add(userField);
        loginFrame.add(passLabel);
        loginFrame.add(passField);
        loginFrame.add(new JLabel(""));
        loginFrame.add(loginButton);

        loginButton.addActionListener(e -> {
            String username = userField.getText();
            String password = String.valueOf(passField.getPassword());
            if (authenticateUser(username, password)) {
                JOptionPane.showMessageDialog(loginFrame, "Login successful!");
            } else {
                JOptionPane.showMessageDialog(loginFrame, "Invalid credentials.");
            }
        });

        loginFrame.setVisible(true);
    }

    private void openRegisterWindow() {
        JFrame registerFrame = new JFrame("Register");
        registerFrame.setSize(400, 200);
        registerFrame.setLayout(new GridLayout(3, 2));

        JLabel userLabel = new JLabel("Username:");
        JTextField userField = new JTextField();
        JLabel passLabel = new JLabel("Password:");
        JPasswordField passField = new JPasswordField();
        JCheckBox librarianCheckBox = new JCheckBox("Librarian?");
        JButton registerButton = new JButton("Register");

        registerFrame.add(userLabel);
        registerFrame.add(userField);
        registerFrame.add(passLabel);
        registerFrame.add(passField);
        registerFrame.add(librarianCheckBox);
        registerFrame.add(registerButton);

        registerButton.addActionListener(e -> {
            String username = userField.getText();
            String password = String.valueOf(passField.getPassword());
            boolean isLibrarian = librarianCheckBox.isSelected();
            if (registerUser(username, password, isLibrarian)) {
                JOptionPane.showMessageDialog(registerFrame, "Registration successful!");
            } else {
                JOptionPane.showMessageDialog(registerFrame, "Registration failed. Username might already exist.");
            }
        });

        registerFrame.setVisible(true);
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

    private boolean registerUser(String username, String password, boolean isLibrarian) {
        String query = "INSERT INTO User (userName, password, librarian) VALUES (?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            preparedStatement.setBoolean(3, isLibrarian);
            preparedStatement.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
//    int id_user;
//    private boolean add_prefference(int id_user)
//    {
//        String query = "INSERT INTO preferinte (iduser, classics, fantasy, fiction, history, horror, economy, mystery, poetry, psychology, SF, romance, science) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
//        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
//             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
//            preparedStatement.setInt(1, id_user);
//            preparedStatement.setBoolean(2,);
//            preparedStatement.setBoolean(3,);
//            preparedStatement.setBoolean(4,);
//            preparedStatement.setBoolean(5,);
//            preparedStatement.setBoolean(6,);
//            preparedStatement.setBoolean(7,);
//            preparedStatement.setBoolean(8,);
//            preparedStatement.setBoolean(9,);
//            preparedStatement.setBoolean(10,);
//            preparedStatement.setBoolean(11,);
//            preparedStatement.setBoolean(12,);
//            preparedStatement.setBoolean(13,);
//            preparedStatement.executeUpdate();
//            return true;
//        } catch (SQLException e) {
//            e.printStackTrace();
//            return false;
//    }

//}
    }
