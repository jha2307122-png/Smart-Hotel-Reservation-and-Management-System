package com.smarthotel;

import com.smarthotel.database.Database;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

import java.net.URL;

/** Application entry point. Loads the FXML login screen and provides a visible
 * startup error window instead of leaving the user with a blank stage. */
public class Main extends Application {
    private static final String BLUE = "#0B5ED7";
    private static final String DARK_BLUE = "#073B7A";

    @Override
    public void start(Stage stage) {
        try {
            Database.initialize();
            URL fxml = Main.class.getResource("/fxml/login.fxml");
            URL css = Main.class.getResource("/css/app.css");

            if (fxml == null) throw new IllegalStateException("Missing resource: /fxml/login.fxml");
            if (css == null) throw new IllegalStateException("Missing resource: /css/app.css");

            FXMLLoader loader = new FXMLLoader(fxml);
            Scene scene = new Scene(loader.load(), 1200, 760);
            scene.getStylesheets().add(css.toExternalForm());

            stage.setTitle("Smart Hotel | Reservation & Management System");
            stage.setMinWidth(1000);
            stage.setMinHeight(650);
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();
        } catch (Throwable error) {
            showStartupError(stage, error);
        }
    }

    private void showStartupError(Stage stage, Throwable error) {
        Label title = new Label("Smart Hotel could not start");
        title.setStyle("-fx-font-size:24px;-fx-font-weight:800;-fx-text-fill:" + DARK_BLUE + ";");
        Label intro = new Label("The application started, but a required component failed to load.");
        intro.setWrapText(true);
        intro.setStyle("-fx-text-fill:#475569;-fx-font-size:14px;");

        Label details = new Label(buildError(error));
        details.setWrapText(true);
        details.setStyle("-fx-font-family:Consolas;-fx-font-size:12px;-fx-text-fill:#7f1d1d;");

        ScrollPane scroll = new ScrollPane(details);
        scroll.setFitToWidth(true);
        scroll.setPrefViewportHeight(230);
        scroll.setStyle("-fx-background-color:#fff1f2;");

        Button close = new Button("Close");
        close.setStyle("-fx-background-color:" + BLUE + ";-fx-text-fill:white;-fx-font-weight:bold;-fx-padding:10 24;-fx-background-radius:8;");
        close.setOnAction(e -> stage.close());

        VBox root = new VBox(16, title, intro, scroll, close);
        root.setPadding(new Insets(32));
        root.setAlignment(Pos.CENTER_LEFT);
        root.setStyle("-fx-background-color:white;");
        Scene scene = new Scene(root, 760, 430);
        stage.setTitle("Smart Hotel | Startup Error");
        stage.setScene(scene);
        stage.show();

        error.printStackTrace();
    }

    private String buildError(Throwable error) {
        StringBuilder out = new StringBuilder();
        Throwable current = error;
        int depth = 0;
        while (current != null && depth++ < 5) {
            if (depth > 1) out.append("\nCaused by: ");
            out.append(current.getClass().getName()).append(": ").append(current.getMessage());
            current = current.getCause();
        }
        return out.toString();
    }

    @Override
    public void stop() {
        try { com.smarthotel.util.Async.shutdown(); } catch (Exception ignored) {}
    }

    public static void main(String[] args) {
        System.out.println("Smart Hotel starting...");
        launch(args);
    }
}
