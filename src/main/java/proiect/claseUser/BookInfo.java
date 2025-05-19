package proiect.claseUser;

/**
 * Represents information about a book, including its status and associated book details.
 * This class associates a {@link Book} instance with a specific status.
 */
public class BookInfo {

    private final String status;

    private final Book book;

    /**
     * Constructs a new BookInfo object with the specified book and status.
     *
     * @param book   the {@link Book} instance representing the book details
     * @param status the current status associated with the book, such as "available" or "checked out"
     */
    public BookInfo(Book book, String status) {
        this.book = book;
        this.status = status;

    }

    /**
     * Retrieves the current status associated with the book.
     *
     * @return the status as a String, representing the current state of the book (e.g., "available", "checked out").
     */
    public String getStatus() { return status; }

    /**
     * Retrieves the associated Book instance.
     *
     * @return the {@link Book} instance representing the details of the book.
     */
    public Book getBook() { return book; }
}