package ui;

import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.User;

public class LoginView {

    public void show(Stage stage) {
        ComboBox<String> roleBox = new ComboBox<>();
        roleBox.getItems().addAll("Admin", "Developer");
        roleBox.setPromptText("Select Role");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        
        Button loginBtn = new Button("Login");

        roleBox.setOnAction(e -> {
            String selectedRole = roleBox.getValue();

            if ("Admin".equals(selectedRole)) {
                usernameField.setText("Aditya");
                usernameField.setEditable(false);
            } else {
                usernameField.clear();
                usernameField.setEditable(true);
            }
        });

        VBox root = new VBox(15, roleBox, usernameField, passwordField, loginBtn);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-padding: 40;");

        loginBtn.setOnAction(e -> {
            String role = roleBox.getValue();
            String username = usernameField.getText();
            String password = passwordField.getText();

            if (role == null) {
                showAlert("Please select role");
                return;
            }

            if (role.equals("Admin")) {

                if (username.equals("Aditya") && password.equals("Aditya111")) {
                    User user = new User(username, "ADMIN");
                    FadeTransition fadeOut = new FadeTransition(Duration.millis(300), stage.getScene().getRoot());
                    fadeOut.setFromValue(1.0);
                    fadeOut.setToValue(0.0);
                    fadeOut.setOnFinished(event -> {
                        new MainApp().showDashboard(stage, user);
                    });
                    fadeOut.play();
                } else {
                    showAlert("Invalid Admin Credentials");
                }

            } else {

                if (!username.isEmpty() && !password.isEmpty()) {
                    User user = new User(username, "DEVELOPER");
                    FadeTransition fadeOut = new FadeTransition(Duration.millis(300), stage.getScene().getRoot());
                    fadeOut.setFromValue(1.0);
                    fadeOut.setToValue(0.0);
                    fadeOut.setOnFinished(event -> {
                        new MainApp().showDashboard(stage, user);
                    });
                    fadeOut.play();
                } else {
                    showAlert("Enter username and password");
                }
            }
        });

        Scene scene = new Scene(root, 400, 300);
        stage.setScene(scene);
        stage.setTitle("Login - Bug Tracking System");
        stage.getIcons().add(
                new javafx.scene.image.Image(
                        getClass().getResourceAsStream("/icon.png")
                )
        );
        stage.show();
    }
    
    private void showAlert(String message) {
        Alert alert = new Alert(AlertType.ERROR, message);
        alert.showAndWait();
    }
}
