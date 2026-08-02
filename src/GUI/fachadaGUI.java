package GUI;

import javax.swing.*;
import java.awt.*;

public class fachadaGUI {
    private clientController cc;
    private VentanaPrincipal vp;

    public static void main(String args[]) {
        clientController cc = new clientController();
        fachadaGUI fgui = new fachadaGUI(cc);
        VLogin vl = new VLogin(fgui, cc);
        vl.pack();
        vl.setVisible(true);
        vl.setLocationRelativeTo(null);
    }

    public fachadaGUI(clientController cc) {
        this.cc = cc;
    }


    public void initVPrincipal(){
        vp = new VentanaPrincipal(this, cc);
        vp.pack();
        vp.setLocationRelativeTo(null);
        vp.setSize(600, 500);
    }

    public void startVPrincipal(){
        vp.setName(cc.getUsername());
        vp.setTitle(cc.getUsername());

        ImageIcon logoIcon = new ImageIcon("src/GUI/whats2appIcon.png");
        Image logo = logoIcon.getImage();

        // Colocar logo de aplicación
        vp.setIconImage(logo);

        vp.setVisible(true);
    }

    public void startVRegister(){
        VRegister vreg = new VRegister(this, cc);
        vreg.pack();
        vreg.setLocationRelativeTo(null);
        vreg.setVisible(true);
    }

    public void notification(String notif, boolean priority){
        VNotif vn = new VNotif(notif, priority);
        vn.pack();
        vn.setLocationRelativeTo(null);
        vn.setVisible(true);
    }


}
