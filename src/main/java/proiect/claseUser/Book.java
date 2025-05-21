package proiect.claseUser;

import java.sql.*;

/**
 * Represents a Book with various attributes such as id, title, author, description, genre,
 * and a cover image URL. This class provides methods to retrieve book attributes and
 * initialize the book with data from a database based on a cover URL.
 */
public class Book {
    private int id;
    private String title;
    private String author;
    private String description;
    private String genre;
    private String coverUrl;

    /**
     * Constructs a Book object with the specified details.
     *
     * @param id          the unique identifier of the book.
     * @param title       the title of the book.
     * @param author      the author of the book.
     * @param description a brief description or summary of the book.
     * @param genre       the genre or category of the book.
     * @param coverUrl    the URL of the book's cover image.
     */
    public Book(int id, String title, String author, String description,
                String genre, String coverUrl) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.description = description;
        this.genre = genre;
        this.coverUrl = coverUrl;
    }


    /**
     * Retrieves the unique identifier of the book.
     *
     * @return the integer representing the ID of the book.
     */
    public int getId() {
        return id;
    }

    /**
     * Retrieves the title of the book.
     *
     * @return the string representing the title of the book.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Retrieves the author of the book.
     *
     * @return the string representing the author of the book.
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Retrieves the description of the book.
     *
     * @return the string representing the description of the book.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Retrieves the genre of the book.
     *
     * @return the string representing the genre of the book.
     */
    public String getGenre() {
        return genre;
    }

    /**
     * Retrieves the cover URL of the book.
     *
     * @return the string representing the cover URL of the book.
     */
    public String getCoverUrl() {
        return coverUrl;
    }

    /**
     * Fetches book data from the database using the given cover URL and initializes the book instance with the retrieved values.
     *
     * @param URL the cover URL used to query the database for book information.
     * @return the current instance of the Book initialized with the retrieved data, or partially initialized if an error occurs or no data exists for the provided URL.
     */
    public Book initializare(String URL)
    {

        String DB_URL = "jdbc:mysql://localhost:3306/mydb";
        String DB_USER = "root";
        String DB_PASSWORD = "simone";
        String query = "SELECT idCarte, titluCarti, autorCarte, descriere, genCarte, numarCarte, coverCarte FROM carte WHERE coverCarte=?";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, URL);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                   this.id = resultSet.getInt("idCarte");
                   this.title = resultSet.getString("titluCarti");
                   this.author = resultSet.getString("autorCarte");
                   this.description = resultSet.getString("descriere");
                   this.genre = resultSet.getString("genCarte");
                    this.coverUrl = resultSet.getString("coverCarte");

                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }



        return this;
    }

}