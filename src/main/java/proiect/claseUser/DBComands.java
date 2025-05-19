package proiect.claseUser;

import javafx.scene.control.Alert;

import java.sql.*;
import java.util.*;
import java.util.Date;

/**
 * The DBComands class provides a collection of methods for interacting with a database.
 * It primarily focuses on managing user preferences, book reservations, and retrieving
 * relevant data such as user-specific recommendations or borrowing history.
 * <p>
 * This class handles database connections, executes SQL queries, and processes
 * the resulting data into various return types like lists, maps, and Boolean values.
 * It includes operations for inserting, updating, and selecting data,
 * with safety mechanisms such as rollback on errors and validation of user constraints.
 */
public class DBComands {
    /**
     * Inserts user preferences into the database. The method processes a list of genre IDs
     * associated with a specific user and saves them into the database in a batch operation.
     * If an error occurs during the database interaction, a rollback is performed and an error
     * message is displayed via an alert.
     *
     * @param query         the SQL query to insert the user preferences
     * @param DB_URL        the URL of the database to which the connection should be established
     * @param DB_USER       the username for database authentication
     * @param DB_PASS       the password for database authentication
     * @param userId        the unique identifier of the user whose preferences are being saved
     * @param validGenreIds a list of valid genre IDs to be inserted as user preferences
     */
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

