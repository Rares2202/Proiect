package proiect.clase;

import javafx.scene.control.Alert;

import java.sql.*;
import java.util.*;

public class DBComands {

        public void INSERT_INTO_USERPREF(String query, String DB_URL, String DB_USER, String DB_PASS,
                                         int userId, List<Integer> validGenreIds) {

            try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {

                connection.setAutoCommit(false);

                try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                    for (Integer genreId : validGenreIds) {
                        pstmt.setInt(1, 1);
                        pstmt.setInt(2, userId);
                        pstmt.setInt(3, genreId);
                        pstmt.addBatch();
                    }

                    int[] results = pstmt.executeBatch();
                    connection.commit();

                    int totalInserted = Arrays.stream(results).sum();
                    if (totalInserted > 0) {
                        showAlert("Successfully saved " + totalInserted + " preferences!");
                    } else {
                        showAlert("No preferences were saved.");
                    }

                } catch (SQLException e) {
                    connection.rollback();
                    e.printStackTrace();
                    showAlert("Error saving preferences: " + e.getMessage());
                }

            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Database connection error: " + e.getMessage());
            }
        }
    public Map<String, Integer> SELECT_FROM_GENURI(String query, String DB_URL, String DB_USER, String DB_PASS)
    {
        Map<String, Integer> genreMap = new HashMap<>();

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                genreMap.put(rs.getString("genuri"), rs.getInt("idpreferinte"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return genreMap;
    }
public Boolean USER_ARE_PREF(String query, String DB_URL, String DB_USER, String DB_PASS,int id)
{
    try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {

        try (PreparedStatement checkStmt = connection.prepareStatement(query)) {
            checkStmt.setInt(1, id);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                return true;
            }
        }

    } catch (SQLException e) {
        throw new RuntimeException(e);
    }
    return false;
}
public List<Book> SELECT_ALL_FROM_BOOKS(String query, String DB_URL, String DB_USER, String DB_PASS)
{
    List<Book> books = new ArrayList<>();
         query = "SELECT idCarte, titluCarti, autorCarte, descriere, genCarte, numarCarte, coverCarte FROM carte WHERE coverCarte IS NOT NULL";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Book book = new Book(
                        rs.getInt("idCarte"),
                        rs.getString("titluCarti"),
                        rs.getString("autorCarte"),
                        rs.getString("descriere"),
                        rs.getString("genCarte"),
                        rs.getInt("numarCarte"),
                        rs.getString("coverCarte")
                );
                books.add(book);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching books: " + e.getMessage());
        }
        System.out.println(books.size() + " books found");


        return books;
}

public List<String>SELECT_COVER_FROM_MYREADS( String DB_URL, String DB_USER, String DB_PASS,int userId)
{
    List<String> covers = new ArrayList<>();
    String query = "SELECT coverCarte FROM myreads WHERE user_idUser=?";

    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);

         PreparedStatement stmt = conn.prepareStatement(query)) {
        stmt.setInt(1,userId );
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            String coverUrl = rs.getString("coverCarte");
            if (coverUrl != null && !coverUrl.isEmpty()) {
                covers.add(coverUrl);
            }
        }
    } catch (SQLException e) {
        System.err.println("Error fetching myreads covers: " + e.getMessage());
    }

    return covers;
}
    public List<Book> REZULTATE(String DB_URL, String DB_USER, String DB_PASS, String titlu, String autor) {
        List<Book> books = new ArrayList<>();
        // Folosim PreparedStatement cu parametri corecți pentru LIKE
        String query = "SELECT * FROM carte WHERE titluCarti LIKE ? OR autorCarte LIKE ?";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement stmt = connection.prepareStatement(query)) {

            // Setăm parametrii pentru căutare (adaugăm % pentru wildcard)
            stmt.setString(1, "%" + titlu + "%");
            stmt.setString(2, "%" + autor + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Book book = new Book(
                            rs.getInt("idCarte"),
                            rs.getString("titluCarti"),  // Corectat din "titluCarti"
                            rs.getString("autorCarte"),
                            rs.getString("descriere"),
                            rs.getString("genCarte"),
                            rs.getInt("numarCarte"),
                            rs.getString("coverCarte")
                    );
                    books.add(book);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching books: " + e.getMessage());
        }

        System.out.println(books.size() + " books found");
        return books;
    }

  public void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Pane Changed");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
