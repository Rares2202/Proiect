package proiect;

/**
 * The abstract class `book` defines a model for representing the basic properties
 * and functionalities relevant to books in the system. This class serves as a
 * parent for more specific implementations of books.
 */
public abstract class book {
    String id;
    /**
     * Represents the name of the book. This field is intended to hold the title
     * or identifier of a book in the system.
     */
    public String book_name;
    /**
     * Represents the name of the author of the book. This field stores the full name
     * of the individual who authored the book, providing essential metadata for
     * identification, cataloging, or display purposes in the context of the system.
     */
    public String author_name;
    /**
     * Represents the genre or category name of the book.
     * This variable is intended to store a descriptive string
     * that identifies the genre or classification of the book,
     * such as "Fiction", "History", or "Science".
     */
    public String gen_name;
    /**
     * Represents the unique identifier or reference number associated with a book.
     * This attribute may be used to store information such as an ISBN, library
     * tracking number, or any other unique numeric or alphanumeric code that
     * identifies a specific book.
     */
    public String number;

    public String getBookId() {
        return id;
    }

    public String getBookName() {
        return book_name;
    }

    void f() {}

}
