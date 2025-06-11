package interfaces;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.text.SimpleDateFormat;
import java.util.List;
import entité.Commande;
import entité.Commande_produits;
import Controller.CommandeController;

public class OrdersClientPanel extends JPanel {
    private final CommandeController commandeController;
    private final int clientId;
    private Commande nouvelleCommande;
    private JPanel ordersPanel;
    private JScrollPane scrollPane;
    
    // Constantes pour les statuts
    public static final String EN_ATTENTE = "en_attente";
    public static final String EN_PREPARATION = "en préparation";
    public static final String PRETE = "prête";
    public static final String EN_COURS_LIVRAISON = "en cours de livraison";
    public static final String LIVREE = "livrée";
    public static final String SERVIE = "servie";
    public static final String PRETE_RECUPERER = "prête à récupérer";
    public static final String RECUPEREE = "récupérée";
    public static final String ANNULEE = "annulée";
    
    // Couleurs des statuts
    private static final Color STATUS_PENDING = new Color(255, 152, 0); // Orange
    private static final Color STATUS_PREPARATION = new Color(33, 150, 243); // Bleu
    private static final Color STATUS_READY = new Color(76, 175, 80); // Vert
    private static final Color STATUS_DELIVERY = new Color(156, 39, 176); // Violet
    private static final Color STATUS_DELIVERED = new Color(104, 159, 56); // Vert foncé
    private static final Color STATUS_SERVED = new Color(0, 150, 136); // Turquoise
    private static final Color STATUS_READY_FOR_PICKUP = new Color(255, 193, 7); // Jaune
    private static final Color STATUS_PICKED_UP = new Color(139, 195, 74); // Vert clair
    private static final Color STATUS_CANCELLED = new Color(244, 67, 54); // Rouge
    private static final Color STATUS_DEFAULT = new Color(150, 150, 150); // Gris
    
    // Autres couleurs
    private static final Color PRIMARY_COLOR = new Color(40, 110, 180);
    private static final Color BACKGROUND_COLOR = new Color(245, 247, 250);
    private static final Color HIGHLIGHT_COLOR = new Color(220, 240, 255);
    private static final int HIGHLIGHT_DURATION = 3000;
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color TEXT_COLOR = new Color(33, 33, 33);
    private static final Color SUBTEXT_COLOR = new Color(117, 117, 117);
    private static final Color SHADOW_COLOR = new Color(0, 0, 0, 30);

    public OrdersClientPanel(int clientId) {
        this(clientId, null);
    }

