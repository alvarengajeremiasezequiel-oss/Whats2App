package Server;

import Client.ServerInterface;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class ServerMain {
    public static void main(String[] args) {
        try {
            ServerInterface server = new ServerImpl();

            Scanner scanner = new Scanner(System.in);

            //System.out.println("\nIntroduce el puerto: ");
            //int p = scanner.nextInt();
            //scanner.close();
            int p = 1044;

            Registry registry = LocateRegistry.createRegistry(p);
            registry.rebind("ChatServer", server);
            System.out.println("Servidor iniciado y esperando conexiones...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
