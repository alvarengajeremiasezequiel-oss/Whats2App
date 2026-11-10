package GUI;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class MainView implements VistaPrincipal {
    private final fachadaGUI fgui;
    private final clientController cc;
    private final TabPane root;

    private final ObservableList<String> friendsList = FXCollections.observableArrayList();
    private final ObservableList<String> requestsList = FXCollections.observableArrayList();
    private final ObservableList<String> onlineFriendsList = FXCollections.observableArrayList();
    private final ObservableList<Mensaje> chatMsgs = FXCollections.observableArrayList();
    private final Map<String, Conversation> chats = Collections.synchronizedMap(new HashMap<>());

    private ListView<String> friendList;
    private ListView<String> friendReqList;
    private ListView<Mensaje> chat;
    private Label friendChatLabel;
    private Label youLabel;
    private Label RequestLabel;
    private Label addAmigoLabel;
    private TextField chatField;
    private TextField addAmigoField;
    private Button enviarMensajeButton;
    private Button unfriendButton;
    private Button aceptarReqButton;
    private Button rechazarReqButton;
    private Button addAmigoButton;
    private Button cambiarPasswButton;
    private PasswordField passwField;
    private PasswordField repeatPasswField;
    private PasswordField newPasswField;

    private int nFriends = 0;
    private int nOnlineFriends = 0;
    private int nRequests = 0;
    private boolean start = false;
    private boolean exiting = false;

    public MainView(fachadaGUI fgui, clientController cc) {
        this.fgui = fgui;
        this.cc = cc;
        cc.setVPrincipal(this);

        root = new TabPane();
        root.getStyleClass().add("main-tabpane");
        root.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        root.getTabs().addAll(buildHomeTab(), buildRequestsTab(), buildSettingsTab());
    }

    private Tab buildHomeTab() {
        friendList = new ListView<>(friendsList);
        friendList.setPrefWidth(220);
        friendList.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> list) {
                return new ListCell<String>() {
                    @Override
                    protected void updateItem(String value, boolean empty) {
                        super.updateItem(value, empty);
                        if (empty || value == null) {
                            setText(null);
                            return;
                        }
                        setText(value);
                        if (onlineFriendsList.contains(value)) {
                            setStyle("-fx-text-fill: #1a7f37;");
                        } else {
                            setStyle("-fx-text-fill: #6b6b6b;");
                        }
                    }
                };
            }
        });
        friendList.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> onFriendSelected(newV));

        unfriendButton = new Button("Eliminar amistad");
        unfriendButton.setDisable(true);
        unfriendButton.setOnAction(e -> onEliminarAmigo());

        VBox left = new VBox(10, friendList, unfriendButton);
        left.setPrefWidth(230);

        friendChatLabel = new Label("");
        friendChatLabel.getStyleClass().add("chat-friend-label");
        youLabel = new Label("Tú");
        youLabel.getStyleClass().add("chat-you-label");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox header = new HBox(10, friendChatLabel, spacer, youLabel);
        header.setAlignment(Pos.CENTER_LEFT);

        chat = new ListView<>(chatMsgs);
        chat.setCellFactory(new Callback<ListView<Mensaje>, ListCell<Mensaje>>() {
            @Override
            public ListCell<Mensaje> call(ListView<Mensaje> list) {
                return new ListCell<Mensaje>() {
                    private final Label label = new Label();
                    private final HBox box = new HBox(label);

                    @Override
                    protected void updateItem(Mensaje value, boolean empty) {
                        super.updateItem(value, empty);
                        if (empty || value == null) {
                            setGraphic(null);
                            return;
                        }
                        label.setText(value.getMsg());
                        label.setWrapText(true);
                        label.setMaxWidth(460);
                        box.setPadding(new Insets(3, 5, 3, 5));
                        if (value.getMiembro() == 1) {
                            label.getStyleClass().setAll("bubble", "bubble-mine");
                            box.setAlignment(Pos.CENTER_RIGHT);
                        } else {
                            label.getStyleClass().setAll("bubble", "bubble-other");
                            box.setAlignment(Pos.CENTER_LEFT);
                        }
                        setGraphic(box);
                    }
                };
            }
        });

        chatField = new TextField();
        chatField.setPromptText("Escribe un mensaje...");
        enviarMensajeButton = new Button("Enviar");
        enviarMensajeButton.setDisable(true);
        enviarMensajeButton.setOnAction(e -> onEnviarMensaje());
        chatField.setOnAction(e -> onEnviarMensaje());
        HBox input = new HBox(10, chatField, enviarMensajeButton);
        HBox.setHgrow(chatField, Priority.ALWAYS);

        VBox right = new VBox(10, header, chat, input);
        VBox.setVgrow(chat, Priority.ALWAYS);

        HBox home = new HBox(10, left, right);
        HBox.setHgrow(right, Priority.ALWAYS);
        home.setPadding(new Insets(10));

        return new Tab("Inicio", home);
    }

    private Tab buildRequestsTab() {
        friendReqList = new ListView<>(requestsList);
        friendReqList.setPrefWidth(220);
        friendReqList.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> onReqSelected(newV));

        RequestLabel = new Label("");
        RequestLabel.setWrapText(true);
        aceptarReqButton = new Button("Aceptar");
        rechazarReqButton = new Button("Rechazar");
        aceptarReqButton.setDisable(true);
        rechazarReqButton.setDisable(true);
        aceptarReqButton.setOnAction(e -> onAceptarReq());
        rechazarReqButton.setOnAction(e -> onRechazarReq());

        HBox reqButtons = new HBox(10, aceptarReqButton, rechazarReqButton);
        VBox reqRight = new VBox(10, RequestLabel, reqButtons);
        reqRight.setPadding(new Insets(6, 0, 6, 6));

        HBox reqPanel = new HBox(10, friendReqList, reqRight);
        HBox.setHgrow(reqRight, Priority.ALWAYS);
        VBox.setVgrow(reqPanel, Priority.ALWAYS);

        addAmigoLabel = new Label("Añadir amigo");
        addAmigoField = new TextField();
        addAmigoField.setPromptText("nombre de usuario");
        addAmigoButton = new Button("Enviar solicitud");
        addAmigoButton.setOnAction(e -> onEnviarSolicitudAmistad());
        addAmigoField.setOnAction(e -> onEnviarSolicitudAmistad());
        HBox addInput = new HBox(10, addAmigoField, addAmigoButton);
        HBox.setHgrow(addAmigoField, Priority.ALWAYS);

        VBox container = new VBox(15, reqPanel, addAmigoLabel, addInput);
        container.setPadding(new Insets(10));

        return new Tab("Solicitudes de amistad", container);
    }

    private Tab buildSettingsTab() {
        Label title = new Label("Cambiar contraseña");
        title.getStyleClass().add("settings-title");

        Label pLabel = new Label("Contraseña");
        Label rpLabel = new Label("Repita la contraseña");
        Label npLabel = new Label("Nueva contraseña");
        passwField = new PasswordField();
        repeatPasswField = new PasswordField();
        newPasswField = new PasswordField();

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);
        grid.addRow(0, pLabel, passwField);
        grid.addRow(1, rpLabel, repeatPasswField);
        grid.addRow(2, npLabel, newPasswField);
        GridPane.setHgrow(passwField, Priority.ALWAYS);
        GridPane.setHgrow(repeatPasswField, Priority.ALWAYS);
        GridPane.setHgrow(newPasswField, Priority.ALWAYS);

        cambiarPasswButton = new Button("Confirmar");
        cambiarPasswButton.setOnAction(e -> onCambiarPassw());

        VBox settings = new VBox(15, title, grid, cambiarPasswButton);
        settings.setAlignment(Pos.CENTER);
        settings.setPadding(new Insets(20));

        return new Tab("Ajustes", settings);
    }

    private void onFriendSelected(String selected) {
        if (selected == null) {
            unfriendButton.setDisable(true);
            enviarMensajeButton.setDisable(true);
            friendChatLabel.setText("");
            youLabel.setText("");
            chatMsgs.clear();
            return;
        }
        unfriendButton.setDisable(false);
        enviarMensajeButton.setDisable(false);
        friendChatLabel.setText(selected);
        youLabel.setText("Tú");
        refreshChat();
    }

    private void refreshChat() {
        String selected = friendList.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        Conversation convo;
        synchronized (chats) {
            convo = chats.get(selected);
        }
        if (convo == null) return;
        chatMsgs.setAll(convo.getMensajes());
        if (!chatMsgs.isEmpty()) chat.scrollTo(chatMsgs.size() - 1);
    }

    private void onReqSelected(String selected) {
        if (selected == null) {
            RequestLabel.setText("");
            aceptarReqButton.setDisable(true);
            rechazarReqButton.setDisable(true);
        } else {
            RequestLabel.setText("El usuario " + selected + " te ha mandado una solicitud de amistad");
            aceptarReqButton.setDisable(false);
            rechazarReqButton.setDisable(false);
        }
    }

    private void onAceptarReq() {
        String usuario = friendReqList.getSelectionModel().getSelectedItem();
        if (usuario == null) return;
        try {
            cc.acceptReq(usuario);
            RequestLabel.setText("");
            aceptarReqButton.setDisable(true);
            rechazarReqButton.setDisable(true);
        } catch (Exception e) {
            fgui.notification(e.getMessage(), true);
        }
    }

    private void onRechazarReq() {
        String usuario = friendReqList.getSelectionModel().getSelectedItem();
        if (usuario == null) return;
        try {
            cc.rejectReq(usuario);
            RequestLabel.setText("");
            aceptarReqButton.setDisable(true);
            rechazarReqButton.setDisable(true);
        } catch (Exception e) {
            fgui.notification(e.getMessage(), true);
        }
    }

    private void onEnviarSolicitudAmistad() {
        try {
            String usuario = addAmigoField.getText().trim();
            if (usuario.isBlank()) return;
            cc.befriend(usuario);
            addAmigoField.clear();
        } catch (Exception e) {
            fgui.notification(e.getMessage(), true);
        }
    }

    private void onEnviarMensaje() {
        try {
            String selected = friendList.getSelectionModel().getSelectedItem();
            if (selected == null) return;
            if (!onlineFriendsList.contains(selected)) return;
            if (chatField.getText().isBlank()) return;
            String msg = chatField.getText();
            Conversation convo;
            synchronized (chats) {
                convo = chats.get(selected);
            }
            if (convo == null) return;
            convo.addMensaje(cc.getUsername(), msg);
            cc.msg(selected, msg);
            chatField.clear();
            refreshChat();
        } catch (Exception e) {
            fgui.notification(e.getMessage(), true);
        }
    }

    private void onEliminarAmigo() {
        String selected = friendList.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        try {
            cc.unfriend(selected);
        } catch (Exception e) {
            fgui.notification(e.getMessage(), true);
        }
    }

    private void onCambiarPassw() {
        String newpassw = new String(newPasswField.getText());
        if (newpassw.isBlank()) return;
        try {
            if (!passwField.getText().equals(repeatPasswField.getText())) {
                fgui.notification("Las contraseñas no coinciden", true);
                newPasswField.clear();
                return;
            }
            if (newpassw.contains(" ")) {
                fgui.notification("La nueva contraseña no puede contener espacios", true);
                newPasswField.clear();
                return;
            }
            cc.editPassword(new String(passwField.getText()), newpassw);
            fgui.notification("Contraseña actualizada con éxito", true);
            passwField.clear();
            repeatPasswField.clear();
            newPasswField.clear();
        } catch (Exception e) {
            fgui.notification(e.getMessage(), true);
        }
    }

    @Override
    public void update(Vector<String> friends, Vector<String> requests, Vector<String> onlineFriends) {
        Platform.runLater(() -> {
            friendsList.setAll(friends);
            requestsList.setAll(requests);
            onlineFriendsList.setAll(onlineFriends);

            if ((friends.size() > nFriends) && start) {
                fgui.notification("Un nuevo amigo ha sido añadido", false);
            }
            if (onlineFriends.size() > nOnlineFriends) {
                fgui.notification("Hay nuevos usuarios en línea", false);
            }

            synchronized (chats) {
                for (int i = 0; i < onlineFriends.size(); i++) {
                    if (!chats.containsKey(onlineFriends.get(i))) {
                        chats.put(onlineFriends.get(i), new Conversation(cc.getUsername(), onlineFriends.get(i)));
                    }
                }
                for (Object convo : chats.keySet()) {
                    if (!onlineFriends.contains((String) convo)) {
                        chats.remove(convo);
                    }
                }
            }

            if (requests.size() > nRequests) {
                fgui.notification("Tienes nuevas peticiones de amistad pendientes", false);
            }
            nFriends = friends.size();
            nOnlineFriends = onlineFriends.size();
            nRequests = requests.size();
            if (!start) start = true;

            refreshChat();
        });
    }

    @Override
    public void receiveMsg(String emisor, String msg) {
        Platform.runLater(() -> {
            if (!onlineFriendsList.contains(emisor)) return;
            Conversation convo;
            synchronized (chats) {
                convo = chats.get(emisor);
            }
            if (convo == null) return;
            convo.addMensaje(emisor, msg);
            if (friendList.getSelectionModel().getSelectedItem() != null
                    && friendList.getSelectionModel().getSelectedItem().equals(emisor)) {
                refreshChat();
            }
        });
    }

    public void onExit() {
        if (exiting) return;
        exiting = true;
        try {
            cc.exit();
        } catch (Exception e) {
            fgui.notification(e.getMessage(), true);
        }
        Platform.exit();
    }

    public TabPane getRoot() {
        return root;
    }
}
