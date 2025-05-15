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
    /**
     * <li>Nu are usage dar las o pentru un eventual debugging</li>
     */
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
                "WHERE User_idUser = ? AND status = 'REZERVAT'";

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
            String status = "REZERVAT";
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
    public void UPDATE_USERPREF(String DB_URL, String DB_USER, String DB_PASS, int number, int userId, int genre) {
        String query = "UPDATE userpref SET number = number + ? WHERE user_idUser = ? AND preferinte_idpreferinte = ?";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            connection.setAutoCommit(false);

            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setInt(1, number);
                pstmt.setInt(2, userId);
                pstmt.setInt(3, genre);

                int rowsUpdated = pstmt.executeUpdate();  // executi efectiv update-ul

                connection.commit(); // finalizezi tranzactia

                if (rowsUpdated > 0) {
                    showAlert("Preferințele au fost actualizate!");
                } else {
                    showAlert("Nu s-a actualizat nimic!");
                }

            } catch (SQLException e) {
                connection.rollback();
                e.printStackTrace();
                showAlert("Eroare la actualizare: " + e.getMessage());
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Eroare conexiune DB: " + e.getMessage());
        }
    }

    public List<Book> SELECT_ALL_FROM_USERPREF(String DB_URL, String DB_USER, String DB_PASS, int userId) {
        String query = "SELECT c.* \n" +
                "FROM mydb.carte c \n" +
                "JOIN mydb.genuri g ON c.genCarte = g.genuri \n" +
                "JOIN mydb.userpref u ON g.idpreferinte = u.preferinte_idpreferinte\n" +
                "WHERE u.user_idUser = ?\n" +
                "AND c.coverCarte NOT IN (SELECT coverCarte FROM mydb.myreads WHERE user_idUser=? )\n" +
                "ORDER BY u.number DESC";

        List<Book> books = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            connection.setAutoCommit(false);

            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setInt(1, userId);
                pstmt.setInt(2, userId);

                try (ResultSet rs = pstmt.executeQuery()) {
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
                }
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                e.printStackTrace();
                showAlert("Error fetching books: " + e.getMessage());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database connection error: " + e.getMessage());
        }

        return books;
    }
    public int SEARCH_BY_COVER(String DB_URL, String DB_USER, String DB_PASS, String cover) {
        int preferinta_id = -1;
        String query = "SELECT g.idpreferinte FROM genuri g JOIN carte c ON c.genCarte = g.genuri WHERE c.coverCarte = ?";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, cover);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    preferinta_id = resultSet.getInt("idpreferinte");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();

        }

        return preferinta_id;
    }

    public void INSERT_INTO_USERPREF_GEN(String DB_URL, String DB_USER, String DB_PASS,
                                         int number, int userId, int preferinte) {

        String checkQuery = "SELECT 1 FROM userpref WHERE user_idUser = ? AND preferinte_idpreferinte = ?";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            connection.setAutoCommit(false);

            try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {
                checkStmt.setInt(1, userId);
                checkStmt.setInt(2, preferinte);
                System.out.println("Preferinte "+preferinte);

                boolean exists = checkStmt.executeQuery().next();
                System.out.println("EXISTĂ în baza de date? " + exists);
                if (exists) {
                    System.out.println("Fac update...");
                    UPDATE_USERPREF(DB_URL, DB_USER, DB_PASS, number, userId, preferinte);
                } else {
                    System.out.println("preferinta "+ preferinte);
                    String insertQuery = "INSERT INTO userpref(number, user_idUser, preferinte_idpreferinte) VALUES(?,?,?)";

                    try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {
                        insertStmt.setInt(1, number);
                        insertStmt.setInt(2, userId);
                        insertStmt.setInt(3, preferinte);

                        int rowsAffected = insertStmt.executeUpdate();
                        connection.commit();

                        if (rowsAffected > 0) {
                            showAlert("Ati introdus un nou gen!");
                            exists=false;
                        }
                        else
                        {
                            showAlert("Nu s-a introdus nimic!");
                        }
                    }
                }
            } catch (SQLException e) {
                connection.rollback();
                e.printStackTrace();
                showAlert("Eroare la adaugare in preferinte: " + e.getMessage());
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Eroare la conexiunea cu baza de date: " + e.getMessage());
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
public List<Review> SELECT_ALL_FROM_REVIEWS(String DB_URL, String DB_USER, String DB_PASS,int idCarte) {

            List<Review>reviews=new ArrayList<>();
            String query="SELECT * FROM review WHERE  Carte_idCarte = ?";
    try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
        connection.setAutoCommit(false);

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, idCarte);

            try (ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {
                    Review review=new Review(rs.getString("reviewText"),
                        rs.getInt("reviewRating"),
                        rs.getInt("User_idUser"),
                        rs.getInt("Carte_idCarte"));
                    reviews.add(review);
                }
            }
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            e.printStackTrace();
            showAlert("Error fetching books: " + e.getMessage());
        }
    } catch (SQLException e) {
        e.printStackTrace();
        showAlert("Database connection error: " + e.getMessage());
    }
return reviews;
}

    public void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Atention!");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
