package proiect.claseUser;

import java.sql.*;

/**
 * The type Review.
 */
public class Review {
    /**
     * The Review text.
     */
    String reviewText;
    /**
     * The Review rating.
     */
    int reviewRating;
    /**
     * The User id user.
     */
    int User_idUser;
    /**
     * The Carte id carte.
     */
    int Carte_idCarte;

    /**
     * Instantiates a new Review.
     *
     * @param review  the review
     * @param rating  the rating
     * @param idUser  the id user
     * @param idCarte the id carte
     */
    public  Review(String review, int rating, int idUser, int idCarte) {
        this.reviewText = review;
        this.reviewRating = rating;
        this.User_idUser = idUser;
        this.Carte_idCarte = idCarte;
    }

    /**
     * Gets review rating.
     *
     * @return the review rating
     */
    public int getReviewRating() {
        return reviewRating;
    }

    /**
     * Gets user id user.
     *
     * @return the user id user
     */
    public int getUser_idUser() {return User_idUser;}

    /**
     * Gets review text.
     *
     * @return the review text
     */
    public String getReviewText() {return reviewText;}


    /**
     * Trimite review.
     *
     * @param review the review
     */
    public void trimiteReview(Review review) {
        String DB_URL = "jdbc:mysql://localhost:3306/mydb";
        String DB_USER = "root";
        String DB_PASSWORD = "root";

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

    private boolean existaUser(Connection conn, int userId) throws SQLException {
        String checkUser = "SELECT 1 FROM user WHERE idUser = ?";
        try (PreparedStatement stmt = conn.prepareStatement(checkUser)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

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
     * Exista review boolean.
     *
     * @param conn    the conn
     * @param userId  the user id
     * @param carteId the carte id
     * @return the boolean
     * @throws SQLException the sql exception
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
     * Select user from reviews string.
     *
     * @param DB_URL  the db url
     * @param DB_USER the db user
     * @param DB_PASS the db pass
     * @param userId  the user id
     * @return the string
     * @throws SQLException the sql exception
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
