package GUI;

import Client.MsgExcep;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class VentanaPrincipal extends JFrame {
    private JPanel contentPane;
    private JTabbedPane tabbedPane;
    private JPanel Home;
    private JPanel FriendRequests;
    private JPanel Settings;
    private JScrollPane friendsScrollPane;
    private JList friendList;
    private JList friendReqList;
    private JScrollPane friendReqScrollPane;
    private JButton rechazarReqButton;
    private JButton aceptarReqButton;
    private JLabel RequestLabel;
    private JLabel addAmigoLabel;
    private JTextField addAmigoField;
    private JButton addAmigoButton;
    private JLabel friendChatLabel;
    private JTextField chatField;
    private JList chat;
    private JButton enviarMensajeButton;
    private JScrollPane chatScrollPane;
    private JButton unfriendButton;
    private JLabel youLabel;
    private JLabel cambiarPasswLabel;
    private JPasswordField passwField;
    private JPasswordField repeatPasswField;
    private JPasswordField newPasswField;
    private JLabel pLabel;
    private JLabel rpLabel;
    private JLabel npLabel;
    private JButton cambiarPasswButton;
    private fachadaGUI fgui;
    private clientController cc;
    private Vector<String> friends = new Vector<>();
    private Vector<String> requests = new Vector<>();
    private Vector<String> onlineFriends = new Vector<>();
    private Map<String, Conversation> chats = Collections.synchronizedMap(new HashMap<>());
    private int nFriends = 0;
    private int nOnlineFriends = 0;
    private int nRequests = 0;
    private boolean start = false;


    public VentanaPrincipal(fachadaGUI fgui, clientController cc) {
        this.fgui = fgui;
        this.cc = cc;

        contentPane = new JPanel(new GridLayout(1, 1));
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));

        tabbedPane = new JTabbedPane();

        // Tab Home
        Home = new JPanel(new GridLayout(1, 2, 10, 0));

        friendsScrollPane = new JScrollPane();
        friendList = new JList();
        friendsScrollPane.setViewportView(friendList);

        unfriendButton = new JButton("Eliminar amistad");
        JPanel friendsButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        friendsButtons.add(unfriendButton);

        JPanel friendsColumn = new JPanel(new GridLayout(2, 1, 0, 10));
        friendsColumn.add(friendsScrollPane);
        friendsColumn.add(friendsButtons);

        friendChatLabel = new JLabel("");
        youLabel = new JLabel("Tú");
        JPanel chatHeader = new JPanel(new BorderLayout());
        chatHeader.add(friendChatLabel, BorderLayout.WEST);
        chatHeader.add(youLabel, BorderLayout.EAST);

        chatScrollPane = new JScrollPane();
        chat = new JList();
        chatScrollPane.setViewportView(chat);

        chatField = new JTextField();
        enviarMensajeButton = new JButton("Enviar");
        JPanel chatInput = new JPanel(new BorderLayout(10, 0));
        chatInput.add(chatField, BorderLayout.CENTER);
        chatInput.add(enviarMensajeButton, BorderLayout.EAST);

        JPanel chatColumn = new JPanel(new BorderLayout(0, 10));
        chatColumn.add(chatHeader, BorderLayout.NORTH);
        chatColumn.add(chatScrollPane, BorderLayout.CENTER);
        chatColumn.add(chatInput, BorderLayout.SOUTH);

        Home.add(friendsColumn);
        Home.add(chatColumn);

        // Tab FriendRequests
        FriendRequests = new JPanel(new GridLayout(2, 1, 0, 10));

        friendReqScrollPane = new JScrollPane();
        friendReqList = new JList();
        friendReqScrollPane.setViewportView(friendReqList);

        RequestLabel = new JLabel("");
        aceptarReqButton = new JButton("Aceptar");
        rechazarReqButton = new JButton("Rechazar");

        JPanel reqButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        reqButtons.add(aceptarReqButton);
        reqButtons.add(rechazarReqButton);

        JPanel reqRight = new JPanel(new GridLayout(2, 1, 0, 10));
        reqRight.add(RequestLabel);
        reqRight.add(reqButtons);

        JPanel reqListPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        reqListPanel.add(friendReqScrollPane);
        reqListPanel.add(reqRight);

        addAmigoLabel = new JLabel("Añadir amigo");
        addAmigoField = new JTextField();
        addAmigoButton = new JButton("Enviar solicitud");

        JPanel addAmigoPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        addAmigoPanel.add(addAmigoLabel);
        JPanel addAmigoInput = new JPanel(new BorderLayout(10, 0));
        addAmigoInput.add(addAmigoField, BorderLayout.CENTER);
        addAmigoInput.add(addAmigoButton, BorderLayout.EAST);
        addAmigoPanel.add(addAmigoInput);

        FriendRequests.add(reqListPanel);
        FriendRequests.add(addAmigoPanel);

        // Tab Settings
        Settings = new JPanel(new GridLayout(2, 1, 0, 15));

        cambiarPasswLabel = new JLabel("Cambiar contraseña");
        pLabel = new JLabel("Contraseña");
        rpLabel = new JLabel("Repita la contraseña");
        npLabel = new JLabel("Nueva contraseña");
        passwField = new JPasswordField();
        repeatPasswField = new JPasswordField();
        newPasswField = new JPasswordField();

        JPanel passwFields = new JPanel(new GridLayout(3, 2, 10, 5));
        passwFields.add(pLabel);
        passwFields.add(passwField);
        passwFields.add(rpLabel);
        passwFields.add(repeatPasswField);
        passwFields.add(npLabel);
        passwFields.add(newPasswField);

        JPanel settingsTop = new JPanel(new GridLayout(2, 1, 0, 10));
        settingsTop.add(cambiarPasswLabel);
        settingsTop.add(passwFields);

        cambiarPasswButton = new JButton("Confirmar");
        JPanel settingsBottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        settingsBottom.add(cambiarPasswButton);

        Settings.add(settingsTop);
        Settings.add(settingsBottom);

        tabbedPane.addTab("Home", Home);
        tabbedPane.addTab("FriendRequests", FriendRequests);
        tabbedPane.addTab("Settings", Settings);
        contentPane.add(tabbedPane);

        setContentPane(contentPane);
        cc.setVPrincipal(this);
        friendList.setListData(friends);

        friendReqList.setListData(requests);


        chat.setCellRenderer(new ListCellRenderer<Mensaje>() {

            private final JLabel label = new JLabel();

            @Override
            public Component getListCellRendererComponent(
                    JList<? extends Mensaje> list,
                    Mensaje value,
                    int index,
                    boolean isSelected,
                    boolean cellHasFocus
                    ) {

                // Set text
                label.setText(value.getMsg());

                // Set alignment based on content or condition
                if (value.getMiembro() == 1) {
                    label.setHorizontalAlignment(SwingConstants.RIGHT);
                } else {
                    label.setHorizontalAlignment(SwingConstants.LEFT);
                }

                // Handle selection and focus
                if (isSelected) {
                    label.setBackground(list.getSelectionBackground());
                    label.setForeground(list.getSelectionForeground());
                } else {
                    label.setBackground(list.getBackground());
                    label.setForeground(list.getForeground());
                }

                label.setOpaque(true);
                return label;
            }
        });

        friendList.setCellRenderer(new ListCellRenderer<String>() {

            private final JLabel label = new JLabel();

            @Override
            public Component getListCellRendererComponent(
                    JList<? extends String> list,
                    String value,
                    int index,
                    boolean isSelected,
                    boolean cellHasFocus
            ) {

                // Set text
                label.setText(value);

                // Set alignment based on content or condition
                if (onlineFriends.contains(value)) {
                    label.setForeground(Color.GREEN);
                } else {
                    label.setForeground(Color.GRAY);
                }

                // Handle selection and focus
                if (isSelected) {
                    label.setBackground(list.getSelectionBackground());
                    //label.setForeground(list.getSelectionForeground());
                } else {
                    label.setBackground(list.getBackground());
                    //label.setForeground(list.getForeground());
                }

                label.setOpaque(true);
                return label;
            }
        });

        aceptarReqButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onAceptarReq();
            }

        });

        rechazarReqButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onRechazarReq();
            }
        });

        addAmigoButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onEnviarSolicitudAmistad();
            }
        });

        enviarMensajeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onEnviarMensaje();
            }
        });

        unfriendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onEliminarAmigo();
            }
        });

        cambiarPasswButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCambiarPassw();
            }
        });

        friendList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                friendListClicked();
            }
        });

        friendReqList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                friendReqListClicked();
            }
        });


        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onExit();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onExit();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        contentPane.addComponentListener(new ComponentAdapter() {
        });
    }


    private void onExit(){
        try {
            cc.exit();
        } catch (MsgExcep e) {
            fgui.notification(e.getMessage(), true);
        }
        dispose();
    }

    //----------------Funciones de pestaña de FriendRequests ------------------------------------

    private void onAceptarReq(){
        try {
            String usuario = (String) friendReqList.getSelectedValue();
            cc.acceptReq(usuario);
            RequestLabel.setText("");
            aceptarReqButton.setEnabled(false);
            rechazarReqButton.setEnabled(false);
        } catch (MsgExcep e) {
            fgui.notification(e.getMessage(), true);
        }
    }

    private void onRechazarReq(){
        try {
            String usuario = (String) friendReqList.getSelectedValue();
            cc.rejectReq(usuario);
            RequestLabel.setText("");
            aceptarReqButton.setEnabled(false);
            rechazarReqButton.setEnabled(false);
        } catch (MsgExcep e) {
            fgui.notification(e.getMessage(), true);
        }
    }

    private void onEnviarSolicitudAmistad(){
        try {
            String usuario = addAmigoField.getText();
            cc.befriend(usuario);
            addAmigoField.setText("");
        } catch (MsgExcep e) {
            fgui.notification(e.getMessage(), true);
        }
    }

    private void friendReqListClicked(){
        if(( friendReqList.getSelectedValue()) == null){
            RequestLabel.setText("");
            aceptarReqButton.setEnabled(false);
            rechazarReqButton.setEnabled(false);
        }else{

            String reqmensaje = "El usuario "+friendReqList.getSelectedValue()+" te ha mandado una solicitud de amistad";
            RequestLabel.setText(reqmensaje);
            aceptarReqButton.setEnabled(true);
            rechazarReqButton.setEnabled(true);
        }
    }

    //----------------Funciones de pestaña Home -------------------------------------------------

    private void friendListClicked(){
        Object[] msgs;
        String selected = (String) friendList.getSelectedValue();
        if(selected == null){
            unfriendButton.setEnabled(false);
            enviarMensajeButton.setEnabled(false);
            friendChatLabel.setText("");
            youLabel.setText("");
            chat.updateUI();
            return;
        }
        if(!onlineFriends.contains(selected)){
            unfriendButton.setEnabled(true);
            enviarMensajeButton.setEnabled(false);
            friendChatLabel.setText(selected+" (offline)");
            youLabel.setText("Tú");
            chat.setListData(new Mensaje[0]);
            chat.updateUI();
            return;
        }
        unfriendButton.setEnabled(true);
        enviarMensajeButton.setEnabled(true);
        friendChatLabel.setText(selected);
        youLabel.setText("Tú");

        Conversation convo;
        synchronized (chats){
            convo = chats.get(friendList.getSelectedValue());
        }
        msgs = convo.getMensajes().toArray();
        chat.setListData(msgs);

        chat.updateUI();
    }

    public void receiveMsg(String emisor, String msg){
        if(onlineFriends.contains(emisor)){
            chats.get(emisor).addMensaje(emisor, msg);
            Object[] msgs;
            if((friendList.getSelectedValue() != null) && friendList.getSelectedValue().equals(emisor)){
                synchronized (chats){
                    msgs = chats.get(emisor).getMensajes().toArray();
                }
                chat.setListData(msgs);
            }
        }
    }

    public void onEnviarMensaje(){
        try{
            if(!onlineFriends.contains(friendList.getSelectedValue())) return;

            if(!chatField.getText().isBlank() && friendList.getSelectedValue() != null){
                String msg = chatField.getText();
                Conversation convo;
                synchronized (chats){
                    convo = chats.get(friendList.getSelectedValue());
                }
                convo.addMensaje(cc.getUsername(), msg);
                cc.msg((String)friendList.getSelectedValue(), msg);
                chatField.setText("");

                Object[] msgs;
                msgs = convo.getMensajes().toArray();
                chat.setListData(msgs);
            }
        } catch (Exception e) {
            fgui.notification(e.getMessage(), true);
        }

    }

    public void onEliminarAmigo(){
        try{
            if(friendList.getSelectedValue() != null){
                cc.unfriend((String) friendList.getSelectedValue());
                friendList.setListData(friends);
            }
        } catch (Exception e) {
            fgui.notification(e.getMessage(), true);
        }
    }

    //----------------Funciones de de ajustes----------------------------------------------------

    public void onCambiarPassw(){
        String newpassw = new String(newPasswField.getPassword());
        if(newpassw.isBlank()) return;
        try{
            if(!Arrays.equals(passwField.getPassword(),repeatPasswField.getPassword())){
                fgui.notification("Las contraseñas no coinciden", true);
                newPasswField.setText("");
                return;
            }

            if(newpassw.contains(" ")){
                fgui.notification("La nueva contraseña no puede contener espacios", true);
                newPasswField.setText("");
            }
            String oldpassw = new String(passwField.getPassword());
            cc.editPassword(oldpassw, newpassw);
            fgui.notification("Contraseña actualizada con éxito", true);

        } catch (Exception e) {
            fgui.notification(e.getMessage(), true);
        }
    }

    public void update(Vector<String> friends, Vector<String> requests, Vector<String> onlineFriends){
        this.friends = friends;
        this.requests = requests;
        this.onlineFriends = onlineFriends;
        friendReqList.setListData(requests);
        friendList.setListData(friends);
        if((friends.size() > nFriends) && start){
            fgui.notification("Un nuevo amigo ha sido añadido", false);
        }
        if(onlineFriends.size() > nOnlineFriends){
            fgui.notification("Hay nuevos usuarios en línea", false);
        }

        synchronized (chats){
            for (int i = 0; i < onlineFriends.size(); i++) {
                if(!chats.containsKey( onlineFriends.get(i) )){
                    chats.put(onlineFriends.get(i), new Conversation(cc.getUsername(), onlineFriends.get(i)));
                }
            }
            for(Object convo : chats.keySet()){
                if(!onlineFriends.contains((String) convo)){
                    chats.remove(convo);
                }
            }
        }


        if(requests.size() > nRequests){
            fgui.notification("Tienes nuevas peticiones de amistad pendientes", false);
        }
        nFriends = friends.size();
        nOnlineFriends = onlineFriends.size();
        nRequests = requests.size();
        if(!start) start = true;

    }
}
