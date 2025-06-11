package interfaces;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import Controller.ProduitController;
import entité.Produit;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import java.awt.geom.RoundRectangle2D;

public class ProductCardManager {
    private final ProduitController controller;
    private JPanel cardsPanel;
    private final Color PRIMARY_COLOR = new Color(40, 167, 153);
    private final Color BUTTON_BG = new Color(245, 245, 245);
    private final Color BUTTON_HOVER = new Color(235, 235, 235);
    private final Color BUTTON_ACTIVE = new Color(40, 167, 153);
    private final Color BUTTON_TEXT = new Color(70, 70, 70);
    private final Color BUTTON_ACTIVE_TEXT = Color.WHITE;
    
    // URLs des icônes de catégorie
    private static final String[] CATEGORY_ICON_URLS = {
        "https://cdn-icons-png.flaticon.com/512/3176/3176286.png", // Plat
        "https://cdn-icons-png.flaticon.com/512/2718/2718224.png", // Sandwich
        "https://cdn-icons-png.flaticon.com/512/2329/2329866.png", // Salade
        "https://cdn-icons-png.flaticon.com/512/3721/3721766.png", // Dessert
        "https://cdn-icons-png.flaticon.com/512/2965/2965574.png", // Viennoiserie
        "https://cdn-icons-png.flaticon.com/512/3050/3050102.png"  // Boisson
    };
    
    private String currentCategory = "Toutes";
    
    public ProductCardManager(ProduitController controller) {
        this.controller = controller;
    }

    public JPanel createViewPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(250, 250, 250));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        panel.add(createFilterPanel(), BorderLayout.NORTH);
        panel.add(createCardsScrollPane(), BorderLayout.CENTER);
        
        loadProducts();
        return panel;
    }

    private JPanel createFilterPanel() {
        JPanel filterPanel = new JPanel(new BorderLayout(10, 0));
        filterPanel.setBackground(new Color(250, 250, 250));
        filterPanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        // Titre du filtre
        JLabel filterLabel = new JLabel("CATÉGORIES");
        filterLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        filterLabel.setForeground(new Color(100, 100, 100));
        filterLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        // Panel pour les boutons de catégorie avec scroll horizontal
        JPanel categoryButtonsPanel = new JPanel();
        categoryButtonsPanel.setLayout(new BoxLayout(categoryButtonsPanel, BoxLayout.X_AXIS));
        categoryButtonsPanel.setBackground(new Color(250, 250, 250));
        categoryButtonsPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        
        String[] categories = {"Toutes", "Plat", "Sandwich", "Salade", "Dessert", "Viennoiserie", "Boisson"};
        
        for (int i = 0; i < categories.length; i++) {
            String category = categories[i];
            JButton categoryButton = createModernCategoryButton(category, i-1);
            categoryButton.addActionListener(e -> {
                currentCategory = category;
                filterByCategory(category);
                // Mettre à jour l'état des boutons
                for (Component comp : categoryButtonsPanel.getComponents()) {
                    if (comp instanceof JButton) {
                        JButton btn = (JButton) comp;
                        boolean isActive = btn.getText().equals(currentCategory);
                        btn.setBackground(isActive ? BUTTON_ACTIVE : BUTTON_BG);
                        btn.setForeground(isActive ? BUTTON_ACTIVE_TEXT : BUTTON_TEXT);
                    }
                }
            });
            categoryButtonsPanel.add(categoryButton);
            if (i < categories.length - 1) {
                categoryButtonsPanel.add(Box.createRigidArea(new Dimension(10, 0)));
            }
        }
        
        // Scroll horizontal pour les boutons
        JScrollPane scrollPane = new JScrollPane(categoryButtonsPanel);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(10);
        scrollPane.getHorizontalScrollBar().setBackground(new Color(250, 250, 250));
        
        filterPanel.add(filterLabel, BorderLayout.NORTH);
        filterPanel.add(scrollPane, BorderLayout.CENTER);
        
        return filterPanel;
    }

    private JButton createModernCategoryButton(String category, int iconIndex) {
        JButton button = new JButton(category) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2.setColor(BUTTON_ACTIVE.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(category.equals(currentCategory) ? BUTTON_ACTIVE : BUTTON_HOVER);
                } else {
                    g2.setColor(category.equals(currentCategory) ? BUTTON_ACTIVE : BUTTON_BG);
                }
                
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
                
                super.paintComponent(g);
            }
            
            @Override
            protected void paintBorder(Graphics g) {
                // Pas de bordure peinte ici
            }
        };
        
        button.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        button.setForeground(category.equals(currentCategory) ? BUTTON_ACTIVE_TEXT : BUTTON_TEXT);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setFocusPainted(false);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Effet de survol
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!category.equals(currentCategory)) {
                    button.setForeground(BUTTON_TEXT);
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                if (!category.equals(currentCategory)) {
                    button.setForeground(BUTTON_TEXT);
                }
            }
        });
        
        if (iconIndex >= 0 && iconIndex < CATEGORY_ICON_URLS.length) {
            try {
                URL iconUrl = new URL(CATEGORY_ICON_URLS[iconIndex]);
                ImageIcon icon = new ImageIcon(iconUrl);
                Image scaledIcon = icon.getImage().getScaledInstance(18, 18, Image.SCALE_SMOOTH);
                
                // Ajuster la couleur de l'icône
                BufferedImage buffered = new BufferedImage(scaledIcon.getWidth(null), 
                    scaledIcon.getHeight(null), BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2 = buffered.createGraphics();
                g2.drawImage(scaledIcon, 0, 0, null);
                g2.dispose();
                
                for (int y = 0; y < buffered.getHeight(); y++) {
                    for (int x = 0; x < buffered.getWidth(); x++) {
                        int rgba = buffered.getRGB(x, y);
                        Color col = new Color(rgba, true);
                        if (col.getAlpha() > 0) {
                            col = category.equals(currentCategory) 
                                ? BUTTON_ACTIVE_TEXT 
                                : new Color(100, 100, 100);
                            buffered.setRGB(x, y, col.getRGB());
                        }
                    }
                }
                
                button.setIcon(new ImageIcon(buffered));
                button.setHorizontalTextPosition(SwingConstants.TRAILING);
                button.setIconTextGap(10);
            } catch (Exception e) {
                System.err.println("Erreur de chargement de l'icône: " + e.getMessage());
            }
        }
        
        return button;
    }

    // Les méthodes suivantes restent inchangées
    private JScrollPane createCardsScrollPane() {
        cardsPanel = new JPanel(new GridLayout(0, 3, 20, 20));
        cardsPanel.setBackground(new Color(250, 250, 250));
        cardsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(cardsPanel);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setBackground(new Color(250, 250, 250));
        return scrollPane;
    }

    public void loadProducts() {
        List<Produit> produits = controller.getTousProduits();
        refreshProductCards(produits);
    }

    public void addProductCard(Produit produit) {
        ProductCard card = new ProductCard(produit, controller, this);
        cardsPanel.add(card);
    }

    private void filterByCategory(String category) {
        List<Produit> produits;
        
        if ("Toutes".equals(category)) {
            produits = controller.getTousProduits();
        } else {
            produits = controller.getProduitsParCategorie(category);
        }
        
        refreshProductCards(produits);
    }

    public void refreshProductCards(List<Produit> produits) {
        cardsPanel.removeAll();
        for (Produit produit : produits) {
            addProductCard(produit);
        }
        cardsPanel.revalidate();
        cardsPanel.repaint();
    }
}