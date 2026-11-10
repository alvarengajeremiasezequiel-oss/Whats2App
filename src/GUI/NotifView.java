package GUI;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class NotifView {
    private final Stage stage;

    public NotifView(String mensaje, boolean priority, Stage owner) {
        Label msgLabel = new Label(mensaje);
        msgLabel.setWrapText(true);
        msgLabel.getStyleClass().add("notif-label");

        Button okButton = new Button("OK");
        okButton.setDefaultButton(true);

        VBox root = new VBox(16, msgLabel, okButton);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        root.getStyleClass().add("form-root");
        root.setMaxWidth(360);

        stage = new Stage();
        stage.initOwner(owner);
        stage.initModality(priority ? Modality.WINDOW_MODAL : Modality.NONE);
        stage.setTitle("Whats2App");

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        stage.setScene(scene);
        okButton.setOnAction(e -> stage.close());
    }

    public void show() {
        if (stage.getModality() == Modality.WINDOW_MODAL) {
            stage.showAndWait();
        } else {
            stage.show();
        }
    }
}
