package Controller;

import entité.Commande;
import entité.Commande_produits;
import entité.Database;
import entité.Produit;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommandeController {
    
    private static final String[] TYPES_COMMANDE_VALIDES = {"livraison", "sur_place", "emporter"};
    
    public boolean creerCommande(Commande commande) {
        if (!validerTypeCommande(commande.getTypeCommande())) {
            System.err.println("Type de commande invalide: " + commande.getTypeCommande());
            return false;
        }

        String query = "INSERT INTO commandes (client_id, date_commande, type_commande, statut, adresse_livraison) " +
                       "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, commande.getClientId());
            stmt.setTimestamp(2, new Timestamp(commande.getDateCommande().getTime()));
            stmt.setString(3, commande.getTypeCommande());
            stmt.setString(4, commande.getStatut());
            stmt.setString(5, commande.getAdresseLivraison());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                return false;
            }

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    commande.setId(rs.getInt(1));
                    return true;
                }
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la création de la commande:");
            e.printStackTrace();
            return false;
        }
    }
    
    private boolean validerTypeCommande(String type) {
        for (String valide : TYPES_COMMANDE_VALIDES) {
            if (valide.equalsIgnoreCase(type)) {
                return true;
            }
        }
        return false;
    }
    
    public Commande getCommandeById(int id) {
        String query = "SELECT * FROM commandes WHERE id = ?";
        
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                
                Commande commande = new Commande(
                    rs.getInt("id"),
                    rs.getInt("client_id"),
                    new Date(rs.getTimestamp("date_commande").getTime()),
                    rs.getString("type_commande"),
                    rs.getString("statut"),
                    rs.getString("adresse_livraison")
                );
                
                commande.setProduits(getProduitsPourCommande(commande.getId()));
                return commande;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de la commande #" + id);
            e.printStackTrace();
            return null;
        }
    }
    
    public boolean changerStatutCommande(int id, String nouveauStatut) {
        String query = "UPDATE commandes SET statut = ? WHERE id = ?";
        
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            
            stmt.setString(1, nouveauStatut);
            stmt.setInt(2, id);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors du changement de statut de la commande #" + id);
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean creerCommandeAvecProduits(Commande commande, List<Commande_produits> produits) {
    	String type = commande.getTypeCommande();
        if (!type.equals("livraison") && !type.equals("sur_place") && !type.equals("a_emporter")) {
            System.err.println("Type de commande invalide: " + type);
            return false;
        }
        if (produits == null || produits.isEmpty()) {
            System.err.println("Aucun produit dans la commande");
            return false;
        }

        String queryCommande = "INSERT INTO commandes (client_id, date_commande, type_commande, statut, adresse_livraison) " +
                              "VALUES (?, ?, ?, ?, ?)";
        
        String queryProduits = "INSERT INTO commande_produits (commande_id, produit_id, quantite) VALUES (?, ?, ?)";
        
        Connection connection = null;
        try {
            connection = Database.getConnection();
            connection.setAutoCommit(false);
            
            // 1. Créer la commande principale
            try (PreparedStatement stmtCommande = connection.prepareStatement(queryCommande, Statement.RETURN_GENERATED_KEYS)) {
                stmtCommande.setInt(1, commande.getClientId());
                stmtCommande.setTimestamp(2, new Timestamp(commande.getDateCommande().getTime()));
                stmtCommande.setString(3, commande.getTypeCommande());
                stmtCommande.setString(4, commande.getStatut());
                stmtCommande.setString(5, commande.getAdresseLivraison());
                
                if (stmtCommande.executeUpdate() == 0) {
                    connection.rollback();
                    return false;
                }
                
                // Récupérer l'ID généré
                try (ResultSet rs = stmtCommande.getGeneratedKeys()) {
                    if (!rs.next()) {
                        connection.rollback();
                        return false;
                    }
                    int commandeId = rs.getInt(1);
                    
                    // 2. Ajouter les produits
                    try (PreparedStatement stmtProduits = connection.prepareStatement(queryProduits)) {
                        for (Commande_produits ligne : produits) {
                            if (ligne.getQuantite() <= 0) {
                                System.err.println("Quantité invalide pour le produit " + ligne.getProduit().getId());
                                connection.rollback();
                                return false;
                            }
                            
                            stmtProduits.setInt(1, commandeId);
                            stmtProduits.setInt(2, ligne.getProduit().getId());
                            stmtProduits.setInt(3, ligne.getQuantite());
                            stmtProduits.addBatch();
                        }
                        
                        stmtProduits.executeBatch();
                    }
                    
                    connection.commit();
                    commande.setId(commandeId);
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la création de la commande avec produits:");
            e.printStackTrace();
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    public List<Commande> getCommandesParClient(int clientId) {
        List<Commande> commandes = new ArrayList<>();
        String query = "SELECT * FROM commandes WHERE client_id = ? ORDER BY date_commande DESC";
        
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            
            stmt.setInt(1, clientId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Commande commande = new Commande(
                        rs.getInt("id"),
                        rs.getInt("client_id"),
                        new Date(rs.getTimestamp("date_commande").getTime()),
                        rs.getString("type_commande"),
                        rs.getString("statut"),
                        rs.getString("adresse_livraison")
                    );
                    
                    commande.setProduits(getProduitsPourCommande(commande.getId()));
                    commandes.add(commande);
                }
            }
            return commandes;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des commandes du client #" + clientId);
            e.printStackTrace();
            return commandes;
        }
    }
    
    public boolean modifierCommandeAvecProduits(Commande commande, List<Commande_produits> produits) {
    	System.out.println("Type de commande reçu: " + commande.getTypeCommande());
    	String type = commande.getTypeCommande();
        if (!type.equals("livraison") && !type.equals("sur_place") && !type.equals("a_emporter")) {
            System.err.println("Type de commande invalide: " + type);
            return false;
        }

        if (produits == null || produits.isEmpty()) {
            System.err.println("Aucun produit dans la commande");
            return false;
        }
        String updateCommande = "UPDATE commandes SET type_commande = ?, adresse_livraison = ? WHERE id = ?";
        String updateProduit = "UPDATE commande_produits SET quantite = ? WHERE commande_id = ? AND produit_id = ?";
        
        Connection connection = null;
        try {
            connection = Database.getConnection();
            connection.setAutoCommit(false);
            
            // 1. Mettre à jour la commande
            try (PreparedStatement stmtCommande = connection.prepareStatement(updateCommande)) {
                stmtCommande.setString(1, commande.getTypeCommande());
                stmtCommande.setString(2, commande.getAdresseLivraison());
                stmtCommande.setInt(3, commande.getId());
                
                int rowsUpdated = stmtCommande.executeUpdate();
                System.out.println("[Controller] Lignes mises à jour: " + rowsUpdated);
                
                if (rowsUpdated == 0) {
                    connection.rollback();
                    return false;
                }
            }
            
            // 2. Mettre à jour les produits
            try (PreparedStatement stmtProduits = connection.prepareStatement(updateProduit)) {
                for (Commande_produits cp : produits) {
                    if (cp.getQuantite() <= 0) {
                        System.err.println("Quantité invalide pour le produit " + cp.getProduit().getId());
                        connection.rollback();
                        return false;
                    }
                    
                    stmtProduits.setInt(1, cp.getQuantite());
                    stmtProduits.setInt(2, commande.getId());
                    stmtProduits.setInt(3, cp.getProduit().getId());
                    stmtProduits.addBatch();
                }
                
                stmtProduits.executeBatch();
            }
            
            connection.commit();
            return true;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la modification de la commande #" + commande.getId());
            e.printStackTrace();
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public List<Commande> getCommandesByType(String type) {
        // Vérification du type de commande
        if (!validerTypeCommande(type)) {
            System.err.println("Type de commande invalide: " + type);
            return new ArrayList<>();
        }

        List<Commande> commandes = new ArrayList<>();
        String query = "SELECT * FROM commandes WHERE type_commande = ? ORDER BY date_commande DESC";
        
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            
            stmt.setString(1, type.toLowerCase());
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Commande commande = new Commande(
                        rs.getInt("id"),
                        rs.getInt("client_id"),
                        new Date(rs.getTimestamp("date_commande").getTime()),
                        rs.getString("type_commande"),
                        rs.getString("statut"),
                        rs.getString("adresse_livraison")
                    );
                    
                    commande.setProduits(getProduitsPourCommande(commande.getId()));
                    commandes.add(commande);
                }
            }
            return commandes;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des commandes de type " + type);
            e.printStackTrace();
            return commandes;
        }
    }
    
    public List<Commande_produits> getProduitsPourCommande(int commandeId) {
        List<Commande_produits> produits = new ArrayList<>();
        String query = "SELECT cp.*, p.nom, p.prix, p.description, p.categorie " +
                       "FROM commande_produits cp " +
                       "JOIN produits p ON cp.produit_id = p.id " +
                       "WHERE cp.commande_id = ?";
        
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            
            stmt.setInt(1, commandeId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Produit produit = new Produit(
                        rs.getInt("produit_id"),
                        rs.getString("nom"),
                        rs.getString("description"),
                        rs.getDouble("prix"),
                        rs.getString("categorie"),
                        null
                    );
                    
                    Commande_produits cp = new Commande_produits();
                    cp.setCommandeId(commandeId);
                    cp.setProduit(produit);
                    cp.setQuantite(rs.getInt("quantite"));
                    
                    produits.add(cp);
                }
            }
            return produits;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des produits pour la commande #" + commandeId);
            e.printStackTrace();
            return produits;
        }
    }
    
    public boolean modifierCommande(Commande commande) {
        if (!validerTypeCommande(commande.getTypeCommande())) {
            System.err.println("Type de commande invalide: " + commande.getTypeCommande());
            return false;
        }

        String query = "UPDATE commandes SET type_commande = ?, adresse_livraison = ? WHERE id = ?";
        
        try (Connection connection = Database.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            
            stmt.setString(1, commande.getTypeCommande());
            stmt.setString(2, commande.getAdresseLivraison());
            stmt.setInt(3, commande.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la modification de la commande #" + commande.getId());
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean supprimerCommande(int id) {
        String deleteProduits = "DELETE FROM commande_produits WHERE commande_id = ?";
        String deleteCommande = "DELETE FROM commandes WHERE id = ?";
        
        Connection connection = null;
        try {
            connection = Database.getConnection();
            connection.setAutoCommit(false);
            
            // 1. Supprimer d'abord les produits associés
            try (PreparedStatement stmtProduits = connection.prepareStatement(deleteProduits)) {
                stmtProduits.setInt(1, id);
                stmtProduits.executeUpdate();
            }
            
            // 2. Supprimer la commande
            try (PreparedStatement stmtCommande = connection.prepareStatement(deleteCommande)) {
                stmtCommande.setInt(1, id);
                
                if (stmtCommande.executeUpdate() > 0) {
                    connection.commit();
                    return true;
                }
            }
            
            connection.rollback();
            return false;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de la commande #" + id);
            e.printStackTrace();
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public List<Commande> getAllCommandes() {
        List<Commande> commandes = new ArrayList<>();
        String query = "SELECT * FROM commandes ORDER BY date_commande DESC";
        
        try (Connection connection = Database.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                Commande commande = new Commande(
                    rs.getInt("id"),
                    rs.getInt("client_id"),
                    new Date(rs.getTimestamp("date_commande").getTime()),
                    rs.getString("type_commande"),
                    rs.getString("statut"),
                    rs.getString("adresse_livraison")
                );
                
                commande.setProduits(getProduitsPourCommande(commande.getId()));
                commandes.add(commande);
            }
            return commandes;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de toutes les commandes");
            e.printStackTrace();
            return commandes;
        }
    }
}