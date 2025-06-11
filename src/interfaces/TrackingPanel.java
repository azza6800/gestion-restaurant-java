package interfaces;

import javax.swing.*;
import javax.swing.border.*;
import entité.Commande;
import entité.Commande_produits;
import Controller.CommandeController;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class TrackingPanel extends JPanel implements ClientDashboard.RefreshablePanel {
    // Couleurs modernes
    private static final Color PRIMARY_COLOR = new Color(30, 136, 229); // Bleu vif
    private static final Color BACKGROUND_COLOR = new Color(250, 250, 252); // Gris très clair
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color STATUS_PENDING = new Color(255, 152, 0); // Orange
    private static final Color STATUS_PREPARATION = new Color(33, 150, 243); // Bleu
    private static final Color STATUS_READY = new Color(76, 175, 80); // Vert
    private static final Color STATUS_DELIVERY = new Color(156, 39, 176); // Violet
    private static final Color STATUS_DELIVERED = new Color(104, 159, 56); // Vert foncé
    private static final Color STATUS_SERVED = new Color(0, 150, 136); // Turquoise
    private static final Color STATUS_READY_FOR_PICKUP = new Color(255, 193, 7); // Jaune
    private static final Color STATUS_PICKED_UP = new Color(139, 195, 74); // Vert clair
    private static final Color STATUS_CANCELLED = new Color(244, 67, 54); // Rouge
    private static final Color STEP_COMPLETED = new Color(189, 189, 189); // Gris
    private static final Color STEP_FUTURE = new Color(238, 238, 238); // Gris très clair
    private static final Color DELIVERY_COLOR = new Color(92, 107, 192); // Violet
    private static final Color ONSITE_COLOR = new Color(56, 142, 60); // Vert foncé
    private static final Color TAKEOUT_COLOR = new Color(255, 152, 0); // Orange
    private static final Color TEXT_COLOR = new Color(33, 33, 33); // Noir profond
    private static final Color SUBTEXT_COLOR = new Color(117, 117, 117); // Gris moyen
    private static final Color SHADOW_COLOR = new Color(0, 0, 0, 30);
    
    // Polices
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font SUBTITLE_FONT = new Font("Segoe UI", Font.BOLD, 16);
    private static final Font BODY_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font SMALL_FONT = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font BOLD_SMALL_FONT = new Font("Segoe UI", Font.BOLD, 12);
    
    private CommandeController commandeController;
    private int clientId;
    private List<Commande> commandes;
    private Commande commandeSelectionnee;
    private JSplitPane splitPane;
    private JPanel commandListPanel;
    private JPanel trackingDetailsPanel;

    public TrackingPanel(int clientId) {
        this.clientId = clientId;
        this.commandeController = new CommandeController();
        initUI();
    }

    @Override
    public void refreshData() {
        loadCommandes();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        loadCommandes();
    }

    private void loadCommandes() {
        this.commandes = commandeController.getCommandesParClient(clientId);
        
        if (commandes == null || commandes.isEmpty()) {
            showEmptyState("Aucune commande trouvée");
            this.commandeSelectionnee = null;
            return;
        }
        
        // Trier les commandes par date (plus récentes en premier)
        commandes.sort((c1, c2) -> c2.getDateCommande().compareTo(c1.getDateCommande()));
        
        this.commandeSelectionnee = commandes.get(0);
        createMainInterface();
    }

    private void showEmptyState(String message) {
        removeAll();
        add(createModernEmptyState(message), BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private void createMainInterface() {
        removeAll();
        
        // En-tête
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BACKGROUND_COLOR);
        headerPanel.setBorder(new EmptyBorder(0, 0, 30, 0));
        
        JLabel titleLabel = new JLabel("Suivi de vos commandes");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(TEXT_COLOR);
        
        JLabel subtitleLabel = new JLabel("Visualisez l'état de vos commandes en cours");
        subtitleLabel.setFont(BODY_FONT);
        subtitleLabel.setForeground(SUBTEXT_COLOR);
        
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Contenu principal
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(350);
        splitPane.setBorder(null);
        splitPane.setDividerSize(10);
        splitPane.setBackground(BACKGROUND_COLOR);
        
        commandListPanel = createModernCommandListPanel();
        trackingDetailsPanel = createModernTrackingDetailsPanel();
        
        splitPane.setLeftComponent(commandListPanel);
        splitPane.setRightComponent(trackingDetailsPanel);
        
        add(splitPane, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private JPanel createModernCommandListPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(0, 0, 0, 20));
        
        JLabel title = new JLabel("Historique des commandes");
        title.setFont(SUBTITLE_FONT);
        title.setForeground(TEXT_COLOR);
        title.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        panel.add(title, BorderLayout.NORTH);
        
        JScrollPane scrollPane = new JScrollPane(createCommandList());
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(BACKGROUND_COLOR);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }

    private JPanel createCommandList() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        for (Commande commande : commandes) {
            if (commande != null) {
                panel.add(createModernCommandCard(commande));
                panel.add(Box.createRigidArea(new Dimension(0, 15)));
            }
        }
        
        return panel;
    }
    

    private JPanel createModernCommandCard(Commande commande) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(CARD_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(0, 0, 0, 0, BACKGROUND_COLOR),
            new EmptyBorder(20, 20, 20, 20)
        ));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        // Effet de sélection
        if (commande.equals(commandeSelectionnee)) {
            card.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 4, 0, 0, getTypeColor(commande.getTypeCommande())),
                new EmptyBorder(20, 16, 20, 20)
            ));
            card.setBackground(new Color(245, 245, 245));
        }
        
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!commande.equals(commandeSelectionnee)) {
                    card.setBackground(new Color(248, 248, 248));
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                if (!commande.equals(commandeSelectionnee)) {
                    card.setBackground(CARD_COLOR);
                }
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
                commandeSelectionnee = commande;
                updateTrackingDisplay();
            }
        });
        
        // En-tête de la carte
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        
        JLabel orderId = new JLabel("Commande #" + commande.getId());
        orderId.setFont(BODY_FONT.deriveFont(Font.BOLD));
        orderId.setForeground(TEXT_COLOR);
        
        JLabel typeLabel = new JLabel(commande.getTypeCommande().toUpperCase());
        typeLabel.setFont(BOLD_SMALL_FONT);
        typeLabel.setForeground(getTypeColor(commande.getTypeCommande()));
        
        // Badge de statut
        JLabel status = new JLabel();
        status.setFont(BOLD_SMALL_FONT);
        status.setForeground(Color.WHITE);
        status.setOpaque(true);
        status.setBackground(getStatusColor(commande.getStatut()));
        status.setBorder(new EmptyBorder(3, 8, 3, 8));
        status.setHorizontalAlignment(SwingConstants.RIGHT);
        
        header.add(orderId, BorderLayout.WEST);
        header.add(typeLabel, BorderLayout.CENTER);
        header.add(status, BorderLayout.EAST);
        
        // Détails de la commande
        JPanel detailsPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        detailsPanel.setOpaque(false);
        detailsPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        String dateStr = new SimpleDateFormat("E, dd MMM yyyy - HH:mm").format(commande.getDateCommande());
        JLabel dateLabel = new JLabel(dateStr);
        dateLabel.setFont(SMALL_FONT);
        dateLabel.setForeground(SUBTEXT_COLOR);
        dateLabel.setIcon(new ImageIcon(createIcon(new Color(189, 189, 189), 12, 12)));
        
        double montantTotal = 0.0;
        if (commande.getProduits() != null) {
            for (Commande_produits cp : commande.getProduits()) {
                if (cp != null && cp.getProduit() != null && cp.getQuantite() > 0) {
                    montantTotal += cp.getProduit().getPrix() * cp.getQuantite();
                }
            }
        }

        JLabel priceLabel = new JLabel(String.format("%.2f€", montantTotal));
        priceLabel.setFont(BOLD_SMALL_FONT);
        priceLabel.setForeground(TEXT_COLOR);
        priceLabel.setIcon(new ImageIcon(createIcon(new Color(76, 175, 80), 12, 12)));
        
        detailsPanel.add(dateLabel);
        detailsPanel.add(priceLabel);
        
        card.add(header, BorderLayout.NORTH);
        card.add(detailsPanel, BorderLayout.CENTER);
        
        return card;
    }

    private BufferedImage createIcon(Color color, int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(color);
        g2.fillOval(0, 0, width, height);
        g2.dispose();
        return image;
    }

    private Color getTypeColor(String type) {
        if (type == null) return PRIMARY_COLOR;
        
        switch(type.toLowerCase()) {
            case "livraison": return DELIVERY_COLOR;
            case "sur place": return ONSITE_COLOR;
            case "à emporter": return TAKEOUT_COLOR;
            default: return PRIMARY_COLOR;
        }
    }

    private JPanel createModernTrackingDetailsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(0, 20, 0, 0));
        
        if (commandeSelectionnee == null) {
            panel.add(createModernEmptyState("Aucune commande sélectionnée"), BorderLayout.CENTER);
            return panel;
        }
        
        // En-tête des détails
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BACKGROUND_COLOR);
        headerPanel.setBorder(new EmptyBorder(0, 0, 30, 0));
        
        JLabel titleLabel = new JLabel("Suivi de la commande #" + commandeSelectionnee.getId());
        titleLabel.setFont(SUBTITLE_FONT);
        titleLabel.setForeground(TEXT_COLOR);
        
        // Badge de type de commande
        JLabel typeLabel = new JLabel(commandeSelectionnee.getTypeCommande().toUpperCase());
        typeLabel.setFont(BOLD_SMALL_FONT);
        typeLabel.setForeground(Color.WHITE);
        typeLabel.setOpaque(true);
        typeLabel.setBackground(getTypeColor(commandeSelectionnee.getTypeCommande()));
        typeLabel.setBorder(new EmptyBorder(3, 8, 3, 8));
        
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(typeLabel, BorderLayout.SOUTH);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Étapes de suivi
        JScrollPane scrollPane = new JScrollPane(createModernTrackingSteps());
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(BACKGROUND_COLOR);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Informations supplémentaires
        panel.add(createModernTrackingInfo(), BorderLayout.SOUTH);
        
        return panel;
    }

    private JPanel createModernTrackingSteps() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(20, 40, 20, 40));
        
        String[] steps = getStatusesForOrderType(commandeSelectionnee.getTypeCommande());
        
        // Ajout d'un titre pour la progression
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(BACKGROUND_COLOR);
        titlePanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        JLabel progressTitle = new JLabel("Progression de votre commande");
        progressTitle.setFont(SUBTITLE_FONT);
        progressTitle.setForeground(TEXT_COLOR);
        
        titlePanel.add(progressTitle, BorderLayout.NORTH);
        panel.add(titlePanel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        for (int i = 0; i < steps.length; i++) {
            String step = steps[i];
            if (!step.equals("annulée")) {
                panel.add(createEnhancedStepPanel(step, i == steps.length - 1));
                if (i < steps.length - 1 && !steps[i+1].equals("annulée")) {
                    panel.add(Box.createRigidArea(new Dimension(0, 15)));
                }
            }
        }
        
        return panel;
    }

    private JPanel createEnhancedStepPanel(String step, boolean isLast) {
        JPanel panel = new JPanel(new BorderLayout(15, 0));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setAlignmentX(LEFT_ALIGNMENT);
        
        // Cercle de l'étape amélioré
        JPanel circle = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int size = 36; // Légèrement plus grand
                int x = (getWidth() - size) / 2;
                int y = (getHeight() - size) / 2;
                
                // Effet d'ombre pour le cercle
                g2.setColor(new Color(0, 0, 0, 20));
                g2.fillOval(x+2, y+2, size, size);
                
                // Fond du cercle avec dégradé
                Color color = getStepColor(step);
                GradientPaint gradient = new GradientPaint(
                    x, y, color.brighter(),
                    x+size, y+size, color.darker()
                );
                g2.setPaint(gradient);
                g2.fillOval(x, y, size, size);
                
                // Bordure blanche pour le cercle actuel
                if (commandeSelectionnee != null && step.equals(commandeSelectionnee.getStatut())) {
                    g2.setStroke(new BasicStroke(2));
                    g2.setColor(Color.WHITE);
                    g2.drawOval(x, y, size, size);
                }
                
                // Icône selon l'état
                g2.setColor(Color.WHITE);
                if (isStepCompleted(step)) {
                    // Icône de coche plus élégante
                    g2.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    int[] xPoints = {x+12, x+16, x+24};
                    int[] yPoints = {y+20, y+24, y+16};
                    g2.drawPolyline(xPoints, yPoints, 3);
                } 
                else if (commandeSelectionnee != null && step.equals(commandeSelectionnee.getStatut())) {
                    // Icône d'état actuel plus visible
                    if (step.equals("servie") || step.equals("récupérée") || step.equals("livrée")) {
                        g2.fillRoundRect(x+13, y+13, 10, 10, 3, 3);
                    } else {
                        g2.fillOval(x+13, y+13, 10, 10);
                    }
                }
            }
        };
        circle.setPreferredSize(new Dimension(60, 60)); // Plus grand pour plus d'impact
        
        // Texte de l'étape amélioré
        JPanel textPanel = new JPanel(new GridBagLayout());
        textPanel.setBackground(BACKGROUND_COLOR);
        textPanel.setBorder(new EmptyBorder(5, 10, 5, 0));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        
        JLabel stepLabel = new JLabel(getStepDisplayName(step));
        stepLabel.setFont(BODY_FONT.deriveFont(Font.BOLD));
        if (commandeSelectionnee != null && step.equals(commandeSelectionnee.getStatut())) {
            stepLabel.setForeground(getStatusColor(commandeSelectionnee.getStatut()));
        } else {
            stepLabel.setForeground(isStepCompleted(step) ? SUBTEXT_COLOR : new Color(189, 189, 189));
        }
        
        // Date estimée avec icône
        JPanel timePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        timePanel.setBackground(BACKGROUND_COLOR);
        
        JLabel timeIcon = new JLabel(new ImageIcon(createClockIcon()));
        JLabel timeLabel = new JLabel(getStepTimeEstimate(step));
        timeLabel.setFont(SMALL_FONT);
        timeLabel.setForeground(SUBTEXT_COLOR);
        
        timePanel.add(timeIcon);
        timePanel.add(timeLabel);
        
        // Description de l'étape
        JLabel descLabel = new JLabel(getStepDescription(step));
        descLabel.setFont(SMALL_FONT);
        descLabel.setForeground(SUBTEXT_COLOR);
        
        textPanel.add(stepLabel, gbc);
        gbc.gridy++;
        gbc.insets = new Insets(2, 0, 0, 0);
        textPanel.add(timePanel, gbc);
        gbc.gridy++;
        textPanel.add(descLabel, gbc);
        
        if (!isLast && !step.equals("annulée")) {
            // Connecteur entre les étapes amélioré
            JPanel connector = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    int width = 3;
                    int x = (getWidth() - width) / 2;
                    
                    // Dégradé pour le connecteur
                    GradientPaint gradient = new GradientPaint(
                        0, 0, isStepCompleted(step) ? STEP_COMPLETED : STEP_FUTURE,
                        0, getHeight(), isStepCompleted(step) ? STEP_COMPLETED : STEP_FUTURE
                    );
                    g2.setPaint(gradient);
                    
                    // Effet de ligne pointillée pour les étapes futures
                    if (!isStepCompleted(step)) {
                        float[] dash = {5.0f};
                        g2.setStroke(new BasicStroke(width, BasicStroke.CAP_ROUND, 
                            BasicStroke.JOIN_ROUND, 1.0f, dash, 0.0f));
                    } else {
                        g2.setStroke(new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    }
                    
                    g2.drawLine(x, 0, x, getHeight());
                }
            };
            connector.setPreferredSize(new Dimension(60, 40));
            
            JPanel rightPanel = new JPanel(new BorderLayout());
            rightPanel.setBackground(BACKGROUND_COLOR);
            rightPanel.add(textPanel, BorderLayout.NORTH);
            rightPanel.add(connector, BorderLayout.CENTER);
            
            panel.add(circle, BorderLayout.WEST);
            panel.add(rightPanel, BorderLayout.CENTER);
        } else {
            panel.add(circle, BorderLayout.WEST);
            panel.add(textPanel, BorderLayout.CENTER);
        }
        
        return panel;
    }
    private BufferedImage createClockIcon() {
        BufferedImage image = new BufferedImage(14, 14, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Cadran de l'horloge
        g2.setColor(SUBTEXT_COLOR);
        g2.drawOval(1, 1, 12, 12);
        
        // Aiguilles
        g2.drawLine(7, 7, 7, 3); // Aiguille des minutes
        g2.drawLine(7, 7, 11, 7); // Aiguille des heures
        
        g2.dispose();
        return image;
    }

    private String getStepDisplayName(String step) {
        switch(step) {
            case "en_attente": return "Commande reçue";
            case "en préparation": return "En préparation";
            case "prête": 
                if (commandeSelectionnee.getTypeCommande().toLowerCase().contains("livraison")) {
                    return "Prête pour livraison";
                } else if (commandeSelectionnee.getTypeCommande().toLowerCase().contains("sur place")) {
                    return "Prête à servir";
                } else {
                    return "Prête à récupérer";
                }
            case "en cours de livraison": return "En livraison";
            case "livrée": return "Livrée";
            case "servie": return "Servie";
            case "prête à récupérer": return "Disponible";
            case "récupérée": return "Récupérée";
            default: return step;
        }
    }

    private String getStepDescription(String step) {
        switch(step) {
            case "en_attente": 
                return "Votre commande a été enregistrée et est en attente de traitement";
            case "en préparation": 
                return "Notre équipe prépare votre commande avec soin";
            case "prête": 
                if (commandeSelectionnee.getTypeCommande().toLowerCase().contains("livraison")) {
                    return "Votre commande est prête pour le livreur";
                } else if (commandeSelectionnee.getTypeCommande().toLowerCase().contains("sur place")) {
                    return "Votre commande arrive bientôt à votre table";
                } else {
                    return "Votre commande vous attend au comptoir";
                }
            case "en cours de livraison": 
                return "Notre livreur est en route vers vous";
            case "livrée": 
                return "Votre commande a été livrée avec succès";
            case "servie": 
                return "Bon appétit ! Profitez de votre repas";
            case "prête à récupérer": 
                return "Votre commande est disponible au comptoir";
            case "récupérée": 
                return "Merci pour votre commande !";
            default: 
                return "";
        }
    }

    private JPanel createModernTrackingInfo() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(30, 40, 30, 40));
        
        if (commandeSelectionnee == null) {
            return panel;
        }
        
        // Carte d'information améliorée
        JPanel infoCard = new JPanel(new BorderLayout());
        infoCard.setBackground(new Color(240, 242, 245));
        infoCard.setBorder(BorderFactory.createCompoundBorder(
            new RoundBorder(16, new Color(220, 220, 220)),
            new EmptyBorder(25, 25, 25, 25)
        ));
        infoCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));
        
        // En-tête de la carte avec icône
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JLabel statusLabel = getDisplayStatus(commandeSelectionnee.getStatut());
        statusLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        JLabel iconLabel = new JLabel(new ImageIcon(createStatusIcon(commandeSelectionnee.getStatut())));
        iconLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        headerPanel.add(statusLabel, BorderLayout.WEST);
        headerPanel.add(iconLabel, BorderLayout.EAST);
        
        // Message de statut
        JLabel message = new JLabel("<html><div style='text-align:center; line-height:1.4;'>" 
            + getStatusMessage() + "</div></html>");
        message.setFont(BODY_FONT);
        message.setForeground(TEXT_COLOR);
        message.setAlignmentX(CENTER_ALIGNMENT);
        message.setBorder(new EmptyBorder(10, 0, 15, 0));
        
        // Temps estimé avec barre de progression
        JPanel progressPanel = new JPanel(new BorderLayout());
        progressPanel.setOpaque(false);
        
        JLabel timeLabel = new JLabel(getEstimatedTime());
        timeLabel.setFont(SMALL_FONT.deriveFont(Font.BOLD));
        timeLabel.setForeground(SUBTEXT_COLOR);
        timeLabel.setAlignmentX(CENTER_ALIGNMENT);
        
        JProgressBar progressBar = new JProgressBar();
        progressBar.setMaximum(100);
        progressBar.setValue(calculateProgress());
        progressBar.setStringPainted(false);
        progressBar.setBorderPainted(false);
        progressBar.setForeground(getStatusColor(commandeSelectionnee.getStatut()));
        progressBar.setBackground(new Color(230, 230, 230));
        progressBar.setPreferredSize(new Dimension(100, 6));
        progressBar.setBorder(new EmptyBorder(8, 0, 0, 0));
        
        progressPanel.add(timeLabel, BorderLayout.NORTH);
        progressPanel.add(progressBar, BorderLayout.CENTER);
        
        infoCard.add(headerPanel, BorderLayout.NORTH);
        infoCard.add(message, BorderLayout.CENTER);
        infoCard.add(progressPanel, BorderLayout.SOUTH);
        
        panel.add(infoCard);
        
        // Ajout des détails de la commande si nécessaire
        if (!commandeSelectionnee.getStatut().equals("annulée")) {
            panel.add(Box.createRigidArea(new Dimension(0, 20)));
            panel.add(createOrderDetailsPanel());
        }
        
        return panel;
    }

    private BufferedImage createStatusIcon(String status) {
        BufferedImage image = new BufferedImage(24, 24, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        Color iconColor = getStatusColor(status).darker();
        
        if (status.equals("en_attente")) {
            g2.setColor(iconColor);
            g2.fillRoundRect(4, 4, 16, 16, 4, 4);
        } 
        else if (status.equals("en préparation")) {
            g2.setColor(iconColor);
            g2.fillOval(4, 4, 16, 16);
        }
        else if (status.equals("prête")) {
            g2.setColor(iconColor);
            g2.fillRoundRect(4, 4, 16, 16, 8, 8);
        }
        else if (status.equals("en cours de livraison") || status.equals("livrée")) {
            g2.setColor(iconColor);
            g2.fillRoundRect(2, 8, 20, 8, 4, 4);
            g2.fillOval(4, 4, 8, 8);
            g2.fillOval(12, 4, 8, 8);
        }
        else if (status.equals("servie") || status.equals("récupérée")) {
            g2.setColor(iconColor);
            g2.fillOval(4, 4, 16, 16);
            g2.setColor(Color.WHITE);
            g2.fillArc(6, 6, 12, 12, 45, 270);
        }
        
        g2.dispose();
        return image;
    }

    private int calculateProgress() {
        String[] steps = getStatusesForOrderType(commandeSelectionnee.getTypeCommande());
        int currentStep = -1;
        
        for (int i = 0; i < steps.length; i++) {
            if (steps[i].equals(commandeSelectionnee.getStatut())) {
                currentStep = i;
                break;
            }
        }
        
        if (currentStep == -1) return 0;
        
        return (int) ((currentStep / (double) (steps.length - 1)) * 100);
    }

    private JPanel createOrderDetailsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(0, 0, 0, 0));
        
        JLabel title = new JLabel("Détails de la commande");
        title.setFont(BODY_FONT.deriveFont(Font.BOLD));
        title.setForeground(TEXT_COLOR);
        title.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        JPanel detailsPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        detailsPanel.setBackground(BACKGROUND_COLOR);
        
        // Date
        addDetailRow(detailsPanel, "Date", new SimpleDateFormat("E, dd MMM yyyy - HH:mm")
            .format(commandeSelectionnee.getDateCommande()));
        
        // Type
        addDetailRow(detailsPanel, "Type", commandeSelectionnee.getTypeCommande());
        
        // Adresse si livraison
        if (commandeSelectionnee.getTypeCommande().equalsIgnoreCase("livraison") && 
            commandeSelectionnee.getAdresseLivraison() != null) {
            addDetailRow(detailsPanel, "Adresse", commandeSelectionnee.getAdresseLivraison());
        }
        
        panel.add(title, BorderLayout.NORTH);
        panel.add(detailsPanel, BorderLayout.CENTER);
        
        return panel;
    }

    private void addDetailRow(JPanel panel, String label, String value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(BACKGROUND_COLOR);
        
        JLabel labelLabel = new JLabel(label + ":");
        labelLabel.setFont(SMALL_FONT);
        labelLabel.setForeground(SUBTEXT_COLOR);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(SMALL_FONT.deriveFont(Font.BOLD));
        valueLabel.setForeground(TEXT_COLOR);
        valueLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        
        row.add(labelLabel, BorderLayout.WEST);
        row.add(valueLabel, BorderLayout.CENTER);
        panel.add(row);
    }

    private String[] getStatusesForOrderType(String orderType) {
        if (orderType == null) {
            return new String[]{"en_attente", "en préparation", "prête", "annulée"};
        }
        
        // Convertir en minuscules et supprimer les espaces/underscores pour la comparaison
        String normalizedType = orderType.toLowerCase().replace(" ", "_").replace("à_", "a_");
        
        switch(normalizedType) {
            case "livraison":
                return new String[]{"en_attente", "en préparation", "prête", "en cours de livraison", "livrée", "annulée"};
            case "sur_place":
                return new String[]{"en_attente", "en préparation", "prête", "servie", "annulée"};
            case "a_emporter":
                return new String[]{"en_attente", "en préparation", "prête", "prête à récupérer", "récupérée", "annulée"};
            default:
                return new String[]{"en_attente", "en préparation", "prête", "annulée"};
        }
    }

    private JPanel createModernStepPanel(String step, boolean isLast) {
        JPanel panel = new JPanel(new BorderLayout(15, 0));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setAlignmentX(LEFT_ALIGNMENT);
        
        // Cercle de l'étape
        JPanel circle = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int size = 32;
                int x = (getWidth() - size) / 2;
                int y = (getHeight() - size) / 2;
                
                // Fond du cercle
                Color color = getStepColor(step);
                g2.setColor(color);
                g2.fillOval(x, y, size, size);
                
                // Icône selon l'état
                if (isStepCompleted(step)) {
                    // Icône de coche
                    g2.setColor(Color.WHITE);
                    g2.setStroke(new BasicStroke(2));
                    int[] xPoints = {x+10, x+14, x+22};
                    int[] yPoints = {y+18, y+22, y+14};
                    g2.drawPolyline(xPoints, yPoints, 3);
                } 
                else if (commandeSelectionnee != null && step.equals(commandeSelectionnee.getStatut())) {
                    // Icône d'état actuel
                    g2.setColor(Color.WHITE);
                    if (step.equals("servie") || step.equals("récupérée") || step.equals("livrée")) {
                        // Carré pour les états finaux
                        g2.fillRoundRect(x+11, y+11, 10, 10, 3, 3);
                    } else {
                        // Cercle pour les états intermédiaires
                        g2.fillOval(x+11, y+11, 10, 10);
                    }
                }
            }
        };
        circle.setPreferredSize(new Dimension(50, 50));
        
        // Texte de l'étape
        JLabel label = new JLabel();
        label.setFont(BODY_FONT);
        if (commandeSelectionnee != null && step.equals(commandeSelectionnee.getStatut())) {
            label.setFont(BODY_FONT.deriveFont(Font.BOLD));
            label.setForeground(getStatusColor(commandeSelectionnee.getStatut()));
        } else {
            label.setForeground(isStepCompleted(step) ? SUBTEXT_COLOR : new Color(189, 189, 189));
        }
        
        // Date estimée
        JLabel timeLabel = new JLabel(getStepTimeEstimate(step));
        timeLabel.setFont(SMALL_FONT);
        timeLabel.setForeground(SUBTEXT_COLOR);
        
        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        textPanel.setBackground(BACKGROUND_COLOR);
        textPanel.add(label);
        textPanel.add(timeLabel);
        
        if (!isLast && !step.equals("annulée")) {
            // Connecteur entre les étapes
            JPanel connector = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    int width = 4;
                    int x = (getWidth() - width) / 2;
                    
                    GradientPaint gradient = new GradientPaint(
                        0, 0, isStepCompleted(step) ? STEP_COMPLETED : STEP_FUTURE,
                        0, getHeight(), isStepCompleted(step) ? STEP_COMPLETED : STEP_FUTURE
                    );
                    g2.setPaint(gradient);
                    g2.fillRoundRect(x, 0, width, getHeight(), 5, 5);
                }
            };
            connector.setPreferredSize(new Dimension(50, 30));
            
            JPanel rightPanel = new JPanel(new BorderLayout());
            rightPanel.setBackground(BACKGROUND_COLOR);
            rightPanel.add(textPanel, BorderLayout.NORTH);
            rightPanel.add(connector, BorderLayout.CENTER);
            
            panel.add(circle, BorderLayout.WEST);
            panel.add(rightPanel, BorderLayout.CENTER);
        } else {
            panel.add(circle, BorderLayout.WEST);
            panel.add(textPanel, BorderLayout.CENTER);
        }
        
        return panel;
    }

    private String getStepTimeEstimate(String step) {
        if (commandeSelectionnee == null) return "";
        
        switch(step) {
            case "en_attente":
                return "15-30 min";
            case "en préparation":
                return "20-40 min";
            case "prête":
                String type = commandeSelectionnee.getTypeCommande().toLowerCase();
                if (type.contains("livraison")) return "30 min";
                if (type.contains("sur place")) return "Immédiat";
                return "À récupérer";
            case "en cours de livraison":
                return "15-30 min";
            case "livrée":
            case "servie":
            case "récupérée":
                return new SimpleDateFormat("HH:mm").format(commandeSelectionnee.getDateCommande());
            default:
                return "";
        }
    }

    

    private JLabel getDisplayStatus(String statut) {
        String statusText;
        Color bgColor;
        
        if (statut == null) {
            statusText = "INCONNU";
            bgColor = PRIMARY_COLOR;
        } else {
            switch(statut.toLowerCase()) {
                case "en_attente":
                    statusText = "EN ATTENTE";
                    bgColor = STATUS_PENDING;
                    break;
                case "en préparation":
                    statusText = "EN PRÉPARATION";
                    bgColor = STATUS_PREPARATION;
                    break;
                case "prête":
                    statusText = "PRÊTE";
                    bgColor = STATUS_READY;
                    break;
                case "en cours de livraison":
                    statusText = "EN LIVRAISON";
                    bgColor = STATUS_DELIVERY;
                    break;
                case "livrée":
                    statusText = "LIVRÉE";
                    bgColor = STATUS_DELIVERED;
                    break;
                case "servie":
                    statusText = "SERVIE";
                    bgColor = STATUS_SERVED;
                    break;
                case "prête à récupérer":
                    statusText = "PRÊTE À RÉCUPÉRER";
                    bgColor = STATUS_READY_FOR_PICKUP;
                    break;
                case "récupérée":
                    statusText = "RÉCUPÉRÉE";
                    bgColor = STATUS_PICKED_UP;
                    break;
                case "annulée":
                    statusText = "ANNULÉE";
                    bgColor = STATUS_CANCELLED;
                    break;
                default:
                    statusText = statut.toUpperCase();
                    bgColor = PRIMARY_COLOR;
            }
        }
        
        JLabel badge = new JLabel(statusText);
        badge.setFont(BOLD_SMALL_FONT);
        badge.setForeground(Color.WHITE);
        badge.setOpaque(true);
        badge.setBackground(bgColor);
        badge.setBorder(new EmptyBorder(3, 8, 3, 8));
        badge.setHorizontalAlignment(SwingConstants.CENTER);
        
        return badge;
    }

    private boolean isStepCompleted(String step) {
        if (commandeSelectionnee == null || commandeSelectionnee.getStatut().equals("annulée")) return false;
        
        String[] steps = getStatusesForOrderType(commandeSelectionnee.getTypeCommande());
        int currentIndex = -1;
        int stepIndex = -1;
        
        for (int i = 0; i < steps.length; i++) {
            if (steps[i].equals(commandeSelectionnee.getStatut())) {
                currentIndex = i;
            }
            if (steps[i].equals(step)) {
                stepIndex = i;
            }
        }
        
        return stepIndex < currentIndex;
    }

    private Color getStepColor(String step) {
        if (commandeSelectionnee == null) return STEP_FUTURE;
        
        if (step.equals(commandeSelectionnee.getStatut())) {
            return getStatusColor(commandeSelectionnee.getStatut());
        } 
        else if (isStepCompleted(step)) {
            return STEP_COMPLETED;
        } 
        else {
            return STEP_FUTURE;
        }
    }

    private Color getStatusColor(String status) {
        if (status == null) return STATUS_PENDING;
        
        switch(status) {
            case "en_attente": return STATUS_PENDING;
            case "en préparation": return STATUS_PREPARATION;
            case "prête": return STATUS_READY;
            case "en cours de livraison": return STATUS_DELIVERY;
            case "livrée": return STATUS_DELIVERED;
            case "servie": return STATUS_SERVED;
            case "prête à récupérer": return STATUS_READY_FOR_PICKUP;
            case "récupérée": return STATUS_PICKED_UP;
            case "annulée": return STATUS_CANCELLED;
            default: return STATUS_PENDING;
        }
    }

    private String getStatusMessage() {
        if (commandeSelectionnee == null) return "";
        
        String status = commandeSelectionnee.getStatut();
        String type = commandeSelectionnee.getTypeCommande().toLowerCase();
        
        switch(status) {
            case "en_attente":
                return "Votre commande a été reçue et sera traitée prochainement";
            case "en préparation":
                return "Votre commande est en cours de préparation";
            case "prête":
                if (type.contains("livraison")) {
                    return "Votre commande est prête pour la livraison";
                } else if (type.contains("sur place")) {
                    return "Votre commande est prête à être servie";
                } else {
                    return "Votre commande est prête à être récupérée";
                }
            case "en cours de livraison":
                return "Votre commande est en cours de livraison";
            case "servie":
                return "Votre commande a été servie avec succès";
            case "récupérée":
                return "Vous avez récupéré votre commande";
            case "livrée":
                return "Votre commande a été livrée avec succès";
            case "annulée":
                return "Votre commande a été annulée";
            default:
                return "Suivi de votre commande";
        }
    }

    private String getEstimatedTime() {
        if (commandeSelectionnee == null) return "";
        
        String status = commandeSelectionnee.getStatut();
        String type = commandeSelectionnee.getTypeCommande().toLowerCase();
        
        switch(status) {
            case "en_attente":
                return "Temps estimé avant traitement: 15-30 minutes";
            case "en préparation":
                return "Temps estimé: 20-40 minutes";
            case "prête":
                if (type.contains("livraison")) {
                    return "Livraison prévue dans les 30 minutes";
                } else if (type.contains("sur place")) {
                    return "Service imminent";
                } else {
                    return "Disponible au retrait immédiat";
                }
            case "en cours de livraison":
                return "Livraison estimée dans 15-30 minutes";
            case "servie":
            case "récupérée":
            case "livrée":
                return "Terminée à " + new SimpleDateFormat("HH:mm").format(commandeSelectionnee.getDateCommande());
            case "annulée":
                return "Commande annulée";
            default:
                return "";
        }
    }

    private JPanel createModernEmptyState(String message) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(50, 0, 50, 0));
        
        JLabel label = new JLabel(message);
        label.setFont(BODY_FONT);
        label.setForeground(SUBTEXT_COLOR);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        
        panel.add(label, BorderLayout.CENTER);
        return panel;
    }

    private void updateTrackingDisplay() {
        if (commandeSelectionnee == null) {
            showEmptyState("Aucune commande disponible");
            return;
        }
        
        if (splitPane != null) {
            trackingDetailsPanel = createModernTrackingDetailsPanel();
            commandListPanel = createModernCommandListPanel();
            
            splitPane.setRightComponent(trackingDetailsPanel);
            splitPane.setLeftComponent(commandListPanel);
            
            splitPane.revalidate();
            splitPane.repaint();
        } else {
            createMainInterface();
        }
    }

    // Classe helper pour les bordures arrondies
    private static class RoundBorder extends AbstractBorder {
        private int radius;
        private Color color;
        
        public RoundBorder(int radius, Color color) {
            this.radius = radius;
            this.color = color;
        }
        
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.draw(new RoundRectangle2D.Double(x, y, width-1, height-1, radius, radius));
            g2.dispose();
        }
        
        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(radius, radius, radius, radius);
        }
        
        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.left = insets.right = insets.bottom = insets.top = radius;
            return insets;
        }
    }
}