package Server;

import Client.ClientInterface;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Vector;

public interface ClientPrivInterface extends Remote{
    String getUsername() throws RemoteException;

    // Petición de amistad
    Boolean befriend(String targetUser) throws RemoteException;

    // Eliminación de amistad
    Boolean unfriend(String targetUser) throws RemoteException;

    // Funciones de gestión de peticiones entrantes
    Boolean acceptRequest(String targetUser) throws RemoteException;

    Boolean rejectRequest(String targetUser) throws RemoteException;

    // Recibir petición desde servidor
    Boolean receiveRequest(String targetUser) throws RemoteException;

    // Actualizar amigos, peticiones y qué amigos están en línea
    Boolean update(Vector<String> friends, Vector<ClientInterface> onlineFriends, Vector<String> requests) throws RemoteException;
}
