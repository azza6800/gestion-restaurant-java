package interfaces;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.net.URL;
import javax.imageio.ImageIO;

public class SidebarPanel extends JPanel {
    // Couleurs du thème
    private final Color bgColor = new Color(20, 20, 20);       // Noir profond
    private final Color textColor = new Color(255, 255, 255); // Blanc
    private final Color accentColor = new Color(0, 180, 0);   // Vert clair
    private final Color hoverColor = new Color(30, 30, 30);   // Noir plus clair
    
    // Icônes modernes (Font Awesome)
    private final String avatarUrl = "https://cdn-icons-png.flaticon.com/128/3135/3135715.png";
    private final String[] menuIcons = {
        "https://cdn-icons-png.flaticon.com/128/8489/8489208.png",  // Tableau de bord (dashboard)
        "https://cdn-icons-png.flaticon.com/128/5737/5737162.png", // Clients (users)
        "https://cdn-icons-png.flaticon.com/128/1037/1037855.png",   // Menu (restaurant menu)
        "https://cdn-icons-png.flaticon.com/128/12341/12341340.png",  // Commandes (orders/shopping cart)
        "https://cdn-icons-png.flaticon.com/128/3309/3309960.png",  // Statistiques (charts)
        "https://cdn-icons-png.flaticon.com/128/7586/7586487.png"   // Paramètres (settings)
    };
    private final String logoutIcon = "https://cdn-icons-png.flaticon.com/512/2550/2550432.png"; // Déconnexion (logout)

    private SidebarButton[] menuButtons;

