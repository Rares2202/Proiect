package proiect.claseUser;

import java.sql.*;

/**
 * Represents a review entry associated with a user and a book.
 * This class provides methods to manage and persist reviews to the database.
 */
public class Review {
    String reviewText;
    int reviewRating;
    int User_idUser;
    int Carte_idCarte;

    /**
     * Constructs a new Review object with specified review text, rating, user ID, and book ID.
     *
     * @param review the text of the review
     * @param rating the rating value associated with the review
     * @param idUser the unique identifier of the user submitting the review
     * @param idCarte the unique identifier of the book being reviewed
     */
  public  Review(String review, int rating, int idUser, int idCarte) {
        this.reviewText = review;
        this.reviewRating = rating;
        this.User_idUser = idUser;
        this.Carte_idCarte = idCarte;
    }

    /**
     * Retrieves the rating value associated with the review.
     *
     * @return the integer value representing the review rating
     */
    public int getReviewRating() {
        return reviewRating;
    }

    /**
     * Retrieves the unique identifier of the user associated with the review.
     *
     * @return an integer representing the unique user ID
     */
    public int getUser_idUser() {return User_idUser;}

    /**
     * Retrieves the textual content of the review.
     *
     * @return a string representing the review text
     */
    public String getReviewText() {return reviewText;}

    /**
     * Persists or updates a review in the database. The method inserts a new review
     * or updates an existing review based on whether the review already exists
     * for the specific user and book combination.
     *
     * @param review the review object containing the review text, rating, user ID,
     *               and book ID to be processed
     * @throws RuntimeException if the user or book does not exist in the database
     *                          or if a database-related error occurs
     */
    public void trimiteReview(Review review) {
        String DB_URL = "jdbc:mysql://localhost:3306/mydb";
        String DB_USER = "root";
        String DB_PASSWORD = "simone";

        String insertQuery = "INSERT INTO review (reviewText, reviewRating, User_idUser, Carte_idCarte) VALUES (?, ?, ?, ?)";
        String updateQuery = "UPDATE review SET reviewText = ?, reviewRating = ? WHERE User_idUser = ? AND Carte_idCarte = ?";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {


            if (!existaUser(connection, review.User_idUser)) {
                throw new RuntimeException("Userul cu ID " + review.User_idUser + " nu există.");
            }
            if (!existaCarte(connection, review.Carte_idCarte)) {
                throw new RuntimeException("Cartea cu ID " + review.Carte_idCarte + " nu există.");
            }



            if (existaReview(connection, review.User_idUser, review.Carte_idCarte)) {
                try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
                    updateStmt.setString(1, review.reviewText);
                    updateStmt.setInt(2, review.reviewRating);
                    updateStmt.setInt(3, review.User_idUser);
                    updateStmt.setInt(4, review.Carte_idCarte);

                    int rows = updateStmt.executeUpdate();
                    System.out.println(rows > 0 ? "Review actualizat cu succes." : "Reviewul NU a fost actualizat.");
                }
            } else {

                try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {
                    insertStmt.setString(1, review.reviewText);
                    insertStmt.setInt(2, review.reviewRating);
                    insertStmt.setInt(3, review.User_idUser);
                    insertStmt.setInt(4, review.Carte_idCarte);

                    int rows = insertStmt.executeUpdate();
                    System.out.println(rows > 0 ? "Review adaugat cu succes." : "Reviewul NU a fost adaugat.");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Eroare la procesarea review-ului: " + e.getMessage(), e);
        }
    }

    /**
     * Checks if a user with the given ID exists in the database.
     *
     * @param conn the active database connection to be used for the query
     * @param userId the unique identifier of the user to check for existence
     * @return true if the user exists in the database, false otherwise
     * @throws SQLException if any SQL errors occur during the execution of the query
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
     * Checks if a book with the given ID exists in the database.
     *
     * @param conn the active database connection to be used for the query
     * @param carteId the unique identifier of the book to check for existence
     * @return true if the book exists in the database, false otherwise
     * @throws SQLException if an SQL error occurs during the execution of the query
     */
    private boolean existaCarte(Connection conn, int carteId) throws SQLException {
        String checkCarte = "SELECT 1 FROM carte WHERE idCarte = ?";
        try (PreparedStatement stmt = conn.prepareStatement(checkCarte)) {
            stmt.setInt(1, carteId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Checks if a review exists in the database for a specific combination of user and book.
     *
     * @param conn the active database connection to be used for the query
     * @param userId the unique identifier of the user
     * @param carteId the unique identifier of the book
     * @return true if a review exists for the given user and book, false otherwise
     * @throws SQLException if any SQL errors occur during the query execution
     */
  public boolean existaReview(Connection conn, int userId, int carteId) throws SQLException {
        String query = "SELECT 1 FROM review WHERE User_idUser = ? AND Carte_idCarte = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, carteId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Executes a query to retrieve the username associated with a specified user ID from the database.
     *
     * @param DB_URL the database connection URL
     * @param DB_USER the username for database authentication
     * @param DB_PASS the password for database authentication
     * @param userId the unique identifier of the user whose username is to be retrieved
     * @return the username of the user if found, or an empty string if no user exists with the given ID
     * @throws SQLException if a database access error occurs or the query fails
     */
    public String SELECT_USER_FROM_REVIEWS(String DB_URL, String DB_USER, String DB_PASS, int userId) throws SQLException {
        Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
      String query = "SELECT userName FROM user WHERE idUser = ?";
      String cv=" ";
      try (PreparedStatement stmt = conn.prepareStatement(query)) {
          stmt.setInt(1, userId);
          try (ResultSet rs = stmt.executeQuery()) {
              if (rs.next()) {
                  cv=rs.getString("userName");
              }
          }
      }
      return cv;
    }


}
