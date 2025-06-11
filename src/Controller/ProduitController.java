package Controller;

import entité.Produit;
import entité.Database;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProduitController {
    
    // Ajouter un produit
    public boolean ajouterProduit(Produit produit) {
        String query = "INSERT INTO produits (nom, description, prix, categorie, image) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, produit.getNom());
            stmt.setString(2, produit.getDescription());
            stmt.setDouble(3, produit.getPrix());
            stmt.setString(4, produit.getCategorie());
            stmt.setBytes(5, produit.getImage());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        produit.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Modifier un produit
    public boolean modifierProduit(Produit produit) {
        String query = "UPDATE produits SET nom = ?, description = ?, prix = ?, categorie = ?, image = ? WHERE id = ?";
        
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            
            stmt.setString(1, produit.getNom());
            stmt.setString(2, produit.getDescription());
            stmt.setDouble(3, produit.getPrix());
            stmt.setString(4, produit.getCategorie());
            stmt.setBytes(5, produit.getImage());
            stmt.setInt(6, produit.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Supprimer un produit
    public boolean supprimerProduit(int id) {
        String query = "DELETE FROM produits WHERE id = ?";
        
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Obtenir un produit par ID
    public Produit getProduitById(int id) {
        String query = "SELECT * FROM produits WHERE id = ?";
        
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Produit(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("description"),
                        rs.getDouble("prix"),
                        rs.getString("categorie"),
                        rs.getBytes("image")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Obtenir tous les produits
    public List<Produit> getTousProduits() {
        List<Produit> produits = new ArrayList<>();
        String query = "SELECT * FROM produits";
        
        try (Connection connection = Database.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                produits.add(new Produit(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("description"),
                    rs.getDouble("prix"),
                    rs.getString("categorie"),
                    rs.getBytes("image")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return produits;
    }

    // Obtenir les produits par catégorie
    public List<Produit> getProduitsParCategorie(String categorie) {
        List<Produit> produits = new ArrayList<>();
        String query = "SELECT * FROM produits WHERE categorie = ?";
        
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            
            stmt.setString(1, categorie);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    produits.add(new Produit(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("description"),
                        rs.getDouble("prix"),
                        rs.getString("categorie"),
                        rs.getBytes("image")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return produits;
    }
}