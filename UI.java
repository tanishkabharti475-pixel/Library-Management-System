
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.function.Consumer;

public class UI {

    private static final Color BLUE = Color.rgb(65, 130, 190);
    private static final Color LIGHT_BLUE = Color.rgb(235, 245, 255);
    private static final Color DARK = Color.rgb(45, 55, 65);

    private static final double FIELD_WIDTH = 300;
    private static final double FIELD_HEIGHT = 40;
    private static final double BUTTON_WIDTH = 190;
    private static final double BUTTON_HEIGHT = 45;

    // COMMON BUTTON DESIGN
    private static void styleButton(Button button, boolean primary) {
        button.setPrefSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        button.setMinSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        button.setMaxSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        button.setFont(Font.font("Arial", 14));

        if (primary) {
            button.setStyle(
                "-fx-background-color: #4182BE;" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 6;"
            );
        } else {
            button.setStyle(
                "-fx-background-color: #EBF5FF;" +
                "-fx-text-fill: #2D3741;" +
                "-fx-background-radius: 6;"
            );
        }
    }

    // COMMON FIELD DESIGN
    private static void styleField(Control field) {
        field.setPrefSize(FIELD_WIDTH, FIELD_HEIGHT);
        field.setMinSize(FIELD_WIDTH, FIELD_HEIGHT);
        field.setMaxSize(FIELD_WIDTH, FIELD_HEIGHT);
        field.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-background-radius: 5;"
        );
    }

    // LOGIN SCREEN
    public static Scene loginScene(Consumer<String[]> loginAction) {

        Label heading = new Label("BookNest");
        heading.setFont(Font.font("Arial", 30));
        heading.setTextFill(BLUE);

        Label subtitle = new Label("Library Management System");
        subtitle.setFont(Font.font("Arial", 15));
        subtitle.setTextFill(DARK);

        TextField userId = new TextField();
        userId.setPromptText("Enter User ID");
        styleField(userId);

        PasswordField password = new PasswordField();
        password.setPromptText("Enter Password");
        styleField(password);

        Button loginButton = new Button("Login");
        styleButton(loginButton, true);

        loginButton.setOnAction(e -> {

            String id = userId.getText().trim();
            String pass = password.getText().trim();

            if (id.isEmpty() || pass.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("BookNest");
                alert.setHeaderText(null);
                alert.setContentText(
                    "Please enter User ID and Password"
                );
                alert.showAndWait();
                return;
            }

            loginAction.accept(new String[]{id, pass});
        });

        VBox card = new VBox(16);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(35));
        card.setPrefWidth(390);
        card.setMaxWidth(390);

        card.getChildren().addAll(
            heading,
            subtitle,
            new Separator(),
            userId,
            password,
            loginButton
        );

        card.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 12;"
        );

        VBox root = new VBox(card);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(25));
        root.setStyle("-fx-background-color: #EBF5FF;");

        return new Scene(root, 1000, 650);
    }

    // DASHBOARD SCREEN
    public static Scene dashboardScene(
            User currentUser,
            Runnable addAction,
            Runnable viewAction,
            Runnable searchAction,
            Runnable issueAction,
            Runnable returnAction,
            Runnable deleteAction,
            Runnable logoutAction) {

        Label heading = new Label("BookNest LMS");
        heading.setFont(Font.font("Arial", 30));
        heading.setTextFill(BLUE);

        Label welcome = new Label(
            "Welcome, " + currentUser.getRole() + "!"
        );
        welcome.setFont(Font.font("Arial", 17));
        welcome.setTextFill(DARK);

        Button addButton = new Button("Add Book");
        Button viewButton = new Button("View Books");
        Button searchButton = new Button("Search Book");
        Button issueButton = new Button("Issue Book");
        Button returnButton = new Button("Return Book");
        Button deleteButton = new Button("Delete Book");
        Button logoutButton = new Button("Logout");

        Button[] buttons = {
            addButton, viewButton, searchButton,
            issueButton, returnButton, deleteButton
        };

        for (Button button : buttons) {
            styleButton(button, false);
        }

        styleButton(logoutButton, true);

        addButton.setDisable(!currentUser.canManageBooks());
        deleteButton.setDisable(!currentUser.canManageBooks());

        addButton.setOnAction(e -> addAction.run());
        viewButton.setOnAction(e -> viewAction.run());
        searchButton.setOnAction(e -> searchAction.run());
        issueButton.setOnAction(e -> issueAction.run());
        returnButton.setOnAction(e -> returnAction.run());
        deleteButton.setOnAction(e -> deleteAction.run());
        logoutButton.setOnAction(e -> logoutAction.run());

        GridPane buttonGrid = new GridPane();
        buttonGrid.setAlignment(Pos.CENTER);
        buttonGrid.setHgap(20);
        buttonGrid.setVgap(18);

        buttonGrid.add(addButton, 0, 0);
        buttonGrid.add(viewButton, 1, 0);
        buttonGrid.add(searchButton, 0, 1);
        buttonGrid.add(issueButton, 1, 1);
        buttonGrid.add(returnButton, 0, 2);
        buttonGrid.add(deleteButton, 1, 2);

        VBox root = new VBox(22);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));

        root.getChildren().addAll(
            heading,
            welcome,
            new Separator(),
            buttonGrid,
            logoutButton
        );

        root.setStyle("-fx-background-color: white;");

        return new Scene(root, 1000, 650);
    }
}
