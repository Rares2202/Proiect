package proiect.claseUser;

public class BookInfo {

    private String status;

    private Book book;

    public BookInfo(Book book, String status) {
        this.book = book;
        this.status = status;

    }

    public String getStatus() { return status; }
    public Book getBook() { return book; }
}