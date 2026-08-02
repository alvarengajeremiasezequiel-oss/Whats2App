package Client;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Vector;
import GUI.clientController;

public class ClientImpl extends UnicastRemoteObject implements ClientInterface {
    private final String username;
    private ServerInterface server; // Referencia al servidor
    private Vector<ClientInterface> onlineUsers;
    private clientController cc;

    // Constructor que llama a super()
    public ClientImpl(String username, clientController cc) throws RemoteException {
        super();
        this.username = username;
        this.cc = cc;
        onlineUsers = new Vector<ClientInterface>();
    }

    public void setServer(ServerInterface server) {
        this.server = server;
    }

    @Override
    public String getUsername() throws RemoteException {
        return username;
    }


    @Override
    public void notifyNewConnection(String username) throws RemoteException {
        System.out.println("Nuevo cliente conectado: " + username);
    }

    @Override
    public void notifyDisconnection(String username) throws RemoteException {
        System.out.println("Cliente desconectado: " + username);
    }

    @Override
    public void receiveMessage(String fromUsername, String message) throws RemoteException {
        System.out.println("Mensaje de " + fromUsername + ": " + message);
        cc.updateMessages(fromUsername, message);
    }


}
