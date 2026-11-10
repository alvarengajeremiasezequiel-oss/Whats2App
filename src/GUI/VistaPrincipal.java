package GUI;

import java.util.Vector;

public interface VistaPrincipal {
    void update(Vector<String> friends, Vector<String> requests, Vector<String> onlineFriends);
    void receiveMsg(String emisor, String msg);
}
