
public abstract class User {

    private String userId;
    private String password;

    public User(String userId, String password) {
        this.userId = userId;
        this.password = password;
    }

    public String getUserId() {
        return userId;
    }

    public String getPassword() {
        return password;
    }

    public abstract String getRole();

    public abstract boolean canManageBooks();

    public abstract boolean canIssueReturnBooks();
}

// Student class
class Student extends User {

    public Student(String userId, String password) {
        super(userId, password);
    }

    @Override
    public String getRole() {
        return "Student";
    }

    @Override
    public boolean canManageBooks() {
        return false;
    }

    @Override
    public boolean canIssueReturnBooks() {
        return true;
    }
}

// Librarian class
class Librarian extends User {

    public Librarian(String userId, String password) {
        super(userId, password);
    }

    @Override
    public String getRole() {
        return "Librarian";
    }

    @Override
    public boolean canManageBooks() {
        return true;
    }

    @Override
    public boolean canIssueReturnBooks() {
        return true;
    }
}