    /**
     * Executes an SQL query to fetch data from a database table and maps the resulting rows
     * into a Map String, Integer representation. The map keys correspond to the 'genuri' column
     * values, and the values correspond to the 'idpreferinte' column values from the result set.
     *
     * @param query   the SQL query to be executed against the database
     * @param DB_URL  the URL of the database where the query will be executed
     * @param DB_USER the username for database authentication
     * @param DB_PASS the password for database authentication
     * @return a map where keys are the 'genuri' (genre) column values and values are the
     * 'idpreferinte' (preference ID) column values; returns an empty map if no data
     * is found or if an error occurs during the operation
     */
    public Map<String, Integer> SELECT_FROM_GENURI(String query, String DB_URL, String DB_USER, String DB_PASS) {
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

    /**
     * Checks if a user has preferences stored in the database by executing a given query.
     * The method connects to the database using the provided connection parameters,
     * executes the query with the user ID as a parameter, and returns whether the query
     * finds any matching records.
     *
     * @param query   the SQL query to be executed, which is expected to check user preferences
     * @param DB_URL  the URL of the database to connect to
     * @param DB_USER the username for authenticating with the database
     * @param DB_PASS the password for authenticating with the database
     * @param id      the unique identifier of the user to check preferences for
     * @return true if the user has preferences; false otherwise
     */
    public Boolean USER_ARE_PREF(String query, String DB_URL, String DB_USER, String DB_PASS, int id) {
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
     * Method intended for debugging purposes.
     * Not used in production code.
     *
     * @param DB_URL  the database URL
     * @param DB_USER the database username
     * @param DB_PASS the database password
     * @return a list of Book objects fetched from the database
     */
    public List<Book> SELECT_ALL_FROM_BOOKS(String DB_URL, String DB_USER, String DB_PASS) {
        List<Book> books = new ArrayList<>();
        String query = "SELECT idCarte, titluCarti, autorCarte, descriere, genCarte, numarCarte, coverCarte FROM carte WHERE coverCarte IS NOT NULL";

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

    /**
     * Retrieves a list of cover URLs from the "myreads" database table for a given user ID.
     * This method establishes a connection to the database, executes a query to fetch
     * cover URLs corresponding to the specified user, and returns the results as a list.
     *
     * @param DB_URL  the URL of the database to connect to
     * @param DB_USER the username for database authentication
     * @param DB_PASS the password for database authentication
     * @param userId  the unique identifier of the user
     * @return a list of cover URL strings from the myreads table
     */
    public List<String> SELECT_COVER_FROM_MYREADS(String DB_URL, String DB_USER, String DB_PASS, int userId) {
        List<String> covers = new ArrayList<>();
        String query = "SELECT coverCarte FROM myreads WHERE user_idUser=?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);

             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
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

    /**
     * Retrieves a list of books from the database based on the provided title or author.
     * The method uses a SQL query with wildcard search to find books that match the
     * given title or author either partially or fully.
     *
     * @param DB_URL  the URL of the database to connect to
     * @param DB_USER the username for database authentication
     * @param DB_PASS the password for database authentication
     * @param titlu   the title of the book to search for (supports partial matches using wildcard)
     * @param autor   the author of the book to search for (supports partial matches using wildcard)
     * @return a list of Book objects that match the search criteria
     */
    public List<Book> REZULTATE(String DB_URL, String DB_USER, String DB_PASS, String titlu, String autor) {
        List<Book> books = new ArrayList<>();
        String query = "SELECT * FROM carte WHERE titluCarti LIKE ? OR autorCarte LIKE ?";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, "%" + titlu + "%");
            stmt.setString(2, "%" + autor + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Book book = new Book(
                            rs.getInt("idCarte"),
                            rs.getString("titluCarti"),
                            rs.getString("autorCarte"),
                            rs.getString("descriere"),
                            rs.getString("genCarte"),

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

    /**
     * Inserts a reservation for a book into the database for a specific user.
     * Before insertion, the method checks if the user has already reserved the book
     * or if the user has reached the maximum allowed limit for reservations.
     * If either condition is met, it shows an alert and terminates the operation.
     * Otherwise, it proceeds with the insertion.
     *
     * @param DB_URL  the URL of the database for establishing the connection
     * @param DB_USER the username for authenticating with the database
     * @param DB_PASS the password for authenticating with the database
     * @param userId  the unique identifier of the user attempting the reservation
     * @param titlu   the title of the book the user wants to reserve
     */
    public void insertIntoImprumuturi(String DB_URL, String DB_USER, String DB_PASS, int userId, String titlu) {
        String duplicateCheckQuery = "SELECT COUNT(*) AS count FROM cartiimprumutate " +
                "WHERE User_idUser = ? AND Carte_idCarte = " +
                "(SELECT idCarte FROM carte WHERE titluCarti = ?) " +
                "AND status IN ('INVENTAR', 'REZERVAT')";

        String countQuery = "SELECT COUNT(*) AS count FROM cartiimprumutate " +
                "WHERE User_idUser = ? AND status = 'REZERVAT'";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
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

            Date date = new Date();
            String status = "REZERVAT";
            int carteId;

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

    /**
     * Fetches a list of books along with their statuses that are associated with a specific user,
     * limited to the most recent six records, ordered by status and borrowing date.
     * The information is retrieved from the database and returned as a list of BookInfo objects.
     *
     * @param DB_URL  the URL of the database to connect to
     * @param DB_USER the username for database authentication
     * @param DB_PASS the password for database authentication
     * @param userId  the unique identifier of the user for whom the borrowed books are being retrieved
     * @return a list of BookInfo objects containing book details and their statuses
     */
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

                    Book book = new Book(0, null, null, null, null, null).initializare(coverUrl);
                    books.add(new BookInfo(book, status));
                }
            }
        } catch (SQLException e) {
            showAlert("Eroare la accesarea datelor: " + e.getMessage());
            e.printStackTrace();
        }
        for (BookInfo book : books) {
            Book carti = book.getBook();
            System.out.println(carti.getTitle() + " " + book.getStatus());
        }

        return books;
    }

    /**
     * Updates the user preferences in the database by incrementing the specified preference count
     * for a given user and genre id. If the operation is successful, a confirmation message is
     * displayed; otherwise, an error message is shown.
     *
     * @param DB_URL  the URL of the database to which the connection should be established
     * @param DB_USER the username for database authentication
     * @param DB_PASS the password for database authentication
     * @param number  the value to increment the preference count by
     * @param userId  the unique identifier of the user whose preference is being updated
     * @param genre   the unique identifier of the genre to update the preference for
     */
    public void UPDATE_USERPREF(String DB_URL, String DB_USER, String DB_PASS, int number, int userId, int genre) {
        String query = "UPDATE userpref SET number = number + ? WHERE user_idUser = ? AND preferinte_idpreferinte = ?";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            connection.setAutoCommit(false);

            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setInt(1, number);
                pstmt.setInt(2, userId);
                pstmt.setInt(3, genre);

                int rowsUpdated = pstmt.executeUpdate();

                connection.commit();

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

    /**
     * Retrieves a list of books from the database based on user preferences.
     * The method connects to the database using the provided connection parameters
     * and executes a query to fetch books that match the user's preferred genres.
     * Books that the user has already interacted with in the "myreads" table are excluded.
     * The results are sorted by the preference count in descending order.
     *
     * @param DB_URL  the URL of the database to connect to
     * @param DB_USER the username for database authentication
     * @param DB_PASS the password for database authentication
     * @param userId  the unique identifier of the user whose preferences are being used
     * @return a list of Book objects matching the user's preferences, excluding already read books
     */
    public List<Book> SELECT_ALL_FROM_USERPREF(String DB_URL, String DB_USER, String DB_PASS, int userId) {
        String query = """
                SELECT c.*\s
                FROM mydb.carte c\s
                JOIN mydb.genuri g ON c.genCarte = g.genuri\s
                JOIN mydb.userpref u ON g.idpreferinte = u.preferinte_idpreferinte
                WHERE u.user_idUser = ?
                AND c.coverCarte NOT IN (SELECT coverCarte FROM mydb.myreads WHERE user_idUser=? )
                ORDER BY u.number DESC""";

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

    /**
     * Retrieves the preference ID associated with a book's cover from the database.
     * Establishes a connection to the database, executes a query to search for the
     * genre preference ID that corresponds to the specified cover, and returns the result.
     *
     * @param DB_URL  the URL of the database to connect to
     * @param DB_USER the username for database authentication
     * @param DB_PASS the password for database authentication
     * @param cover   the book cover to be used as the search parameter
     * @return the preference ID associated with the specified cover, or -1 if no match is found
     */
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

    /**
     * Inserts or updates a user's preferences into the database.
     * If the specified preference already exists for the given user, the existing record is updated.
     * Otherwise, a new record is inserted into the database.
     *
     * @param DB_URL     the URL of the database to establish a connection
     * @param DB_USER    the username for database authentication
     * @param DB_PASS    the password for database authentication
     * @param number     the value to be set for the preference
     * @param userId     the unique identifier of the user
     * @param preferinte the preference ID to insert or update
     */
    public void INSERT_INTO_USERPREF_GEN(String DB_URL, String DB_USER, String DB_PASS,
                                         int number, int userId, int preferinte) {

        String checkQuery = "SELECT 1 FROM userpref WHERE user_idUser = ? AND preferinte_idpreferinte = ?";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            connection.setAutoCommit(false);

            try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {
                checkStmt.setInt(1, userId);
                checkStmt.setInt(2, preferinte);
                System.out.println("Preferinte " + preferinte);

                boolean exists = checkStmt.executeQuery().next();
                System.out.println("EXISTĂ în baza de date? " + exists);
                if (exists) {
                    System.out.println("Fac update...");
                    UPDATE_USERPREF(DB_URL, DB_USER, DB_PASS, number, userId, preferinte);
                } else {
                    System.out.println("preferinta " + preferinte);
                    String insertQuery = "INSERT INTO userpref(number, user_idUser, preferinte_idpreferinte) VALUES(?,?,?)";

                    try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {
                        insertStmt.setInt(1, number);
                        insertStmt.setInt(2, userId);
                        insertStmt.setInt(3, preferinte);

                        int rowsAffected = insertStmt.executeUpdate();
                        connection.commit();

                        if (rowsAffected > 0) {
                            showAlert("Ati introdus un nou gen!");
                        } else {
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

    /**
     * Deletes records from the "myreads" table in the database for a specific user
     * based on the provided list of cover images.
     *
     * @param DB_URL     the database URL used to establish the connection
     * @param DB_USER    the username used to authenticate with the database
     * @param DB_PASS    the password associated with the database user
     * @param userId     the ID of the user whose records are to be deleted
     * @param coverImage a list of cover image URLs corresponding to the records to be deleted
     */
    public void DELETE_FROM_MYREADS(String DB_URL, String DB_USER, String DB_PASS, int userId, List<String> coverImage) {
        String query = "DELETE FROM myreads WHERE user_idUser = ? AND  coverCarte = ?";
        for (String coverUrl : coverImage) {
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

    /**
     * Deletes a list of books from the "I'm Reading" section for a specified user in the database.
     *
     * @param DB_URL  the URL of the database to connect to
     * @param DB_USER the username for database authentication
     * @param DB_PASS the password for database authentication
     * @param userId  the ID of the user whose books are to be deleted
     * @param books   a list of books to be removed from the user's "I'm Reading" list
     */
    public void DELETE_FROM_IMREADING(String DB_URL, String DB_USER, String DB_PASS, int userId, List<Book> books) {
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

    /**
     * Retrieves a list of reviews from the database for a specified book ID.
     * The method connects to the specified database, executes a query to fetch
     * reviews associated with the given book ID, and returns the results as a list.
     *
     * @param DB_URL  the URL of the database to connect to
     * @param DB_USER the username for database authentication
     * @param DB_PASS the password for database authentication
     * @param idCarte the ID of the book for which reviews are to be fetched
     * @return a list of Review objects for the specified book ID
     */
    public List<Review> SELECT_ALL_FROM_REVIEWS(String DB_URL, String DB_USER, String DB_PASS, int idCarte) {

        List<Review> reviews = new ArrayList<>();
        String query = "SELECT * FROM review WHERE  Carte_idCarte = ?";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            connection.setAutoCommit(false);

            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setInt(1, idCarte);

                try (ResultSet rs = pstmt.executeQuery()) {

                    while (rs.next()) {
                        Review review = new Review(rs.getString("reviewText"),
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

    /**
     * Displays an alert dialog with the given message.
     *
     * @param message the text message to be displayed in the alert dialog
     */
    public void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Atention!");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}