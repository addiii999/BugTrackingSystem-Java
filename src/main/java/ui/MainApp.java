package ui;

import java.util.Optional;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Bug;
import model.User;
import service.BugService;

public class MainApp extends Application {

    private BugService service = new BugService();
    private TableView<Bug> table = new TableView<>();
    private User currentUser;  // STEP 2: Store logged user

    @Override
    public void start(Stage stage) {
        new LoginView().show(stage);
    }

    public void showDashboard(Stage stage, User user) {  // STEP 2: Accept user param
        
        this.currentUser = user;  // STEP 2: Store current user
        
        service.loadFromFile();

        // ===== Title =====
        Label title = new Label("Bug Tracking System");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        Button logoutBtn = new Button("Logout");
        logoutBtn.setStyle("-fx-background-color: #2c3e50; -fx-text-fill: white;");
        logoutBtn.setOnAction(e -> {
            service.saveToFile();
            new LoginView().show(stage);
        });

        Button fullScreenBtn = new Button("⛶");
        fullScreenBtn.setStyle("-fx-background-color: transparent; -fx-font-size: 16px;");
        fullScreenBtn.setOnAction(e -> {
            stage.setFullScreen(!stage.isFullScreen());
        });

        HBox topBar = new HBox(20, title, fullScreenBtn, logoutBtn);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(10));

        // ===== Search Section =====
        TextField searchField = new TextField();
        searchField.setPromptText("Search by ID or Title");

        Button searchBtn = new Button("Search");
        searchBtn.setStyle("-fx-background-color: #34495e; -fx-text-fill: white;");

        HBox searchSection = new HBox(10, searchField, searchBtn);
        searchSection.setAlignment(Pos.CENTER);

        // ===== Input Fields =====
        TextField idField = new TextField();
        idField.setPromptText("Bug ID");

        TextField titleField = new TextField();
        titleField.setPromptText("Title");

        TextField descField = new TextField();
        descField.setPromptText("Description");

        ComboBox<String> priorityBox = new ComboBox<>();
        priorityBox.getItems().addAll("Low", "Medium", "High");
        priorityBox.setPromptText("Priority");

        HBox form = new HBox(10, idField, titleField, descField, priorityBox);
        form.setAlignment(Pos.CENTER);
        form.setPadding(new Insets(15));

        // ===== Buttons =====
        Button createBtn = new Button("Create");
        Button deleteBtn = new Button("Delete");
        Button refreshBtn = new Button("Refresh");
        Button updateStatusBtn = new Button("Update Status");

        createBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");
        deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        refreshBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        updateStatusBtn.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white;");

        ComboBox<String> statusBox = new ComboBox<>();
        statusBox.getItems().addAll("Open", "In Progress", "Closed");
        statusBox.setPromptText("Select Status");

        HBox statusSection = new HBox(10, statusBox, updateStatusBtn);
        statusSection.setAlignment(Pos.CENTER);

        HBox buttons = new HBox(15, createBtn, deleteBtn, refreshBtn);
        buttons.setAlignment(Pos.CENTER);
        buttons.setPadding(new Insets(10));

        // ===== Assign Section =====
        TextField assignField = new TextField();
        assignField.setPromptText("Developer Name");

        Button assignBtn = new Button("Assign");
        assignBtn.setStyle("-fx-background-color: #8e44ad; -fx-text-fill: white;");

        HBox assignSection = new HBox(10, assignField, assignBtn);
        assignSection.setAlignment(Pos.CENTER);

        addHoverEffect(createBtn);
        addHoverEffect(deleteBtn);
        addHoverEffect(refreshBtn);
        addHoverEffect(assignBtn);
        addHoverEffect(updateStatusBtn);

        // STEP 4: Hide Admin Controls for Developer
        if (user.getRole().equals("DEVELOPER")) {
            createBtn.setVisible(false);
            deleteBtn.setVisible(false);
            assignBtn.setVisible(false);
        }

