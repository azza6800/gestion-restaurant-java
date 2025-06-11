package interfaces;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import Controller.ClientController;
import Controller.ProduitController;
import entité.Produit;

import java.text.DecimalFormat;
import java.util.Map;
import java.util.stream.Collectors;

public class StatsPanel extends JPanel {
    private ClientController clientController;
    private ProduitController produitController;
    private DecimalFormat df = new DecimalFormat("#,###");

    public StatsPanel(GerantDashboard parent) {
        this.clientController = new ClientController();
        this.produitController = new ProduitController();
        
        setLayout(new GridLayout(2, 2, 20, 20));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(new Color(240, 242, 245));

        // Cartes de statistiques avec données réelles
        add(createStatCard("Clients inscrits", 
            df.format(clientController.getAllClients().size()), 
            getClientGrowthText(), 
            new Color(70, 130, 180)));
            
        add(createStatCard("Produits disponibles", 
            df.format(produitController.getTousProduits().size()), 
            getTopCategory(), 
            new Color(60, 179, 113)));
            
        add(createStatCard("Catégories", 
            String.valueOf(getCategoryCount()), 
            "Top: " + getMostPopularCategory(), 
            new Color(255, 165, 0)));
            
        add(createStatCard("Prix moyen", 
            df.format(getAveragePrice()) + " DH", 
            getPriceTrend(), 
            new Color(147, 112, 219)));
    }

    private String getClientGrowthText() {
        // Ici vous pourriez implémenter une logique pour calculer l'évolution
        // Pour l'exemple, nous faisons une simulation
        int clientCount = clientController.getAllClients().size();
        if (clientCount > 100) return "↑ " + (clientCount/10) + "% ce mois";
        return "Stable ce mois";
    }
    
    private String getTopCategory() {
        Map<String, Long> categories = produitController.getTousProduits().stream()
            .collect(Collectors.groupingBy(Produit::getCategorie, Collectors.counting()));
            
        return categories.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(entry -> "Plus de " + entry.getKey())
            .orElse("Aucune catégorie");
    }
    
    private int getCategoryCount() {
        return (int) produitController.getTousProduits().stream()
            .map(Produit::getCategorie)
            .distinct()
            .count();
    }
    
    private String getMostPopularCategory() {
        // Similaire à getTopCategory mais retourne juste le nom
        return produitController.getTousProduits().stream()
            .collect(Collectors.groupingBy(Produit::getCategorie, Collectors.counting()))
            .entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("Aucune");
    }
    
    private double getAveragePrice() {
        return produitController.getTousProduits().stream()
            .mapToDouble(Produit::getPrix)
            .average()
            .orElse(0.0);
    }
    
    private String getPriceTrend() {
        double avg = getAveragePrice();
        if (avg > 50) return "Haut de gamme";
        if (avg > 20) return "Gamme moyenne";
        return "Approche économique";
    }

    // La méthode createStatCard reste inchangée
    private JPanel createStatCard(String title, String value, String description, Color color) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                RoundRectangle2D roundedRectangle = new RoundRectangle2D.Float(
                    0, 0, getWidth(), getHeight(), 20, 20);
                g2d.setColor(Color.WHITE);
                g2d.fill(roundedRectangle);
                
                g2d.setColor(color);
                g2d.fillRect(0, 0, getWidth(), 5);
            }
        };
        
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(15, 20, 15, 20));
        card.setPreferredSize(new Dimension(250, 150));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titleLabel.setForeground(new Color(100, 100, 100));
        card.add(titleLabel, BorderLayout.NORTH);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLabel.setForeground(new Color(50, 50, 50));
        valueLabel.setBorder(new EmptyBorder(10, 0, 10, 0));
        card.add(valueLabel, BorderLayout.CENTER);
        
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descLabel.setForeground(color.darker());
        card.add(descLabel, BorderLayout.SOUTH);
        
        return card;
    }
}