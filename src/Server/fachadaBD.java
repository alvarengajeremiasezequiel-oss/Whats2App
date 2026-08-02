package Server;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Vector;

public class fachadaBD {
    private File usuarios = new File("dbUsers.txt");
    private File amigos = new File("dbFriends.txt");
    private File peticiones = new File("dbRequests.txt");

    public ArrayList<String> getUsuarios() throws FileNotFoundException {
        Scanner scan = new Scanner(usuarios);
        ArrayList<String> us = new ArrayList<String>();
        while (scan.hasNextLine()) {
            String line = scan.nextLine();
            us.add(line.split(" ")[0]);
        }
        scan.close();
        return us;
    }

    public String getPassword(String user) throws FileNotFoundException {
        Scanner scan = new Scanner(usuarios);
        while (scan.hasNextLine()) {
            String line = scan.nextLine();
            if(line.split(" ")[0].equals(user)){
                scan.close();
                return line.split(" ")[1];
            }
        }
        scan.close();
        return null;
    }

    public ArrayList<String> getUser(String username) throws FileNotFoundException {
        Scanner scan = new Scanner(usuarios);
        while (scan.hasNextLine()) {
            String line = scan.nextLine();
            if(line.split(" ").length == 2 && line.split(" ")[0].equals(username)){
                scan.close();
                return new ArrayList<String>(Arrays.stream(line.split(" ")).toList());
            }
        }
        scan.close();
        return null;
    }

    public Vector<String> getAmigos(String user) throws FileNotFoundException {
        Scanner scan = new Scanner(amigos);
        Vector<String> friends = new Vector<String>();
        while (scan.hasNextLine()) {
            String line = scan.nextLine();
            if(line.split(" ").length == 2){
                if(line.split(" ")[0].equals(user) && !friends.contains(line.split(" ")[1])) friends.add(line.split(" ")[1]);
                if(line.split(" ")[1].equals(user) && !friends.contains(line.split(" ")[0])) friends.add(line.split(" ")[0]);
            }

        }
        scan.close();
        return friends;
    }

    public Vector<String> getPeticiones(String user) throws FileNotFoundException {
        Scanner scan = new Scanner(peticiones);
        Vector<String> requests = new Vector<String>();
        while (scan.hasNextLine()) {
            String line = scan.nextLine();
            if(line.split(" ").length == 2){
                String source = line.split(" ")[0];
                if(line.split(" ")[1].equals(user) && !requests.contains(source)) requests.add(source);
            }

        }
        scan.close();
        return requests;
    }

    public void addUsuario(String usuario, String passw) throws IOException {
        FileWriter myWriter = new FileWriter(usuarios.getName(), true);
        myWriter.write(usuario+" "+passw+"\n");
        myWriter.close();
    }

    public void addAmistad(String user1, String user2) throws IOException {
        FileWriter myWriter = new FileWriter(amigos.getName(), true);
        myWriter.write(user1+" "+user2+"\n");
        myWriter.close();
    }

    public void removeAmistad(String user1, String user2) throws IOException, MsgExcep {
        removeFromFile(amigos.getName(), user1+" "+user2);
        removeFromFile(amigos.getName(), user2+" "+user1);
    }

    public void addPeticion(String user1, String user2) throws IOException {
        FileWriter myWriter = new FileWriter(peticiones.getName(), true);
        myWriter.write(user1+" "+user2+"\n");
        myWriter.close();
    }

    public void removePeticion(String user1, String user2) throws IOException, MsgExcep {
        removeFromFile(peticiones.getName(), user1+" "+user2);
    }

    public void editPassword(String usuario, String oldpassw, String newpassw) throws IOException {
        removeFromFile(usuarios.getName(), usuario+" "+oldpassw);
        FileWriter myWriter = new FileWriter(usuarios.getName(), true);
        myWriter.write(usuario+" "+newpassw+"\n");
        myWriter.close();
    }

    private void removeFromFile(String file, String lineRemove) throws MsgExcep {

        try {

            File inFile = new File(file);

            if (!inFile.isFile()) {
                throw new MsgExcep("El archivo no existe");
            }

            File tempFile = new File(inFile.getAbsolutePath() + ".tmp");

            BufferedReader br = new BufferedReader(new FileReader(file));
            PrintWriter pw = new PrintWriter(new FileWriter(tempFile));

            String line = null;

            while ((line = br.readLine()) != null) {
                if(line.isBlank()){
                    continue;
                }
                if (!line.trim().equals(lineRemove)) {

                    pw.println(line);
                    pw.flush();
                }
            }
            pw.close();
            br.close();

            if (!inFile.delete()) {
                throw new MsgExcep("No se pudo borrar el archivo");
            }

            if (!tempFile.renameTo(inFile))
                System.out.println("No se pudo renombrar el archivo");

        }
        catch (IOException er) {
            throw new MsgExcep(er.getMessage());
        }
    }


}
