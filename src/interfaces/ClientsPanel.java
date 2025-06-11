package interfaces;

import entité.Client;
import Controller.ClientController;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.net.URL;
import javax.imageio.ImageIO;

public class ClientsPanel extends JPanel {
    private final GerantDashboard parent;
    private final DefaultTableModel model;
    private final JTable table;
    private Timer refreshTimer;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private final Color backgroundColor = new Color(248, 249, 252);
    private final Color cardColor = Color.WHITE;
    private final Color primaryColor = new Color(65, 105, 225);
    private final Color borderColor = new Color(230, 230, 230);

    public ClientsPanel(GerantDashboard parent) {
        this.parent = parent;
        this.model = createTableModel();
        this.table = createTable();
        initUI();
        initAutoRefresh();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setBackground(backgroundColor);

        JPanel cardPanel = new JPanel(new BorderLayout());
        cardPanel.setBackground(cardColor);
        cardPanel.setBorder(new CompoundBorder(
            new MatteBorder(1, 1, 1, 1, borderColor),
            new EmptyBorder(20, 20, 20, 20)
        ));
        
        cardPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        cardPanel.add(createTablePanel(), BorderLayout.CENTER);

        add(cardPanel, BorderLayout.CENTER);
        refreshTable();
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        headerPanel.setBackground(cardColor);

        // Barre de recherche à gauche
        headerPanel.add(createSearchPanel(), BorderLayout.WEST);

        // Titre centré
        JLabel titleLabel = new JLabel("Gestion des Clients", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 22));
        titleLabel.setForeground(new Color(60, 60, 60));
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        // Bouton d'ajout à droite
        JButton addButton = createModernButton("+ Nouveau Client", primaryColor, 
            e -> showAddClientDialog(), true, true);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        buttonPanel.setBackground(cardColor);
        buttonPanel.add(addButton);
        headerPanel.add(buttonPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBackground(cardColor);
        searchPanel.setBorder(new EmptyBorder(0, 0, 0, 20));

        JPanel searchContainer = new JPanel(new BorderLayout());
        searchContainer.setBackground(new Color(240, 241, 245));
        searchContainer.setBorder(new CompoundBorder(
            new MatteBorder(1, 1, 1, 1, borderColor),
            new EmptyBorder(5, 10, 5, 5)
        ));
        searchContainer.setPreferredSize(new Dimension(300, 40));

        JTextField searchField = new JTextField();
        styleSearchField(searchField);

        JButton searchButton = createIconButton("https://cdn-icons-png.flaticon.com/128/5868/5868370.png", 
            e -> performSearch(searchField.getText()));

        searchContainer.add(searchField, BorderLayout.CENTER);
        searchContainer.add(searchButton, BorderLayout.EAST);
        searchPanel.add(searchContainer, BorderLayout.WEST);

        return searchPanel;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(cardColor);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new MatteBorder(1, 0, 0, 0, borderColor));
        scrollPane.getViewport().setBackground(cardColor);
        
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        return tablePanel;
    }

    private DefaultTableModel createTableModel() {
        String[] columns = {"ID", "Nom", "Prénom", "Email", "Téléphone", "Adresse", "Date Naissance", "Actions"};
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 7;
            }
        };
    }

    private JTable createTable() {
        JTable table = new JTable(model) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? cardColor : new Color(249, 251, 253));
                }
                return c;
            }
        };
        
        customizeTable(table);
        return table;
    }

    private void customizeTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(48);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(240, 240, 240));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(250, 250, 250));
        table.getTableHeader().setForeground(new Color(100, 100, 100));
        table.getTableHeader().setBorder(new MatteBorder(0, 0, 1, 0, borderColor));
        table.getTableHeader().setPreferredSize(new Dimension(0, 40));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setIntercellSpacing(new Dimension(0, 0));

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        // On garde les boutons Modifier/Supprimer d'origine
        table.getColumnModel().getColumn(7).setCellRenderer(
            new ButtonRenderer(parent.primaryColor, parent.dangerColor));
        table.getColumnModel().getColumn(7).setCellEditor(new ButtonEditor(parent));
    }

    private JButton createIconButton(String iconUrl, ActionListener action) {
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(30, 30));
        button.setBackground(new Color(240, 241, 245));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.addActionListener(action);

        try {
            URL url = new URL(iconUrl);
            Image image = ImageIO.read(url);
            if (image != null) {
                Image scaledImage = image.getScaledInstance(16, 16, Image.SCALE_SMOOTH);
                button.setIcon(new ImageIcon(scaledImage));
            }
        } catch (Exception ex) {
            button.setText("🔍");
            System.err.println("Erreur de chargement de l'icône: " + ex.getMessage());
        }

        return button;
    }

    private JButton createModernButton(String text, Color bgColor, ActionListener action, 
                                     boolean hasShadow, boolean hasIcon) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (hasShadow) {
                    g2.setColor(new Color(0, 0, 0, 15));
                    g2.fillRoundRect(1, 3, getWidth()-2, getHeight()-2, 8, 8);
                }
                
                if (getModel().isPressed()) {
                    g2.setColor(bgColor.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(bgColor.brighter());
                } else {
                    g2.setColor(bgColor);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight()-3, 8, 8);
                g2.dispose();
                
                super.paintComponent(g);
            }
        };
        
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setForeground(Color.WHITE);
        button.setContentAreaFilled(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.addActionListener(action);
        
        if (hasIcon) {
            button.setIconTextGap(10);
            button.setHorizontalTextPosition(SwingConstants.RIGHT);
        }
        
        return button;
    }

    private void styleSearchField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        field.setBackground(new Color(240, 241, 245));
        field.putClientProperty("JTextField.placeholderText", "Rechercher par nom, email...");
    }

    private void performSearch(String query) {
        TableRowSorter<DefaultTableModel> sorter = (TableRowSorter<DefaultTableModel>) table.getRowSorter();
        sorter.setRowFilter(query.trim().isEmpty() ? null : RowFilter.regexFilter("(?i)" + query));
    }

    public void refreshTable() {
        SwingUtilities.invokeLater(() -> {
            model.setRowCount(0);
            List<Client> clients = new ClientController().getAllClients();
            
            clients.forEach(client -> model.addRow(new Object[]{
                client.getId(),
                client.getNom(),
                client.getPrenom(),
                client.getLogin(),
                client.getTelephone(),
                client.getAdresse(),
                dateFormat.format(client.getDateNaissance()),
                "Modifier/Supprimer"
            }));
        });
    }

    private void initAutoRefresh() {
        refreshTimer = new Timer(30000, e -> refreshTable());
        refreshTimer.start();
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        if (refreshTimer != null) {
            refreshTimer.stop();
        }
    }

    private void showAddClientDialog() {
        new AddClientDialog(parent, this).setVisible(true);
    }
}