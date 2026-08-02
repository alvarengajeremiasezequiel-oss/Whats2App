package GUI;

import javax.swing.*;
import java.awt.event.*;

public class VNotif extends JDialog{
    private JButton okButton;
    private JPanel panel1;
    private JLabel MsgLabel;
    private String mensaje;

    public VNotif(String mensaje,boolean priority) {
        this.mensaje = mensaje;
        createUIComponents();
        setContentPane(panel1);
        setModal(priority);
        getRootPane().setDefaultButton(okButton);
        MsgLabel.setText(mensaje);

        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        panel1.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK(){
        //MsgLabel.setText(mensaje);
        dispose();
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        //MsgLabel.setText(mensaje);
    }
}
