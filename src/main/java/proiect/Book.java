package proiect;

public class Book {
    private int id;
    private String title;
    private String author;
    private String description;
    private String genre;
    private int copies;
    private String coverUrl;

    public Book(int id, String title, String author, String description,
                String genre, int copies, String coverUrl) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.description = description;
        this.genre = genre;
        this.copies = copies;
        this.coverUrl = coverUrl;
    }

    // Getters and setters
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getDescription() { return description; }
    public String getGenre() { return genre; }
    public int getCopies() { return copies; }
    public String getCoverUrl() { return coverUrl; }
}