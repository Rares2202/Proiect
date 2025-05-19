package proiect.claseUser;

/**
 * The type Book info.
 */
public class BookInfo {

    private final String status;

    private final Book book;

    /**
     * Instantiates a new Book info.
     *
     * @param book   the book
     * @param status the status
     */
    public BookInfo(Book book, String status) {
        this.book = book;
        this.status = status;

    }

    /**
     * Gets status.
     *
     * @return the status
     */
    public String getStatus() { return status; }

    /**
     * Gets book.
     *
     * @return the book
     */
    public Book getBook() { return book; }
}