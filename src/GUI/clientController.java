package GUI;
import Client.*;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import static java.rmi.server.UnicastRemoteObject.unexportObject;


public class clientController {
    private ClientImpl client;
    private ClientPrivImpl clientPriv;
    private String username;
    private String password;
    private ServerInterface server;
    private final String p = "1044";
    private final String addr = "localhost";
    private VistaPrincipal vp;

    private void setServer() throws MsgExcep {
        try{
            // Conexión al servidor RMI
            Registry registry = LocateRegistry.getRegistry(addr, Integer.parseInt(p));
            server = (ServerInterface) registry.lookup("ChatServer");
        }catch (Exception e){
            throw new MsgExcep(e.getMessage());
        }
    }

    public String getUsername(){
        return username;
    }

    public void login(String username, String password) throws MsgExcep{
        try {
            this.username = username;
            setServer();
            //Creación de cliente y cliente privado
            client = new ClientImpl(username, this);
            clientPriv = new ClientPrivImpl(username, this);

            //Asignación de referencia a servidor remoto
            client.setServer(server);
            clientPriv.setServer(server);

            //Inicio de sesión en servidor
            boolean res = server.loginUser(client, clientPriv, username, password);
            if(!res) return;


            //Sincronización de datos
            server.updateFriends(clientPriv);
        }catch (Exception e){
            throw new MsgExcep(e.getMessage());
        }
    }

    public void register(String username, String password) throws MsgExcep{
        try {
            this.username = username;
            setServer();
            //Creación de cliente y cliente privado
            client = new ClientImpl(username, this);
            clientPriv = new ClientPrivImpl(username, this);

            //Asignación de referencia a servidor remoto
            client.setServer(server);
            clientPriv.setServer(server);

            server.registerUser(client, clientPriv, username, password);

            //Sincronización de datos
            server.updateFriends(clientPriv);

        } catch (Exception e) {
            throw new MsgExcep(e.getMessage());
        }

    }

    public void exit() throws MsgExcep{
        try {
            server.disconnect(client);  // Desconectar del servidor
            unexportObject(client, true);
            unexportObject(clientPriv, true);
        } catch (Exception e) {
            throw new MsgExcep(e.getMessage());
        }
    }

    public void msg(String usuario, String mensaje) throws MsgExcep{
        try {
            clientPriv.sendMessage(usuario, mensaje);
        } catch (Exception e) {
            throw new MsgExcep(e.getMessage());
        }

    }

    public void befriend(String usuario) throws MsgExcep{
        try {
            clientPriv.befriend(usuario);
        } catch (Exception e) {
            throw new MsgExcep(e.getMessage());
        }
    }

    public void unfriend(String usuario) throws MsgExcep{
        try {
            clientPriv.unfriend(usuario);
        } catch (Exception e) {
            throw new MsgExcep(e.getMessage());
        }
    }

    public void acceptReq(String usuario) throws MsgExcep{
        try {
            clientPriv.acceptRequest(usuario);
        } catch (Exception e) {
            throw new MsgExcep(e.getMessage());
        }
    }
    public void rejectReq(String usuario) throws MsgExcep{
        try {
            clientPriv.rejectRequest(usuario);
        } catch (Exception e) {
            throw new MsgExcep(e.getMessage());
        }
    }

    public void editPassword(String oldpassw, String newpassw) throws MsgExcep{
        try {
            clientPriv.changePassword(oldpassw, newpassw);
        } catch (Exception e) {
            throw new MsgExcep(e.getMessage());
        }
    }

    public void setVPrincipal(VistaPrincipal vp){
        this.vp = vp;
    }

    public void updateGUI(){
        vp.update(clientPriv.getFriends(),clientPriv.getRequests(), clientPriv.getOnlineFriends());
    }

    public void updateMessages(String emisor, String msg){
        vp.receiveMsg(emisor, msg);
    }
}
