package GUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

public class VNotif extends JDialog{
    private JButton okButton;
    private JPanel panel1;
    private JLabel MsgLabel;
    private String mensaje;

    public VNotif(String mensaje,boolean priority) {
        this.mensaje = mensaje;

        panel1 = new JPanel(new BorderLayout(10, 10));
        panel1.setBorder(new EmptyBorder(10, 10, 10, 10));

        MsgLabel = new JLabel(mensaje, SwingConstants.CENTER);
        okButton = new JButton("OK");

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonsPanel.add(okButton);

        panel1.add(MsgLabel, BorderLayout.CENTER);
        panel1.add(buttonsPanel, BorderLayout.SOUTH);

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
}
