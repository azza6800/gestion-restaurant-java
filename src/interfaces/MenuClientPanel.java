package interfaces;

import javax.swing.*;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;

import entité.Commande;
import entité.Commande_produits;
import entité.Panier;
import entité.Produit;
import Controller.CommandeController;
import Controller.ProduitController;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import entité.PanierItem;

public class MenuClientPanel extends JPanel {
    
    private ProduitController produitController;
    private JPanel productsPanel;
    private Color primaryColor = new Color(40, 110, 180);   // Bleu moderne
    private Color secondaryColor = new Color(255, 105, 97); // Rouge corail
    private Color darkColor = new Color(45, 45, 55);        // Noir profond
    private Color lightColor = new Color(250, 250, 252);    // Blanc cassé
    private Color successColor = new Color(46, 204, 113);   // Vert succès
    private Color warningColor = new Color(241, 196, 15);   // Jaune avertissement
    
    private Panier panier = new Panier();
    private JLabel panierBadge;
    private JDialog panierDialog;
    
    public MenuClientPanel() {
        produitController = new ProduitController();
        initUI();
    }
    
    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(lightColor);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setOpaque(false);
        
        // En-tête avec titre, recherche et panier
        JPanel headerPanel = createHeaderPanel();
        
        // Panel de filtrage par catégorie
        JPanel filterPanel = createFilterPanel();
        
        // Liste des produits dans un panel avec GridLayout
        productsPanel = new JPanel(new GridLayout(0, 3, 20, 20));
        productsPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        productsPanel.setOpaque(false);
        
        loadProducts(null);
        
        // Création du JScrollPane pour les produits
        JScrollPane productsScrollPane = new JScrollPane(productsPanel);
        productsScrollPane.setBorder(null);
        productsScrollPane.setOpaque(false);
        productsScrollPane.getViewport().setOpaque(false);
        productsScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        productsScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        productsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        // Panel de contenu qui contient filterPanel et productsScrollPane
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        contentPanel.add(filterPanel, BorderLayout.NORTH);
        contentPanel.add(productsScrollPane, BorderLayout.CENTER);
        
        // Assemblage final
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        // Titre
        JLabel titleLabel = new JLabel("Notre Menu Gourmand");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(darkColor);
        
