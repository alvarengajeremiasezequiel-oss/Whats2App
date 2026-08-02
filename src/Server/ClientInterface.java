package Server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientInterface extends Remote {
    String getUsername() throws RemoteException;

    //Funciones de notificacion
    void notifyNewConnection(String username) throws RemoteException;
    void notifyDisconnection(String username) throws RemoteException;

    // Función para recibir mensajes
    void receiveMessage(String fromUsername, String message) throws RemoteException;
}
