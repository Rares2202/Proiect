package proiect.claseUser;

import java.sql.*;

public class MyReads {

    int user_idUser;
   String URL;
    public MyReads(int user_idUser, String carte_idCarte) {

        this.user_idUser = user_idUser;
        this.URL = carte_idCarte;
    }
    public int getUser_idUser() {return user_idUser;}
    public String getCarte_idCarte() {return URL;}
    public void addBook(MyReads myReads) {
        String DB_URL = "jdbc:mysql://localhost:3306/mydb";
        String DB_USER = "root";
        String DB_PASSWORD = "simone";
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
    private boolean existaUser(Connection conn, int userId) throws SQLException {
        String checkUser = "SELECT 1 FROM user WHERE idUser = ?";
        try (PreparedStatement stmt = conn.prepareStatement(checkUser)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    private boolean existaCarte(Connection conn, String carteId) throws SQLException {
        String checkCarte = "SELECT 1 FROM carte WHERE coverCarte = ?";
        try (PreparedStatement stmt = conn.prepareStatement(checkCarte)) {
            stmt.setString(1, carteId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }
    private boolean existaURL(Connection conn, String URL) throws SQLException {
        String checkURL = "SELECT 1 FROM myreads WHERE coverCarte = ?";
        try (PreparedStatement stmt = conn.prepareStatement(checkURL)) {
            stmt.setString(1, URL);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }
//    public MyReads[] getMyReads() {
//
//    }
}