        // Panier
        panierBadge = new JLabel("0");
        panierBadge.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panierBadge.setForeground(Color.WHITE);
        panierBadge.setBackground(secondaryColor);
        panierBadge.setOpaque(true);
        panierBadge.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.WHITE, 1),
            BorderFactory.createEmptyBorder(2, 8, 2, 8)
        ));
        
        JButton panierButton = new JButton("Panier");
        panierButton.setIcon(createIcon("https://cdn-icons-png.flaticon.com/512/3144/3144456.png", 20, 20));
        panierButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panierButton.setForeground(primaryColor);
        panierButton.setContentAreaFilled(false);
        panierButton.setBorder(new EmptyBorder(5, 10, 5, 10));
        panierButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panierButton.addActionListener(e -> showPanierDialog());
        
        JPanel panierPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panierPanel.setOpaque(false);
        panierPanel.add(panierBadge);
        panierPanel.add(panierButton);
        
        // Recherche
        JTextField searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(250, 40));
        searchField.setBorder(new RoundBorder(20));
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.putClientProperty("JTextField.placeholderText", "Rechercher un plat...");
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filterProducts(searchField.getText());
            }
        });
        
        // Assemblage header
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(searchField, BorderLayout.CENTER);
        headerPanel.add(panierPanel, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    private JPanel createFilterPanel() {
        JPanel filterPanel = new JPanel(new BorderLayout(10, 0));
        filterPanel.setBackground(lightColor);
        filterPanel.setBorder(new EmptyBorder(15, 0, 20, 0));
        
        // Titre du filtre
        JLabel filterLabel = new JLabel("FILTRER PAR CATÉGORIE");
        filterLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        filterLabel.setForeground(new Color(100, 100, 100));
        filterLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        // Panel pour les boutons de catégorie
        JPanel categoryButtonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        categoryButtonsPanel.setBackground(lightColor);
        categoryButtonsPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        
        String[] categories = {"Toutes", "Plat", "Sandwich", "Salade", "Dessert", "Viennoiserie", "Boisson"};
        
        for (int i = 0; i < categories.length; i++) {
            String category = categories[i];
            JButton categoryButton = createCategoryButton(category, i);
            categoryButton.addActionListener(e -> filterByCategory(category.equals("Toutes") ? null : category));
            categoryButtonsPanel.add(categoryButton);
        }
        
        filterPanel.add(filterLabel, BorderLayout.NORTH);
        filterPanel.add(categoryButtonsPanel, BorderLayout.CENTER);
        
        return filterPanel;
    }
    
    private JButton createCategoryButton(String text, int index) {
        Color[] colors = {
            new Color(30, 85, 135),    // Bleu foncé
            new Color(40, 110, 180),   // Bleu royal
            new Color(50, 130, 210),   // Bleu vif
            new Color(70, 150, 230),   // Bleu ciel
            new Color(90, 170, 240),   // Bleu clair
            new Color(110, 190, 250),  // Bleu pastel
            new Color(130, 210, 255)   // Bleu très clair
        };
        
        Color baseColor = colors[index % colors.length];
        Color hoverColor = baseColor.brighter();
        Color pressedColor = baseColor.darker();
        
        JButton button = new JButton(text.toUpperCase()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2.setColor(pressedColor);
                } else if (getModel().isRollover()) {
                    g2.setColor(hoverColor);
                } else {
                    g2.setColor(baseColor);
                }
                
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                // Texte
                g2.setColor(Color.WHITE);
                FontMetrics fm = g2.getFontMetrics();
                Rectangle2D bounds = fm.getStringBounds(getText(), g2);
                int x = (getWidth() - (int)bounds.getWidth()) / 2;
                int y = (getHeight() - (int)bounds.getHeight()) / 2 + fm.getAscent();
                g2.drawString(getText(), x, y);
                
                g2.dispose();
            }
        };
        
        button.setFont(new Font("Segoe UI Semibold", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setContentAreaFilled(false);
        button.setBorder(new EmptyBorder(8, 20, 8, 20));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(120, 40));
        
        // Icône de catégorie
        String emoji = getCategoryEmoji(text);
        button.setText(emoji + "  " + text.toUpperCase());
        
        return button;
    }
    
    private void filterByCategory(String category) {
        productsPanel.removeAll();
        loadProducts(category);
        productsPanel.revalidate();
        productsPanel.repaint();
    }
    
    private void filterProducts(String searchText) {
        productsPanel.removeAll();
        List<Produit> produits = produitController.getTousProduits();
        
        if (searchText != null && !searchText.isEmpty()) {
            produits = produits.stream()
                .filter(p -> p.getNom().toLowerCase().contains(searchText.toLowerCase()) || 
                            p.getDescription().toLowerCase().contains(searchText.toLowerCase()))
                .collect(Collectors.toList());
        }
        
        for (Produit produit : produits) {
            productsPanel.add(createProductCard(produit));
        }
        
        productsPanel.revalidate();
        productsPanel.repaint();
    }
    
    private void loadProducts(String category) {
        List<Produit> produits = category == null ? 
            produitController.getTousProduits() : 
            produitController.getProduitsParCategorie(category);
        
        for (Produit produit : produits) {
            productsPanel.add(createProductCard(produit));
        }
    }
    
    private JPanel createProductCard(Produit produit) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Ombre portée
                g2d.setColor(new Color(0, 0, 0, 15));
                g2d.fillRoundRect(5, 5, getWidth()-10, getHeight()-10, 25, 25);
                
                // Fond de la carte
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth()-10, getHeight()-10, 25, 25);
            }
        };
        
        card.setLayout(new BorderLayout());
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(300, 420));
        card.setBorder(new EmptyBorder(15, 15, 15, 15));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Image du produit
        JPanel imageContainer = new JPanel(new BorderLayout());
        imageContainer.setPreferredSize(new Dimension(270, 180));
        imageContainer.setBackground(new Color(245, 245, 245));
        imageContainer.setBorder(new CompoundBorder(
            new MatteBorder(0, 0, 3, 0, primaryColor),
            new EmptyBorder(0, 0, 5, 0)
        ));
        imageContainer.setOpaque(false);

        JLabel imageLabel = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setClip(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
                super.paintComponent(g2);
            }
        };
        
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setVerticalAlignment(SwingConstants.CENTER);

        if (produit.getImage() != null && produit.getImage().length > 0) {
            ImageIcon icon = new ImageIcon(produit.getImage());
            Image img = icon.getImage().getScaledInstance(270, 180, Image.SCALE_SMOOTH);
            imageLabel.setIcon(new ImageIcon(img));
        } else {
            imageLabel.setText(getCategoryEmoji(produit.getCategorie()));
            imageLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 60));
            imageLabel.setForeground(new Color(180, 180, 180));
        }

        imageContainer.add(imageLabel);
        card.add(imageContainer, BorderLayout.NORTH);

        // Détails du produit
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBackground(new Color(0, 0, 0, 0));
        detailsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // En-tête avec nom et catégorie
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(0, 0, 0, 0));
        headerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel nameLabel = new JLabel(produit.getNom());
        nameLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 20));
        nameLabel.setForeground(new Color(51, 51, 51));
        
        // Badge de catégorie
        JLabel categoryBadge = new JLabel(produit.getCategorie().toUpperCase()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(primaryColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                super.paintComponent(g2);
            }
        };
        categoryBadge.setFont(new Font("Segoe UI", Font.BOLD, 10));
        categoryBadge.setForeground(Color.WHITE);
        categoryBadge.setBorder(new EmptyBorder(4, 12, 4, 12));
        
        headerPanel.add(nameLabel, BorderLayout.CENTER);
        headerPanel.add(categoryBadge, BorderLayout.EAST);
        
        // Séparateur
        JSeparator separator = new JSeparator(JSeparator.HORIZONTAL);
        separator.setForeground(new Color(220, 220, 220));
        separator.setPreferredSize(new Dimension(100, 2));
        separator.setMaximumSize(new Dimension(Short.MAX_VALUE, 2));

        // Description
        JPanel descPanel = new JPanel(new BorderLayout(10, 0));
        descPanel.setBackground(new Color(0, 0, 0, 0));
        descPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        descPanel.setBorder(new EmptyBorder(10, 0, 15, 0));
        
        JLabel descIcon = new JLabel("📝");
        descIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        
        JTextArea descArea = new JTextArea(produit.getDescription());
        descArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setEditable(false);
        descArea.setBackground(new Color(0, 0, 0, 0));
        descArea.setForeground(new Color(100, 100, 100));
        descArea.setBorder(new EmptyBorder(0, 5, 0, 0));
        
        descPanel.add(descIcon, BorderLayout.WEST);
        descPanel.add(descArea, BorderLayout.CENTER);

        // Pied de carte avec prix et bouton
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(new Color(0, 0, 0, 0));
        footerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Prix
        JPanel pricePanel = new JPanel(new BorderLayout());
        pricePanel.setBackground(new Color(0, 0, 0, 0));
        
        JLabel priceTag = new JLabel("PRIX:");
        priceTag.setFont(new Font("Segoe UI", Font.BOLD, 10));
        priceTag.setForeground(new Color(150, 150, 150));
        priceTag.setBorder(new EmptyBorder(0, 0, 2, 5));
        
        JLabel priceLabel = new JLabel(String.format("%.2f DT", produit.getPrix()));
        priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        priceLabel.setForeground(primaryColor);
        
        pricePanel.add(priceTag, BorderLayout.NORTH);
        pricePanel.add(priceLabel, BorderLayout.CENTER);
        
        // Bouton d'action
        JButton orderButton = new JButton("AJOUTER") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2.setColor(successColor.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(successColor.brighter());
                } else {
                    g2.setColor(successColor);
                }
                
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                
                // Icône et texte
                g2.setColor(Color.WHITE);
                super.paintComponent(g2);
            }
        };
        
        orderButton.setIcon(createIcon("https://cdn-icons-png.flaticon.com/512/3144/3144456.png", 16, 16));
        orderButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        orderButton.setForeground(Color.WHITE);
        orderButton.setContentAreaFilled(false);
        orderButton.setBorder(new EmptyBorder(10, 15, 10, 15));
        orderButton.setFocusPainted(false);
        orderButton.addActionListener(e -> commanderProduit(produit));
        orderButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        footerPanel.add(pricePanel, BorderLayout.WEST);
        footerPanel.add(orderButton, BorderLayout.EAST);

        // Assemblage final
        detailsPanel.add(headerPanel);
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        detailsPanel.add(separator);
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        detailsPanel.add(descPanel);
        detailsPanel.add(Box.createVerticalGlue());
        detailsPanel.add(footerPanel);

        card.add(detailsPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    private void commanderProduit(Produit produit) {
        // Créer un spinner pour la quantité
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(1, 1, 100, 1);
        JSpinner quantiteSpinner = new JSpinner(spinnerModel);
        JComponent editor = ((JSpinner.DefaultEditor)quantiteSpinner.getEditor());
        ((DefaultEditor) editor).getTextField().setFont(new Font("Segoe UI", Font.PLAIN, 14));
        ((DefaultEditor) editor).getTextField().setColumns(3);
        
        // Créer un panel pour le formulaire
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Panel pour la quantité
        JPanel quantitePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        quantitePanel.add(new JLabel("Quantité:"));
        quantitePanel.add(quantiteSpinner);
        
        // Boutons
        JButton ajouterButton = createDialogButton("Ajouter au panier", successColor);
        ajouterButton.addActionListener(e -> {
            int quantite = (int) quantiteSpinner.getValue();
            panier.ajouterProduit(produit, quantite);
            updatePanierBadge();
            ((Window)SwingUtilities.getRoot(panel)).dispose();
            showSuccessMessage("Produit ajouté au panier!");
        });
        
        JButton commanderButton = createDialogButton("Commander maintenant", primaryColor);
        commanderButton.addActionListener(e -> {
            int quantite = (int) quantiteSpinner.getValue();
            panier.ajouterProduit(produit, quantite);
            updatePanierBadge();
            ((Window)SwingUtilities.getRoot(panel)).dispose();
            showCommanderDialog();
        });
        
        JButton annulerButton = createDialogButton("Annuler", new Color(180, 180, 180));
        annulerButton.addActionListener(e -> ((Window)SwingUtilities.getRoot(panel)).dispose());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.add(annulerButton);
        buttonPanel.add(ajouterButton);
        buttonPanel.add(commanderButton);
        
        // Assemblage
        panel.add(new JLabel("<html><h3>Ajouter " + produit.getNom() + " au panier</h3></html>"), BorderLayout.NORTH);
        panel.add(quantitePanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Afficher la boîte de dialogue
        JDialog dialog = new JDialog();
        dialog.setTitle("Ajouter au panier");
        dialog.setContentPane(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void showPanierDialog() {
        if (panier.getItems().isEmpty()) {
            showInfoMessage("Votre panier est vide", "Parcourez notre menu pour ajouter des délicieux produits");
            return;
        }
        
        if (panierDialog != null) {
            panierDialog.dispose();
        }
        
        panierDialog = new JDialog();
        panierDialog.setTitle("Votre Panier");
        panierDialog.setLayout(new BorderLayout());
        panierDialog.setModal(true);
        panierDialog.setPreferredSize(new Dimension(700, 600));
        panierDialog.setMinimumSize(new Dimension(500, 400));
        
        // Panel principal avec fond
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // En-tête avec titre et bouton de fermeture
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("VOTRE PANIER");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(darkColor);
        
        JButton closeButton = new JButton("×");
        closeButton.setFont(new Font("Segoe UI", Font.BOLD, 20));
        closeButton.setContentAreaFilled(false);
        closeButton.setBorder(new EmptyBorder(0, 0, 0, 0));
        closeButton.setForeground(new Color(120, 120, 120));
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeButton.addActionListener(e -> panierDialog.dispose());
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(closeButton, BorderLayout.EAST);
        
        // Panel des articles avec BoxLayout
        JPanel itemsPanel = new JPanel();
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
        itemsPanel.setOpaque(false);
        itemsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        for (PanierItem item : panier.getItems()) {
            itemsPanel.add(createCartItemPanel(item));
            itemsPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        }
        
        // Ajout d'un glue pour pousser le contenu vers le haut
        itemsPanel.add(Box.createVerticalGlue());
        
        // Création du JScrollPane pour les articles
        JScrollPane itemsScrollPane = new JScrollPane(itemsPanel);
        itemsScrollPane.setBorder(null);
        itemsScrollPane.setOpaque(false);
        itemsScrollPane.getViewport().setOpaque(false);
        itemsScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        itemsScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        // Panel du total
        JPanel totalPanel = new JPanel(new BorderLayout());
        totalPanel.setOpaque(false);
        totalPanel.setBorder(new CompoundBorder(
            new MatteBorder(1, 0, 0, 0, new Color(220, 220, 220)),
            new EmptyBorder(15, 0, 15, 0)
        ));
        
        JLabel totalLabel = new JLabel(String.format("TOTAL: %.2f DT", panier.getTotal()));
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        totalLabel.setForeground(primaryColor);
        
        totalPanel.add(totalLabel, BorderLayout.EAST);
        
        // Panel des boutons d'action
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(15, 0, 0, 0));
        
        JButton emptyButton = createActionButton("Vider le panier", secondaryColor);
        emptyButton.addActionListener(e -> {
            panier.vider();
            updatePanierBadge();
            panierDialog.dispose();
            showSuccessMessage("Votre panier a été vidé avec succès");
        });
        
        JButton orderButton = createActionButton("Passer commande", successColor);
        orderButton.addActionListener(e -> {
            panierDialog.dispose();
            showCommanderDialog();
        });
        
        buttonPanel.add(emptyButton);
        buttonPanel.add(orderButton);
        
        // Assemblage final
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(itemsScrollPane, BorderLayout.CENTER);
        mainPanel.add(totalPanel, BorderLayout.SOUTH);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        panierDialog.add(mainPanel, BorderLayout.CENTER);
        panierDialog.pack();
        panierDialog.setLocationRelativeTo(this);
        panierDialog.setVisible(true);
    }
    
    private JPanel createCartItemPanel(PanierItem item) {
        JPanel panel = new JPanel(new BorderLayout(15, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Fond avec ombre et coins arrondis
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
                // Bordure subtile
                g2d.setColor(new Color(230, 230, 230));
                g2d.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);
            }
        };
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.setMaximumSize(new Dimension(Short.MAX_VALUE, 120));
        
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
        
        if (item.getProduit().getImage() != null && item.getProduit().getImage().length > 0) {
            ImageIcon icon = new ImageIcon(item.getProduit().getImage());
            Image scaledImage = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
            imageLabel.setIcon(new ImageIcon(scaledImage));
        } else {
            imageLabel.setText(getCategoryEmoji(item.getProduit().getCategorie()));
            imageLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
            imageLabel.setForeground(new Color(180, 180, 180));
        }
        
        // Détails du produit
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setOpaque(false);
        detailsPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        
        JLabel nameLabel = new JLabel(item.getProduit().getNom());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        nameLabel.setForeground(darkColor);
        
        JLabel priceLabel = new JLabel(String.format("%.2f DT x %d", 
            item.getProduit().getPrix(), item.getQuantite()));
        priceLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        priceLabel.setForeground(new Color(120, 120, 120));
        
        JLabel subtotalLabel = new JLabel(String.format("Sous-total: %.2f DT", 
            item.getProduit().getPrix() * item.getQuantite()));
        subtotalLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        subtotalLabel.setForeground(primaryColor);
        
        detailsPanel.add(nameLabel);
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        detailsPanel.add(priceLabel);
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        detailsPanel.add(subtotalLabel);
        
        // Contrôles de quantité
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        controlPanel.setOpaque(false);
        
        JButton minusButton = createQuantityButton("-", secondaryColor);
        minusButton.addActionListener(e -> {
            if (item.getQuantite() > 1) {
                item.setQuantite(item.getQuantite() - 1);
                updatePanierBadge();
                refreshCartItem(panel, item);
            } else {
                panier.retirerProduit(item.getProduit().getId());
                updatePanierBadge();
                refreshCartDialog();
            }
        });
        
        JLabel quantityLabel = new JLabel(String.valueOf(item.getQuantite()));
        quantityLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        quantityLabel.setForeground(darkColor);
        quantityLabel.setBorder(new EmptyBorder(0, 10, 0, 10));
        
        JButton plusButton = createQuantityButton("+", successColor);
        plusButton.addActionListener(e -> {
            item.setQuantite(item.getQuantite() + 1);
            updatePanierBadge();
            refreshCartItem(panel, item);
        });
        
        controlPanel.add(minusButton);
        controlPanel.add(quantityLabel);
        controlPanel.add(plusButton);
        
        // Bouton de suppression
        JButton deleteButton = new JButton("✕");
        deleteButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        deleteButton.setContentAreaFilled(false);
        deleteButton.setBorder(new EmptyBorder(0, 0, 0, 0));
        deleteButton.setForeground(new Color(180, 180, 180));
        deleteButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        deleteButton.addActionListener(e -> {
            panier.retirerProduit(item.getProduit().getId());
            updatePanierBadge();
            refreshCartDialog();
        });
        
        // Assemblage final
        panel.add(imageLabel, BorderLayout.WEST);
        panel.add(detailsPanel, BorderLayout.CENTER);
        panel.add(controlPanel, BorderLayout.SOUTH);
        panel.add(deleteButton, BorderLayout.EAST);
        
        return panel;
    }
    private void refreshCartItem(JPanel itemPanel, PanierItem item) {
        // Parcourir tous les composants du panel d'item
        for (Component comp : itemPanel.getComponents()) {
            if (comp instanceof JPanel) {
                JPanel innerPanel = (JPanel) comp;
                // Parcourir les composants du panel interne
                for (Component innerComp : innerPanel.getComponents()) {
                    if (innerComp instanceof JLabel) {
                        JLabel label = (JLabel) innerComp;
                        // Mettre à jour le label de quantité
                        if (label.getText().matches("\\d+")) {
                            label.setText(String.valueOf(item.getQuantite()));
                        }
                        // Mettre à jour le prix x quantité
                        else if (label.getText().contains("x")) {
                            label.setText(String.format("%.2f DT x %d", 
                                item.getProduit().getPrix(), item.getQuantite()));
                        }
                        // Mettre à jour le sous-total
                        else if (label.getText().contains("Sous-total")) {
                            label.setText(String.format("Sous-total: %.2f DT", 
                                item.getProduit().getPrix() * item.getQuantite()));
                        }
                    }
                }
            }
        }

        // Mettre à jour le total général dans le panier
        updateTotalInCartDialog();
    }

    private void updateTotalInCartDialog() {
        if (panierDialog != null) {
            Component[] comps = panierDialog.getContentPane().getComponents();
            for (Component comp : comps) {
                if (comp instanceof JPanel) {
                    JPanel panel = (JPanel) comp;
                    for (Component subComp : panel.getComponents()) {
                        if (subComp instanceof JLabel && ((JLabel)subComp).getText().contains("TOTAL")) {
                            ((JLabel)subComp).setText(String.format("TOTAL: %.2f DT", panier.getTotal()));
                        }
                    }
                }
            }
        }
    }
    
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
        
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setContentAreaFilled(false);
        button.setBorder(new EmptyBorder(0, 0, 0, 0));
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(30, 25));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
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
        
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setContentAreaFilled(false);
        button.setBorder(new EmptyBorder(10, 25, 10, 25));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return button;
    }
    
    private JButton createDialogButton(String text, Color color) {
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
        
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setContentAreaFilled(false);
        button.setBorder(new EmptyBorder(8, 15, 8, 15));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return button;
    }
    
   
    
    private void refreshCartDialog() {
        if (panier.getItems().isEmpty()) {
            panierDialog.dispose();
            showInfoMessage("Votre panier est vide", "Parcourez notre menu pour ajouter des délicieux produits");
        } else {
            panierDialog.getContentPane().removeAll();
            showPanierDialog();
        }
    }
    
    private void updatePanierBadge() {
        int totalItems = panier.getItems().stream()
                              .mapToInt(PanierItem::getQuantite)
                              .sum();
        panierBadge.setText(String.valueOf(totalItems));
        panierBadge.setVisible(totalItems > 0);
    }
    
    private void showCommanderDialog() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header avec icône et titre
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        headerPanel.setBackground(new Color(245, 245, 245));
        
        JLabel titleLabel = new JLabel("Finaliser la commande");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(51, 51, 51));
        headerPanel.add(titleLabel);

        panel.add(headerPanel, BorderLayout.NORTH);

        // Panel pour les options de commande
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(new Color(245, 245, 245));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // Style moderne pour les composants
        Font inputFont = new Font("Segoe UI", Font.PLAIN, 14);
        Color inputBg = Color.WHITE;
        Color inputBorderColor = new Color(220, 220, 220);

        // Type de commande
        JPanel typePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        typePanel.setBackground(new Color(245, 245, 245));
        JLabel typeLabel = new JLabel("Type de commande:");
        typeLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        typeLabel.setForeground(new Color(80, 80, 80));
        typePanel.add(typeLabel);

        JComboBox<String> typeCombo = new JComboBox<>(new String[]{
            "Livraison à domicile", 
            "Sur place", 
            "À emporter"
        });
        typeCombo.setFont(inputFont);
        typeCombo.setBackground(inputBg);
        typeCombo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(inputBorderColor),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        typeCombo.setPreferredSize(new Dimension(250, 35));
        typeCombo.setRenderer(new ModernComboBoxRenderer());
        typePanel.add(typeCombo);

        // Adresse (cachée par défaut)
        JPanel addressPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        addressPanel.setBackground(new Color(245, 245, 245));
        JLabel addressLabel = new JLabel("Adresse de livraison:");
        addressLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        addressLabel.setForeground(new Color(80, 80, 80));
        addressPanel.add(addressLabel);

        JTextField adresseField = new JTextField();
        adresseField.setFont(inputFont);
        adresseField.setBackground(inputBg);
        adresseField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(inputBorderColor),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        adresseField.setPreferredSize(new Dimension(250, 35));
        adresseField.setVisible(false);
        addressPanel.add(adresseField);

        // Écouteur pour afficher/masquer le champ d'adresse
        typeCombo.addActionListener(e -> {
            String selected = (String) typeCombo.getSelectedItem();
            adresseField.setVisible("Livraison à domicile".equals(selected));
            panel.revalidate();
            panel.repaint();
        });

        formPanel.add(typePanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        formPanel.add(addressPanel);

        panel.add(formPanel, BorderLayout.CENTER);

        // Panel pour les boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(new Color(245, 245, 245));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        // Bouton Annuler
        JButton cancelButton = createActionButton("Annuler", new Color(220, 80, 80));
        cancelButton.addActionListener(e -> {
            ((Window)SwingUtilities.getRoot(panel)).dispose();
        });

        // Bouton Valider
        JButton confirmButton = createActionButton("Confirmer", new Color(80, 180, 80));
        confirmButton.addActionListener(e -> {
            String typeCommande = (String) typeCombo.getSelectedItem();
            String adresse = adresseField.getText();
            
            // Correction: Ne valider l'adresse QUE pour la livraison
            if ("Livraison à domicile".equals(typeCommande)) {
                if (adresse == null || adresse.trim().isEmpty()) {
                    showErrorMessage("Veuillez saisir une adresse de livraison");
                    return;
                }
            }
            
            // Convertir le type de commande pour la base de données
            String typeCommandeDB = "";
            switch(typeCommande) {
                case "Livraison à domicile": 
                    typeCommandeDB = "livraison";
                    break;
                case "Sur place": 
                    typeCommandeDB = "sur_place";
                    break;
                case "À emporter": 
                    typeCommandeDB = "a_emporter";
                    break;
                default:
                    typeCommandeDB = "sur_place"; // Valeur par défaut
            }
            
            // Créer la commande
            Commande commande = new Commande(
                0, // id sera généré
                getCurrentClientId(),
                new Date(),
                typeCommandeDB,
                Commande.EN_ATTENTE,
                "Livraison à domicile".equals(typeCommande) ? adresse : ""
            );
            
            // Créer les lignes de commande
            List<Commande_produits> lignes = panier.getItems().stream()
                .map(item -> new Commande_produits(item.getProduit(), item.getQuantite()))
                .collect(Collectors.toList());
            
            // Enregistrer la commande
            CommandeController commandeController = new CommandeController();
            boolean success = commandeController.creerCommandeAvecProduits(commande, lignes);
            
            if (success) {
                showSuccessMessage("Commande passée avec succès!");
                panier.vider();
                updatePanierBadge();
                ((Window)SwingUtilities.getRoot(panel)).dispose();
                
                // Rafraîchir l'interface des commandes
                refreshOrdersClientPanel(commande);
                
                // Ouvrir automatiquement l'onglet des commandes
                showOrdersTab();
            } else {
                showErrorMessage("Erreur lors de la commande");
            }
        });

        buttonPanel.add(cancelButton);
        buttonPanel.add(confirmButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Créer une fenêtre modale
        JDialog dialog = new JDialog();
        dialog.setTitle("Confirmation de commande");
        dialog.setModal(true);
        dialog.setContentPane(panel);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    private void showOrdersTab() {
        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        if (parentWindow instanceof JFrame) {
            JFrame frame = (JFrame) parentWindow;
            
            for (Component comp : frame.getContentPane().getComponents()) {
                if (comp instanceof JTabbedPane) {
                    JTabbedPane tabbedPane = (JTabbedPane) comp;
                    
                    // Trouver l'index de l'onglet "Mes Commandes"
                    for (int i = 0; i < tabbedPane.getTabCount(); i++) {
                        if ("Mes Commandes".equals(tabbedPane.getTitleAt(i))) {
                            tabbedPane.setSelectedIndex(i);
                            return;
                        }
                    }
                    
                    // Si l'onglet n'existe pas, le créer
                    int clientId = getCurrentClientId();
                    OrdersClientPanel ordersPanel = new OrdersClientPanel(clientId);
                    tabbedPane.addTab("Mes Commandes", ordersPanel);
                    tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
                }
            }
        }
    }

    private void refreshOrdersClientPanel(Commande nouvelleCommande) {
        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        if (parentWindow instanceof JFrame) {
            JFrame frame = (JFrame) parentWindow;
            
            // Trouver le JTabbedPane parent
            for (Component comp : frame.getContentPane().getComponents()) {
                if (comp instanceof JTabbedPane) {
                    JTabbedPane tabbedPane = (JTabbedPane) comp;
                    
                    // Vérifier si le panneau des commandes existe déjà
                    for (int i = 0; i < tabbedPane.getTabCount(); i++) {
                        if (tabbedPane.getTitleAt(i).equals("Mes Commandes")) {
                            Component tabComponent = tabbedPane.getComponentAt(i);
                            if (tabComponent instanceof OrdersClientPanel) {
                                OrdersClientPanel ordersPanel = (OrdersClientPanel) tabComponent;
                                ordersPanel.refreshWithNewOrder(nouvelleCommande);
                                tabbedPane.setSelectedIndex(i);
                                return;
                            }
                        }
                    }
                    
                    // Si le panneau n'existe pas encore, le créer
                    int clientId = getCurrentClientId();
                    OrdersClientPanel ordersPanel = new OrdersClientPanel(clientId, nouvelleCommande);
                    
                    // Ajouter l'onglet et le sélectionner
                    int newTabIndex = tabbedPane.getTabCount();
                    tabbedPane.addTab("Mes Commandes", ordersPanel);
                    tabbedPane.setSelectedIndex(newTabIndex);
                    
                    break;
                }
            }
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
    
    private void showInfoMessage(String title, String message) {
        JOptionPane.showMessageDialog(
            this, 
            "<html><div style='text-align: center;'><h3>" + title + "</h3><p>" + message + "</p></div></html>", 
            "Information", 
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
    
    private ImageIcon createIcon(String url, int width, int height) {
        try {
            URL imageUrl = new URL(url);
            ImageIcon originalIcon = new ImageIcon(imageUrl);
            Image scaledImage = originalIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
        } catch (Exception e) {
            return null;
        }
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
    
    private int getCurrentClientId() {
        // À implémenter selon votre système d'authentification
        return 1; // Exemple
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
    
    class RoundBorder extends AbstractBorder {
        private int radius;
        
        public RoundBorder(int radius) {
            this.radius = radius;
        }
        
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(c.getBackground().darker());
            g2.drawRoundRect(x, y, width-1, height-1, radius, radius);
            g2.dispose();
        }
        
        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(radius/2, radius/2, radius/2, radius/2);
        }
    }
}