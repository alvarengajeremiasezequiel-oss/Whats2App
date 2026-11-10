package GUI;

import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class LoginView {
    private final VBox root;
    private final fachadaGUI fgui;
    private final clientController cc;

    public LoginView(fachadaGUI fgui, clientController cc) {
        this.fgui = fgui;
        this.cc = cc;

        Label title = new Label("Whats2App");
        title.getStyleClass().add("app-title");

        Label userLabel = new Label("Nombre de usuario");
        TextField userField = new TextField();
        userField.setPromptText("usuario");

        Label passLabel = new Label("Clave");
        PasswordField passField = new PasswordField();
        passField.setPromptText("contraseña");

        Button loginButton = new Button("Iniciar sesión");
        loginButton.setDefaultButton(true);
        loginButton.setOnAction(e -> onLogin(userField, passField));

        Button registerButton = new Button("Registrarse");
        registerButton.getStyleClass().add("button-secondary");
        registerButton.setOnAction(e -> fgui.showRegister());

        root = new VBox(12, title, userLabel, userField, passLabel, passField, loginButton, registerButton);
        root.setPadding(new Insets(24));
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("form-root");
        root.setMaxWidth(380);
    }

    private void onLogin(TextField userField, PasswordField passField) {
        String username = userField.getText().trim();
        String password = new String(passField.getText());
        if (username.isBlank() || password.isBlank()) {
            fgui.notification("Introduce usuario y contraseña", true);
            return;
        }

        MainView mainView = new MainView(fgui, cc);

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() {
                try {
                    cc.login(username, password);
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage());
                }
                return null;
            }
        };
        task.setOnSucceeded(e -> fgui.showMain(mainView));
        task.setOnFailed(e -> {
            Throwable t = task.getException();
            fgui.notification(t != null ? t.getMessage() : "Error al iniciar sesión", false);
        });
        new Thread(task).start();
    }

    public VBox getRoot() {
        return root;
    }
}
