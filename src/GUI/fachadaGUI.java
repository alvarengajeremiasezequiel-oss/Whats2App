package GUI;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;

public class fachadaGUI extends Application {
    private clientController cc;
    private Stage primaryStage;

    public static void main(String args[]) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        this.cc = new clientController();
        this.primaryStage = stage;
        setAppIcon();
        showLogin();
    }

    private void setAppIcon() {
        try {
            File icon = new File("src/GUI/whats2appIcon.png");
            if (icon.isFile()) {
                primaryStage.getIcons().add(new javafx.scene.image.Image(icon.toURI().toString()));
            }
        } catch (Exception ignored) {
        }
    }

    public void showLogin() {
        LoginView login = new LoginView(this, cc);
        Scene scene = new Scene(login.getRoot(), 420, 360);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        primaryStage.setOnCloseRequest(e -> Platform.exit());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Whats2App - Iniciar sesión");
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    public void showRegister() {
        RegisterView reg = new RegisterView(this, cc);
        Scene scene = new Scene(reg.getRoot(), 420, 420);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        primaryStage.setOnCloseRequest(e -> Platform.exit());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Whats2App - Registro");
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    public void showMain(MainView mainView) {
        Scene scene = new Scene(mainView.getRoot(), 900, 600);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        primaryStage.setOnCloseRequest(e -> mainView.onExit());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Whats2App - " + cc.getUsername());
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    public void notification(String notif, boolean priority) {
        Platform.runLater(() -> {
            NotifView vn = new NotifView(notif, priority, primaryStage);
            vn.show();
        });
    }
}
