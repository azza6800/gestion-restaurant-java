package interfaces;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import Controller.CommandeController;
import entité.Commande;
import entité.Commande_produits;
import entité.Produit;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;
import java.io.File;
import javax.imageio.ImageIO;

public class OrdersPanel extends JPanel {
    private GerantDashboard parent;
    private JTable commandesTable;
    private DefaultTableModel tableModel;
    private CommandeController commandeController;
    private JComboBox<String> statusComboBox;
    
    // Nouvelle palette de couleurs élégantes
    private Color primaryColor = new Color(72, 129, 168);    // Bleu sophistiqué
    private Color secondaryColor = new Color(248, 249, 250); // Gris très clair
    private Color accentColor = new Color(108, 91, 123);     // Violet doux
    private Color darkColor = new Color(33, 37, 41);         // Noir profond
    private Color successColor = new Color(100, 200, 120);   // Vert clair moderne
    private Color warningColor = new Color(255, 220, 100);   // Or clair
    private Color errorColor = new Color(255, 120, 120);     // Rouge clair
    private Color deliveredColor = new Color(120, 200, 150); // Vert livraison clair
    private Color textColor = new Color(52, 58, 64);         // Gris foncé pour texte
    
    // Chemin des images par défaut
    private String defaultProductImagePath = "path/to/default/product/image.png";
    
