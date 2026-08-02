package GUI;

public class Mensaje implements java.io.Serializable{
    private final int miembro;
    private final String msg;

    public Mensaje(int miembro, String msg){
        this.miembro = miembro;
        this.msg = msg;
    }

    public int getMiembro() {
        return miembro;
    }

    public String getMsg() {
        return msg;
    }

}
