package Controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import entité.Database;

public class LoginController {
    public boolean authenticate(String username, String password) {
        String query = "SELECT id FROM clients WHERE login = ? AND password = SHA2(?, 256)";
        String adminQuery = "SELECT id FROM admin WHERE username = ? AND password = SHA2(?, 256)";
        
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, username);
            stmt.setString(2, password);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return true;
            }
            
            try (PreparedStatement adminStmt = conn.prepareStatement(adminQuery)) {
                adminStmt.setString(1, username);
                adminStmt.setString(2, password);
                return adminStmt.executeQuery().next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean isAdmin(String username) {
        String query = "SELECT id FROM admin WHERE username = ?";
        
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, username);
            return stmt.executeQuery().next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public int getClientId(String username) {
        String query = "SELECT id FROM clients WHERE login = ?";
        
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getInt("id") : -1;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }
    
    public boolean isFirstLogin(String username) {
        String query = "SELECT first_login FROM clients WHERE login = ?";
        
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getBoolean("first_login");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}