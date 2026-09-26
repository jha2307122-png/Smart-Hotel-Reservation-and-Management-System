package com.smarthotel.controller;

import com.smarthotel.model.User;
import com.smarthotel.service.AuthService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {
    @FXML private StackPane loginRoot;
    @FXML private VBox loginCard;
    @FXML private TextField username;
    @FXML private PasswordField password;
    @FXML private Label message, modeTitle, modeHint, demoLabel;
    @FXML private Button registerButton, adminTab, customerTab;

    private final AuthService auth = new AuthService();
    private String selectedRole = "ADMIN";

    @FXML
    public void initialize() {
        showAdmin();
        // Explicit width/height property bindings demonstrate responsive JavaFX design.
        loginCard.prefWidthProperty().bind(loginRoot.widthProperty().multiply(0.32));
        loginCard.maxWidthProperty().bind(loginRoot.widthProperty().multiply(0.40));
        loginCard.prefHeightProperty().bind(loginRoot.heightProperty().multiply(0.72));
    }

    @FXML
    private void showAdmin() {
        selectedRole = "ADMIN";
        modeTitle.setText("Admin / Staff Login");
        modeHint.setText("Authorized staff can access hotel management features.");
        demoLabel.setText("");
        registerButton.setVisible(false);
        registerButton.setManaged(false);
        if (!adminTab.getStyleClass().contains("active-tab")) adminTab.getStyleClass().add("active-tab");
        customerTab.getStyleClass().remove("active-tab");
        message.setText("");
    }

    @FXML
    private void showCustomer() {
        selectedRole = "CUSTOMER";
        modeTitle.setText("Customer Login");
        modeHint.setText("Customers must create an account before signing in.");
        demoLabel.setText("New customer? Click Create Customer Account below.");
        registerButton.setVisible(true);
        registerButton.setManaged(true);
        if (!customerTab.getStyleClass().contains("active-tab")) customerTab.getStyleClass().add("active-tab");
        adminTab.getStyleClass().remove("active-tab");
        message.setText("");
    }

    @FXML
    private void login() {
        try {
            String u = username.getText().trim();
            String p = password.getText();
            if (u.isBlank() || p.isBlank()) {
                message.setText("Enter username and password.");
                return;
            }
            User user = auth.login(u, p, selectedRole);
            if (user == null) {
                message.setText("Invalid " + ("ADMIN".equals(selectedRole) ? "admin/staff" : "customer") + " username or password.");
                return;
            }
            open(user);
        } catch (Exception e) {
            message.setText("Login failed: " + safeMessage(e));
        }
    }

    @FXML
    private void register() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/register.fxml"));
            Scene scene = new Scene(loader.load(), 1050, 720);
            applyCss(scene);
            Stage stage = (Stage) username.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Smart Hotel — Create Customer Account");
            stage.centerOnScreen();
        } catch (Exception e) {
            message.setText("Unable to open registration screen: " + safeMessage(e));
        }
    }

    private void open(User user) throws Exception {
        String file = "ADMIN".equals(user.role()) || "STAFF".equals(user.role())
                ? "/fxml/admin.fxml" : "/fxml/customer.fxml";
        FXMLLoader loader = new FXMLLoader(getClass().getResource(file));
        Scene scene = new Scene(loader.load(), 1320, 840);
        applyCss(scene);
        Object controller = loader.getController();
        if (controller instanceof SessionAware sessionAware) sessionAware.setUser(user);
        Stage stage = (Stage) username.getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("Smart Hotel — " + ("ADMIN".equals(user.role()) ? "Admin Dashboard" : "Customer Dashboard"));
        stage.centerOnScreen();
    }

    private void applyCss(Scene scene) {
        var css = getClass().getResource("/css/app.css");
        if (css != null) scene.getStylesheets().add(css.toExternalForm());
    }

    private String safeMessage(Exception e) {
        StringBuilder out = new StringBuilder();
        Throwable current = e;
        int depth = 0;
        while (current != null && depth++ < 6) {
            String m = current.getMessage();
            if (m != null && !m.isBlank()) {
                if (out.length() > 0) out.append(" | ");
                out.append(m);
            }
            current = current.getCause();
        }
        return out.length() == 0 ? "Please try again." : out.toString();
    }
}
