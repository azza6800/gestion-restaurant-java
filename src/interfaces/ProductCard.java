package interfaces;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import entité.Produit;
import Controller.ProduitController;

public class ProductCard extends JPanel {
    private final Produit produit;
    private final ProduitController controller;
    final ProductCardManager cardManager;
    
    // Couleurs modernes
    private final Color PRIMARY_COLOR = new Color(40, 167, 153); // Teal
    private final Color SECONDARY_COLOR = new Color(255, 107, 107); // Coral
    private final Color BACKGROUND_COLOR = Color.WHITE;
    private final Color TEXT_COLOR = new Color(51, 51, 51);
    private final Color DETAIL_COLOR = new Color(100, 100, 100);
    private final Color SHADOW_COLOR = new Color(0, 0, 0, 20);
    private final Color LIGHT_GRAY = new Color(245, 245, 245);
    private final Color BORDER_COLOR = new Color(230, 230, 230);
    
    // URLs des icônes
    private static final String ICON_PRICE = "https://cdn-icons-png.flaticon.com/128/2150/2150150.png";
    private static final String ICON_STOCK = "https://cdn-icons-png.flaticon.com/512/3652/3652191.png";
    private static final String ICON_DESC = "https://cdn-icons-png.flaticon.com/512/395/395659.png";
    private static final String ICON_EDIT = "https://cdn-icons-png.flaticon.com/512/1828/1828911.png";
    private static final String ICON_DELETE = "https://cdn-icons-png.flaticon.com/512/1214/1214428.png";
    
    private static final String[] CATEGORY_ICON_URLS = {
        "https://cdn-icons-png.flaticon.com/512/3176/3176286.png", // Plat
        "https://cdn-icons-png.flaticon.com/512/2718/2718224.png", // Sandwich
        "https://cdn-icons-png.flaticon.com/512/2329/2329866.png", // Salade
        "https://cdn-icons-png.flaticon.com/512/3721/3721766.png", // Dessert
        "https://cdn-icons-png.flaticon.com/512/2965/2965574.png", // Viennoiserie
        "https://cdn-icons-png.flaticon.com/512/3050/3050102.png"  // Boisson
    };

    public ProductCard(Produit produit, ProduitController controller, ProductCardManager cardManager) {
        this.produit = produit;
        this.controller = controller;
        this.cardManager = cardManager;
        setLayout(new BorderLayout(0, 8));
        setPreferredSize(new Dimension(280, 380));
        setBackground(BACKGROUND_COLOR);
        setBorder(new EmptyBorder(12, 12, 12, 12));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        buildCard();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Ombre
        g2d.setColor(SHADOW_COLOR);
        g2d.fillRoundRect(5, 5, getWidth()-10, getHeight()-10, 20, 20);
        
        // Fond
        g2d.setColor(BACKGROUND_COLOR);
        g2d.fillRoundRect(0, 0, getWidth()-10, getHeight()-10, 20, 20);
        
        // Bordure
        g2d.setColor(BORDER_COLOR);
        g2d.drawRoundRect(0, 0, getWidth()-11, getHeight()-11, 20, 20);
        
        g2d.dispose();
    }

    private void buildCard() {
        setOpaque(false);
        add(createImageComponent(), BorderLayout.NORTH);
        add(createDetailsPanel(), BorderLayout.CENTER);
        add(createActionButtons(), BorderLayout.SOUTH);
    }

