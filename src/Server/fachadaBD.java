package Server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Scanner;
import java.util.Vector;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class fachadaBD {
    private static final int ITERATIONS = 100000;
    private static final int KEY_LENGTH = 256;
    private static final int SALT_LENGTH = 16;
    private static final SecureRandom RANDOM = new SecureRandom();

    private Connection conn;

    public fachadaBD() {
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:whats2app.db");
            try (Statement st = conn.createStatement()) {
                st.executeUpdate("CREATE TABLE IF NOT EXISTS users (username TEXT PRIMARY KEY, password TEXT NOT NULL)");
                st.executeUpdate("CREATE TABLE IF NOT EXISTS friends (user1 TEXT NOT NULL, user2 TEXT NOT NULL)");
                st.executeUpdate("CREATE TABLE IF NOT EXISTS requests (source_user TEXT NOT NULL, target_user TEXT NOT NULL)");
            }
        } catch (SQLException e) {
            throw new MsgExcep("Error al inicializar la base de datos: " + e.getMessage());
        }
    }
    

    public ArrayList<String> getUsuarios() throws FileNotFoundException {
        ArrayList<String> us = new ArrayList<String>();
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT username FROM users")) {
            while (rs.next()) us.add(rs.getString("username"));
        } catch (SQLException e) {
            throw new FileNotFoundException("Error al acceder a la base de datos");
        }
        return us;
    }


    public String getPassword(String user) throws FileNotFoundException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT password FROM users WHERE username = ?")) {
            ps.setString(1, user);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString("password");
            }
        } catch (SQLException e) {
            throw new FileNotFoundException("Error al acceder a la base de datos");
        }
        return null;
    }

    public ArrayList<String> getUser(String username) throws FileNotFoundException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT username, password FROM users WHERE username = ?")) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ArrayList<String> user = new ArrayList<String>();
                    user.add(rs.getString("username"));
                    user.add(rs.getString("password"));
                    return user;
                }
            }
        } catch (SQLException e) {
            throw new FileNotFoundException("Error al acceder a la base de datos");
        }
        return null;
    }

    public Vector<String> getAmigos(String user) throws FileNotFoundException {
        Vector<String> friends = new Vector<String>();
        try (PreparedStatement ps = conn.prepareStatement("SELECT user1, user2 FROM friends WHERE user1 = ? OR user2 = ?")) {
            ps.setString(1, user);
            ps.setString(2, user);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String user1 = rs.getString("user1");
                    String user2 = rs.getString("user2");
                    if (user1.equals(user) && !friends.contains(user2)) friends.add(user2);
                    if (user2.equals(user) && !friends.contains(user1)) friends.add(user1);
                }
            }
        } catch (SQLException e) {
            throw new FileNotFoundException("Error al acceder a la base de datos");
        }
        return friends;
    }

    public Vector<String> getPeticiones(String user) throws FileNotFoundException {
        Vector<String> requests = new Vector<String>();
        try (PreparedStatement ps = conn.prepareStatement("SELECT source_user FROM requests WHERE target_user = ?")) {
            ps.setString(1, user);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String source = rs.getString("source_user");
                    if (!requests.contains(source)) requests.add(source);
                }
            }
        } catch (SQLException e) {
            throw new FileNotFoundException("Error al acceder a la base de datos");
        }
        return requests;
    }

    public void addUsuario(String usuario, String passw) throws IOException {
        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO users(username, password) VALUES (?, ?)")) {
            ps.setString(1, usuario);
            ps.setString(2, hashPassword(passw));
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IOException("Error al almacenar usuario en base de datos");
        }
    }

    public void addAmistad(String user1, String user2) throws IOException {
        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO friends(user1, user2) VALUES (?, ?)")) {
            ps.setString(1, user1);
            ps.setString(2, user2);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IOException("Error al almacenar amistad en base de datos");
        }
    }

    public void removeAmistad(String user1, String user2) throws IOException, MsgExcep {
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM friends WHERE (user1 = ? AND user2 = ?) OR (user1 = ? AND user2 = ?)")) {
            ps.setString(1, user1);
            ps.setString(2, user2);
            ps.setString(3, user2);
            ps.setString(4, user1);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IOException("Error al eliminar amistad de la base de datos");
        }
    }

    public void addPeticion(String user1, String user2) throws IOException {
        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO requests(source_user, target_user) VALUES (?, ?)")) {
            ps.setString(1, user1);
            ps.setString(2, user2);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IOException("Error al almacenar petición en base de datos");
        }
    }

    public void removePeticion(String user1, String user2) throws IOException, MsgExcep {
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM requests WHERE source_user = ? AND target_user = ?")) {
            ps.setString(1, user1);
            ps.setString(2, user2);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IOException("Error al eliminar petición de la base de datos");
        }
    }

    public void editPassword(String usuario, String oldpassw, String newpassw) throws IOException {
        if (!verifyPassword(usuario, oldpassw)) {
            throw new MsgExcep("Clave incorrecta");
        }
        try (PreparedStatement ps = conn.prepareStatement("UPDATE users SET password = ? WHERE username = ?")) {
            ps.setString(1, hashPassword(newpassw));
            ps.setString(2, usuario);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IOException("Error al actualizar contraseña en la base de datos");
        }
    }

    public boolean verifyPassword(String user, String plain) throws FileNotFoundException {
        String stored = getPassword(user);
        if (stored == null) return false;
        if (stored.indexOf(':') == -1) {
            if (stored.equals(plain)) {
                try (PreparedStatement ps = conn.prepareStatement("UPDATE users SET password = ? WHERE username = ?")) {
                    ps.setString(1, hashPassword(plain));
                    ps.setString(2, user);
                    ps.executeUpdate();
                } catch (SQLException e) {
                    throw new FileNotFoundException("Error al acceder a la base de datos");
                }
                return true;
            }
            return false;
        }
        return verifyHash(stored, plain);
    }

    private String hashPassword(String passw) {
        byte[] salt = new byte[SALT_LENGTH];
        RANDOM.nextBytes(salt);
        return saltBase64(salt) + ":" + ITERATIONS + ":" + Base64.getEncoder().encodeToString(pbkdf2(passw, salt));
    }

    private boolean verifyHash(String stored, String plain) {
        try {
            String[] parts = stored.split(":");
            if (parts.length != 3) return false;
            byte[] salt = Base64.getDecoder().decode(parts[0]);
            int iterations = Integer.parseInt(parts[1]);
            byte[] expected = Base64.getDecoder().decode(parts[2]);
            byte[] actual = pbkdf2(plain, salt, iterations);
            return MessageDigest.isEqual(expected, actual);
        } catch (Exception e) {
            return false;
        }
    }

    private byte[] pbkdf2(String passw, byte[] salt) {
        return pbkdf2(passw, salt, ITERATIONS);
    }

    private byte[] pbkdf2(String passw, byte[] salt, int iterations) {
        try {
            KeySpec spec = new PBEKeySpec(passw.toCharArray(), salt, iterations, KEY_LENGTH);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            return factory.generateSecret(spec).getEncoded();
        } catch (Exception e) {
            throw new MsgExcep("Error al cifrar la contraseña");
        }
    }

    private String saltBase64(byte[] salt) {
        return Base64.getEncoder().encodeToString(salt);
    }
}