        // ===== Table Columns =====
        TableColumn<Bug, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().id).asObject());

        TableColumn<Bug, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().title));

        TableColumn<Bug, String> priorityCol = new TableColumn<>("Priority");
        priorityCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().priority));

        TableColumn<Bug, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().status));

        TableColumn<Bug, String> assignedCol = new TableColumn<>("Assigned To");
        assignedCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().assignedTo == null ? "" : data.getValue().assignedTo
                )
        );

        table.getColumns().addAll(idCol, titleCol, priorityCol, statusCol, assignedCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        table.setRowFactory(tv -> {
            TableRow<Bug> row = new TableRow<>();
            row.setOnMouseEntered(e -> {
                if (!row.isEmpty()) {
                    row.setStyle("-fx-background-color: #e8f0fe;");
                }
            });
            row.setOnMouseExited(e -> {
                row.setStyle("");
            });
            return row;
        });

        // Auto-fill form when row clicked (Pro UX)
        table.setOnMouseClicked(e -> {
            Bug selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                idField.setText(String.valueOf(selected.id));
                titleField.setText(selected.title);
                descField.setText(selected.description);
                priorityBox.setValue(selected.priority);
            }
        });

        // ===== Charts =====
        PieChart pieChart = new PieChart();
        updatePieChart(pieChart);

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        updateBarChart(barChart);

        // STEP 4: Hide charts for developers
        HBox charts = new HBox(20, pieChart, barChart);
        charts.setAlignment(Pos.CENTER);
        if (user.getRole().equals("DEVELOPER")) {
            charts.setVisible(false);
        }

        refreshTable(currentUser);  // STEP 3: Pass currentUser

        // ===== Button Actions =====
        createBtn.setOnAction(e -> {
            try {
                int id = Integer.parseInt(idField.getText());
                String t = titleField.getText();
                String d = descField.getText();
                String p = priorityBox.getValue();

                Bug bug = new Bug(id, t, d, p);
                service.createBug(bug);
                service.saveToFile();
                refreshTable(currentUser);  // STEP 3
                updatePieChart(pieChart);
                updateBarChart(barChart);

                idField.clear();
                titleField.clear();
                descField.clear();
                priorityBox.getSelectionModel().clearSelection();

            } catch (Exception ex) {
                showAlert("Invalid Input");
            }
        });

        // Confirm Before Delete (Real-world UX)
        deleteBtn.setOnAction(e -> {
            Bug selected = table.getSelectionModel().getSelectedItem();

            if (selected == null) {
                showAlert("Select a bug to delete");
                return;
            }

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirm Delete");
            confirm.setHeaderText("Delete Bug ID: " + selected.id);
            confirm.setContentText("Are you sure you want to delete this bug?");

            Optional<ButtonType> result = confirm.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                service.deleteBug(selected.id);
                service.saveToFile();
                refreshTable(currentUser);  // STEP 3
                updatePieChart(pieChart);
                updateBarChart(barChart);
            }
        });

        refreshBtn.setOnAction(e -> {
            refreshTable(currentUser);  // STEP 3
            updatePieChart(pieChart);
            updateBarChart(barChart);
        });

        updateStatusBtn.setOnAction(e -> {
            Bug selected = table.getSelectionModel().getSelectedItem();
            String newStatus = statusBox.getValue();

            // STEP 5: Restrict status change
            if (selected != null && newStatus != null) {
                if (currentUser.getRole().equals("DEVELOPER")
                        && !currentUser.getUsername().equals(selected.assignedTo)) {
                    showAlert("You can only update your assigned bugs");
                    return;
                }

                service.changeStatus(selected.id, newStatus);
                service.saveToFile();
                refreshTable(currentUser);  // STEP 3
                updatePieChart(pieChart);
                updateBarChart(barChart);
                statusBox.getSelectionModel().clearSelection();
            } else {
                showAlert("Select a bug and status");
            }
        });

        assignBtn.setOnAction(e -> {
            Bug selected = table.getSelectionModel().getSelectedItem();
            String dev = assignField.getText();

            if (selected != null && dev != null && !dev.isEmpty()) {
                service.assignBug(selected.id, dev);
                service.saveToFile();
                refreshTable(currentUser);  // STEP 3
                assignField.clear();
            } else {
                showAlert("Select a bug and enter developer name");
            }
        });

        searchBtn.setOnAction(e -> {
            String input = searchField.getText();

            if (input == null || input.isEmpty()) {
                refreshTable(currentUser);  // STEP 3
                return;
            }

            Bug result = service.searchBug(input);
            if (result != null) {
                table.getItems().setAll(result);
            } else {
                showAlert("No bug found");
            }
        });

        // ===== FINAL LAYOUT =====
        VBox root = new VBox(10, topBar, searchSection, form, buttons, statusSection, assignSection, charts, table);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #f4f6f8;");
        root.setSpacing(15);
        VBox.setVgrow(table, Priority.ALWAYS);

        Scene scene = new Scene(root, 1200, 900);
        stage.setMaximized(true);
        stage.setTitle("Bug Tracking System");
        stage.getIcons().add(
                new javafx.scene.image.Image(
                        getClass().getResourceAsStream("/icon.png")
                )
        );
        stage.setScene(scene);

        root.setOpacity(0);
        root.setTranslateY(30);

        FadeTransition fade = new FadeTransition(Duration.millis(400), root);
        fade.setFromValue(0);
        fade.setToValue(1);

        TranslateTransition slide = new TranslateTransition(Duration.millis(400), root);
        slide.setFromY(30);
        slide.setToY(0);

        ParallelTransition entrance = new ParallelTransition(fade, slide);
        entrance.play();

        stage.show();
    }

    // STEP 1: Updated refreshTable with user filtering
    private void refreshTable(User user) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(150), table);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);

        fadeOut.setOnFinished(e -> {
            if (user.getRole().equals("ADMIN")) {
                table.getItems().setAll(service.getAllBugs());
            } else {
                table.getItems().setAll(
                        service.getAllBugs().stream()
                                .filter(b -> user.getUsername().equals(b.assignedTo))
                                .toList()
                );
            }

            FadeTransition fadeIn = new FadeTransition(Duration.millis(150), table);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();
        });

        fadeOut.play();
    }

    private void updatePieChart(PieChart chart) {
        chart.setAnimated(true);
        var bugs = service.getAllBugs();

        long open = bugs.stream()
                .filter(b -> b.status.equals("Open"))
                .count();

        long inProgress = bugs.stream()
                .filter(b -> b.status.equals("In Progress"))
                .count();

        long closed = bugs.stream()
                .filter(b -> b.status.equals("Closed"))
                .count();

        chart.setData(FXCollections.observableArrayList(
                new PieChart.Data("Open", open),
                new PieChart.Data("In Progress", inProgress),
                new PieChart.Data("Closed", closed)
        ));
    }

    private void updateBarChart(BarChart<String, Number> chart) {
        chart.setAnimated(true);
        chart.getData().clear();
        
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Priority Count");

        try {
            long high = service.countByPriority("High");
            long medium = service.countByPriority("Medium");
            long low = service.countByPriority("Low");

            series.getData().add(new XYChart.Data<>("High", (Number) high));
            series.getData().add(new XYChart.Data<>("Medium", (Number) medium));
            series.getData().add(new XYChart.Data<>("Low", (Number) low));
        } catch (Exception e) {
            series.getData().add(new XYChart.Data<>("High", 0));
            series.getData().add(new XYChart.Data<>("Medium", 0));
            series.getData().add(new XYChart.Data<>("Low", 0));
        }

        chart.getData().add(series);
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(msg);
        alert.show();
    }

    private void addHoverEffect(Button btn) {
        btn.setOnMouseEntered(e -> {
            btn.setScaleX(1.05);
            btn.setScaleY(1.05);
        });

        btn.setOnMouseExited(e -> {
            btn.setScaleX(1);
            btn.setScaleY(1);
        });

        btn.setOnMousePressed(e -> {
            btn.setScaleX(0.95);
            btn.setScaleY(0.95);
        });

        btn.setOnMouseReleased(e -> {
            btn.setScaleX(1.05);
            btn.setScaleY(1.05);
        });
    }

    public static void main(String[] args) {
        launch();
    }
}
