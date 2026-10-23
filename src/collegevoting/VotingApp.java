
package collegevoting;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class VotingApp extends Application {

    private final String BLACK = "#0A0A0D";
    private final String PANEL = "#17131F";
    private final String AMETHYST = "#6633A8";
    private final String CYAN = "#00E5FF";
    private final String WHITE = "#F5F3FA";
    private final String MUTED = "#AAA4B8";

    private BorderPane root;
    private VBox content;
    private Label pageTitle;

    @Override
    public void start(Stage stage) {

        root = new BorderPane();
        root.setStyle("-fx-background-color: " + BLACK + ";");

        VBox sidebar = new VBox(14);
        sidebar.setPadding(new Insets(28, 18, 24, 18));
        sidebar.setPrefWidth(225);
        sidebar.setStyle("-fx-background-color: #100D16;");

        Label brand = new Label("CAMPUS\nVOTE");
        brand.setStyle(
            "-fx-font-size: 27px; -fx-font-weight: bold;"
            + "-fx-text-fill: " + CYAN + ";"
            + "-fx-padding: 0 0 25 5;"
        );

        sidebar.getChildren().add(brand);

        addNav(sidebar, "Dashboard", () -> showDashboard());
        addNav(sidebar, "Students", () -> showStudents());
        addNav(sidebar, "Candidates", () -> showCandidates());
        addNav(sidebar, "Cast Vote", () -> showVoting());
        addNav(sidebar, "Results", () -> showResults());

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        sidebar.getChildren().add(spacer);

        Label footer = new Label("COLLEGE ELECTIONS");
        footer.setStyle("-fx-text-fill: " + MUTED
                + "; -fx-font-size: 10px;");
        sidebar.getChildren().add(footer);

        root.setLeft(sidebar);

        content = new VBox(20);
        content.setPadding(new Insets(30));
        root.setCenter(content);

        showDashboard();

        Scene scene = new Scene(root, 1150, 750);
        stage.setTitle("CampusVote | College Voting System");
        stage.setMinWidth(900);
        stage.setMinHeight(600);
        stage.setScene(scene);
        stage.show();
    }

    private void addNav(VBox sidebar, String text, Runnable action) {
        Button button = new Button(text);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setAlignment(Pos.CENTER_LEFT);
        button.setPadding(new Insets(13, 15, 13, 15));
        button.setStyle(
            "-fx-background-color: transparent;"
            + "-fx-text-fill: " + WHITE + ";"
            + "-fx-font-size: 14px;"
            + "-fx-background-radius: 8;"
            + "-fx-cursor: hand;"
        );

        button.setOnMouseEntered(e ->
            button.setStyle(
                "-fx-background-color: " + AMETHYST + ";"
                + "-fx-text-fill: white;"
                + "-fx-font-size: 14px;"
                + "-fx-background-radius: 8;"
            )
        );

        button.setOnMouseExited(e ->
            button.setStyle(
                "-fx-background-color: transparent;"
                + "-fx-text-fill: " + WHITE + ";"
                + "-fx-font-size: 14px;"
                + "-fx-background-radius: 8;"
            )
        );

        button.setOnAction(e -> action.run());
        sidebar.getChildren().add(button);
    }

    private void page(String title, String subtitle) {
        content.getChildren().clear();

        HBox heading = new HBox();
        heading.setAlignment(Pos.CENTER_LEFT);

        VBox titles = new VBox(6);
        pageTitle = new Label(title);
        pageTitle.setStyle(
            "-fx-text-fill: " + WHITE + ";"
            + "-fx-font-size: 28px; -fx-font-weight: bold;"
        );

        Label sub = new Label(subtitle);
        sub.setStyle("-fx-text-fill: " + MUTED + ";");

        titles.getChildren().addAll(pageTitle, sub);
        heading.getChildren().add(titles);
        content.getChildren().add(heading);
    }

    private Label label(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-text-fill: " + WHITE + "; -fx-font-size: 13px;");
        return l;
    }

    private TextField field(String prompt) {
        TextField f = new TextField();
        f.setPromptText(prompt);
        f.setStyle(
            "-fx-background-color: #211B2B;"
            + "-fx-text-fill: white;"
            + "-fx-prompt-text-fill: #92899F;"
            + "-fx-border-color: #453454;"
            + "-fx-border-radius: 6;"
            + "-fx-background-radius: 6;"
            + "-fx-padding: 11;"
        );
        return f;
    }

    private Button primary(String text) {
        Button b = new Button(text);
        b.setMaxWidth(Double.MAX_VALUE);
        b.setPadding(new Insets(11, 16, 11, 16));
        b.setStyle(
            "-fx-background-color: " + CYAN + ";"
            + "-fx-text-fill: #0A0A0D;"
            + "-fx-font-weight: bold;"
            + "-fx-background-radius: 7;"
            + "-fx-cursor: hand;"
        );
        return b;
    }

    private VBox card(String title) {
        VBox box = new VBox(14);
        box.setPadding(new Insets(20));
        box.setStyle(
            "-fx-background-color: " + PANEL + ";"
            + "-fx-background-radius: 12;"
            + "-fx-border-color: #352640;"
            + "-fx-border-radius: 12;"
        );

        Label heading = new Label(title);
        heading.setStyle(
            "-fx-text-fill: " + WHITE + ";"
            + "-fx-font-size: 17px; -fx-font-weight: bold;"
        );
        box.getChildren().add(heading);
        return box;
    }

    private void showDashboard() {
        page("Dashboard", "College election management overview");

        HBox cards = new HBox(15);
        cards.getChildren().addAll(
            statCard("Students", count("students")),
            statCard("Candidates", count("candidates")),
            statCard("Votes Cast", count("votes"))
        );

        VBox welcome = card("Welcome to CampusVote");
        Label info = new Label(
            "Manage students, register candidates, conduct voting, "
            + "and review election results from one place."
        );
        info.setWrapText(true);
        info.setStyle("-fx-text-fill: " + MUTED + ";");

        Button students = primary("Manage Students");
        students.setOnAction(e -> showStudents());

        Button candidates = primary("Manage Candidates");
        candidates.setOnAction(e -> showCandidates());

        Button results = primary("View Election Results");
        results.setOnAction(e -> showResults());

        welcome.getChildren().addAll(info, students, candidates, results);
        content.getChildren().addAll(cards, welcome);
    }

    private VBox statCard(String title, int value) {
        VBox box = card(title);

        Label number = new Label(String.valueOf(value));
        number.setStyle(
            "-fx-text-fill: " + CYAN + ";"
            + "-fx-font-size: 30px; -fx-font-weight: bold;"
        );

        box.getChildren().add(number);
        HBox.setHgrow(box, Priority.ALWAYS);
        box.setPrefWidth(200);
        return box;
    }

    private int count(String table) {
        if (!table.matches("students|candidates|votes")) {
            return 0;
        }

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(
                 "SELECT COUNT(*) FROM " + table)) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {
            error(e);
        }
        return 0;
    }

    private void showStudents() {
        page("Students", "Register and view students");

        TextField id = field("Student ID");
        TextField name = field("Full name");
        TextField dept = field("Department");

        VBox form = card("Add New Student");
        form.getChildren().addAll(
            label("Student ID"), id,
            label("Full Name"), name,
            label("Department"), dept
        );

        Button add = primary("Add Student");
        add.setOnAction(e -> {
            if (id.getText().isBlank() || name.getText().isBlank()
                    || dept.getText().isBlank()) {
                alert("Please fill in all fields.");
                return;
            }

            String sql = "INSERT INTO students "
                + "(student_id, full_name, department, password_hash) "
                + "VALUES (?, ?, ?, ?)";

            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setString(1, id.getText().trim());
                ps.setString(2, name.getText().trim());
                ps.setString(3, dept.getText().trim());
                ps.setString(4, "TEMP_" + id.getText().trim());
                ps.executeUpdate();

                alert("Student added successfully!");
                id.clear();
                name.clear();
                dept.clear();
                showStudents();

            } catch (Exception ex) {
                error(ex);
            }
        });

        form.getChildren().add(add);

        TableView<ObservableList<String>> table =
            createTable(new String[]{
                "Student ID", "Full Name", "Department"
            });

        loadTable(table,
            "SELECT student_id, full_name, department FROM students");

        Button refresh = primary("Refresh Students");
        refresh.setOnAction(e -> loadTable(table,
            "SELECT student_id, full_name, department FROM students"));

        content.getChildren().addAll(form, label("Registered Students"),
            table, refresh);
        VBox.setVgrow(table, Priority.ALWAYS);
    }

    private void showCandidates() {
        page("Candidates", "Manage candidates and election positions");

        TextField name = field("Candidate full name");
        TextField position = field("Position (e.g. Chairman)");
        TextField dept = field("Department");

        VBox form = card("Register Candidate");
        form.getChildren().addAll(
            label("Candidate Name"), name,
            label("Election Position"), position,
            label("Department"), dept
        );

        Button add = primary("Add Candidate");
        add.setOnAction(e -> {
            if (name.getText().isBlank()
                    || position.getText().isBlank()
                    || dept.getText().isBlank()) {
                alert("Please fill in all fields.");
                return;
            }

            String sql = "INSERT INTO candidates "
                + "(full_name, position, department) VALUES (?, ?, ?)";

            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setString(1, name.getText().trim());
                ps.setString(2, position.getText().trim());
                ps.setString(3, dept.getText().trim());
                ps.executeUpdate();

                alert("Candidate added successfully!");
                showCandidates();

            } catch (Exception ex) {
                error(ex);
            }
        });

        form.getChildren().add(add);

        TableView<ObservableList<String>> table =
            createTable(new String[]{
                "Candidate ID", "Name", "Position", "Department"
            });

        loadTable(table,
            "SELECT candidate_id, full_name, position, department "
            + "FROM candidates");

        Button refresh = primary("Refresh Candidates");
        refresh.setOnAction(e -> loadTable(table,
            "SELECT candidate_id, full_name, position, department "
            + "FROM candidates"));

        content.getChildren().addAll(form, label("Registered Candidates"),
            table, refresh);
        VBox.setVgrow(table, Priority.ALWAYS);
    }

    private void showVoting() {
        page("Cast Vote", "Submit a student's election vote");

        TextField studentId = field("Student ID");
        ComboBox<String> candidate = new ComboBox<>();
        candidate.setMaxWidth(Double.MAX_VALUE);
        candidate.setPromptText("Choose candidate");
        candidate.setStyle(
            "-fx-background-color: #211B2B;"
            + "-fx-text-fill: white;"
        );

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(
                 "SELECT candidate_id, full_name, position "
                 + "FROM candidates ORDER BY position, full_name")) {

            while (rs.next()) {
                candidate.getItems().add(
                    rs.getInt("candidate_id") + " - "
                    + rs.getString("full_name") + " ("
                    + rs.getString("position") + ")");
            }

        } catch (Exception e) {
            error(e);
        }

        VBox form = card("Submit Your Vote");
        form.getChildren().addAll(
            label("Student ID"), studentId,
            label("Candidate"), candidate
        );

        Button vote = primary("Confirm Vote");
        vote.setOnAction(e -> {
            if (studentId.getText().isBlank()
                    || candidate.getValue() == null) {
                alert("Enter your Student ID and choose a candidate.");
                return;
            }

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Submit your vote? This action cannot be undone.",
                ButtonType.YES, ButtonType.NO);
            confirm.setHeaderText("Confirm Vote");

            if (confirm.showAndWait().orElse(ButtonType.NO)
                    != ButtonType.YES) return;

            String selected = candidate.getValue();
            int dash = selected.indexOf(" - ");
            int candidateId;

            try {
                candidateId = Integer.parseInt(
                    selected.substring(0, dash));
            } catch (Exception ex) {
                alert("Invalid candidate selection.");
                return;
            }

            try (Connection con = DBConnection.getConnection()) {

                try (PreparedStatement check =
                    con.prepareStatement(
                        "SELECT student_id FROM students "
                        + "WHERE student_id = ?")) {

                    check.setString(1, studentId.getText().trim());

                    try (ResultSet rs = check.executeQuery()) {
                        if (!rs.next()) {
                            alert("Student ID not found.");
                            return;
                        }
                    }
                }

                try (PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO votes (student_id, candidate_id) "
                    + "VALUES (?, ?)")) {

                    ps.setString(1, studentId.getText().trim());
                    ps.setInt(2, candidateId);
                    ps.executeUpdate();
                    alert("Vote submitted successfully!");
                    studentId.clear();
                    showVoting();
                }

            } catch (Exception ex) {
                alert("Vote failed. The student may have already voted, "
                    + "or the candidate ID is invalid.\n"
                    + ex.getMessage());
            }
        });

        form.getChildren().add(vote);
        content.getChildren().add(form);
    }

    private void showResults() {
        page("Election Results", "Vote counts for each candidate");

        TableView<ObservableList<String>> table =
            createTable(new String[]{
                "Candidate", "Position", "Votes"
            });

        loadTable(table,
            "SELECT c.full_name, c.position, COUNT(v.vote_id) "
            + "FROM candidates c LEFT JOIN votes v "
            + "ON c.candidate_id = v.candidate_id "
            + "GROUP BY c.candidate_id, c.full_name, c.position "
            + "ORDER BY c.position, COUNT(v.vote_id) DESC");

        Button refresh = primary("Refresh Results");
        refresh.setOnAction(e -> loadTable(table,
            "SELECT c.full_name, c.position, COUNT(v.vote_id) "
            + "FROM candidates c LEFT JOIN votes v "
            + "ON c.candidate_id = v.candidate_id "
            + "GROUP BY c.candidate_id, c.full_name, c.position "
            + "ORDER BY c.position, COUNT(v.vote_id) DESC"));

        content.getChildren().addAll(table, refresh);
        VBox.setVgrow(table, Priority.ALWAYS);
    }

    private TableView<ObservableList<String>> createTable(
            String[] headers) {

        TableView<ObservableList<String>> table = new TableView<>();
        table.setColumnResizePolicy(
            TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        table.setPlaceholder(label("No records to display"));

        for (int i = 0; i < headers.length; i++) {
            final int index = i;
            TableColumn<ObservableList<String>, String> col =
                new TableColumn<>(headers[i]);

            col.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                    data.getValue().get(index)));

            table.getColumns().add(col);
        }

        table.setStyle(
            "-fx-background-color: " + PANEL + ";"
            + "-fx-control-inner-background: " + PANEL + ";"
            + "-fx-text-background-color: " + WHITE + ";"
            + "-fx-accent: " + AMETHYST + ";"
        );

        return table;
    }

    private void loadTable(TableView<ObservableList<String>> table,
                           String sql) {

        ObservableList<ObservableList<String>> data =
            FXCollections.observableArrayList();

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            int columns = rs.getMetaData().getColumnCount();

            while (rs.next()) {
                ObservableList<String> row =
                    FXCollections.observableArrayList();

                for (int i = 1; i <= columns; i++) {
                    row.add(rs.getString(i));
                }

                data.add(row);
            }

            table.setItems(data);

        } catch (Exception e) {
            error(e);
        }
    }

    private void alert(String message) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("CampusVote");
        a.setHeaderText(null);
        a.setContentText(message);
        a.showAndWait();
    }

    private void error(Exception e) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Database Error");
        a.setHeaderText("The operation could not be completed");
        a.setContentText(e.getMessage());
        a.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
