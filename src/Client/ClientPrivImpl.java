package Client;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Vector;
import GUI.clientController;

public class ClientPrivImpl extends UnicastRemoteObject implements ClientPrivInterface{
    private final String username;
    private ServerInterface server;
    private Vector<String> friends = new Vector<String>();
    private Vector<String> requests = new Vector<String>();
    private Vector<String> onlineUsers = new Vector<String>();
    private Vector<ClientInterface> onlineFriends = new Vector<ClientInterface>();
    private clientController cc;

    public ClientPrivImpl(String username, clientController cc) throws RemoteException{
        super();
        this.username = username;
        this.cc = cc;
    }

    public void setServer(ServerInterface server) {
        this.server = server;
    }

    @Override
    public String getUsername(){
        return username;
    }

    public Vector<String> getFriends() {
        return friends;
    }

    public Vector<String> getOnlineFriends() {
        Vector<String> of = new Vector<>();
        for (int i = 0; i < friends.size(); i++) {
            if(onlineUsers.contains(friends.get(i)))
                of.add(friends.get(i));
        }
        return of;
    }

    public Vector<String> getRequests(){
        return requests;
    }

    @Override
    public Boolean befriend(String targetUser) throws RemoteException{
        try{
            server.friendRequest(username, targetUser);
            System.out.println("Solicitud enviada a " + targetUser);
            return true;
        } catch (MsgExcep e) {
            System.out.println(e.getMessage());
            return false;
        }

    }

    @Override
    public Boolean unfriend(String targetUser) throws RemoteException{
        try{
            server.unfriend(username, targetUser);
            System.out.println("Amistad con "+targetUser+" eliminada");
            return true;
        } catch (MsgExcep e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    @Override
    public Boolean acceptRequest(String targetUser) throws RemoteException{
        try{
            server.acceptRequest(targetUser, username);
            System.out.println(username+" y "+targetUser+" sois amigos");
            return true;
        } catch (MsgExcep e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    @Override
    public Boolean rejectRequest(String targetUser) throws RemoteException{
        try{
            server.rejectRequest(targetUser, username);
            return true;
        } catch (MsgExcep e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    @Override
    public Boolean receiveRequest(String targetUser) throws RemoteException{
        requests.add(targetUser);
        System.out.println("Petición de amistad recibida de "+targetUser);
        return true;
    }

    @Override
    public Boolean update(Vector<String> friends, Vector<ClientInterface> onlineFriends, Vector<String> requests) throws RemoteException{
        this.friends.clear();
        this.friends = friends;
        this.requests.clear();
        this.requests = requests;
        this.onlineFriends.clear();
        this.onlineFriends = onlineFriends;
        onlineUsers.clear();
        onlineUsers = server.getOnlineUsers();
        cc.updateGUI();
        return true;
    }

    public void sendMessage(String targetUsername, String message) throws MsgExcep{
        try {
            // Buscar al destinatario por nombre de usuario
            if(!onlineUsers.contains(targetUsername)){
                throw new MsgExcep("El usuario " + targetUsername + " no está en línea");
            }
            if(!friends.contains(targetUsername)){
                throw new MsgExcep("El usuario " + targetUsername + " no es amigo tuyo");
            }
            for (int i = 0; i < onlineFriends.size(); i++) {
                ClientInterface c = onlineFriends.get(i);
                if (c.getUsername().equals(targetUsername)) {
                    c.receiveMessage(username, message);
                    System.out.println("Mensaje enviado a " + targetUsername);
                    return;
                }
            }

        } catch (Exception e) {
            throw new MsgExcep("Error al enviar el mensaje: " + e.getMessage());
        }
    }

    public Boolean changePassword(String oldpassw, String newpassw) throws RemoteException, MsgExcep {
        try{
            server.changePassword(this, oldpassw, newpassw);
            return true;
        } catch (MsgExcep e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

}
