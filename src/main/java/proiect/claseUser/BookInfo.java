package proiect.claseUser;

public class BookInfo {

    private final String status;

    private final Book book;

    public BookInfo(Book book, String status) {
        this.book = book;
        this.status = status;

    }

    public String getStatus() { return status; }
    public Book getBook() { return book; }
}