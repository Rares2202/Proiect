package proiect.claseUser;

import javafx.scene.control.Alert;

import java.sql.*;
import java.util.*;
import java.util.Date;

public class DBComands {

        public void INSERT_INTO_USERPREF(String query, String DB_URL, String DB_USER, String DB_PASS,
                                         int userId, List<Integer> validGenreIds) {

            try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {

                connection.setAutoCommit(false);

                try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                    for (Integer genreId : validGenreIds) {
                        pstmt.setInt(1, 3);
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
    public void insertIntoImprumuturi(String DB_URL, String DB_USER, String DB_PASS, int userId, String titlu) {
        // First check if user already has this book
        String duplicateCheckQuery = "SELECT COUNT(*) AS count FROM cartiimprumutate " +
                "WHERE User_idUser = ? AND Carte_idCarte = " +
                "(SELECT idCarte FROM carte WHERE titluCarti = ?) " +
                "AND status IN ('INVENTAR', 'REZERVAT')";

        // Check maximum limit query remains the same
        String countQuery = "SELECT COUNT(*) AS count FROM cartiimprumutate " +
                "WHERE User_idUser = ? AND status = 'INVENTAR'";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            // Check for duplicate book first
            try (PreparedStatement duplicateStmt = connection.prepareStatement(duplicateCheckQuery)) {
                duplicateStmt.setInt(1, userId);
                duplicateStmt.setString(2, titlu);

                try (ResultSet rs = duplicateStmt.executeQuery()) {
                    if (rs.next() && rs.getInt("count") > 0) {
                        showAlert("Ați rezervat deja această carte!");
                        return;
                    }
                }
            }

            // Then check maximum limit
            try (PreparedStatement countStmt = connection.prepareStatement(countQuery)) {
                countStmt.setInt(1, userId);

                try (ResultSet rs = countStmt.executeQuery()) {
                    if (rs.next()) {
                        int currentCount = rs.getInt("count");
                        if (currentCount >= 6) {
                            showAlert("Ați atins limita maximă de 6 rezervări simultane!");
                            return;
                        }
                    }
                }
            }

            // Proceed with insertion if checks pass
            Date date = new Date();
            String status = "INVENTAR";
            int carteId = -1;

            // Get book ID
            String query = "SELECT idCarte FROM carte WHERE titluCarti = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, titlu);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        carteId = resultSet.getInt("idCarte");
                    } else {
                        showAlert("Nu s-a găsit cartea!");
                        return;
                    }
                }

                // Insert the record
                query = "INSERT INTO cartiimprumutate(dataImprumut, User_idUser, Carte_idCarte, status) " +
                        "VALUES (?, ?, ?, ?)";

                connection.setAutoCommit(false);
                try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                    java.sql.Date sqlDate = new java.sql.Date(date.getTime());

                    pstmt.setDate(1, sqlDate);
                    pstmt.setInt(2, userId);
                    pstmt.setInt(3, carteId);
                    pstmt.setString(4, status);

                    pstmt.executeUpdate();
                    connection.commit();
                    showAlert("Rezervarea a fost făcută. Așteptați confirmarea.");
                } catch (SQLException e) {
                    connection.rollback();
                    showAlert("Eroare la inserarea înregistrării: " + e.getMessage());
                } finally {
                    connection.setAutoCommit(true);
                }
            }
        } catch (SQLException e) {
            showAlert("Eroare la conexiunea cu baza de date: " + e.getMessage());
        }
    }
    public List<BookInfo> IS_IN_USE(String DB_URL, String DB_USER, String DB_PASS, int userId) {
        List<BookInfo> books = new ArrayList<>();
        String query = "SELECT c.coverCarte, ci.status, c.titluCarti " +
                "FROM cartiimprumutate ci " +
                "JOIN carte c ON ci.Carte_idCarte = c.idCarte " +
                "WHERE ci.user_idUser = ? " +
                "ORDER BY ci.status DESC, ci.dataImprumut DESC " +
                "LIMIT 6";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, userId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String coverUrl = resultSet.getString("coverCarte");
                    String status = resultSet.getString("status");
                    String title = resultSet.getString("titluCarti");
                    Book book=new Book(0,null,null,null,null,0,null).initializare(coverUrl);
                    books.add(new BookInfo(book, status));
                }
            }
        } catch (SQLException e) {
            showAlert("Eroare la accesarea datelor: " + e.getMessage());
            e.printStackTrace();
        }
        for (BookInfo book : books) {
            Book carti=book.getBook();
            System.out.println(carti.getTitle()+" "+book.getStatus());
        }

        return books;
    }
    public void UPDATE_USERPREF(String DB_URL, String DB_USER, String DB_PASS, int userId,String genre,int number) {
        String query="UPDATE userpref up JOIN genuri g ON up.preferinte_idpreferinte = g.idpreferinte SET up.number = up.number+?   WHERE up.user_idUser = ? AND g.genuri = ?";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {

            connection.setAutoCommit(false);

            try (PreparedStatement pstmt = connection.prepareStatement(query)) {

                    pstmt.setInt(1, number);
                    pstmt.setInt(2, userId);
                    pstmt.setString(3, genre);
                    pstmt.addBatch();


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
    public void SELECT_ALL_FROM_USERPREF(String DB_URL, String DB_USER, String DB_PASS, int userId) {
            String query="SELECT * FROM userpref WHERE user_idUser=? ORDER BY number DESC";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {

            connection.setAutoCommit(false);

            try (PreparedStatement pstmt = connection.prepareStatement(query)) {

                pstmt.setInt(1, userId);
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
    public void INSERT_INTO_USERPREF_GEN( String DB_URL, String DB_USER, String DB_PASS,
                                     int userId, String genre) {
        String query="INSERT INTO userpref VALUES(?,?,?,?,?,?)";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {

            connection.setAutoCommit(false);

            try (PreparedStatement pstmt = connection.prepareStatement(query)) {

                    pstmt.setInt(1, 3);
                    pstmt.setInt(2, userId);
                    pstmt.setString(3, genre);
                    pstmt.addBatch();


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
    public void DELETE_FROM_MYREADS(String DB_URL, String DB_USER, String DB_PASS, int userId, List<String>coverImage) {
        String query = "DELETE FROM myreads WHERE user_idUser = ? AND  coverCarte = ?";
        for(String coverUrl : coverImage) {
            try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {

                preparedStatement.setInt(1, userId);
                preparedStatement.setString(2, coverUrl);

                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("Successfully deleted book from My Reads");
                } else {
                    System.out.println("No book found with that cover image in user's collection");
                }
            } catch (SQLException e) {
                System.err.println("Error deleting from My Reads: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void DELETE_FROM_IMREADING(String DB_URL, String DB_USER, String DB_PASS, int userId, List<Book>books) {
        String query = "DELETE FROM cartiimprumutate WHERE User_idUser = ? AND  Carte_idCarte = ?";
        for (Book book : books) {
            try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {

                preparedStatement.setInt(1, userId);
                preparedStatement.setInt(2, book.getId());

                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("Successfully deleted book from I'm Reading");
                } else {
                    System.out.println("No book found with that ID in user's currently reading list");
                }
            } catch (SQLException e) {
                System.err.println("Error deleting from I'm Reading: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }


    public void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Atention!");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
