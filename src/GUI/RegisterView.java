package GUI;

import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class RegisterView {
    private final VBox root;
    private final fachadaGUI fgui;
    private final clientController cc;

    public RegisterView(fachadaGUI fgui, clientController cc) {
        this.fgui = fgui;
        this.cc = cc;

        Label title = new Label("Crear cuenta");
        title.getStyleClass().add("app-title");

        Label userLabel = new Label("Nombre de usuario");
        TextField userField = new TextField();
        userField.setPromptText("usuario");

        Label passLabel = new Label("Contraseña");
        PasswordField passField = new PasswordField();
        passField.setPromptText("contraseña");

        Label repeatLabel = new Label("Repita la contraseña");
        PasswordField repeatField = new PasswordField();
        repeatField.setPromptText("repetir contraseña");

        Button registerButton = new Button("Registrarse");
        registerButton.setDefaultButton(true);
        registerButton.setOnAction(e -> onRegister(userField, passField, repeatField));

        Button backButton = new Button("Volver");
        backButton.getStyleClass().add("button-secondary");
        backButton.setOnAction(e -> fgui.showLogin());

        root = new VBox(12, title, userLabel, userField, passLabel, passField, repeatLabel, repeatField, registerButton, backButton);
        root.setPadding(new Insets(24));
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("form-root");
        root.setMaxWidth(380);
    }

    private void onRegister(TextField userField, PasswordField passField, PasswordField repeatField) {
        String username = userField.getText().trim();
        String passw = new String(passField.getText());

        if (!passField.getText().equals(repeatField.getText())) {
            fgui.notification("Las contraseñas no coinciden", true);
            return;
        }
        if (username.isBlank() || passw.isBlank()) {
            fgui.notification("Rellena todos los campos", true);
            return;
        }

        MainView mainView = new MainView(fgui, cc);

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() {
                try {
                    cc.register(username, passw);
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage());
                }
                return null;
            }
        };
        task.setOnSucceeded(e -> fgui.showMain(mainView));
        task.setOnFailed(e -> {
            Throwable t = task.getException();
            fgui.notification(t != null ? t.getMessage() : "Error al registrarse", false);
        });
        new Thread(task).start();
    }

    public VBox getRoot() {
        return root;
    }
}