    private JComponent createImageComponent() {
        JPanel imageContainer = new JPanel(new BorderLayout());
        imageContainer.setPreferredSize(new Dimension(250, 150));
        imageContainer.setBackground(LIGHT_GRAY);
        imageContainer.setBorder(new CompoundBorder(
            new MatteBorder(0, 0, 3, 0, PRIMARY_COLOR),
            new EmptyBorder(0, 0, 5, 0)
        ));
        imageContainer.setOpaque(false);

        JLabel imageLabel = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setClip(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 15, 15));
                super.paintComponent(g2);
            }
        };
        
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setVerticalAlignment(SwingConstants.CENTER);

        if (produit.getImage() != null && produit.getImage().length > 0) {
            ImageIcon icon = new ImageIcon(produit.getImage());
            Image img = icon.getImage().getScaledInstance(250, 150, Image.SCALE_SMOOTH);
            imageLabel.setIcon(new ImageIcon(img));
        } else {
            try {
                BufferedImage categoryIcon = loadCategoryIcon(produit.getCategorie());
                if (categoryIcon != null) {
                    Image scaledIcon = categoryIcon.getScaledInstance(60, 60, Image.SCALE_SMOOTH);
                    imageLabel.setIcon(new ImageIcon(scaledIcon));
                } else {
                    imageLabel.setText(getCategoryEmoji(produit.getCategorie()));
                    imageLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 50));
                    imageLabel.setForeground(new Color(180, 180, 180));
                }
            } catch (IOException e) {
                imageLabel.setText(getCategoryEmoji(produit.getCategorie()));
                imageLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 50));
                imageLabel.setForeground(new Color(180, 180, 180));
            }
        }

        imageContainer.add(imageLabel);
        return imageContainer;
    }

    private BufferedImage loadCategoryIcon(String category) throws IOException {
        int iconIndex = getCategoryIndex(category);
        if (iconIndex >= 0 && iconIndex < CATEGORY_ICON_URLS.length) {
            URL iconUrl = new URL(CATEGORY_ICON_URLS[iconIndex]);
            return ImageIO.read(iconUrl);
        }
        return null;
    }

    private int getCategoryIndex(String category) {
        return switch(category) {
            case "Plat" -> 0;
            case "Sandwich" -> 1;
            case "Salade" -> 2;
            case "Dessert" -> 3;
            case "Viennoiserie" -> 4;
            case "Boisson" -> 5;
            default -> -1;
        };
    }

    private String getCategoryEmoji(String categorie) {
        return switch(categorie) {
            case "Plat" -> "🍽️";
            case "Sandwich" -> "🥪";
            case "Salade" -> "🥗";
            case "Dessert" -> "🍰";
            case "Viennoiserie" -> "🥐";
            case "Boisson" -> "🥤";
            default -> "🍴";
        };
    }

    private JPanel createDetailsPanel() {
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBackground(new Color(0, 0, 0, 0));
        detailsPanel.setBorder(new EmptyBorder(8, 8, 8, 8));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(0, 0, 0, 0));
        headerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel nameLabel = new JLabel(produit.getNom() != null ? produit.getNom() : "Nouveau Produit");
        nameLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 18));
        nameLabel.setForeground(TEXT_COLOR);
        
        JLabel categoryBadge = createCategoryBadge();
        
        headerPanel.add(nameLabel, BorderLayout.CENTER);
        headerPanel.add(categoryBadge, BorderLayout.EAST);
        
        // Separator
        JSeparator separator = new JSeparator(JSeparator.HORIZONTAL);
        separator.setForeground(new Color(220, 220, 220));
        separator.setPreferredSize(new Dimension(80, 2));
        separator.setMaximumSize(new Dimension(Short.MAX_VALUE, 2));

        // Description
        JPanel descPanel = createDetailRow(ICON_DESC, produit.getDescription() != null ? 
            shortenDescription(produit.getDescription()) : "Pas de description");
        
        
        // Price
        JPanel pricePanel = new JPanel(new BorderLayout());
        pricePanel.setBackground(new Color(0, 0, 0, 0));
        pricePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel priceLabel = new JLabel(String.format("%.2f DT", produit.getPrix()));
        priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        priceLabel.setForeground(PRIMARY_COLOR);
        
        try {
            URL priceIconUrl = new URL(ICON_PRICE);
            ImageIcon icon = new ImageIcon(priceIconUrl);
            Image scaledIcon = icon.getImage().getScaledInstance(18, 18, Image.SCALE_SMOOTH);
            JLabel iconLabel = new JLabel(new ImageIcon(scaledIcon));
            iconLabel.setBorder(new EmptyBorder(0, 0, 0, 8));
            pricePanel.add(iconLabel, BorderLayout.WEST);
        } catch (Exception e) {
            // Ignore si l'icône ne charge pas
        }
        
        pricePanel.add(priceLabel, BorderLayout.CENTER);
        
        // Assembly
        detailsPanel.add(headerPanel);
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        detailsPanel.add(separator);
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        detailsPanel.add(descPanel);
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        
        detailsPanel.add(Box.createVerticalGlue());
        detailsPanel.add(pricePanel);

        return detailsPanel;
    }

    private String shortenDescription(String description) {
        if (description.length() > 60) {
            return description.substring(0, 57) + "...";
        }
        return description;
    }

    private JPanel createDetailRow(String iconUrl, String text) {
        JPanel panel = new JPanel(new BorderLayout(8, 0));
        panel.setBackground(new Color(0, 0, 0, 0));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setBorder(new EmptyBorder(4, 0, 4, 0));
        
        try {
            URL url = new URL(iconUrl);
            ImageIcon icon = new ImageIcon(url);
            Image scaledIcon = icon.getImage().getScaledInstance(14, 14, Image.SCALE_SMOOTH);
            JLabel iconLabel = new JLabel(new ImageIcon(scaledIcon));
            panel.add(iconLabel, BorderLayout.WEST);
        } catch (Exception e) {
            // Ignore si l'icône ne charge pas
        }
        
        JTextArea textArea = new JTextArea(text);
        textArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        textArea.setBackground(new Color(0, 0, 0, 0));
        textArea.setForeground(DETAIL_COLOR);
        textArea.setBorder(new EmptyBorder(0, 4, 0, 0));
        
        panel.add(textArea, BorderLayout.CENTER);
        return panel;
    }

    private JLabel createCategoryBadge() {
        JLabel badge = new JLabel(produit.getCategorie() != null ? 
            produit.getCategorie().toUpperCase() : "CATÉGORIE") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(PRIMARY_COLOR);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                super.paintComponent(g);
            }
        };
        badge.setFont(new Font("Segoe UI", Font.BOLD, 9));
        badge.setForeground(Color.WHITE);
        badge.setBorder(new EmptyBorder(3, 10, 3, 10));
        return badge;
    }

    private JPanel createActionButtons() {
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonPanel.setBackground(new Color(0, 0, 0, 0));
        buttonPanel.setBorder(new EmptyBorder(8, 0, 0, 0));

        JButton editButton = createModernButton("MODIFIER", PRIMARY_COLOR, e -> editProduct());
        JButton deleteButton = createModernButton("SUPPRIMER", SECONDARY_COLOR, e -> deleteProduct());

        try {
            URL editIconUrl = new URL(ICON_EDIT);
            ImageIcon editIcon = new ImageIcon(editIconUrl);
            Image scaledEditIcon = editIcon.getImage().getScaledInstance(14, 14, Image.SCALE_SMOOTH);
            editButton.setIcon(new ImageIcon(scaledEditIcon));
            editButton.setHorizontalTextPosition(SwingConstants.LEFT);
            editButton.setIconTextGap(6);
            
            URL deleteIconUrl = new URL(ICON_DELETE);
            ImageIcon deleteIcon = new ImageIcon(deleteIconUrl);
            Image scaledDeleteIcon = deleteIcon.getImage().getScaledInstance(14, 14, Image.SCALE_SMOOTH);
            deleteButton.setIcon(new ImageIcon(scaledDeleteIcon));
            deleteButton.setHorizontalTextPosition(SwingConstants.LEFT);
            deleteButton.setIconTextGap(6);
        } catch (Exception e) {
            System.err.println("Erreur de chargement des icônes: " + e.getMessage());
        }

        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        return buttonPanel;
    }

    private JButton createModernButton(String text, Color color, ActionListener listener) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                if (getModel().isRollover()) {
                    g2.setColor(new Color(255, 255, 255, 30));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                }
                
                g2.setColor(Color.WHITE);
                super.paintComponent(g2);
            }
        };
        
        button.setFont(new Font("Segoe UI", Font.BOLD, 11));
        button.setForeground(Color.WHITE);
        button.setContentAreaFilled(false);
        button.setBorder(new EmptyBorder(8, 12, 8, 12));
        button.setFocusPainted(false);
        button.addActionListener(listener);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        return button;
    }

    private void editProduct() {
        EditProductDialog dialog = new EditProductDialog(this, produit);
        dialog.setVisible(true);
    }

    private void deleteProduct() {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "<html><div style='text-align: center;'><b>Confirmer la suppression</b><br><br>" + 
            produit.getNom() + "<br><br>Cette action est irréversible.</div></html>", 
            "Confirmation", 
            JOptionPane.YES_NO_OPTION, 
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (controller.supprimerProduit(produit.getId())) {
                cardManager.refreshProductCards(controller.getTousProduits());
                showSuccessMessage("<html><b>Produit supprimé</b><br>" + produit.getNom() + "</html>");
            } else {
                showErrorMessage("Échec de la suppression du produit");
            }
        }
    }

    private void showSuccessMessage(String message) {
        JOptionPane.showMessageDialog(this, 
            "<html><div style='text-align: center;'>" + message + "</div></html>", 
            "Succès", 
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, 
            "<html><div style='text-align: center;'><b>Erreur</b><br>" + message + "</div></html>", 
            "Erreur", 
            JOptionPane.ERROR_MESSAGE);
    }
}