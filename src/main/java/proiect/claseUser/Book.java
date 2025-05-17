package proiect.claseUser;

import java.sql.*;

public class Book {
    private int id;
    private String title;
    private String author;
    private String description;
    private String genre;
    private String coverUrl;

    public Book(int id, String title, String author, String description,
                String genre, String coverUrl) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.description = description;
        this.genre = genre;
        this.coverUrl = coverUrl;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getDescription() { return description; }
    public String getGenre() { return genre; }
    public String getCoverUrl() { return coverUrl; }

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