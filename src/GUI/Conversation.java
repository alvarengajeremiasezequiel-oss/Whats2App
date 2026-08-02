package GUI;

import java.util.ArrayList;

public class Conversation implements java.io.Serializable{
    private ArrayList<Mensaje> mensajes;
    private String miembro1; //Usuario local
    private String miembro2; //Usuario remoto

    public Conversation(String miembro1, String miembro2) {
        this.miembro1 = miembro1;
        this.miembro2 = miembro2;
        mensajes = new ArrayList<Mensaje>();
    }

    public Boolean addMensaje(String miembro, String texto){

        //Agregar mensaje propio
        if(miembro.equals(miembro1)){
            mensajes.add(new Mensaje(1,texto));
            return true;
        }

        //Agregar mensaje del otro usuario
        if(miembro.equals(miembro2)){
            mensajes.add(new Mensaje(2,texto));
            return true;
        }
        return false;
    }

    public ArrayList<Mensaje> getMensajes() {
        return mensajes;
    }

    public String getMiembro1() {
        return miembro1;
    }

    public String getMiembro2() {
        return miembro2;
    }
}
