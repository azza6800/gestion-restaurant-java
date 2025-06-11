package interfaces;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import Controller.ClientController;
import entité.Client;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.text.SimpleDateFormat;

public class GerantDashboard extends JFrame {
    // Palette de couleurs modernes
    public Color primaryColor = new Color(30, 58, 138); // Bleu foncé
    public Color secondaryColor = new Color(241, 245, 249); // Gris très clair
    public Color accentColor = new Color(59, 130, 246); // Bleu vif
    public Color dangerColor = new Color(220, 38, 38); // Rouge
    public Color successColor = new Color(22, 163, 74); // Vert
    public Color textColor = new Color(31, 41, 55); // Gris foncé
    public Color lightTextColor = new Color(75, 85, 99); // Gris moyen

    // Police moderne
    private Font mainFont = new Font("Segoe UI", Font.PLAIN, 14);
    private Font titleFont = new Font("Segoe UI Semibold", Font.BOLD, 16);

    public GerantDashboard() {
        initUI();
    }

    private void initUI() {
        setTitle("Tableau de bord - Gérant");
        setSize(1280, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setUndecorated(true);
        setShape(new RoundRectangle2D.Double(0, 0, 1280, 800, 30, 30));

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(secondaryColor);
        mainPanel.setBorder(new EmptyBorder(0, 0, 0, 0));

        // Header amélioré
        mainPanel.add(new HeaderPanel(this), BorderLayout.NORTH);

        // Content with sidebar
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        
        // Sidebar amélioré
        contentPanel.add(new SidebarPanel(this, getTitle(), getTitle(), getTitle()), BorderLayout.WEST);
        
        // Main tabs avec style moderne
        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        
        tabbedPane.setFont(mainFont);
        tabbedPane.setBackground(Color.WHITE);
        tabbedPane.setForeground(textColor);
        tabbedPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Style des onglets
        tabbedPane.setUI(new javax.swing.plaf.basic.BasicTabbedPaneUI() {
            @Override
            protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex, 
                                          int x, int y, int w, int h, boolean isSelected) {
                // Pas de bordure
            }
            
            @Override
            protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex, 
                                             int x, int y, int w, int h, boolean isSelected) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (isSelected) {
                    g2d.setColor(accentColor);
                    g2d.fillRoundRect(x, y + h - 5, w, 5, 5, 5);
                }
            }
            
            @Override
            protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
                // Pas de bordure de contenu
            }
        });
        
        // Add tabs with icons
        tabbedPane.addTab("Clients", createIcon("/icons/users.png"), new ClientsPanel(this));
        tabbedPane.addTab("Menu", createIcon("/icons/menu.png"), new MenuPanel(this));
        tabbedPane.addTab("Commandes", createIcon("/icons/orders.png"), new OrdersPanel(this));
        tabbedPane.addTab("Statistiques", createIcon("/icons/stats.png"), new StatsPanel(this));
        
        contentPanel.add(tabbedPane, BorderLayout.CENTER);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        // Ombre portée pour la fenêtre
        setDropShadow(mainPanel);
        
        add(mainPanel);
    }

    private ImageIcon createIcon(String path) {
        try {
            return new ImageIcon(getClass().getResource(path));
        } catch (Exception e) {
            System.err.println("Icon not found: " + path);
            return new ImageIcon(); // retourne une icône vide si non trouvée
        }
    }

    private void setDropShadow(JPanel panel) {
        Border border = BorderFactory.createEmptyBorder(0, 0, 5, 5);
        panel.setBorder(BorderFactory.createCompoundBorder(
            border, 
            new DropShadowBorder(Color.BLACK, 10, 0.5f, 12, false, true, true, true)
        ));
    }

    public void refreshClientTable() {
        SwingUtilities.invokeLater(() -> {
            JTabbedPane tabbedPane = (JTabbedPane) ((JPanel) getContentPane().getComponent(0))
                                    .getComponent(1);
            
            if (tabbedPane.getTabCount() > 0) {
                Component comp = tabbedPane.getComponentAt(0);
                if (comp instanceof ClientsPanel) {
                    ((ClientsPanel) comp).refreshTable();
                }
            }
        });
    }
}

// Classe pour l'ombre portée (à ajouter)
class DropShadowBorder extends AbstractBorder {
    private Color color;
    private int thickness;
    private int corners;
    private boolean shadowTop;
    private boolean shadowLeft;
    private boolean shadowBottom;
    private boolean shadowRight;
    private float opacity;
    
    public DropShadowBorder(Color color, int thickness, float opacity, int corners, 
                           boolean shadowTop, boolean shadowLeft, 
                           boolean shadowBottom, boolean shadowRight) {
        this.color = color;
        this.thickness = thickness;
        this.corners = corners;
        this.shadowTop = shadowTop;
        this.shadowLeft = shadowLeft;
        this.shadowBottom = shadowBottom;
        this.shadowRight = shadowRight;
        this.opacity = opacity;
    }
    
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Créer l'ombre
        int shadowSize = thickness;
        Color shadowColorA = new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)(opacity * 255));
        Color shadowColorB = new Color(color.getRed(), color.getGreen(), color.getBlue(), 0);
        
        GradientPaint gp = new GradientPaint(0, 0, shadowColorA, shadowSize, 0, shadowColorB);
        
        // Dessiner les ombres selon les côtés demandés
        if (shadowBottom) {
            gp = new GradientPaint(0, height - shadowSize, shadowColorA, 0, height, shadowColorB);
            g2d.setPaint(gp);
            g2d.fillRect(x, height - shadowSize, width, shadowSize);
        }
        
        if (shadowRight) {
            gp = new GradientPaint(width - shadowSize, 0, shadowColorA, width, 0, shadowColorB);
            g2d.setPaint(gp);
            g2d.fillRect(width - shadowSize, y, shadowSize, height);
        }
        
        g2d.dispose();
    }
    
    public Insets getBorderInsets(Component c) {
        return new Insets(
            shadowTop ? thickness : 0, 
            shadowLeft ? thickness : 0, 
            shadowBottom ? thickness : 0, 
            shadowRight ? thickness : 0
        );
    }
    
    public Insets getBorderInsets(Component c, Insets insets) {
        insets.left = shadowLeft ? thickness : 0;
        insets.top = shadowTop ? thickness : 0;
        insets.right = shadowRight ? thickness : 0;
        insets.bottom = shadowBottom ? thickness : 0;
        return insets;
    }
}