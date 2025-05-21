package proiect;

/**
 * The abstract class `book` defines a model for representing the basic properties
 * and functionalities relevant to books in the system. This class serves as a
 * parent for more specific implementations of books.
 */
public abstract class Book {
    String id;
    /**
     * Represents the name of the book. This field is intended to hold the title
     * or identifier of a book in the system.
     */
    public String book_name;
    /**
     * Represents the unique identifier or reference number associated with a book.
     * This attribute may be used to store information such as an ISBN, library
     * tracking number, or any other unique numeric or alphanumeric code that
     * identifies a specific book.
     */
    public String number;


}
