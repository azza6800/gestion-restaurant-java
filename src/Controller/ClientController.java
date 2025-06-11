package Controller;

import entité.Client;
import entité.Database;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class ClientController {
	 public List<Client> getAllClients() {
	        List<Client> clients = new ArrayList<>();
	        String query = "SELECT * FROM clients";
	        
	        try (Connection conn = Database.getConnection();
	             Statement stmt = conn.createStatement();
	             ResultSet rs = stmt.executeQuery(query)) {
	            
	            while (rs.next()) {
	                clients.add(new Client(
	                    rs.getInt("id"),
	                    rs.getString("login"),
	                    rs.getString("password"),
	                    rs.getString("nom"),
	                    rs.getString("prenom"),
	                    rs.getDate("date_naissance"),
	                    rs.getString("adresse"),
	                    rs.getString("telephone"),
	                    rs.getBoolean("first_login")
	                ));
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	        return clients;
	    }
	 public boolean updateClient(Client client, boolean passwordChanged) {
	        String query;
	        if (passwordChanged) {
	            query = "UPDATE clients SET login = ?, password = SHA2(?, 256), nom = ?, prenom = ?, " +
	                   "date_naissance = ?, adresse = ?, telephone = ? WHERE id = ?";
	        } else {
	            query = "UPDATE clients SET login = ?, nom = ?, prenom = ?, " +
	                   "date_naissance = ?, adresse = ?, telephone = ? WHERE id = ?";
	        }
	        
	        try (Connection conn = Database.getConnection();
	             PreparedStatement stmt = conn.prepareStatement(query)) {
	            
	            int paramIndex = 1;
	            stmt.setString(paramIndex++, client.getLogin());
	            if (passwordChanged) {
	                stmt.setString(paramIndex++, client.getPassword());
	            }
	            stmt.setString(paramIndex++, client.getNom());
	            stmt.setString(paramIndex++, client.getPrenom());
	            stmt.setDate(paramIndex++, new java.sql.Date(client.getDateNaissance().getTime()));
	            stmt.setString(paramIndex++, client.getAdresse());
	            stmt.setString(paramIndex++, client.getTelephone());
	            stmt.setInt(paramIndex, client.getId());
	            
	            return stmt.executeUpdate() > 0;
	        } catch (SQLException e) {
	            e.printStackTrace();
	            return false;
	        }
	    }

	    public boolean deleteClient(int id) {
	        String query = "DELETE FROM clients WHERE id = ?";
	        
	        try (Connection conn = Database.getConnection();
	             PreparedStatement stmt = conn.prepareStatement(query)) {
	            
	            stmt.setInt(1, id);
	            return stmt.executeUpdate() > 0;
	        } catch (SQLException e) {
	            e.printStackTrace();
	            return false;
	        }
	    }
	    
	    
	    public boolean addClient(Client client) {
	        String query = "INSERT INTO clients (login, password, nom, prenom, date_naissance, adresse, telephone, first_login) " +
	                      "VALUES (?, SHA2(?, 256), ?, ?, ?, ?, ?, TRUE)";
	        
	        try (Connection conn = Database.getConnection();
	             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
	            
	            stmt.setString(1, client.getLogin());
	            stmt.setString(2, client.getPassword()); // Le mot de passe est hashé avec SHA-256
	            stmt.setString(3, client.getNom());
	            stmt.setString(4, client.getPrenom());
	            stmt.setDate(5, new java.sql.Date(client.getDateNaissance().getTime()));
	            stmt.setString(6, client.getAdresse());
	            stmt.setString(7, client.getTelephone());
	            
	            int affectedRows = stmt.executeUpdate();
	            
	            if (affectedRows > 0) {
	                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
	                    if (generatedKeys.next()) {
	                        client.setId(generatedKeys.getInt(1));
	                    }
	                }
	                return true;
	            }
	            return false;
	        } catch (SQLException e) {
	            e.printStackTrace();
	            return false;
	        }
	    }
	    public Client getClientById(int id) {
	        String query = "SELECT * FROM clients WHERE id = ?";
	        Client client = null;
	        
	        try (Connection conn = Database.getConnection();
	             PreparedStatement stmt = conn.prepareStatement(query)) {
	            
	            stmt.setInt(1, id);
	            
	            try (ResultSet rs = stmt.executeQuery()) {
	                if (rs.next()) {
	                    client = new Client(
	                        rs.getInt("id"),
	                        rs.getString("login"),
	                        rs.getString("password"),
	                        rs.getString("nom"),
	                        rs.getString("prenom"),
	                        rs.getDate("date_naissance"),
	                        rs.getString("adresse"),
	                        rs.getString("telephone"),
	                        rs.getBoolean("first_login")
	                    );
	                }
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	        return client;
	    }
	    public boolean verifyCurrentPassword(int clientId, String currentPassword) {
	        String query = "SELECT SHA2(?, 256) = password AS password_match FROM clients WHERE id = ?";
	        
	        try (Connection conn = Database.getConnection();
	             PreparedStatement stmt = conn.prepareStatement(query)) {
	            
	            stmt.setString(1, currentPassword);
	            stmt.setInt(2, clientId);
	            
	            ResultSet rs = stmt.executeQuery();
	            if (rs.next()) {
	                return rs.getBoolean("password_match");
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	        return false;
	    }

	    public boolean changePassword(int clientId, String newPassword) {
	        String query = "UPDATE clients SET password = SHA2(?, 256), first_login = FALSE WHERE id = ?";
	        
	        try (Connection conn = Database.getConnection();
	             PreparedStatement stmt = conn.prepareStatement(query)) {
	            
	            // On passe le mot de passe en clair car c'est MySQL qui va le hasher avec SHA2
	            stmt.setString(1, newPassword);
	            stmt.setInt(2, clientId);
	            
	            return stmt.executeUpdate() > 0;
	        } catch (SQLException e) {
	            e.printStackTrace();
	            return false;
	        }
	    }
	    

	   
	    private String hashPassword(String password) {
	        try {
	            MessageDigest digest = MessageDigest.getInstance("SHA-256");
	            byte[] hash = digest.digest(password.getBytes(java.nio.charset.StandardCharsets.UTF_8));
	            return bytesToHex(hash);
	        } catch (NoSuchAlgorithmException e) {
	            e.printStackTrace();
	            return null;
	        }
	    }
	    private static String bytesToHex(byte[] hash) {
	        StringBuilder hexString = new StringBuilder();
	        for (byte b : hash) {
	            String hex = Integer.toHexString(0xff & b);
	            if (hex.length() == 1) hexString.append('0');
	            hexString.append(hex);
	        }
	        return hexString.toString();
	    }
	    public String getClientName(int clientId) {
	        String query = "SELECT nom FROM clients WHERE id = ?";
	        try (Connection connection = Database.getConnection();
	             PreparedStatement stmt = connection.prepareStatement(query)) {
	            
	            stmt.setInt(1, clientId);
	            try (ResultSet rs = stmt.executeQuery()) {
	                if (rs.next()) {
	                    return rs.getString("nom");
	                }
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	        return "Client";
	    }

}
