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

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Parent;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.net.URL;
import java.util.concurrent.atomic.AtomicBoolean;

/** Application entry point. Loads the Smart Hotel application with a branded
 * loading splash screen, application icons, and smooth transitions. */
public class Main extends Application {
    private static final String BLUE = "#0B5ED7";
    private static final String DARK_BLUE = "#073B7A";

    public static void applyAppIcon(Stage stage) {
        if (stage == null) return;
        try {
            var iconSq = Main.class.getResourceAsStream("/images/logo_square.png");
            if (iconSq != null) {
                stage.getIcons().add(new Image(iconSq));
            }
            var iconFull = Main.class.getResourceAsStream("/images/logo.png");
            if (iconFull != null) {
                stage.getIcons().add(new Image(iconFull));
            }
        } catch (Exception ignored) {}
    }

    @Override
    public void start(Stage stage) {
        applyAppIcon(stage);
        showSplashScreen(stage);
    }

    private void showSplashScreen(Stage mainStage) {
        try {
            Stage splashStage = new Stage(StageStyle.TRANSPARENT);
            applyAppIcon(splashStage);

            // Splash card layout
            VBox splashCard = new VBox(16);
            splashCard.setAlignment(Pos.CENTER);
            splashCard.setPadding(new Insets(34, 44, 30, 44));
            splashCard.setPrefSize(520, 360);

            var bgDark = Main.class.getResource("/images/bg_dark.png");
            String bgStyle = bgDark != null
                    ? "-fx-background-image: url('" + bgDark.toExternalForm() + "'); -fx-background-size: cover; -fx-background-position: center center;"
                    : "-fx-background-color: #07162C;";

            splashCard.setStyle(bgStyle + " -fx-background-color: #07162C; -fx-background-radius: 20; -fx-border-radius: 20; -fx-border-color: rgba(96, 165, 250, 0.4); -fx-border-width: 1.5; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.65), 30, 0, 0, 10);");

            // Brand Logo
            ImageView logoView = new ImageView();
            var logoWhite = Main.class.getResourceAsStream("/images/logo_white.png");
            if (logoWhite != null) {
                logoView.setImage(new Image(logoWhite));
                logoView.setFitHeight(92);
                logoView.setFitWidth(92);
                logoView.setPreserveRatio(true);
                logoView.setSmooth(true);
            }

            Label brandTitle = new Label("SMART HOTEL");
            brandTitle.setStyle("-fx-font-size: 27px; -fx-font-weight: 900; -fx-text-fill: white; -fx-letter-spacing: 2px;");

            Label brandSub = new Label("Reservation & Management System");
            brandSub.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #93c5fd;");

            VBox brandBox = new VBox(4, brandTitle, brandSub);
            brandBox.setAlignment(Pos.CENTER);

            // Progress bar and status indicator
            ProgressBar progressBar = new ProgressBar(0.12);
            progressBar.setPrefWidth(300);
            progressBar.setPrefHeight(7);
            progressBar.setStyle("-fx-accent: #3b82f6;");

            Label statusLabel = new Label("Initializing Smart Hotel System...");
            statusLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #cbd5e1;");

            VBox progressBox = new VBox(8, progressBar, statusLabel);
            progressBox.setAlignment(Pos.CENTER);

            splashCard.getChildren().addAll(logoView, brandBox, progressBox);

            StackPane splashRoot = new StackPane(splashCard);
            splashRoot.setPadding(new Insets(20));
            splashRoot.setStyle("-fx-background-color: transparent;");

            Scene splashScene = new Scene(splashRoot, 560, 400);
            splashScene.setFill(Color.TRANSPARENT);
            splashStage.setScene(splashScene);
            splashStage.centerOnScreen();
            splashStage.show();

            AtomicBoolean transitioned = new AtomicBoolean(false);
            Runnable finishSplash = () -> {
                if (transitioned.compareAndSet(false, true)) {
                    FadeTransition fade = new FadeTransition(Duration.millis(300), splashRoot);
                    fade.setFromValue(1.0);
                    fade.setToValue(0.0);
                    fade.setOnFinished(e -> {
                        splashStage.close();
                        openMainScreen(mainStage);
                    });
                    fade.play();
                }
            };

            // Allow user to click to immediately jump into login
            splashRoot.setOnMouseClicked(e -> finishSplash.run());

            Timeline timeline = new Timeline(
                new KeyFrame(Duration.millis(200), e -> {
                    progressBar.setProgress(0.35);
                    statusLabel.setText("Connecting to database...");
                }),
                new KeyFrame(Duration.millis(550), e -> {
                    try {
                        Database.initialize();
                        progressBar.setProgress(0.75);
                        statusLabel.setText("Loading hotel configuration...");
                    } catch (Throwable t) {
                        splashStage.close();
                        showStartupError(mainStage, t);
                    }
                }),
                new KeyFrame(Duration.millis(950), e -> {
                    progressBar.setProgress(1.0);
                    statusLabel.setText("System ready. Welcome!");
                }),
                new KeyFrame(Duration.millis(1250), e -> finishSplash.run())
            );
            timeline.play();

        } catch (Throwable error) {
            showStartupError(mainStage, error);
        }
    }

    private void openMainScreen(Stage stage) {
        try {
            URL fxml = Main.class.getResource("/fxml/login.fxml");
            URL css = Main.class.getResource("/css/app.css");

            if (fxml == null) throw new IllegalStateException("Missing resource: /fxml/login.fxml");
            if (css == null) throw new IllegalStateException("Missing resource: /css/app.css");

            FXMLLoader loader = new FXMLLoader(fxml);
            Parent root = loader.load();
            Scene scene = new Scene(root, 1200, 760);
            scene.getStylesheets().add(css.toExternalForm());

            stage.setTitle("Smart Hotel | Reservation & Management System");
            applyAppIcon(stage);
            stage.setMinWidth(1000);
            stage.setMinHeight(650);
            stage.setScene(scene);
            stage.centerOnScreen();

            root.setOpacity(0.0);
            stage.show();

            FadeTransition fadeIn = new FadeTransition(Duration.millis(350), root);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
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
