package GUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

public class VLogin extends JFrame {
    private JPanel contentPane;
    private JButton buttonLogin;
    private JButton buttonRegister;
    private JTextField usernameField;
    private JLabel usernameLabel;
    private JLabel passwordLabel;
    private JPasswordField passwordField;
    private fachadaGUI fgui;
    private clientController cc;

    public VLogin(fachadaGUI fgui, clientController cc) {
        this.fgui = fgui;
        this.cc = cc;

        contentPane = new JPanel(new GridLayout(2, 1, 0, 15));
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));

        usernameLabel = new JLabel("Nombre de usuario");
        passwordLabel = new JLabel("Clave");
        usernameField = new JTextField();
        passwordField = new JPasswordField();

        JPanel fieldsPanel = new JPanel(new GridLayout(4, 1, 0, 5));
        fieldsPanel.add(usernameLabel);
        fieldsPanel.add(usernameField);
        fieldsPanel.add(passwordLabel);
        fieldsPanel.add(passwordField);

        buttonLogin = new JButton("Iniciar sesión");
        buttonRegister = new JButton("Registrarse");

        JPanel buttonsPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        buttonsPanel.add(buttonLogin);
        buttonsPanel.add(buttonRegister);

        contentPane.add(fieldsPanel);
        contentPane.add(buttonsPanel);

        setContentPane(contentPane);
        //setModal(true);
        getRootPane().setDefaultButton(buttonLogin);

        buttonLogin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onLogin();
            }
        });

        buttonRegister.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onRegister();
            }
        });

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void onLogin() {
        try {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            fgui.initVPrincipal();
            cc.login(username, password);
            fgui.startVPrincipal();

            dispose();
        } catch (Exception e) {
            fgui.notification(e.getMessage(), false);
        }
    }

    private void onRegister() {
        fgui.startVRegister();
        dispose();
    }
}
