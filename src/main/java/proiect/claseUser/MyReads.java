package proiect.claseUser;

import java.sql.*;

/**
 * The MyReads class represents a user's collection of books in a reading list.
 * It provides functionality to add a book to the user's list by interacting with a database.
 * This class ensures that the user and the book exist in the database before adding the book to the list,
 * and avoids duplicate entries.
 */
public class MyReads {

    int user_idUser;
   String URL;

    /**
     * Constructs a new MyReads instance, associating a user with a specific book.
     *
     * @param user_idUser the unique identifier of the user.
     * @param carte_idCarte the unique identifier or URL of the book to associate with the user.
     */
    public MyReads(int user_idUser, String carte_idCarte) {

        this.user_idUser = user_idUser;
        this.URL = carte_idCarte;
    }

    /**
     * Adds a book to the user's "MyReads" list if the user exists,
     * the book exists, and the book is not already in the "MyReads" list.
     *
     * @param myReads the MyReads object containing the user ID and book cover URL
     *                to be added to the user's "MyReads" list.
     *                - user_idUser: the ID of the user associated with the book.
     *                - URL: the cover URL of the book to be added.
     * @throws RuntimeException if the user with the specified ID does not exist
     *                          or if the book with the specified cover URL does not exist.
     */
    public void addBook(MyReads myReads) {
        String DB_URL = "jdbc:mysql://localhost:3306/mydb";
        String DB_USER = "root";
        String DB_PASSWORD = "root";
        String query = "INSERT INTO myreads(user_idUser, coverCarte) VALUES (?, ?)";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {


            if (!existaUser(connection, myReads.user_idUser)) {
                throw new RuntimeException("Userul cu ID " + myReads.user_idUser + " nu exista.");
            }

            if (!existaCarte(connection, myReads.URL)) {
                throw new RuntimeException("Cartea cu ID " + myReads.URL + " nu exista.");
            }
            if(existaURL(connection, myReads.URL)) {
                return;
            }

            try (PreparedStatement insertStmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                insertStmt.setInt(1, myReads.user_idUser);
                insertStmt.setString(2,  myReads.URL);
                int affectedRows = insertStmt.executeUpdate();
                if (affectedRows > 0) {
                    System.out.println("Carte adaugata in MyReads.");
                } else {
                    System.out.println("Cartea nu a fost adaugata in MyReads.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Eroare la inserarea cartii: " + e.getMessage(), e);
        }


    }

    /**
     * Checks if a user with the specified ID exists in the database.
     *
     * @param conn the database connection to use for the query
     * @param userId the unique identifier of the user to be checked
     * @return true if the user exists in the database, false otherwise
     * @throws SQLException if a database access error occurs
     */
    private boolean existaUser(Connection conn, int userId) throws SQLException {
        String checkUser = "SELECT 1 FROM user WHERE idUser = ?";
        try (PreparedStatement stmt = conn.prepareStatement(checkUser)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Checks if a book with the specified ID exists in the "carte" table of the database.
     *
     * @param conn the database connection to use for the query
     * @param carteId the unique identifier of the book to be checked
     * @return true if the book exists in the database, false otherwise
     * @throws SQLException if a database access error occurs
     */
    private boolean existaCarte(Connection conn, String carteId) throws SQLException {
        String checkCarte = "SELECT 1 FROM carte WHERE coverCarte = ?";
        try (PreparedStatement stmt = conn.prepareStatement(checkCarte)) {
            stmt.setString(1, carteId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Checks if a specific URL exists in the "myreads" table of the database.
     *
     * @param conn the database connection to use for the query
     * @param URL the URL to check for existence in the "myreads" table
     * @return true if the URL exists in the database, false otherwise
     * @throws SQLException if a database access error occurs
     */
    private boolean existaURL(Connection conn, String URL) throws SQLException {
        String checkURL = "SELECT 1 FROM myreads WHERE coverCarte = ?";
        try (PreparedStatement stmt = conn.prepareStatement(checkURL)) {
            stmt.setString(1, URL);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }
}
