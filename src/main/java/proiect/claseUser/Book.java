package proiect.claseUser;

import java.sql.*;

/**
 * The type Book.
 */
public class Book {
    private int id;
    private String title;
    private String author;
    private String description;
    private String genre;
    private String coverUrl;

    /**
     * Instantiates a new Book.
     *
     * @param id          the id
     * @param title       the title
     * @param author      the author
     * @param description the description
     * @param genre       the genre
     * @param coverUrl    the cover url
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
     * Gets id.
     *
     * @return the id
     */
    public int getId() { return id; }

    /**
     * Gets title.
     *
     * @return the title
     */
    public String getTitle() { return title; }

    /**
     * Gets author.
     *
     * @return the author
     */
    public String getAuthor() { return author; }

    /**
     * Gets description.
     *
     * @return the description
     */
    public String getDescription() { return description; }

    /**
     * Gets genre.
     *
     * @return the genre
     */
    public String getGenre() { return genre; }

    /**
     * Gets cover url.
     *
     * @return the cover url
     */
    public String getCoverUrl() { return coverUrl; }

    /**
     * Initializare book.
     *
     * @param URL the url
     * @return the book
     */
    public Book initializare(String URL)
    {

        String DB_URL = "jdbc:mysql://localhost:3306/mydb";
        String DB_USER = "root";
        String DB_PASSWORD = "root";
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