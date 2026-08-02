package Server;

import java.util.Objects;

public class Peticion {
    private final String emisor;
    private final String receptor;

    public Peticion(String emisor, String receptor) {
        this.emisor = emisor;
        this.receptor = receptor;
    }

    public String getEmisor() {
        return emisor;
    }

    public String getReceptor() {
        return receptor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Peticion peticion = (Peticion) o;
        return Objects.equals(emisor, peticion.emisor) && Objects.equals(receptor, peticion.receptor);
    }
}
