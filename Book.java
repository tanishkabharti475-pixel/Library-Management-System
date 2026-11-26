
import java.io.Serializable;
import java.time.LocalDate;

public class Book implements Serializable {

    private static final long serialVersionUID = 1L;

    private int bookId;
    private String title;
    private String author;
    private boolean issued;
    private LocalDate issueDate;

    // Constructor
    public Book(int bookId, String title, String author) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.issued = false;
        this.issueDate = null;
    }

    // Getter methods
    public int getBookId() {
        return bookId;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public boolean isIssued() {
        return issued;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    // Setter methods
    public void setIssued(boolean issued) {
        this.issued = issued;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }
}
