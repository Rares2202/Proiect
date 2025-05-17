package proiect.claseUser;

import java.sql.*;

public class Review {
    String reviewText;
    int reviewRating;
    int User_idUser;
    int Carte_idCarte;

  public  Review(String review, int rating, int idUser, int idCarte) {
        this.reviewText = review;
        this.reviewRating = rating;
        this.User_idUser = idUser;
        this.Carte_idCarte = idCarte;
    }

    public int getReviewRating() {
        return reviewRating;
    }
    public int getUser_idUser() {return User_idUser;}

    public String getReviewText() {return reviewText;}


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
