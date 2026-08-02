package GUI;

import Client.MsgExcep;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Arrays;

public class VRegister extends JFrame {
    private JPanel contentPane;
    private JButton buttonRegister;
    private JTextField usernameField;
    private JPasswordField passwField;
    private JPasswordField repeatPasswField;
    private JLabel usernameLabel;
    private JLabel passwLabel;
    private JLabel repeatPasswLabel;
    private fachadaGUI fgui;
    private clientController cc;

    public VRegister(fachadaGUI fgui, clientController cc) {
        this.fgui = fgui;
        this.cc = cc;
        setContentPane(contentPane);
        getRootPane().setDefaultButton(buttonRegister);

        buttonRegister.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onRegister();
            }
        });

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onExit();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onRegister() {
        try{
            String username = usernameField.getText();
            String passw;

            if(!Arrays.equals(passwField.getPassword(),repeatPasswField.getPassword())){
                fgui.notification("Las contraseñas no coinciden",true);
                return;
            }
            passw = new String(passwField.getPassword());

            fgui.initVPrincipal();

            cc.register(username, passw);

            fgui.startVPrincipal();
            dispose();
        }catch (Exception e){
            fgui.notification(e.getMessage(), false);
        }
    }

    private void onExit(){
        dispose();
    }

}
