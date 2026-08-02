package GUI;

import javax.swing.*;
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

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