    public OrdersClientPanel(int clientId, Commande nouvelleCommande) {
        this.clientId = clientId;
        this.nouvelleCommande = nouvelleCommande;
        this.commandeController = new CommandeController();
        
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                refreshOrders();
            }
        });
        
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Content
        ordersPanel = new JPanel();
        ordersPanel.setLayout(new BoxLayout(ordersPanel, BoxLayout.Y_AXIS));
        ordersPanel.setBackground(BACKGROUND_COLOR);
        ordersPanel.setBorder(new EmptyBorder(10, 0, 10, 0));

        scrollPane = new JScrollPane(ordersPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        scrollPane.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateCardWidths();
            }
        });
        
        add(scrollPane, BorderLayout.CENTER);
        loadOrdersData();
    }

    private void updateCardWidths() {
        int availableWidth = scrollPane.getViewport().getWidth() - 30;
        if (availableWidth <= 0) return;
        
        for (Component comp : ordersPanel.getComponents()) {
            if (comp instanceof JPanel) {
                comp.setPreferredSize(new Dimension(availableWidth, comp.getPreferredSize().height));
                comp.setMaximumSize(new Dimension(availableWidth, comp.getPreferredSize().height));
            }
        }
        ordersPanel.revalidate();
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BACKGROUND_COLOR);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        JLabel titleLabel = new JLabel("Mes Commandes");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_COLOR);
        
        JButton refreshButton = new JButton("Actualiser");
        refreshButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        refreshButton.setBackground(new Color(240, 240, 240));
        refreshButton.addActionListener(e -> refreshOrders());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.add(refreshButton);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(buttonPanel, BorderLayout.EAST);
        
        return headerPanel;
    }

    private void loadOrdersData() {
        ordersPanel.removeAll();
        ordersPanel.add(createLoadingPanel());
        revalidate();
        repaint();
        
        new SwingWorker<List<Commande>, Void>() {
            @Override
            protected List<Commande> doInBackground() throws Exception {
                return commandeController.getCommandesParClient(clientId);
            }

            @Override
            protected void done() {
                try {
                    List<Commande> commandes = get();
                    showOrders(commandes);
                } catch (Exception e) {
                    showErrorState("Erreur de chargement des commandes");
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    public void refreshOrders() {
        setEnabled(false);
        ordersPanel.removeAll();
        ordersPanel.add(createLoadingPanel());
        revalidate();
        repaint();
        nouvelleCommande = null;

        new SwingWorker<List<Commande>, Void>() {
            @Override
            protected List<Commande> doInBackground() throws Exception {
                return commandeController.getCommandesParClient(clientId);
            }

            @Override
            protected void done() {
                try {
                    List<Commande> commandes = get();
                    showOrders(commandes);
                    
                    if (nouvelleCommande != null) {
                        SwingUtilities.invokeLater(() -> scrollToNewOrder());
                    }
                } catch (Exception e) {
                    showErrorState("Erreur de chargement des commandes");
                    e.printStackTrace();
                } finally {
                    setEnabled(true);
                }
            }
        }.execute();
    }

    private void showOrders(List<Commande> commandes) {
        ordersPanel.removeAll();

        if (commandes == null || commandes.isEmpty()) {
            ordersPanel.add(createEmptyStatePanel());
        } else {
            for (Commande commande : commandes) {
                JPanel card = createOrderCard(commande);
                
                if (nouvelleCommande != null && commande.getId() == nouvelleCommande.getId()) {
                    card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
                        new EmptyBorder(13, 13, 13, 13)
                    ));
                    highlightCard(card);
                }
                
                ordersPanel.add(card);
                ordersPanel.add(Box.createRigidArea(new Dimension(0, 15)));
            }
        }

        ordersPanel.revalidate();
        ordersPanel.repaint();
        
        if (nouvelleCommande != null) {
            SwingUtilities.invokeLater(() -> scrollToNewOrder());
        }
    }

    private JPanel createOrderCard(Commande commande) {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Ombre légère
                g2.setColor(SHADOW_COLOR);
                g2.fillRoundRect(2, 4, getWidth()-4, getHeight()-4, 12, 12);
                
                // Fond de la carte
                g2.setColor(CARD_COLOR);
                g2.fillRoundRect(0, 0, getWidth()-4, getHeight()-4, 10, 10);
                
                // Bordure
                g2.setColor(new Color(230, 230, 230));
                g2.drawRoundRect(0, 0, getWidth()-4, getHeight()-4, 10, 10);
            }
        };
        
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(15, 15, 15, 15));
        card.putClientProperty("commande_id", commande.getId());

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                GradientPaint gp = new GradientPaint(
                    0, 0, new Color(245, 245, 245),
                    0, getHeight(), new Color(235, 235, 235)
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                
                g2.setColor(new Color(220, 220, 220));
                g2.drawLine(0, getHeight()-1, getWidth(), getHeight()-1);
            }
        };
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(10, 15, 10, 15));
        
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm");
        JLabel orderLabel = new JLabel("Commande #" + commande.getId() + " • " + sdf.format(commande.getDateCommande()));
        orderLabel.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 15));
        orderLabel.setForeground(TEXT_COLOR);
        
        JPanel badgesPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        badgesPanel.setOpaque(false);
        badgesPanel.add(createTypeBadge(commande.getTypeCommande()));
        badgesPanel.add(createStatusBadge(commande.getStatut()));
        
        headerPanel.add(orderLabel, BorderLayout.WEST);
        headerPanel.add(badgesPanel, BorderLayout.EAST);
        
        // Products
        JPanel productsPanel = new JPanel();
        productsPanel.setLayout(new BoxLayout(productsPanel, BoxLayout.Y_AXIS));
        productsPanel.setOpaque(false);
        productsPanel.setBorder(new EmptyBorder(10, 0, 10, 0));

        productsPanel.add(createHeaderRow());
        
        JSeparator separator = new JSeparator();
        separator.setForeground(new Color(230, 230, 230));
        productsPanel.add(separator);
        productsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        boolean alternate = false;
        for (Commande_produits cp : commande.getProduits()) {
            JPanel row = createProductDetailRow(cp, alternate);
            productsPanel.add(row);
            productsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            alternate = !alternate;
        }
        
        // Totals
        JPanel totalsPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        totalsPanel.setOpaque(false);
        totalsPanel.setBorder(new EmptyBorder(15, 0, 0, 0));
        
        int totalQty = commande.getProduits().stream().mapToInt(Commande_produits::getQuantite).sum();
        double totalAmount = commande.getProduits().stream()
                                  .mapToDouble(cp -> cp.getProduit().getPrix() * cp.getQuantite())
                                  .sum();
        
        JPanel qtyPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        qtyPanel.setOpaque(false);
        JLabel qtyLabel = new JLabel(totalQty + " article" + (totalQty > 1 ? "s" : ""));
        qtyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        qtyLabel.setForeground(SUBTEXT_COLOR);
        qtyPanel.add(qtyLabel);
        
        JPanel amountPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        amountPanel.setOpaque(false);
        JLabel amountLabel = new JLabel(String.format("%.2f DT", totalAmount));
        amountLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        amountLabel.setForeground(PRIMARY_COLOR);
        amountPanel.add(amountLabel);
        
        totalsPanel.add(qtyPanel);
        totalsPanel.add(amountPanel);
        
        // Buttons
        JPanel buttonsPanel = createActionButtonsPanel(commande);
        buttonsPanel.setBorder(new EmptyBorder(15, 0, 0, 0));
        
        // Assembly
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        contentPanel.add(headerPanel, BorderLayout.NORTH);
        contentPanel.add(productsPanel, BorderLayout.CENTER);
        contentPanel.add(totalsPanel, BorderLayout.SOUTH);
        
        card.add(contentPanel, BorderLayout.CENTER);
        card.add(buttonsPanel, BorderLayout.SOUTH);
        
        return card;
    }

    private JPanel createHeaderRow() {
        JPanel headerRow = new JPanel(new GridBagLayout());
        headerRow.setOpaque(false);
        headerRow.setBorder(new EmptyBorder(0, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 5, 0, 5);

        // PRODUIT (25% width)
        gbc.gridx = 0;
        gbc.weightx = 0.25;
        gbc.anchor = GridBagConstraints.WEST;
        headerRow.add(createStyledHeaderLabel("PRODUIT"), gbc);

        // PRIX (25% width, align right)
        gbc.gridx = 1;
        gbc.weightx = 0.25;
        gbc.anchor = GridBagConstraints.EAST;
        headerRow.add(createStyledHeaderLabel("PRIX"), gbc);

        // QTY (25% width, align center)
        gbc.gridx = 2;
        gbc.weightx = 0.25;
        gbc.anchor = GridBagConstraints.CENTER;
        headerRow.add(createStyledHeaderLabel("Quantité"), gbc);

        // TOTAL (25% width, align right)
        gbc.gridx = 3;
        gbc.weightx = 0.25;
        gbc.anchor = GridBagConstraints.EAST;
        headerRow.add(createStyledHeaderLabel("TOTAL"), gbc);

        return headerRow;
    }

    private JPanel createProductDetailRow(Commande_produits cp, boolean alternate) {
        JPanel row = new JPanel(new GridBagLayout());
        row.setOpaque(false);
        row.setBackground(alternate ? new Color(248, 248, 248) : Color.WHITE);
        row.setBorder(new EmptyBorder(8, 10, 8, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 5, 0, 5);

        // Nom du produit (25% width)
        gbc.gridx = 0;
        gbc.weightx = 0.25;
        JLabel nameLabel = new JLabel(cp.getProduit().getNom());
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        row.add(nameLabel, gbc);

        // Prix unitaire (25% width, align right)
        gbc.gridx = 1;
        gbc.weightx = 0.25;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel priceLabel = new JLabel(String.format("%.2f DT", cp.getProduit().getPrix()));
        priceLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        row.add(priceLabel, gbc);

        // Quantité (25% width, align center)
        gbc.gridx = 2;
        gbc.weightx = 0.25;
        gbc.anchor = GridBagConstraints.CENTER;
        JLabel qtyLabel = new JLabel("× " + cp.getQuantite());
        qtyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        row.add(qtyLabel, gbc);

        // Sous-total (25% width, align right)
        gbc.gridx = 3;
        gbc.weightx = 0.25;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel subTotalLabel = new JLabel(String.format("%.2f DT", cp.getProduit().getPrix() * cp.getQuantite()));
        subTotalLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        subTotalLabel.setForeground(new Color(80, 80, 80));
        row.add(subTotalLabel, gbc);

        return row;
    }

    private JLabel createTypeBadge(String type) {
        String typeText;
        Color bgColor;
        
        if (type == null) {
            typeText = "INCONNU";
            bgColor = STATUS_DEFAULT;
        } else {
            switch(type.toLowerCase()) {
                case "livraison":
                    typeText = "LIVRAISON";
                    bgColor = new Color(65, 131, 215);
                    break;
                case "sur_place":
                    typeText = "SUR PLACE";
                    bgColor = new Color(76, 175, 80);
                    break;
                case "a_emporter":
                    typeText = "À EMPORTER";
                    bgColor = new Color(239, 108, 0);
                    break;
                default:
                    typeText = type.toUpperCase();
                    bgColor = STATUS_DEFAULT;
            }
        }
        
        return createBadge(typeText, bgColor);
    }

    private JLabel createStatusBadge(String statut) {
        String statusText;
        Color bgColor;
        
        if (statut == null) {
            statusText = "INCONNU";
            bgColor = STATUS_DEFAULT;
        } else {
            switch(statut.toLowerCase()) {
                case EN_ATTENTE:
                    statusText = "EN ATTENTE";
                    bgColor = STATUS_PENDING;
                    break;
                case EN_PREPARATION:
                    statusText = "EN PRÉPARATION";
                    bgColor = STATUS_PREPARATION;
                    break;
                case PRETE:
                    statusText = "PRÊTE";
                    bgColor = STATUS_READY;
                    break;
                case EN_COURS_LIVRAISON:
                    statusText = "EN LIVRAISON";
                    bgColor = STATUS_DELIVERY;
                    break;
                case LIVREE:
                    statusText = "LIVRÉE";
                    bgColor = STATUS_DELIVERED;
                    break;
                case SERVIE:
                    statusText = "SERVIE";
                    bgColor = STATUS_SERVED;
                    break;
                case PRETE_RECUPERER:
                    statusText = "PRÊTE À RÉCUPÉRER";
                    bgColor = STATUS_READY_FOR_PICKUP;
                    break;
                case RECUPEREE:
                    statusText = "RÉCUPÉRÉE";
                    bgColor = STATUS_PICKED_UP;
                    break;
                case ANNULEE:
                    statusText = "ANNULÉE";
                    bgColor = STATUS_CANCELLED;
                    break;
                default:
                    statusText = statut.toUpperCase();
                    bgColor = STATUS_DEFAULT;
            }
        }
        
        return createBadge(statusText, bgColor);
    }

    private JLabel createBadge(String text, Color bgColor) {
        JLabel badge = new JLabel(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2.setColor(bgColor.darker());
                g2.fillRoundRect(0, 1, getWidth(), getHeight(), 8, 8);
                
                g2.setColor(bgColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight()-1, 8, 8);
                
                g2.setColor(Color.WHITE);
                super.paintComponent(g2);
            }
        };
        
        badge.setFont(new Font("Segoe UI", Font.BOLD, 10));
        badge.setForeground(Color.WHITE);
        badge.setBorder(new EmptyBorder(4, 10, 4, 10));
        badge.setHorizontalAlignment(SwingConstants.CENTER);
        
        return badge;
    }

    private JPanel createActionButtonsPanel(Commande commande) {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 5));
        buttonPanel.setOpaque(false);
        
        boolean canEdit = canEditOrder(commande);
        boolean canCancel = canCancelOrder(commande);
        
        if (canEdit) {
            JButton editButton = createActionButton("Modifier", PRIMARY_COLOR);
            editButton.addActionListener(e -> editOrder(commande));
            buttonPanel.add(editButton);
        }
        
        if (canCancel) {
            JButton cancelButton = createActionButton("Annuler", STATUS_CANCELLED);
            cancelButton.addActionListener(e -> cancelOrder(commande));
            buttonPanel.add(cancelButton);
        }
        
        return buttonPanel;
    }

    private JButton createActionButton(String text, Color color) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2.setColor(color.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(color.brighter());
                } else {
                    g2.setColor(color);
                }
                
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                super.paintComponent(g);
            }
        };
        
        button.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return button;
    }

    private boolean canEditOrder(Commande commande) {
        if (commande == null || commande.getStatut() == null) return false;
        String status = commande.getStatut().toLowerCase().trim();
        return status.equals(EN_ATTENTE) ;
    }

    private boolean canCancelOrder(Commande commande) {
        if (commande == null || commande.getStatut() == null) return false;
        String status = commande.getStatut().toLowerCase().trim();
        return status.equals(LIVREE) || status.equals(ANNULEE) || status.equals(EN_ATTENTE) || status.equals(RECUPEREE)|| status.equals(SERVIE) ;
    }

    public void editOrder(Commande commande) {
        if (commande == null || commande.getProduits() == null || commande.getProduits().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Impossible de modifier une commande sans produits",
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            OrderModifierDialog dialog = new OrderModifierDialog(this, commande);
            dialog.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors de l'ouverture de l'éditeur: " + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public void refreshWithNewOrder(Commande newOrder) {
        this.nouvelleCommande = newOrder;
        loadOrdersData();
    }

    private void scrollToNewOrder() {
        if (nouvelleCommande == null) return;
        
        for (Component comp : ordersPanel.getComponents()) {
            if (comp instanceof JPanel) {
                JPanel card = (JPanel) comp;
                if (card.getClientProperty("commande_id") != null && 
                    card.getClientProperty("commande_id").equals(nouvelleCommande.getId())) {
                    card.scrollRectToVisible(card.getBounds());
                    return;
                }
            }
        }
        
        scrollPane.getVerticalScrollBar().setValue(0);
    }

    private void cancelOrder(Commande commande) {
        if (commande == null) return;
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Êtes-vous sûr de vouloir annuler la commande #" + commande.getId() + "?",
            "Confirmer l'annulation",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    return commandeController.supprimerCommande(commande.getId());
                }
                
                @Override
                protected void done() {
                    try {
                        boolean success = get();
                        if (success) {
                            JOptionPane.showMessageDialog(OrdersClientPanel.this, 
                                "Commande annulée avec succès", 
                                "Succès", 
                                JOptionPane.INFORMATION_MESSAGE);
                            refreshOrders();
                        } else {
                            JOptionPane.showMessageDialog(OrdersClientPanel.this, 
                                "Échec de l'annulation de la commande", 
                                "Erreur", 
                                JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(OrdersClientPanel.this, 
                            "Une erreur est survenue: " + e.getMessage(), 
                            "Erreur", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            }.execute();
        }
    }

    private JLabel createStyledHeaderLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(new Color(100, 100, 100));
        label.setBorder(new EmptyBorder(0, 0, 5, 0));
        return label;
    }

    private JPanel createLoadingPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(50, 0, 50, 0));
        
        JLabel label = new JLabel("Chargement des commandes...", SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        label.setForeground(new Color(150, 150, 150));
        
        panel.add(label, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createEmptyStatePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(50, 0, 50, 0));
        
        JLabel label = new JLabel("Aucune commande trouvée", SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        label.setForeground(new Color(150, 150, 150));
        
        panel.add(label, BorderLayout.CENTER);
        return panel;
    }

    private void showErrorState(String message) {
        ordersPanel.removeAll();
        
        JPanel errorPanel = new JPanel(new BorderLayout());
        errorPanel.setBackground(BACKGROUND_COLOR);
        errorPanel.setBorder(new EmptyBorder(50, 0, 50, 0));
        
        JLabel errorLabel = new JLabel(message, SwingConstants.CENTER);
        errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        errorLabel.setForeground(STATUS_CANCELLED);
        
        JButton retryButton = new JButton("Réessayer");
        retryButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        retryButton.addActionListener(e -> loadOrdersData());
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.add(retryButton);
        
        errorPanel.add(errorLabel, BorderLayout.CENTER);
        errorPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        ordersPanel.add(errorPanel);
        ordersPanel.revalidate();
        ordersPanel.repaint();
    }

    private void highlightCard(JPanel card) {
        Timer timer = new Timer(50, null);
        timer.addActionListener(new ActionListener() {
            long startTime = System.currentTimeMillis();
            
            @Override
            public void actionPerformed(ActionEvent e) {
                long elapsed = System.currentTimeMillis() - startTime;
                if (elapsed > HIGHLIGHT_DURATION) {
                    card.setBackground(Color.WHITE);
                    timer.stop();
                    return;
                }
                
                float progress = (float) elapsed / HIGHLIGHT_DURATION;
                int alpha = (int) (255 * (1 - progress * 0.8));
                Color bgColor = new Color(
                    HIGHLIGHT_COLOR.getRed(),
                    HIGHLIGHT_COLOR.getGreen(),
                    HIGHLIGHT_COLOR.getBlue(),
                    alpha
                );
                card.setBackground(bgColor);
                card.repaint();
            }
        });
        timer.start();
    }
}