    public SidebarPanel(GerantDashboard parent, String nom, String prenom, String login) {
        setPreferredSize(new Dimension(280, parent.getHeight()));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(bgColor);
        setBorder(new EmptyBorder(30, 20, 30, 20));

        // Panel profil
        JPanel profilePanel = createProfilePanel(nom, prenom, login);
        add(profilePanel);

        // Boutons du menu
        String[] menuItems = {"Tableau de bord", "Clients", "Menu", "Commandes", "Statistiques", "Paramètres"};
        menuButtons = new SidebarButton[menuItems.length];
        
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setOpaque(false);
        menuPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        for (int i = 0; i < menuItems.length; i++) {
            menuButtons[i] = new SidebarButton(menuItems[i], menuIcons[i]);
            menuButtons[i].setAlignmentX(Component.LEFT_ALIGNMENT);
            menuPanel.add(menuButtons[i]);
            menuPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        add(menuPanel);
        add(Box.createVerticalGlue());

        // Bouton déconnexion
        SidebarButton logoutBtn = new SidebarButton("Déconnexion", logoutIcon);
        logoutBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        logoutBtn.setForeground(new Color(255, 100, 100)); // Rouge clair
        logoutBtn.addActionListener(e -> {
            parent.dispose();
            new LoginFrame1().setVisible(true);
        });
        add(logoutBtn);
    }

    private JPanel createProfilePanel(String nom, String prenom, String login) {
        JPanel profilePanel = new JPanel();
        profilePanel.setLayout(new BoxLayout(profilePanel, BoxLayout.Y_AXIS));
        profilePanel.setOpaque(false);
        profilePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Avatar avec chargement asynchrone
        JLabel avatar = new JLabel();
        avatar.setPreferredSize(new Dimension(80, 80));
        avatar.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Chargement de l'avatar en arrière-plan
        new SwingWorker<ImageIcon, Void>() {
            @Override
            protected ImageIcon doInBackground() throws Exception {
                try {
                    BufferedImage originalImage = ImageIO.read(new URL(avatarUrl));
                    Image scaledImage = originalImage.getScaledInstance(80, 80, Image.SCALE_SMOOTH);
                    BufferedImage circularImage = new BufferedImage(80, 80, BufferedImage.TYPE_INT_ARGB);
                    
                    Graphics2D g2d = circularImage.createGraphics();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.fillOval(0, 0, 80, 80);
                    g2d.setComposite(AlphaComposite.SrcIn);
                    g2d.drawImage(scaledImage, 0, 0, null);
                    
                    // Ajout d'une bordure
                    g2d.setComposite(AlphaComposite.SrcOver);
                    g2d.setStroke(new BasicStroke(2));
                    g2d.setColor(accentColor);
                    g2d.drawOval(0, 0, 80, 80);
                    
                    g2d.dispose();
                    return new ImageIcon(circularImage);
                } catch (Exception e) {
                    // Avatar par défaut si le chargement échoue
                    BufferedImage defaultImage = new BufferedImage(80, 80, BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g2d = defaultImage.createGraphics();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    // Cercle extérieur
                    g2d.setColor(new Color(60, 60, 60));
                    g2d.fillOval(0, 0, 80, 80);
                    
                    // Cercle intérieur
                    g2d.setColor(new Color(255, 218, 185));
                    g2d.fillOval(15, 15, 50, 50);
                    
                    // Bordure
                    g2d.setColor(accentColor);
                    g2d.setStroke(new BasicStroke(2));
                    g2d.drawOval(0, 0, 80, 80);
                    
                    g2d.dispose();
                    return new ImageIcon(defaultImage);
                }
            }

            @Override
            protected void done() {
                try {
                    avatar.setIcon(get());
                } catch (Exception e) {
                    avatar.setIcon(null);
                }
            }
        }.execute();

        // Nom et prénom
        JLabel userName = new JLabel(prenom + " " + nom);
        userName.setFont(new Font("Segoe UI", Font.BOLD, 18));
        userName.setForeground(textColor);
        userName.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Login
        JLabel userLogin = new JLabel("@" + login);
        userLogin.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userLogin.setForeground(new Color(180, 180, 180));
        userLogin.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Badge statut
        JPanel statusBadge = new JPanel();
        statusBadge.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
        statusBadge.setOpaque(false);
        statusBadge.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel statusIcon = new JLabel("★");
        statusIcon.setForeground(accentColor);
        statusIcon.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JLabel statusText = new JLabel("Gérant");
        statusText.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusText.setForeground(new Color(180, 180, 180));

        statusBadge.add(statusIcon);
        statusBadge.add(statusText);

        profilePanel.add(avatar);
        profilePanel.add(Box.createRigidArea(new Dimension(0, 15)));
        profilePanel.add(userName);
        profilePanel.add(userLogin);
        profilePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        profilePanel.add(statusBadge);
        profilePanel.add(Box.createRigidArea(new Dimension(0, 30)));

        return profilePanel;
    }
    
    class SidebarButton extends JButton {
        private boolean active = false;

        public SidebarButton(String text, String iconUrl) {
            super(text);
            try {
                // Charger l'icône avec une taille légèrement plus grande
                Image iconImage = ImageIO.read(new URL(iconUrl)).getScaledInstance(22, 22, Image.SCALE_SMOOTH);
                setIcon(new ImageIcon(iconImage));
            } catch (Exception e) {
                // Si l'icône ne charge pas, utiliser un simple point
                setText("• " + text);
            }

            setFont(new Font("Segoe UI", Font.PLAIN, 14));
            setForeground(textColor);
            setBackground(new Color(0, 0, 0, 0));
            setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
            setHorizontalAlignment(SwingConstants.LEFT);
            setFocusPainted(false);
            setContentAreaFilled(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setIconTextGap(15);
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
            setAlignmentX(Component.LEFT_ALIGNMENT);

            addActionListener(e -> {
                for (SidebarButton btn : menuButtons) {
                    if (btn != null) btn.setActive(false);
                }
                setActive(true);
            });

            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    if (!active) {
                        setBackground(hoverColor);
                        setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createMatteBorder(0, 3, 0, 0, accentColor),
                            BorderFactory.createEmptyBorder(12, 17, 12, 20)
                        ));
                    }
                }

                public void mouseExited(MouseEvent e) {
                    if (!active) {
                        setBackground(new Color(0, 0, 0, 0));
                        setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
                    }
                }
            });
        }

        public void setActive(boolean active) {
            this.active = active;
            if (active) {
                setBackground(new Color(40, 40, 40));
                setForeground(accentColor);
                setFont(getFont().deriveFont(Font.BOLD));
                setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 3, 0, 0, accentColor),
                    BorderFactory.createEmptyBorder(12, 17, 12, 20)
                ));
            } else {
                setBackground(new Color(0, 0, 0, 0));
                setForeground(textColor);
                setFont(getFont().deriveFont(Font.PLAIN));
                setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
            }
        }
    }
}