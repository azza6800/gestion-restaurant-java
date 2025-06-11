package interfaces;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import entité.Commande;
import entité.Commande_produits;
import Controller.CommandeController;

public class HistoriqueClient extends JPanel {
    private CommandeController commandeController;
    private List<Commande> commandesHistorique;
    
    // Couleurs modernes
    private final Color PRIMARY_COLOR = new Color(59, 89, 152);
    private final Color BACKGROUND_COLOR = new Color(245, 247, 250);
    private final Color CARD_COLOR = Color.WHITE;
    private final Color SUCCESS_COLOR = new Color(46, 204, 113);
    private final Color ERROR_COLOR = new Color(231, 76, 60);
    private final Color TEXT_COLOR = new Color(52, 73, 94);
    private final Color SECONDARY_TEXT = new Color(127, 140, 141);
    private final Color BORDER_COLOR = new Color(236, 240, 241);

    // Polices
    private final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private final Font HEADER_FONT = new Font("Segoe UI Semibold", Font.PLAIN, 14);
    private final Font BODY_FONT = new Font("Segoe UI", Font.PLAIN, 13);
    private final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 14);

    public HistoriqueClient(int clientId) {
        this.commandeController = new CommandeController();
        initUI();
        loadCommandesHistorique(clientId);
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Titre
        JLabel titleLabel = new JLabel("Historique des Commandes");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setBorder(new EmptyBorder(0, 0, 20, 0));

        // Panel de filtres
        JPanel filterPanel = createFilterPanel();
        
        // Panel des commandes
        JPanel commandesPanel = new JPanel();
        commandesPanel.setLayout(new BoxLayout(commandesPanel, BoxLayout.Y_AXIS));
        commandesPanel.setBackground(BACKGROUND_COLOR);
        
        JScrollPane scrollPane = new JScrollPane(commandesPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(BACKGROUND_COLOR);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        add(titleLabel, BorderLayout.NORTH);
        add(filterPanel, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.SOUTH);
    }

    private JPanel createFilterPanel() {
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        filterPanel.setBackground(BACKGROUND_COLOR);
        filterPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel filterLabel = new JLabel("Filtrer par:");
        filterLabel.setFont(BODY_FONT);
        filterLabel.setForeground(TEXT_COLOR);

        JButton allButton = createFilterButton("Toutes");
        JButton deliveredButton = createFilterButton("Livrées");
        JButton canceledButton = createFilterButton("Annulées");

        filterPanel.add(filterLabel);
        filterPanel.add(allButton);
        filterPanel.add(deliveredButton);
        filterPanel.add(canceledButton);

        return filterPanel;
    }

    private JButton createFilterButton(String text) {
        JButton button = new JButton(text);
        button.setFont(BODY_FONT);
        button.setBackground(CARD_COLOR);
        button.setForeground(TEXT_COLOR);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(BORDER_COLOR);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(CARD_COLOR);
            }
        });

        return button;
    }

    private void loadCommandesHistorique(int clientId) {
        // Récupérer toutes les commandes du client
        List<Commande> toutesCommandes = commandeController.getCommandesParClient(clientId);
        
        // Filtrer pour ne garder que les commandes livrées ou annulées
        commandesHistorique = new ArrayList<>();
        for (Commande commande : toutesCommandes) {
            if (commande.getStatut().equals(Commande.LIVREE) || commande.getStatut().equals(Commande.ANNULEE)) {
                commandesHistorique.add(commande);
            }
        }
        
        displayCommandes();
    }

    private void displayCommandes() {
        JPanel commandesPanel = (JPanel) ((JScrollPane) getComponent(2)).getViewport().getView();
        commandesPanel.removeAll();

        if (commandesHistorique.isEmpty()) {
            JLabel emptyLabel = new JLabel("Aucune commande trouvée dans l'historique");
            emptyLabel.setFont(BODY_FONT);
            emptyLabel.setForeground(SECONDARY_TEXT);
            emptyLabel.setHorizontalAlignment(SwingConstants.CENTER);
            emptyLabel.setBorder(new EmptyBorder(50, 0, 50, 0));
            commandesPanel.add(emptyLabel);
        } else {
            for (Commande commande : commandesHistorique) {
                commandesPanel.add(createCommandeCard(commande));
                commandesPanel.add(Box.createRigidArea(new Dimension(0, 15)));
            }
        }

        commandesPanel.revalidate();
        commandesPanel.repaint();
    }

    private JPanel createCommandeCard(Commande commande) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBackground(CARD_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

        // En-tête de la carte
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(CARD_COLOR);
        headerPanel.setBorder(new EmptyBorder(0, 0, 10, 0));

        JLabel idLabel = new JLabel("Commande #" + commande.getId());
        idLabel.setFont(HEADER_FONT);
        idLabel.setForeground(PRIMARY_COLOR);

        JLabel dateLabel = new JLabel(commande.getDateCommande().toString());
        dateLabel.setFont(BODY_FONT);
        dateLabel.setForeground(SECONDARY_TEXT);

        JLabel statusLabel = new JLabel(commande.getStatut());
        statusLabel.setFont(HEADER_FONT);
        statusLabel.setForeground(commande.getStatut().equals("Livrée") ? SUCCESS_COLOR : ERROR_COLOR);
        statusLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        headerPanel.add(idLabel, BorderLayout.WEST);
        headerPanel.add(dateLabel, BorderLayout.CENTER);
        headerPanel.add(statusLabel, BorderLayout.EAST);

        // Détails de la commande
        JPanel detailsPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        detailsPanel.setBackground(CARD_COLOR);

        // Type de commande
        JPanel typePanel = new JPanel(new BorderLayout());
        typePanel.setBackground(CARD_COLOR);
        JLabel typeTitle = new JLabel("Type");
        typeTitle.setFont(BODY_FONT);
        typeTitle.setForeground(SECONDARY_TEXT);
        JLabel typeValue = new JLabel(commande.getTypeCommande());
        typeValue.setFont(BODY_FONT);
        typeValue.setForeground(TEXT_COLOR);
        typePanel.add(typeTitle, BorderLayout.NORTH);
        typePanel.add(typeValue, BorderLayout.CENTER);

        // Nombre d'articles
        JPanel itemsPanel = new JPanel(new BorderLayout());
        itemsPanel.setBackground(CARD_COLOR);
        JLabel itemsTitle = new JLabel("Articles");
        itemsTitle.setFont(BODY_FONT);
        itemsTitle.setForeground(SECONDARY_TEXT);
        JLabel itemsValue = new JLabel(String.valueOf(commande.getProduits().size()));
        itemsValue.setFont(BODY_FONT);
        itemsValue.setForeground(TEXT_COLOR);
        itemsPanel.add(itemsTitle, BorderLayout.NORTH);
        itemsPanel.add(itemsValue, BorderLayout.CENTER);

        // Montant total
        JPanel totalPanel = new JPanel(new BorderLayout());
        totalPanel.setBackground(CARD_COLOR);
        JLabel totalTitle = new JLabel("Total");
        totalTitle.setFont(BODY_FONT);
        totalTitle.setForeground(SECONDARY_TEXT);
        
        double total = 0;
        for (Commande_produits cp : commande.getProduits()) {
            total += cp.getProduit().getPrix() * cp.getQuantite();
        }
        
        JLabel totalValue = new JLabel(String.format("%.2f DT", total));
        totalValue.setFont(BODY_FONT);
        totalValue.setForeground(TEXT_COLOR);
        totalPanel.add(totalTitle, BorderLayout.NORTH);
        totalPanel.add(totalValue, BorderLayout.CENTER);

        detailsPanel.add(typePanel);
        detailsPanel.add(itemsPanel);
        detailsPanel.add(totalPanel);

        card.add(headerPanel, BorderLayout.NORTH);
        card.add(detailsPanel, BorderLayout.CENTER);

        return card;
    }
}