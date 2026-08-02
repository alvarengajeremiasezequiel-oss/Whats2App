package Server;

import Client.ClientInterface;
import Client.ClientPrivImpl;
import Client.ClientPrivInterface;
import Client.ServerInterface;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class ServerImpl extends UnicastRemoteObject implements ServerInterface {
    private final Vector<ClientInterface> clients = new Vector<ClientInterface>();
    private final Map clientsPriv = Collections.synchronizedMap(new HashMap<String, ClientPrivInterface>());
    private final Vector<Peticion> pendingRequests = new Vector<Peticion>();
    private final Vector<String> usernames = new Vector<>();
    private final fachadaBD fbd = new fachadaBD();

    public ServerImpl() throws RemoteException {
    }

    @Override
    public synchronized Boolean registerUser(ClientInterface client, ClientPrivInterface clientpriv, String username, String password) throws RemoteException, MsgExcep {
        try {
            if (fbd.getUser(username) != null) {
                throw new MsgExcep("El nombre de usuario ya existe");
            }

            clients.add(client);
            usernames.add(username);
            synchronized (clientsPriv) {
                clientsPriv.put(username, clientpriv);
            }

            fbd.addUsuario(username, password);

            synchronized (clientsPriv){
                for(Object cpriv: clientsPriv.values()){
                    updateFriends((ClientPrivInterface) cpriv);
                }
            }

            System.out.println(username + " se ha conectado.");

            return true;

        } catch (FileNotFoundException e) {
            throw new MsgExcep("Error al acceder a la base de datos");
        } catch (IOException e) {
            throw new MsgExcep(e.getMessage());
        }
    }

    @Override
    public synchronized Boolean loginUser(ClientInterface client, ClientPrivInterface clientpriv, String username, String password) throws RemoteException, MsgExcep {
        if (usernames.contains(username)) {
            throw new MsgExcep("El usuario " + username + " ya tiene una sesión abierta");
        }

        try {
            if (fbd.getUser(username) == null) {
                throw new MsgExcep("El usuario no existe");
            }

            if (!fbd.verifyPassword(username, password)) {
                throw new MsgExcep("Clave incorrecta");
            }

            clients.add(client);
            usernames.add(username);
            synchronized (clientsPriv) {
                clientsPriv.put(username, clientpriv);
            }

            // Notificar a los amigos del cliente
            for (int i = 0; i < clients.size(); i++) {
                ClientInterface c = clients.get(i);
                Vector<String> amigos = fbd.getAmigos(username);
                if (!c.equals(client) && amigos.contains(c.getUsername())) {
                    c.notifyNewConnection(username);
                }
            }

            Vector<String> amigos = fbd.getAmigos(username);
            Vector<ClientPrivInterface> amigosC = new Vector<ClientPrivInterface>();
            synchronized (clientsPriv){
                for(Object cpriv : clientsPriv.keySet()){
                    if(amigos.contains(cpriv)){
                        amigosC.add((ClientPrivInterface) clientsPriv.get(cpriv));
                    }
                }
            }
            for (int i = 0; i < amigosC.size(); i++) {
                updateFriends((ClientPrivInterface) amigosC.get(i));
            }


            for (int i = 0; i < usernames.size(); i++) {

            }

            System.out.println(username + " se ha conectado.");
            return true;

        } catch (FileNotFoundException e) {
            throw new MsgExcep("Error al acceder a la base de datos");
        }

    }


    @Override
    public Vector<ClientInterface> getClients() throws RemoteException {
        return new Vector<ClientInterface>(clients);
    }

    @Override
    public synchronized void disconnect(ClientInterface client) throws RemoteException {
        int index = clients.indexOf(client);
        if (index != -1) {
            String username = usernames.get(index);
            clients.remove(index);
            usernames.remove(index);
            clientsPriv.remove(client.getUsername());

            // Notificar a los demás clientes
            for (ClientInterface c : clients) {
                c.notifyDisconnection(username);
            }
            synchronized (clientsPriv){
                for(Object cpriv: clientsPriv.values()){
                    updateFriends((ClientPrivInterface) cpriv);
                }
            }
            System.out.println(username + " se ha desconectado.");
        }
    }

    @Override
    public Vector<String> getOnlineUsers() throws RemoteException {
        return new Vector<>(usernames);
    }


    @Override
    public Boolean friendRequest(String sourceUser, String targetUser) throws RemoteException, MsgExcep {
        boolean exists = false;

        try {
            if (usernames.contains(targetUser)) {
                synchronized (clientsPriv) {
                    ((ClientPrivInterface) clientsPriv.get(targetUser)).receiveRequest(sourceUser);
                }
                System.out.println("Solicitud de amistad de " + sourceUser + " a " + targetUser + " enviada");
                pendingRequests.add(new Peticion(sourceUser, targetUser));
                fbd.addPeticion(sourceUser, targetUser);
                ClientPrivInterface cpriv;
                synchronized (clientsPriv){
                    cpriv = (ClientPrivInterface) clientsPriv.get(targetUser);
                }
                updateFriends(cpriv);
                return true;
            }

            exists = fbd.getUser(targetUser) != null;

            if (!exists) {
                throw new MsgExcep("El usuario al que se manda la solicitud no existe");
            }

            fbd.addPeticion(sourceUser, targetUser);
            System.out.println("Solicitud de amistad de " + sourceUser + " a " + targetUser + " enviada");
            return true;
        } catch (FileNotFoundException e) {
            throw new MsgExcep("Error al acceder a la base de datos");
        } catch (IOException e) {
            throw new MsgExcep("Error al almacenar solicitud en base de datos");
        }

    }

    @Override
    public Boolean updateFriends(ClientPrivInterface clientPriv) throws RemoteException, MsgExcep {
        try {
            if (fbd.getUser(clientPriv.getUsername()) == null) {
                throw new MsgExcep("El usuario no está registrado");
            }

            Vector<String> amigos = fbd.getAmigos(clientPriv.getUsername());
            Vector<String> peticiones = fbd.getPeticiones(clientPriv.getUsername());
            Vector<ClientInterface> amigosOnline = new Vector<ClientInterface>();
            for (int i = 0; i < clients.size(); i++) {
                if(amigos.contains(clients.get(i).getUsername()))
                    amigosOnline.add(clients.get(i));
            }
            clientPriv.update(amigos,amigosOnline, peticiones);

            return true;

        } catch (FileNotFoundException e) {
            throw new MsgExcep("Error al acceder a la base de datos");
        } catch (RemoteException r) {
            throw new MsgExcep("Error al acceder al cliente privado");
        }
    }

    @Override
    public Boolean unfriend(String sourceUser, String targetUser) throws RemoteException, MsgExcep {
        boolean exists = false;

        try {
            exists = fbd.getUser(targetUser) != null;

            if (!exists) {
                throw new MsgExcep("El usuario al que se manda la solicitud no existe");
            }

            fbd.removeAmistad(sourceUser, targetUser);

            ClientPrivInterface target;
            ClientPrivInterface source;

            synchronized (clientsPriv) {
                target = ((ClientPrivInterface) clientsPriv.get(targetUser));
                source = ((ClientPrivInterface) clientsPriv.get(sourceUser));
            }
            if (usernames.contains(targetUser)) {
                updateFriends(target);
            }
            updateFriends(source);

            System.out.println("Amistad entre " + sourceUser + " y " + targetUser + " eliminada");
            return true;
        } catch (FileNotFoundException e) {
            throw new MsgExcep("Error al acceder a la base de datos");
        } catch (IOException e) {
            throw new MsgExcep("Error al almacenar solicitud en base de datos");
        }
    }

    public Boolean acceptRequest(String sourceUser, String targetUser) throws RemoteException, MsgExcep {
        try {
            Peticion peticion = new Peticion(sourceUser, targetUser);
            boolean exists = false;
            if (pendingRequests.contains(peticion)) {
                pendingRequests.remove(peticion);
                exists = true;
            }

            if(fbd.getPeticiones(targetUser).contains(sourceUser)) {
                fbd.removePeticion(sourceUser, targetUser);
                exists = true;
            }
            if(exists){
                fbd.addAmistad(sourceUser, targetUser);
                synchronized (clientsPriv) {
                    if(clientsPriv.containsKey(targetUser)){
                        updateFriends((ClientPrivInterface) clientsPriv.get(targetUser));
                    }
                    if(clientsPriv.containsKey(sourceUser)){
                        updateFriends((ClientPrivInterface) clientsPriv.get(sourceUser));
                    }
                }
                System.out.println("Amistad establecida entre " + sourceUser + " y " + targetUser);
                return true;
            }else{
                    throw new MsgExcep("No existe una petición del usuario " + sourceUser);
            }

        } catch (IOException e) {
            throw new MsgExcep(e.getMessage());
        }
    }

    public Boolean rejectRequest(String sourceUser, String targetUser) throws RemoteException, MsgExcep {
        try {
            Peticion peticion = new Peticion(sourceUser, targetUser);
            boolean exists = false;
            if (pendingRequests.contains(peticion)) {
                pendingRequests.remove(peticion);
                exists = true;
            }
            if(fbd.getPeticiones(targetUser).contains(sourceUser)) {
                fbd.removePeticion(sourceUser, targetUser);
                exists = true;
            }
            if(exists){
                synchronized (clientsPriv) {
                    if(clientsPriv.containsKey(targetUser)){
                        updateFriends((ClientPrivInterface) clientsPriv.get(targetUser));
                    }
                }
                System.out.println("Solicitud de " + sourceUser + " a " + targetUser+" rechazada");
                return true;
            }else{
                throw new MsgExcep("No existe una petición del usuario " + sourceUser);
            }

        } catch (IOException e) {
            throw new MsgExcep(e.getMessage());
        }
    }

    public Boolean changePassword(ClientPrivInterface clientpriv, String oldPassw, String newPassw) throws RemoteException, MsgExcep {
        try {
            if (fbd.getUser(clientpriv.getUsername()) == null) {
                throw new MsgExcep("El usuario no existe");
            }

            if (!fbd.verifyPassword(clientpriv.getUsername(), oldPassw)) {
                throw new MsgExcep("Clave incorrecta");
            }

            fbd.editPassword(clientpriv.getUsername(), oldPassw, newPassw);
            return true;

        } catch (FileNotFoundException e) {
            throw new MsgExcep("Error al acceder a la base de datos");
        } catch (IOException e) {
            throw new MsgExcep("Error al acceder a la base de datos");
        }
    }
}


