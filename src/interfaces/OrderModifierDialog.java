package interfaces;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;
import entité.Commande;
import entité.Commande_produits;
import Controller.CommandeController;

public class OrderModifierDialog extends JDialog {
    private final CommandeController commandeController;
    private final Commande commande;
    private final OrdersClientPanel parentPanel;
    
    // Palette de couleurs modernisée
    private static final Color PRIMARY_COLOR = new Color(40, 110, 180);   // Bleu moderne
    private static final Color SECONDARY_COLOR = new Color(255, 105, 97); // Rouge corail
    private static final Color DARK_COLOR = new Color(45, 45, 55);        // Noir profond
    private static final Color LIGHT_COLOR = new Color(250, 250, 252);    // Blanc cassé
    private static final Color SUCCESS_COLOR = new Color(46, 204, 113);   // Vert succès
    private static final Color WARNING_COLOR = new Color(241, 196, 15);   // Jaune avertissement
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color BORDER_COLOR = new Color(230, 230, 230);
    
    // Polices
    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font FONT_HEADER = new Font("Segoe UI Semibold", Font.BOLD, 14);
    private static final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_BOLD = new Font("Segoe UI Semibold", Font.BOLD, 14);
    private static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 14);
    
    private List<JSpinner> quantitySpinners = new ArrayList<>();

    public OrderModifierDialog(OrdersClientPanel parent, Commande commande) {
        super((JFrame) SwingUtilities.getWindowAncestor(parent), 
              "Modifier Commande #" + commande.getId(), 
              ModalityType.APPLICATION_MODAL);
        this.parentPanel = parent;
        this.commande = commande;
        this.commandeController = new CommandeController();
        
        initUI();
        pack();
        setLocationRelativeTo(parent);
    }

    private void initUI() {
        setMinimumSize(new Dimension(800, 600));
        getContentPane().setBackground(LIGHT_COLOR);
        
        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(LIGHT_COLOR);
        
        // En-tête avec titre et bouton de fermeture
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        JLabel titleLabel = new JLabel("Modifier Commande #" + commande.getId());
        titleLabel.setFont(FONT_TITLE);
        titleLabel.setForeground(DARK_COLOR);
        
        JButton closeButton = new JButton("×");
        closeButton.setFont(new Font("Segoe UI", Font.BOLD, 20));
        closeButton.setContentAreaFilled(false);
        closeButton.setBorder(new EmptyBorder(0, 0, 0, 0));
        closeButton.setForeground(new Color(120, 120, 120));
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeButton.addActionListener(e -> dispose());
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(closeButton, BorderLayout.EAST);
        
        // Contenu principal avec onglets
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(FONT_HEADER);
        tabbedPane.setBackground(LIGHT_COLOR);
        tabbedPane.setBorder(BorderFactory.createEmptyBorder());
        
        // Onglet Produits
        JPanel productsPanel = createProductsPanel();
        JScrollPane productsScroll = new JScrollPane(productsPanel);
        productsScroll.setBorder(null);
        productsScroll.getVerticalScrollBar().setUnitIncrement(16);
        
        // Onglet Informations
        JPanel infoPanel = createInfoPanel();
        JScrollPane infoScroll = new JScrollPane(infoPanel);
        infoScroll.setBorder(null);
        infoScroll.getVerticalScrollBar().setUnitIncrement(16);
        
        tabbedPane.addTab("Produits", productsScroll);
        tabbedPane.addTab("Informations", infoScroll);
        
        // Boutons
        JPanel buttonPanel = createButtonPanel();
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    private void saveChanges() {
        System.out.println("Type de commande avant conversion: " + commande.getTypeCommande());
        
        // Mise à jour des quantités
        List<Commande_produits> modifiedProduits = new ArrayList<>();
        for (int i = 0; i < commande.getProduits().size() && i < quantitySpinners.size(); i++) {
            Commande_produits cp = commande.getProduits().get(i);
            cp.setQuantite((Integer) quantitySpinners.get(i).getValue());
            modifiedProduits.add(cp);
        }
        
        // Conversion du type de commande pour la base de données
        String typeCommandeDB = convertTypeToDBFormat(commande.getTypeCommande());
        commande.setTypeCommande(typeCommandeDB);
        System.out.println("Type de commande après conversion: " + commande.getTypeCommande());
        
        // Enregistrement
        boolean success = commandeController.modifierCommandeAvecProduits(commande, modifiedProduits);
        
        if (success) {
            System.out.println("Type dans la commande après modification: " + commande.getTypeCommande());
            parentPanel.refreshWithNewOrder(commande);
            dispose();
            showSuccessMessage("Commande modifiée avec succès");
        } else {
            showErrorMessage("Échec de la modification");
        }
    }

    private String convertTypeToDBFormat(String typeUI) {
        return switch(typeUI) {
            case "Livraison" -> "livraison";
            case "Sur place" -> "sur_place";
            case "À emporter" -> "a_emporter";
            default -> typeUI; // au cas où
        };
    }

    private JPanel createProductsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(LIGHT_COLOR);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Liste des produits
        quantitySpinners.clear();
        for (Commande_produits cp : commande.getProduits()) {
            JPanel card = createProductCard(cp);
            panel.add(card);
            panel.add(Box.createRigidArea(new Dimension(0, 15)));
        }
        
        panel.add(Box.createVerticalGlue());
        return panel;
    }

    private JPanel createProductCard(Commande_produits cp) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Fond avec ombre et coins arrondis
                g2d.setColor(CARD_COLOR);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
                // Bordure subtile
                g2d.setColor(BORDER_COLOR);
                g2d.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);
            }
        };
        
        card.setLayout(new BorderLayout(15, 0));
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(15, 15, 15, 15));
        card.setMaximumSize(new Dimension(Short.MAX_VALUE, 120));
        
        // Image du produit
        JLabel imageLabel = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setClip(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                super.paintComponent(g2);
            }
        };
        
        imageLabel.setPreferredSize(new Dimension(100, 100));
        imageLabel.setOpaque(true);
        imageLabel.setBackground(new Color(245, 245, 245));
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setVerticalAlignment(SwingConstants.CENTER);
        
        if (cp.getProduit().getImage() != null && cp.getProduit().getImage().length > 0) {
            ImageIcon icon = new ImageIcon(cp.getProduit().getImage());
            Image scaledImage = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
            imageLabel.setIcon(new ImageIcon(scaledImage));
        } else {
            imageLabel.setIcon(null);
            imageLabel.setText(getCategoryEmoji(cp.getProduit().getCategorie()));
            imageLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
            imageLabel.setForeground(new Color(180, 180, 180));
        }
        
        // Détails du produit
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setOpaque(false);
        detailsPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        
        JLabel nameLabel = new JLabel(cp.getProduit().getNom());
        nameLabel.setFont(FONT_BOLD);
        nameLabel.setForeground(DARK_COLOR);
        
        JLabel priceLabel = new JLabel(String.format("%.2f DT x %d", cp.getProduit().getPrix(), cp.getQuantite()));
        priceLabel.setFont(FONT_REGULAR);
        priceLabel.setForeground(new Color(120, 120, 120));
        
        JLabel subtotalLabel = new JLabel(String.format("Sous-total: %.2f DT", cp.getProduit().getPrix() * cp.getQuantite()));
        subtotalLabel.setFont(FONT_BOLD);
        subtotalLabel.setForeground(PRIMARY_COLOR);
        
        detailsPanel.add(nameLabel);
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        detailsPanel.add(priceLabel);
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        detailsPanel.add(subtotalLabel);
        
        // Contrôles de quantité
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        controlPanel.setOpaque(false);
        
        JButton minusButton = createQuantityButton("-", SECONDARY_COLOR);
        
        // Création du spinner en tant que variable finale pour pouvoir l'utiliser dans les listeners
        final JSpinner spinner = new JSpinner(new SpinnerNumberModel(cp.getQuantite(), 1, 100, 1));
        styleSpinner(spinner);
        quantitySpinners.add(spinner);
        
        spinner.addChangeListener(e -> {
            int qty = (Integer) spinner.getValue();
            subtotalLabel.setText(String.format("Sous-total: %.2f DT", cp.getProduit().getPrix() * qty));
        });
        
        JButton plusButton = createQuantityButton("+", SUCCESS_COLOR);
        
        // Modification des listeners pour utiliser la variable spinner existante
        minusButton.addActionListener(e -> {
            int value = (int) spinner.getValue();
            if (value > 1) {
                spinner.setValue(value - 1);
            }
        });
        
        plusButton.addActionListener(e -> {
            int value = (int) spinner.getValue();
            spinner.setValue(value + 1);
        });
        
        controlPanel.add(minusButton);
        controlPanel.add(spinner);
        controlPanel.add(plusButton);
        
        // Assemblage final
        card.add(imageLabel, BorderLayout.WEST);
        
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(detailsPanel, BorderLayout.CENTER);
        centerPanel.add(controlPanel, BorderLayout.SOUTH);
        
        card.add(centerPanel, BorderLayout.CENTER);
        
        return card;
    }

    private JPanel createInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(LIGHT_COLOR);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 10, 15, 10);
        
        // Style moderne pour les composants
        Font inputFont = FONT_REGULAR;
        Color inputBg = Color.WHITE;
        Color inputBorderColor = new Color(220, 220, 220);
        
        // Type de commande
        JLabel typeLabel = new JLabel("Type de commande:");
        typeLabel.setFont(FONT_BOLD);
        typeLabel.setForeground(new Color(80, 80, 80));
        
        String[] types = {"Livraison", "Sur place", "À emporter"};
        JComboBox<String> typeCombo = new JComboBox<>(types);
        typeCombo.setSelectedItem(commande.getTypeCommande());
        typeCombo.setFont(inputFont);
        typeCombo.setBackground(inputBg);
        typeCombo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(inputBorderColor),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        typeCombo.setPreferredSize(new Dimension(250, 35));
        typeCombo.setRenderer(new ModernComboBoxRenderer());
        
        // Adresse
        JLabel addressLabel = new JLabel("Adresse de livraison:");
        addressLabel.setFont(FONT_BOLD);
        addressLabel.setForeground(new Color(80, 80, 80));
        
        JTextField addressField = new JTextField(commande.getAdresseLivraison(), 25);
        addressField.setFont(inputFont);
        addressField.setBackground(inputBg);
        addressField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(inputBorderColor),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        addressField.setVisible(commande.getTypeCommande().equals("Livraison"));
        
        // Écouteur pour afficher/masquer le champ d'adresse
        typeCombo.addActionListener(e -> {
            String selected = (String) typeCombo.getSelectedItem();
            commande.setTypeCommande(selected); // Mise à jour de l'objet commande
            addressField.setVisible("Livraison".equals(selected));
            panel.revalidate();
            panel.repaint();
        });
        
        // Ajout des composants
        panel.add(typeLabel, gbc);
        gbc.gridx++;
        panel.add(typeCombo, gbc);
        
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(addressLabel, gbc);
        gbc.gridx++;
        panel.add(addressField, gbc);
        
        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        panel.setBackground(LIGHT_COLOR);
        panel.setBorder(new CompoundBorder(
            new MatteBorder(1, 0, 0, 0, new Color(220, 220, 220)),
            new EmptyBorder(15, 0, 0, 0)
        ));
        
        // Bouton Annuler
        JButton cancelButton = createActionButton("Annuler", SECONDARY_COLOR);
        cancelButton.addActionListener(e -> dispose());
        
        // Bouton Enregistrer
        JButton saveButton = createActionButton("Enregistrer", SUCCESS_COLOR);
        saveButton.addActionListener(e -> saveChanges());
        
        panel.add(cancelButton);
        panel.add(saveButton);
        
        return panel;
    }

   

    // Méthodes de style et utilitaires
    private JButton createQuantityButton(String text, Color color) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2d.setColor(color.darker());
                } else if (getModel().isRollover()) {
                    g2d.setColor(color.brighter());
                } else {
                    g2d.setColor(color);
                }
                
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 5, 5);
                
                g2d.setColor(Color.WHITE);
                FontMetrics fm = g2d.getFontMetrics();
                Rectangle2D bounds = fm.getStringBounds(getText(), g2d);
                int x = (getWidth() - (int)bounds.getWidth()) / 2;
                int y = (getHeight() - (int)bounds.getHeight()) / 2 + fm.getAscent();
                g2d.drawString(getText(), x, y);
            }
        };
        
        button.setFont(FONT_BOLD);
        button.setForeground(Color.WHITE);
        button.setContentAreaFilled(false);
        button.setBorder(new EmptyBorder(0, 0, 0, 0));
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(30, 25));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        return button;
    }
    
    private JButton createActionButton(String text, Color color) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2d.setColor(color.darker());
                } else if (getModel().isRollover()) {
                    g2d.setColor(color.brighter());
                } else {
                    g2d.setColor(color);
                }
                
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                
                g2d.setColor(Color.WHITE);
                FontMetrics fm = g2d.getFontMetrics();
                Rectangle2D bounds = fm.getStringBounds(getText(), g2d);
                int x = (getWidth() - (int)bounds.getWidth()) / 2;
                int y = (getHeight() - (int)bounds.getHeight()) / 2 + fm.getAscent();
                g2d.drawString(getText(), x, y);
            }
        };
        
        button.setFont(FONT_BUTTON);
        button.setForeground(Color.WHITE);
        button.setContentAreaFilled(false);
        button.setBorder(new EmptyBorder(10, 25, 10, 25));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        return button;
    }
    
    private void styleSpinner(JSpinner spinner) {
        spinner.setFont(FONT_REGULAR);
        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            JTextField textField = ((JSpinner.DefaultEditor) editor).getTextField();
            textField.setColumns(3);
            textField.setHorizontalAlignment(SwingConstants.CENTER);
            textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
            ));
        }
    }
    
    private void showSuccessMessage(String message) {
        JOptionPane.showMessageDialog(
            this, 
            "<html><div style='text-align: center;'><h3 style='color:#2ecc71;'>" + message + "</h3></div></html>", 
            "Succès", 
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(
            this, 
            "<html><div style='text-align: center;'><h3 style='color:#e74c3c;'>" + message + "</h3></div></html>", 
            "Erreur", 
            JOptionPane.ERROR_MESSAGE
        );
    }
    
    private String getCategoryEmoji(String categorie) {
        if (categorie == null) return "🍴";
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
    
    private static class ModernComboBoxRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, 
                boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            
            setFont(new Font("Segoe UI", Font.PLAIN, 14));
            setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            
            if (isSelected) {
                setBackground(new Color(70, 130, 200));
                setForeground(Color.WHITE);
            } else {
                setBackground(Color.WHITE);
                setForeground(Color.BLACK);
            }
            
            return this;
        }
    }
}