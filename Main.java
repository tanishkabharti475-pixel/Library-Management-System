
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.beans.property.SimpleStringProperty;
import javafx.util.StringConverter;

import java.io.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class Main extends Application {

    private ArrayList<Book> books = new ArrayList<>();
    private User currentUser;
    private Stage stage;

    // START APPLICATION
    @Override
    public void start(Stage stage) {

        this.stage = stage;

        stage.setTitle("BookNest LMS");
        stage.setResizable(true);

        loadBooks();

        showLogin();

        stage.show();

        Platform.runLater(() -> stage.setMaximized(true));
    }

    // CHANGE SCENE
    public void changeScene(Scene scene) {

        stage.setScene(scene);

        Platform.runLater(() -> stage.setMaximized(true));
    }

    // LOGIN SCREEN
    public void showLogin() {

        Scene scene = UI.loginScene(details -> {

            String id = details[0].trim();
            String pass = details[1].trim();

            System.out.println("Login attempt: " + id);

            if (id.equals("admin") && pass.equals("1234")) {

                currentUser = new Librarian(id, pass);

                System.out.println("Librarian login successful");

                showDashboard();

            } else if (id.equals("student") && pass.equals("1234")) {

                currentUser = new Student(id, pass);

                System.out.println("Student login successful");

                showDashboard();

            } else {

                showMessage("Invalid User ID or Password");
            }
        });

        changeScene(scene);
    }

    // DASHBOARD
    public void showDashboard() {

        Scene scene = UI.dashboardScene(
            currentUser,
            this::addBook,
            this::viewBooks,
            this::searchBook,
            this::issueBook,
            this::returnBook,
            this::deleteBook,
            () -> {
                currentUser = null;
                showLogin();
            }
        );

        changeScene(scene);
    }

    // ADD BOOK
    public void addBook() {

        if (!currentUser.canManageBooks()) {
            showMessage("You cannot add books");
            return;
        }

        try {

            String idText = getInput("Enter Book ID");

            if (idText == null || idText.trim().isEmpty()) {
                return;
            }

            int id = Integer.parseInt(idText);

            for (Book b : books) {

                if (b.getBookId() == id) {
                    showMessage("Book ID already exists");
                    return;
                }
            }

            String title = getInput("Enter Book Title");

            if (title == null || title.trim().isEmpty()) {
                return;
            }

            String author = getInput("Enter Author Name");

            if (author == null || author.trim().isEmpty()) {
                return;
            }

            books.add(new Book(id, title, author));

            saveBooks();

            showMessage("Book Added Successfully");

        } catch (NumberFormatException e) {

            showMessage("Please enter a valid Book ID");
        }
    }

    // VIEW BOOKS
    public void viewBooks() {

        if (books.isEmpty()) {
            showMessage("No Books Available");
            return;
        }

        TableView<Book> table = new TableView<>();

        TableColumn<Book, Integer> idColumn =
            new TableColumn<>("Book ID");

        idColumn.setCellValueFactory(
            new PropertyValueFactory<>("bookId")
        );

        TableColumn<Book, String> titleColumn =
            new TableColumn<>("Title");

        titleColumn.setCellValueFactory(
            new PropertyValueFactory<>("title")
        );

        TableColumn<Book, String> authorColumn =
            new TableColumn<>("Author");

        authorColumn.setCellValueFactory(
            new PropertyValueFactory<>("author")
        );

        TableColumn<Book, String> statusColumn =
            new TableColumn<>("Status");

        statusColumn.setCellValueFactory(data ->
            new SimpleStringProperty(
                data.getValue().isIssued() ? "Issued" : "Available"
            )
        );

        table.getColumns().addAll(
            idColumn, titleColumn, authorColumn, statusColumn
        );

        table.getItems().addAll(books);

        idColumn.setPrefWidth(100);
        titleColumn.setPrefWidth(250);
        authorColumn.setPrefWidth(250);
        statusColumn.setPrefWidth(150);

        Button backButton = new Button("Back");

        backButton.setOnAction(e -> showDashboard());

        Label heading = new Label("Book Collection");

        VBox layout = new VBox(15);

        layout.setPadding(new Insets(25));
        layout.setAlignment(Pos.CENTER);

        layout.getChildren().addAll(
            heading, table, backButton
        );

        changeScene(new Scene(layout, 1000, 650));
    }

    // SEARCH BOOK
    public void searchBook() {

        if (books.isEmpty()) {
            showMessage("No Books Available");
            return;
        }

        ComboBox<Book> bookList = new ComboBox<>();

        bookList.getItems().addAll(books);
        bookList.setEditable(true);
        bookList.setPromptText("Type or Select a Book");
        bookList.setPrefSize(320, 42);
        bookList.setMinSize(320, 42);
        bookList.setMaxSize(320, 42);

        bookList.setConverter(new StringConverter<Book>() {

            @Override
            public String toString(Book b) {

                if (b == null) {
                    return "";
                }

                return b.getBookId() + " - " + b.getTitle();
            }

            @Override
            public Book fromString(String text) {
                return null;
            }
        });

        Button searchButton = new Button("Search");
        Button backButton = new Button("Back");

        searchButton.setOnAction(e -> {

            Book selected = bookList.getValue();

            String text = bookList.getEditor().getText();

            if (selected == null && text != null) {

                for (Book b : books) {

                    if (text.equalsIgnoreCase(b.getTitle())
                        || text.equals(String.valueOf(b.getBookId()))
                        || text.equals(b.getBookId() + " - " + b.getTitle())) {

                        selected = b;
                        break;
                    }
                }
            }

            if (selected == null) {

                showMessage("Please select a book from the dropdown");
                return;
            }

            String status = selected.isIssued()
                ? "Issued" : "Available";

            showMessage(
                "Book Found!\n\n" +
                "Book ID: " + selected.getBookId() +
                "\nTitle: " + selected.getTitle() +
                "\nAuthor: " + selected.getAuthor() +
                "\nStatus: " + status
            );
        });

        backButton.setOnAction(e -> showDashboard());

        VBox layout = new VBox(18);

        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(25));

        layout.getChildren().addAll(
            new Label("Search Your Book"),
            bookList,
            searchButton,
            backButton
        );

        changeScene(new Scene(layout, 1000, 650));
    }

    // ISSUE BOOK
    public void issueBook() {

        if (!currentUser.canIssueReturnBooks()) {

            showMessage("You cannot issue books");
            return;
        }

        ComboBox<Book> bookList = new ComboBox<>();

        bookList.setPromptText("Select a Book");
        bookList.setPrefSize(320, 42);
        bookList.setMinSize(320, 42);
        bookList.setMaxSize(320, 42);

        bookList.setConverter(new StringConverter<Book>() {

            @Override
            public String toString(Book b) {

                return b == null ? "" :
                    b.getBookId() + " - " + b.getTitle();
            }

            @Override
            public Book fromString(String text) {
                return null;
            }
        });

        for (Book b : books) {

            if (!b.isIssued()) {
                bookList.getItems().add(b);
            }
        }

        if (bookList.getItems().isEmpty()) {

            showMessage("No Available Books");
            return;
        }

        Button issueButton = new Button("Issue Book");
        Button backButton = new Button("Back");

        issueButton.setOnAction(e -> {

            Book selected = bookList.getValue();

            if (selected == null) {

                showMessage("Please Select a Book");
                return;
            }

            selected.setIssued(true);
            selected.setIssueDate(LocalDate.now());

            saveBooks();

            showMessage("Book Issued Successfully");

            showDashboard();
        });

        backButton.setOnAction(e -> showDashboard());

        VBox layout = new VBox(18);

        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(25));

        layout.getChildren().addAll(
            new Label("Select Book to Issue"),
            bookList,
            issueButton,
            backButton
        );

        changeScene(new Scene(layout, 1000, 650));
    }

    // RETURN BOOK
    public void returnBook() {

        if (!currentUser.canIssueReturnBooks()) {

            showMessage("You cannot return books");
            return;
        }

        ComboBox<Book> bookList = new ComboBox<>();

        bookList.setPromptText("Select a Book");
        bookList.setPrefSize(320, 42);
        bookList.setMinSize(320, 42);
        bookList.setMaxSize(320, 42);

        bookList.setConverter(new StringConverter<Book>() {

            @Override
            public String toString(Book b) {

                return b == null ? "" :
                    b.getBookId() + " - " + b.getTitle();
            }

            @Override
            public Book fromString(String text) {
                return null;
            }
        });

        for (Book b : books) {

            if (b.isIssued()) {
                bookList.getItems().add(b);
            }
        }

        if (bookList.getItems().isEmpty()) {

            showMessage("No Issued Books");
            return;
        }

        Button returnButton = new Button("Return Book");
        Button backButton = new Button("Back");

        returnButton.setOnAction(e -> {

            Book selected = bookList.getValue();

            if (selected == null) {

                showMessage("Please Select a Book");
                return;
            }

            long days = 0;

            if (selected.getIssueDate() != null) {

                days = ChronoUnit.DAYS.between(
                    selected.getIssueDate(),
                    LocalDate.now()
                );
            }

            long fine = Math.max(0, days - 7) * 10;

            selected.setIssued(false);
            selected.setIssueDate(null);

            saveBooks();

            showMessage(
                "Book Returned Successfully\n" +
                "Days: " + days +
                "\nFine: Rs. " + fine
            );

            showDashboard();
        });

        backButton.setOnAction(e -> showDashboard());

        VBox layout = new VBox(18);

        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(25));

        layout.getChildren().addAll(
            new Label("Select Book to Return"),
            bookList,
            returnButton,
            backButton
        );

        changeScene(new Scene(layout, 1000, 650));
    }

    // DELETE BOOK
    public void deleteBook() {

        if (!currentUser.canManageBooks()) {

            showMessage("You cannot delete books");
            return;
        }

        try {

            String idText = getInput("Enter Book ID to Delete");

            if (idText == null || idText.trim().isEmpty()) {
                return;
            }

            int id = Integer.parseInt(idText);

            for (int i = 0; i < books.size(); i++) {

                Book b = books.get(i);

                if (b.getBookId() == id) {

                    if (b.isIssued()) {

                        showMessage("Return the Book Before Deleting");
                        return;
                    }

                    books.remove(i);

                    saveBooks();

                    showMessage("Book Deleted Successfully");
                    return;
                }
            }

            showMessage("Book Not Found");

        } catch (NumberFormatException e) {

            showMessage("Please enter a valid Book ID");
        }
    }

    // INPUT DIALOG
    public String getInput(String message) {

        TextInputDialog dialog = new TextInputDialog();

        dialog.setTitle("BookNest LMS");
        dialog.setHeaderText(message);

        return dialog.showAndWait().orElse(null);
    }

    // MESSAGE DIALOG
    public void showMessage(String message) {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle("BookNest LMS");
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }

    // SAVE BOOKS
    public void saveBooks() {

        try (ObjectOutputStream output = new ObjectOutputStream(
                new FileOutputStream("books.dat"))) {

            output.writeObject(books);

        } catch (IOException e) {

            showMessage("Error Saving Books");
        }
    }

    // LOAD BOOKS
    @SuppressWarnings("unchecked")
    public void loadBooks() {

        File file = new File("books.dat");

        if (!file.exists()) {
            return;
        }

        try (ObjectInputStream input = new ObjectInputStream(
                new FileInputStream(file))) {

            books = (ArrayList<Book>) input.readObject();

        } catch (IOException e) {

            System.out.println("Error Loading Books");

        } catch (ClassNotFoundException e) {

            System.out.println("Book Class Not Found");
        }
    }

    // MAIN METHOD
    public static void main(String[] args) {

        launch(args);
    }
}
