package com.smarthotel.controller;

import com.smarthotel.dao.CustomerDao;
import com.smarthotel.service.AuthService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class RegisterController {
    @FXML private TextField name, email, phone, address, username;
    @FXML private PasswordField password;
    @FXML private Label message;
    private final AuthService auth = new AuthService();

    @FXML private void create() {
        try {
            String n = name.getText().trim(), em = email.getText().trim(), u = username.getText().trim(), p = password.getText();
            if (n.isBlank() || em.isBlank() || u.isBlank() || p.length() < 6) {
                message.setText("Full name, email, username and a 6+ character password are required.");
                return;
            }
            if (!em.contains("@") || !em.contains(".")) {
                message.setText("Enter a valid email address.");
                return;
            }
            int uid = auth.register(u, p, "CUSTOMER");
            new CustomerDao().create(uid, n, em, phone.getText().trim(), address.getText().trim());
            message.setText("Account created successfully. Return to Customer Login.");
            name.clear(); email.clear(); phone.clear(); address.clear(); username.clear(); password.clear();
        } catch (Exception e) {
            String msg = e.getMessage() == null ? "Could not create account." : e.getMessage();
            if (msg.toLowerCase().contains("unique")) msg = "Username or email already exists.";
            message.setText("Registration failed: " + msg);
        }
    }

    @FXML private void back() throws Exception {
        Stage stage = (Stage) name.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
        Scene scene = new Scene(loader.load(), 1200, 760);
        var css = getClass().getResource("/css/app.css");
        if (css != null) scene.getStylesheets().add(css.toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Smart Hotel | Login");
        stage.centerOnScreen();
    }
}
