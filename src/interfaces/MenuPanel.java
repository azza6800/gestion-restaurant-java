package interfaces;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import Controller.ProduitController;

public class MenuPanel extends JPanel {
    private final ProduitController produitController;
    private final ProductCardManager cardManager;
    private final ProductFormManager formManager;
    private final JTabbedPane tabbedPane;
    private final Color primaryColor = new Color(65, 105, 225);
    private final Color backgroundColor = new Color(248, 249, 252);
    private final Color cardColor = Color.WHITE;
    private final Color borderColor = new Color(230, 230, 230);
    
    public MenuPanel(GerantDashboard parent) {
        this.produitController = new ProduitController();
        this.cardManager = new ProductCardManager(produitController);
        this.tabbedPane = new JTabbedPane();
        this.formManager = new ProductFormManager(produitController, cardManager);
        
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 0));
        setBackground(backgroundColor);
        
        // Configuration des onglets
        tabbedPane.addTab("Menu", createMainViewPanel());
        tabbedPane.addTab("Ajouter", formManager.createFormPanel(tabbedPane));
        
        // Masquer la barre d'onglets avec style moderne
        tabbedPane.setUI(new javax.swing.plaf.basic.BasicTabbedPaneUI() {
            @Override protected int calculateTabAreaHeight(int tabPlacement, int horizRunCount, int maxTabHeight) {
                return 0;
            }
        });
        
        // Style du tabbedPane
        tabbedPane.setBackground(backgroundColor);
        tabbedPane.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, borderColor));
        
        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createMainViewPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(backgroundColor);
        
        // Header avec ombre portée
        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(new EmptyBorder(0, 0, 25, 0));
        header.setBackground(backgroundColor);
        
        // Titre avec effet de gradient
        JLabel title = new JLabel("Menu du Restaurant") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
                
                Font font = new Font("Segoe UI Semibold", Font.PLAIN, 24);
                g2.setFont(font);
                
                // Effet de gradient sur le texte
                GradientPaint gp = new GradientPaint(0, 0, new Color(50, 50, 50), 
                                                   0, getHeight(), new Color(80, 80, 80));
                g2.setPaint(gp);
                g2.drawString(getText(), 0, getFontMetrics(font).getAscent());
                g2.dispose();
            }
        };
        title.setPreferredSize(new Dimension(300, 40));
        
        // Bouton ajouter moderne
        JButton addButton = createModernAddButton();
        
        header.add(title, BorderLayout.WEST);
        header.add(addButton, BorderLayout.EAST);
        
        // Panel de contenu avec effet de carte
        JPanel cardPanel = cardManager.createViewPanel();
        cardPanel.setBackground(backgroundColor);
        
        JScrollPane scrollPane = new JScrollPane(cardPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(backgroundColor);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        // Style personnalisé de la scrollbar
        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
        verticalScrollBar.setUI(new ModernScrollBarUI());
        verticalScrollBar.setPreferredSize(new Dimension(8, Integer.MAX_VALUE));
        verticalScrollBar.setBackground(backgroundColor);
        
        mainPanel.add(header, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        return mainPanel;
    }

    private JButton createModernAddButton() {
        JButton button = new JButton("+ Ajouter Produit") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Ombre portée
                g2.setColor(new Color(0, 0, 0, 15));
                g2.fillRoundRect(1, 3, getWidth()-2, getHeight()-2, 12, 12);
                
                // Fond du bouton
                if (getModel().isPressed()) {
                    g2.setColor(primaryColor.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(primaryColor.brighter());
                } else {
                    g2.setColor(primaryColor);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight()-3, 12, 12);
                g2.dispose();
                
                super.paintComponent(g);
            }
            
            @Override
            protected void paintBorder(Graphics g) {
                // Pas de bordure par défaut
            }
        };
        
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setContentAreaFilled(false);
        button.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addActionListener(e -> {
            formManager.resetForm();
            tabbedPane.setSelectedIndex(1);
        });
        
        return button;
    }

    // Classe interne pour la scrollbar moderne
    private static class ModernScrollBarUI extends javax.swing.plaf.basic.BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            this.thumbColor = new Color(200, 200, 200);
            this.trackColor = new Color(240, 240, 240);
        }
        
        @Override
        protected JButton createDecreaseButton(int orientation) {
            return createZeroButton();
        }
        
        @Override
        protected JButton createIncreaseButton(int orientation) {
            return createZeroButton();
        }
        
        private JButton createZeroButton() {
            JButton button = new JButton();
            button.setPreferredSize(new Dimension(0, 0));
            button.setMinimumSize(new Dimension(0, 0));
            button.setMaximumSize(new Dimension(0, 0));
            return button;
        }
        
        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(thumbColor);
            g2.fillRoundRect(thumbBounds.x+1, thumbBounds.y+1, 
                            thumbBounds.width-2, thumbBounds.height-2, 5, 5);
            g2.dispose();
        }
        
        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(trackColor);
            g2.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
            g2.dispose();
        }
    }
}