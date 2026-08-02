package Server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Vector;

public interface ServerInterface extends Remote {
    //Método para registrar nuevo usuario
    Boolean registerUser(ClientInterface client, ClientPrivInterface clientpriv, String username, String password) throws RemoteException, MsgExcep;

    //Método para iniciar sesión de usuario existente
    Boolean loginUser(ClientInterface client, ClientPrivInterface clientpriv, String username, String password) throws RemoteException, MsgExcep;

    void disconnect(ClientInterface client) throws RemoteException;
    Vector<String> getOnlineUsers() throws RemoteException;

    // Nuevo método para obtener clientes conectados
    Vector<ClientInterface> getClients() throws RemoteException;


    //Método para solicitar amistad
    Boolean friendRequest(String sourceUser, String targetUser) throws RemoteException, MsgExcep;

    Boolean updateFriends(ClientPrivInterface client) throws RemoteException, MsgExcep;

    Boolean unfriend(String sourceUser, String targetUser) throws RemoteException, MsgExcep;

    Boolean acceptRequest(String sourceUser, String targetUser) throws RemoteException, MsgExcep;

    Boolean rejectRequest(String sourceUser, String targetUser) throws RemoteException, MsgExcep;

    Boolean changePassword(ClientPrivInterface clientpriv, String oldPassw, String newPassw) throws RemoteException, MsgExcep;
}