    public OrdersPanel(GerantDashboard parent) {
        this.parent = parent;
        this.commandeController = new CommandeController();
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(secondaryColor);
        
        // Configuration de la police globale
        Font mainFont = new Font("Segoe UI", Font.PLAIN, 14);
        UIManager.put("Label.font", mainFont);
        UIManager.put("Button.font", mainFont.deriveFont(Font.BOLD));
        UIManager.put("ComboBox.font", mainFont);

        // Panel titre avec style moderne
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        

        JLabel titleLabel = new JLabel("GESTION DES COMMANDES");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(darkColor);
        topPanel.add(titleLabel, BorderLayout.WEST);
         
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterPanel.setOpaque(false);
        
        // Bouton "Tous"
        JButton allButton = createFilterButton("Tous");
        allButton.addActionListener(e -> refreshCommandes());
        
        // Bouton "Livraison"
        JButton deliveryButton = createFilterButton("Livraison");
        deliveryButton.addActionListener(e -> filterCommandesByType("livraison"));
        
        // Bouton "Sur place"
        JButton onSiteButton = createFilterButton("Sur place");
        onSiteButton.addActionListener(e -> filterCommandesByType("sur_place"));
        
        // Bouton "À emporter"
        JButton takeawayButton = createFilterButton("À emporter");
        takeawayButton.addActionListener(e -> filterCommandesByType("a_emporter"));
        
        filterPanel.add(allButton);
        filterPanel.add(deliveryButton);
        filterPanel.add(onSiteButton);
        filterPanel.add(takeawayButton);
        
        topPanel.add(filterPanel, BorderLayout.CENTER);
        
        // Bouton rafraîchir moderne
        JButton refreshButton = createModernButton("Rafraîchir", new Color(108, 117, 125));
        refreshButton.addActionListener(e -> refreshCommandes());
        topPanel.add(refreshButton, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // Tableau des commandes avec style moderne
        String[] columnNames = {"ID", "Client", "Date", "Type", "Statut", "Adresse", "Total"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 6 ? Double.class : Object.class;
            }
        };

        commandesTable = new JTable(tableModel);
        configureTableAppearance();

        // ScrollPane avec style épuré
        JScrollPane scrollPane = new JScrollPane(commandesTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        add(scrollPane, BorderLayout.CENTER);

        // Panel des actions avec style moderne
        JPanel actionPanel = createActionPanel();
        add(actionPanel, BorderLayout.SOUTH);

        // Écouteur de sélection
        commandesTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateButtonStates();
            }
        });

        // Chargement initial
        refreshCommandes();
    }
    private JButton createFilterButton(String text) {
        JButton button = new JButton(text);
        
        // Police moderne et lisible
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        // Couleurs plus modernes et contraste
        button.setForeground(textColor);
        button.setBackground(secondaryColor);
        
        // Bordure plus subtile avec effet de profondeur
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(primaryColor.darker(), 1),
            new EmptyBorder(8, 20, 8, 20)
        ));
        
        // Effets de survol et de clic plus sophistiqués
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(true);
        
        // Animation au survol
        button.addMouseListener(new MouseAdapter() {
            private final Color originalBg = secondaryColor;
            private final Color hoverBg = new Color(
                Math.min(secondaryColor.getRed() + 20, 255),
                Math.min(secondaryColor.getGreen() + 20, 255),
                Math.min(secondaryColor.getBlue() + 20, 255)
            );
            private final Color pressBg = primaryColor;
            
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverBg);
                button.setForeground(primaryColor);
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(primaryColor, 1),
                    new EmptyBorder(8, 20, 8, 20)
                ));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(originalBg);
                button.setForeground(textColor);
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(primaryColor.darker(), 1),
                    new EmptyBorder(8, 20, 8, 20)
                ));
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                button.setBackground(pressBg);
                button.setForeground(Color.WHITE);
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                button.setBackground(hoverBg);
                button.setForeground(primaryColor);
            }
        });
        
        // Effet de transition douce
        button.getModel().addChangeListener(e -> {
            if (button.getModel().isRollover()) {
                button.repaint();
            }
        });
        
        return button;
    }
    private void filterCommandesByType(String type) {
        // Les valeurs de type doivent correspondre à celles utilisées dans getStatusesForOrderType()
        SwingWorker<List<Commande>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Commande> doInBackground() throws Exception {
                return commandeController.getCommandesByType(type);
            }
            
            @Override
            protected void done() {
                try {
                    List<Commande> commandes = get();
                    tableModel.setRowCount(0);
                    
                    for (Commande commande : commandes) {
                        double total = calculerTotalCommande(commande);
                        tableModel.addRow(new Object[]{
                            commande.getId(),
                            commande.getClientId(),
                            formatDate(commande.getDateCommande()),
                            capitalizeFirstLetter(commande.getTypeCommande()), // Ici la valeur est capitalisée
                            capitalizeFirstLetter(commande.getStatut()),
                            commande.getAdresseLivraison(),
                            total
                        });
                    }
                } catch (Exception ex) {
                    showErrorMessage("Erreur", "Erreur de filtrage: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }
    private void configureTableAppearance() {
        commandesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        commandesTable.setShowVerticalLines(false);
        commandesTable.setShowHorizontalLines(false);
        commandesTable.setRowHeight(45);
        commandesTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        commandesTable.setBorder(BorderFactory.createEmptyBorder());
        commandesTable.setSelectionBackground(new Color(primaryColor.getRed(), primaryColor.getGreen(), primaryColor.getBlue(), 30));
        commandesTable.setSelectionForeground(darkColor);
        commandesTable.setIntercellSpacing(new Dimension(0, 0));

        // Style alterné des lignes
        commandesTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                setBorder(new EmptyBorder(0, 15, 0, 15));
                
                if (!isSelected) {
                    setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 249, 250));
                }
                
                // Style spécifique aux colonnes
                if (column == 4) { // Colonne Statut
                    String status = (String) value;
                    setForeground(getStatusColor(status));
                    setFont(new Font("Segoe UI", Font.BOLD, 14));
                } else if (column == 6) { // Colonne Total
                    setForeground(primaryColor);
                    setHorizontalAlignment(SwingConstants.RIGHT);
                } else {
                    setForeground(textColor);
                }
                
                return this;
            }
        });

        // Style de l'en-tête
        JTableHeader header = commandesTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(240, 241, 242));
        header.setForeground(new Color(73, 80, 87));
        header.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(0, 0, 2, 0, new Color(222, 226, 230)),
            new EmptyBorder(10, 15, 10, 15)
        ));
    }

    private JPanel createActionPanel() {
        JPanel actionPanel = new JPanel();
        actionPanel.setLayout(new BoxLayout(actionPanel, BoxLayout.X_AXIS));
        actionPanel.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(1, 0, 0, 0, new Color(222, 226, 230)),
            new EmptyBorder(20, 0, 0, 0)
        ));
        actionPanel.setOpaque(false);
     // Initialiser avec un modèle vide
        statusComboBox = new JComboBox<>(new DefaultComboBoxModel<>());
        styleModernComboBox(statusComboBox);
        statusComboBox.setEnabled(false);

        // ComboBox des statuts avec style moderne
        String[] statuses = {"en attente", "en préparation", "prête", "livrée", "annulée"};
        statusComboBox = new JComboBox<>(statuses);
        styleModernComboBox(statusComboBox);
        statusComboBox.setEnabled(false);

        // Boutons d'action modernes
        JButton updateStatusButton = createModernButton("Mettre à jour", primaryColor);
        updateStatusButton.setEnabled(false);
        updateStatusButton.addActionListener(e -> updateCommandeStatus());

        JButton detailsButton = createModernButton("Détails", accentColor);
        detailsButton.setEnabled(false);
        detailsButton.addActionListener(e -> showCommandeDetails());

        JButton deleteButton = createModernButton("Supprimer", errorColor);
        deleteButton.setEnabled(false);
        deleteButton.addActionListener(e -> deleteCommande());

        actionPanel.add(Box.createHorizontalGlue());
        actionPanel.add(new JLabel("Nouveau statut:"));
        actionPanel.add(Box.createHorizontalStrut(15));
        actionPanel.add(statusComboBox);
        actionPanel.add(Box.createHorizontalStrut(25));
        actionPanel.add(updateStatusButton);
        actionPanel.add(Box.createHorizontalStrut(15));
        actionPanel.add(detailsButton);
        actionPanel.add(Box.createHorizontalStrut(15));
        actionPanel.add(deleteButton);

        return actionPanel;
    }

    private JButton createModernButton(String text, Color bgColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Fond avec bord arrondi
                if (getModel().isPressed()) {
                    g2.setColor(bgColor.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(bgColor.brighter());
                } else {
                    g2.setColor(bgColor);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                
                // Texte
                g2.setColor(Color.WHITE);
                FontMetrics fm = g2.getFontMetrics();
                Rectangle2D r = fm.getStringBounds(text, g2);
                int x = (getWidth() - (int) r.getWidth()) / 2;
                int y = (getHeight() - (int) r.getHeight()) / 2 + fm.getAscent();
                g2.drawString(text, x, y);
                
                g2.dispose();
            }
            
            @Override
            public void setContentAreaFilled(boolean b) {}
            @Override
            public void setBorderPainted(boolean b) {}
        };
        
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(140, 40));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setFocusPainted(false);
        
        return button;
    }

    private void styleModernComboBox(JComboBox<String> comboBox) {
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboBox.setBackground(Color.WHITE);
        comboBox.setForeground(textColor);
        comboBox.setBorder(BorderFactory.createCompoundBorder(
            new RoundBorder(8, new Color(206, 212, 218)),
            new EmptyBorder(5, 15, 5, 15)
        ));
        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, 
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setFont(new Font("Segoe UI", Font.PLAIN, 14));
                if (isSelected) {
                    setBackground(new Color(233, 236, 239));
                    setForeground(textColor);
                }
                setBorder(new EmptyBorder(5, 15, 5, 15));
                return this;
            }
        });
    }

    private void refreshCommandes() {
        SwingWorker<List<Commande>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Commande> doInBackground() throws Exception {
                return commandeController.getAllCommandes();
            }
            
            @Override
            protected void done() {
                try {
                    List<Commande> commandes = get();
                    tableModel.setRowCount(0);
                    
                    for (Commande commande : commandes) {
                        double total = calculerTotalCommande(commande);
                        tableModel.addRow(new Object[]{
                            commande.getId(),
                            commande.getClientId(),
                            formatDate(commande.getDateCommande()),
                            capitalizeFirstLetter(commande.getTypeCommande()),
                            capitalizeFirstLetter(commande.getStatut()),
                            commande.getAdresseLivraison(),
                            total
                        });
                    }
                } catch (Exception ex) {
                	showErrorMessage("Erreur", "Erreur de chargement: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    private String formatDate(java.util.Date date) {
        return new java.text.SimpleDateFormat("dd MMM yyyy HH:mm").format(date);
    }

    private String capitalizeFirstLetter(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    private double calculerTotalCommande(Commande commande) {
        return commande.getProduits().stream()
                .mapToDouble(cp -> cp.getProduit().getPrix() * cp.getQuantite())
                .sum();
    }

    

    private void updateCommandeStatus() {
        int selectedRow = commandesTable.getSelectedRow();
        if (selectedRow == -1) return;
        
        int commandeId = (int) tableModel.getValueAt(selectedRow, 0);
        String newStatus = (String) statusComboBox.getSelectedItem();
        
        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return commandeController.changerStatutCommande(commandeId, newStatus);
            }
            
            @Override
            protected void done() {
                try {
                    if (get()) {
                        tableModel.setValueAt(capitalizeFirstLetter(newStatus), selectedRow, 4);
                        showSuccessMessage("Statut de la commande mis à jour", 
                                          "Le statut de la commande #" + commandeId + " a été mis à jour avec succès.");
                    } else {
                        showErrorMessage("Échec de la mise à jour", "Impossible de mettre à jour le statut de la commande.");
                    }
                } catch (Exception ex) {
                    showErrorMessage("Erreur", "Une erreur est survenue: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void showCommandeDetails() {
        int selectedRow = commandesTable.getSelectedRow();
        if (selectedRow == -1) return;
        
        int commandeId = (int) tableModel.getValueAt(selectedRow, 0);
        
        SwingWorker<Commande, Void> worker = new SwingWorker<>() {
            @Override
            protected Commande doInBackground() throws Exception {
                return commandeController.getCommandeById(commandeId);
            }
            
            @Override
            protected void done() {
                try {
                    Commande commande = get();
                    if (commande == null) {
                        showErrorMessage("Commande introuvable", "La commande sélectionnée n'a pas été trouvée.");
                        return;
                    }
                    showModernDetailsDialog(commande);
                } catch (Exception ex) {
                    showErrorMessage("Erreur de chargement", "Erreur lors du chargement des détails: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }
   

    private void showModernDetailsDialog(Commande commande) {
        JDialog dialog = new JDialog(parent, "Détails Commande #" + commande.getId(), true);
        dialog.setSize(900, 650); // Augmenté pour accommoder les images
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(secondaryColor);
        
        // En-tête avec style moderne
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(primaryColor);
        headerPanel.setBorder(new EmptyBorder(15, 25, 15, 25));
        
        JLabel titleLabel = new JLabel("COMMANDE #" + commande.getId());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        JLabel statusLabel = new JLabel(capitalizeFirstLetter(commande.getStatut()));
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        statusLabel.setForeground(getStatusColor(commande.getStatut()));
        statusLabel.setBorder(new EmptyBorder(0, 0, 0, 10));
        headerPanel.add(statusLabel, BorderLayout.EAST);
        
        dialog.add(headerPanel, BorderLayout.NORTH);
        
        // Contenu principal avec onglets
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabbedPane.setBackground(Color.WHITE);
        tabbedPane.setForeground(darkColor);
        
        // Onglet Informations
        JPanel infoPanel = createModernInfoPanel(commande);
        tabbedPane.addTab("Informations", infoPanel);
        
        // Onglet Produits
        JPanel productsPanel = createModernProductsPanel(commande);
        tabbedPane.addTab("Produits", productsPanel);
        
        dialog.add(tabbedPane, BorderLayout.CENTER);
        
        // Pied de page avec total
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(Color.WHITE);
        footerPanel.setBorder(new EmptyBorder(15, 25, 15, 25));
        
        double total = calculerTotalCommande(commande);
        JLabel totalLabel = new JLabel("Total: " + String.format("%.2f €", total));
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        totalLabel.setForeground(primaryColor);
        
        JButton closeButton = createModernButton("Fermer", darkColor);
        closeButton.addActionListener(e -> dialog.dispose());
        
        footerPanel.add(totalLabel, BorderLayout.WEST);
        footerPanel.add(closeButton, BorderLayout.EAST);
        
        dialog.add(footerPanel, BorderLayout.SOUTH);
        
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    private JPanel createModernInfoPanel(Commande commande) {
        JPanel panel = new JPanel(new GridLayout(0, 2, 20, 15));
        panel.setBorder(new EmptyBorder(25, 25, 25, 25));
        panel.setBackground(Color.WHITE);
        
        addModernInfoRow(panel, "ID Commande:", String.valueOf(commande.getId()));
        addModernInfoRow(panel, "Client ID:", String.valueOf(commande.getClientId()));
        addModernInfoRow(panel, "Date:", formatDate(commande.getDateCommande()));
        addModernInfoRow(panel, "Type:", capitalizeFirstLetter(commande.getTypeCommande()));
        addModernInfoRow(panel, "Statut:", capitalizeFirstLetter(commande.getStatut()));
        addModernInfoRow(panel, "Adresse:", commande.getAdresseLivraison());
        
        return panel;
    }

    private void addModernInfoRow(JPanel panel, String label, String value) {
        JLabel labelComp = new JLabel(label);
        labelComp.setFont(new Font("Segoe UI", Font.BOLD, 14));
        labelComp.setForeground(new Color(100, 100, 100));
        
        JLabel valueComp = new JLabel(value);
        valueComp.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        valueComp.setForeground(textColor);
        
        panel.add(labelComp);
        panel.add(valueComp);
    }
    

    private JPanel createModernProductsPanel(Commande commande) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        String[] columns = {"Image", "Produit", "Prix unitaire", "Quantité", "Total"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) return ImageIcon.class;
                if (columnIndex == 2 || columnIndex == 4) return Double.class;
                return Object.class;
            }
        };
        
        for (Commande_produits cp : commande.getProduits()) {
            Produit p = cp.getProduit();
            ImageIcon productIcon = createProductIcon(p.getImage());
            
            model.addRow(new Object[]{
                productIcon,
                p.getNom(),
                p.getPrix(),
                cp.getQuantite(),
                p.getPrix() * cp.getQuantite()
            });
        }
        
        JTable table = new JTable(model);
        configureProductsTable(table);
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }

    private ImageIcon createProductIcon(byte[] imageData) {
        if (imageData == null || imageData.length == 0) {
            return createDefaultProductIcon();
        }
        
        try {
            // Créer une image à partir des bytes
            Image originalImage = new ImageIcon(imageData).getImage();
            
            // Redimensionner l'image
            Image scaledImage = originalImage.getScaledInstance(60, 60, Image.SCALE_SMOOTH);
            
            // Créer une image arrondie
            BufferedImage roundedImage = new BufferedImage(60, 60, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = roundedImage.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setClip(new RoundRectangle2D.Float(0, 0, 60, 60, 15, 15));
            g2.drawImage(scaledImage, 0, 0, 60, 60, null);
            g2.dispose();
            
            return new ImageIcon(roundedImage);
        } catch (Exception e) {
            System.err.println("Erreur lors du traitement de l'image: " + e.getMessage());
            return createDefaultProductIcon();
        }
    }
    private void updateButtonStates() {
        int selectedRow = commandesTable.getSelectedRow();
        boolean rowSelected = selectedRow != -1;
        
        statusComboBox.setEnabled(rowSelected);
        
        for (Component c : ((JPanel)getComponent(2)).getComponents()) {
            if (c instanceof JButton) {
                c.setEnabled(rowSelected);
            }
        }
        
        if (rowSelected) {
            String currentStatus = ((String) tableModel.getValueAt(selectedRow, 4)).toLowerCase();
            String orderType = (String) tableModel.getValueAt(selectedRow, 3);
            
            // Mettre à jour les statuts disponibles selon le type de commande
            String[] statuses = getStatusesForOrderType(orderType);
            DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>(statuses);
            statusComboBox.setModel(model);
            
            // Sélectionner le statut actuel s'il est disponible
            if (Arrays.asList(statuses).contains(currentStatus)) {
                statusComboBox.setSelectedItem(currentStatus);
            } else {
                statusComboBox.setSelectedIndex(0);
            }
        }
    }

    private void configureProductsTable(JTable table) {
        table.setRowHeight(80);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        
        // Style de l'en-tête
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(240, 241, 242));
        header.setForeground(new Color(73, 80, 87));
        header.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(0, 0, 2, 0, new Color(222, 226, 230)),
            new EmptyBorder(10, 15, 10, 15)
        ));
        
        // Renderer pour les images
        table.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = new JLabel();
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setVerticalAlignment(SwingConstants.CENTER);
                
                if (value instanceof ImageIcon) {
                    label.setIcon((ImageIcon)value);
                } else {
                    label.setIcon(createDefaultProductIcon());
                }
                
                // Style de la cellule
                label.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 249, 250));
                label.setOpaque(true);
                
                if (isSelected) {
                    label.setBackground(new Color(primaryColor.getRed(), primaryColor.getGreen(), primaryColor.getBlue(), 30));
                }
                
                return label;
            }
        });
        
        // Style des autres cellules
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        
        table.getColumnModel().getColumn(2).setCellRenderer(rightRenderer);
        table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setForeground(primaryColor);
                ((JLabel)c).setHorizontalAlignment(SwingConstants.RIGHT);
                return c;
            }
        });
    }
    
    
    private ImageIcon createDefaultProductIcon() {
        BufferedImage image = new BufferedImage(60, 60, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Fond rond
        g2.setColor(new Color(230, 230, 230));
        g2.fillRoundRect(0, 0, 60, 60, 15, 15);
        
        // Icône de produit
        g2.setColor(new Color(180, 180, 180));
        g2.fillRect(15, 20, 30, 20);  // Corps du produit
        g2.fillOval(20, 10, 20, 20);  // Haut arrondi
        
        g2.dispose();
        return new ImageIcon(image);
    }
    // Méthode pour créer une icône par défaut
    
    private Color getStatusColor(String status) {
        if (status == null) return darkColor;
        switch (status.toLowerCase()) {
            case "en_attente": return new Color(150, 150, 150); // Gris clair
            case "en préparation": return warningColor;
            case "prête": return successColor;
            case "en cours de livraison": return new Color(92, 107, 192); // Violet
            case "livrée": return deliveredColor;
            case "servie": return new Color(56, 142, 60); // Vert foncé
            case "prête à récupérer": return new Color(255, 152, 0); // Orange
            case "récupérée": return deliveredColor;
            case "annulée": return errorColor;
            default: return darkColor;
        }
    }

    private void deleteCommande() {
        int selectedRow = commandesTable.getSelectedRow();
        if (selectedRow == -1) return;
        
        int commandeId = (int) tableModel.getValueAt(selectedRow, 0);
        
        // Création du dialogue de confirmation moderne
        JPanel confirmPanel = new JPanel(new BorderLayout());
        confirmPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        confirmPanel.setBackground(Color.WHITE);
        
        JLabel iconLabel = new JLabel(new ImageIcon("warning_icon.png")); // Remplacez par votre icône
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        iconLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        JLabel message = new JLabel("<html><div style='text-align:center;font-family:Segoe UI;font-size:16px;color:#495057;'>"
                + "Voulez-vous vraiment supprimer la commande #" + commandeId + "?<br>Cette action est irréversible.</div></html>");
        message.setHorizontalAlignment(SwingConstants.CENTER);
        
        confirmPanel.add(iconLabel, BorderLayout.NORTH);
        confirmPanel.add(message, BorderLayout.CENTER);
        
        // Panel des boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        
        JButton confirmButton = createModernButton("Supprimer", errorColor);
        JButton cancelButton = createModernButton("Annuler", new Color(108, 117, 125));
        
        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);
        confirmPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Création du dialogue personnalisé
        JDialog dialog = new JDialog(parent, "Confirmation", true);
        dialog.setSize(400, 250);
        dialog.setLayout(new BorderLayout());
        dialog.add(confirmPanel, BorderLayout.CENTER);
        dialog.setLocationRelativeTo(parent);
        
        // Actions des boutons
        confirmButton.addActionListener(e -> {
            dialog.dispose();
            performDeleteCommande(commandeId, selectedRow);
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        dialog.setVisible(true);
    }
    

    private void performDeleteCommande(int commandeId, int selectedRow) {
        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return commandeController.supprimerCommande(commandeId);
            }
            
            @Override
            protected void done() {
                try {
                    if (get()) {
                        tableModel.removeRow(selectedRow);
                        showSuccessMessage("Commande supprimée", 
                                         "La commande #" + commandeId + " a été supprimée avec succès.");
                    } else {
                        showErrorMessage("Échec de la suppression", 
                                       "Impossible de supprimer la commande #" + commandeId);
                    }
                } catch (Exception ex) {
                    showErrorMessage("Erreur", 
                                    "Une erreur est survenue lors de la suppression: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void showSuccessMessage(String title, String message) {
        showModernDialog(title, message, successColor, "success_icon.png");
    }

    private void showErrorMessage(String title, String message) {
        showModernDialog(title, message, errorColor, "error_icon.png");
    }

    private void showModernDialog(String title, String message, Color color, String iconPath) {
        JDialog dialog = new JDialog(parent, title, true);
        dialog.setSize(450, 250);
        dialog.setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(parent);
        
        // En-tête
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(color);
        headerPanel.setBorder(new EmptyBorder(15, 25, 15, 25));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        // Contenu
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(new EmptyBorder(25, 25, 25, 25));
        
        // Panel avec icône et message
        JPanel messagePanel = new JPanel(new BorderLayout(20, 0));
        messagePanel.setBackground(Color.WHITE);
        
        try {
            ImageIcon icon = new ImageIcon(iconPath);
            JLabel iconLabel = new JLabel(new ImageIcon(icon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH)));
            iconLabel.setBorder(new EmptyBorder(0, 0, 0, 20));
            messagePanel.add(iconLabel, BorderLayout.WEST);
        } catch (Exception e) {
            // Si l'icône ne peut pas être chargée, on continue sans
        }
        
        JTextArea messageArea = new JTextArea(message);
        messageArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        messageArea.setForeground(textColor);
        messageArea.setEditable(false);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        messageArea.setBackground(Color.WHITE);
        messageArea.setBorder(new EmptyBorder(5, 0, 5, 0));
        
        messagePanel.add(messageArea, BorderLayout.CENTER);
        contentPanel.add(messagePanel, BorderLayout.CENTER);
        
        // Bouton
        JButton okButton = createModernButton("OK", color);
        okButton.addActionListener(e -> dialog.dispose());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        buttonPanel.add(okButton);
        
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(headerPanel, BorderLayout.NORTH);
        dialog.add(contentPanel, BorderLayout.CENTER);
        
        dialog.setVisible(true);
    }

    // Classes pour les bordures personnalisées
    class RoundBorder extends AbstractBorder {
        private int radius;
        private Color color;
        
        public RoundBorder(int radius) {
            this(radius, Color.GRAY);
        }
        
        public RoundBorder(int radius, Color color) {
            this.radius = radius;
            this.color = color;
        }
        
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.drawRoundRect(x, y, width-1, height-1, radius, radius);
            g2.dispose();
        }
        
        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(radius/2, radius, radius/2, radius);
        }
    }
    private String[] getStatusesForOrderType(String orderType) {
        if (orderType == null) {
            return new String[]{"en attente", "en préparation", "prête", "annulée"};
        }
        
        // Convertir en minuscules et supprimer les espaces/underscores pour la comparaison
        String normalizedType = orderType.toLowerCase().replace(" ", "_").replace("à_", "a_");
        
        switch(normalizedType) {
            case "livraison":
                return new String[]{ "en préparation", "prête", "en cours de livraison", "livrée", "annulée"};
            case "sur_place":
                return new String[]{ "en préparation", "prête", "servie", "annulée"};
            case "a_emporter":
                return new String[]{"en préparation", "prête", "prête à récupérer", "récupérée", "annulée"};
            default:
                return new String[]{ "en préparation", "prête", "annulée"};
        }
    }
   
}