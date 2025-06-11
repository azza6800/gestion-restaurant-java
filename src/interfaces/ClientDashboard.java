package interfaces;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.border.*;
import javax.imageio.ImageIO;
import java.net.URL;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.awt.event.*;
import entité.Client;
import Controller.ClientController;

public class ClientDashboard extends JFrame {
    protected int clientId;
    protected String nom;
    protected String prenom;
    protected String login;
    protected Date dateNaissance;
    protected String adresse;
    protected String telephone;
    
    protected Color primaryColor = new Color(255, 105, 120);
    protected Color darkColor = new Color(45, 45, 55);
    protected Color lightColor = new Color(250, 250, 252);
    protected Color accentColor = new Color(100, 220, 180);
    protected Color successColor = new Color(70, 160, 70);
    
    protected String logoUrl = "https://cdn-icons-png.flaticon.com/512/732/732247.png";
    protected String avatarUrl = "https://cdn-icons-png.flaticon.com/512/3135/3135715.png";
    protected String[] menuIcons = {
        "https://cdn-icons-png.flaticon.com/512/3177/3177440.png",
        "https://cdn-icons-png.flaticon.com/512/3652/3652191.png",
        "https://cdn-icons-png.flaticon.com/512/3710/3710195.png",
        "https://cdn-icons-png.flaticon.com/512/4383/4383660.png",
        "https://cdn-icons-png.flaticon.com/512/126/126472.png",
        "https://cdn-icons-png.flaticon.com/512/3135/3135715.png"
    };
    protected String logoutIcon = "https://cdn-icons-png.flaticon.com/512/1828/1828479.png";
    
    protected CardLayout cardLayout;
    protected JPanel dynamicContent;
    protected SidebarButton[] menuButtons;

    public ClientDashboard(int clientId, String nom, String prenom, String login, 
                         Date date, String adresse, String telephone) {
        this.clientId = clientId;
        this.nom = nom;
        this.prenom = prenom;
        this.login = login;
        this.dateNaissance = date;
        this.adresse = adresse;
        this.telephone = telephone;

        initUI();
    }

    protected void initUI() {
        setTitle("GourmetGo - Espace Client");
        setSize(1400, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setUndecorated(true);
        setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 30, 30));

        JPanel mainPanel = createMainPanel();
        add(mainPanel);
    }

    protected JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
            }
        };
        mainPanel.setBackground(lightColor);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        mainPanel.add(createTitleBar(), BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        contentPanel.add(createModernSidebar(), BorderLayout.WEST);

        cardLayout = new CardLayout();
        dynamicContent = new JPanel(cardLayout);
        dynamicContent.setOpaque(false);
        setupDynamicContent();

        contentPanel.add(dynamicContent, BorderLayout.CENTER);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(createStatusBar(), BorderLayout.SOUTH);

        return mainPanel;
    }

    protected void setupDynamicContent() {
        dynamicContent.add(new MenuClientPanel(), "menu");
        dynamicContent.add(new OrdersClientPanel(clientId), "orders");
        dynamicContent.add(new TrackingPanel(clientId), "tracking");
        dynamicContent.add(new HistoriqueClient(clientId), "history");
        dynamicContent.add(new JPanel(), "settings");
        dynamicContent.add(new Gerer_profil(this), "profile");

        cardLayout.show(dynamicContent, "menu");
    }

    protected JPanel createTitleBar() {
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(darkColor);
        titleBar.setPreferredSize(new Dimension(getWidth(), 70));

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        controlPanel.setOpaque(false);

        JButton profileBtn = new JButton();
        profileBtn.setContentAreaFilled(false);
        profileBtn.setBorder(new EmptyBorder(5, 15, 5, 15));
        profileBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        try {
            Image avatarImage = ImageIO.read(new URL(avatarUrl)).getScaledInstance(30, 30, Image.SCALE_SMOOTH);
            profileBtn.setIcon(new ImageIcon(avatarImage));
        } catch (Exception e) {
            profileBtn.setText("👤");
        }

        profileBtn.addActionListener(e -> {
            cardLayout.show(dynamicContent, "profile");
            for (SidebarButton btn : menuButtons) {
                if (btn != null) btn.setActive(false);
            }
            menuButtons[5].setActive(true);
        });

        JButton minBtn = new ControlButton("−");
        JButton closeBtn = new ControlButton("×");

        minBtn.addActionListener(e -> setState(Frame.ICONIFIED));
        closeBtn.addActionListener(e -> System.exit(0));

        controlPanel.add(profileBtn);
        controlPanel.add(minBtn);
        controlPanel.add(closeBtn);

        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        logoPanel.setOpaque(false);

        try {
            Image logoImage = ImageIO.read(new URL(logoUrl)).getScaledInstance(40, 40, Image.SCALE_SMOOTH);
            JLabel logoIcon = new JLabel(new ImageIcon(logoImage));
            logoPanel.add(logoIcon);
        } catch (Exception e) {
            logoPanel.add(new JLabel("🍴"));
        }

        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setOpaque(false);

        JLabel title = new JLabel("GourmetGo");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(lightColor);

        JLabel slogan = new JLabel("Votre gastronomie à portée de clic");
        slogan.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        slogan.setForeground(new Color(180, 180, 180));

        textPanel.add(title, BorderLayout.NORTH);
        textPanel.add(slogan, BorderLayout.SOUTH);
        logoPanel.add(textPanel);

        titleBar.add(logoPanel, BorderLayout.WEST);
        titleBar.add(controlPanel, BorderLayout.EAST);

        return titleBar;
    }

    protected JPanel createModernSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(darkColor);
        sidebar.setPreferredSize(new Dimension(280, getHeight()));
        sidebar.setBorder(new EmptyBorder(30, 20, 30, 20));

        JPanel profilePanel = new JPanel();
        profilePanel.setLayout(new BoxLayout(profilePanel, BoxLayout.Y_AXIS));
        profilePanel.setOpaque(false);
        profilePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel avatar = new JLabel();
        avatar.setAlignmentX(Component.CENTER_ALIGNMENT);
        try {
            Image avatarImage = ImageIO.read(new URL(avatarUrl)).getScaledInstance(80, 80, Image.SCALE_SMOOTH);
            avatar.setIcon(new ImageIcon(avatarImage));
        } catch (Exception e) {
            avatar.setIcon(new ImageIcon(new Color(100, 220, 180).getRGB() + ""));
        }
        avatar.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JLabel userName = new JLabel(prenom + " " + nom);
        userName.setFont(new Font("Segoe UI", Font.BOLD, 18));
        userName.setForeground(lightColor);
        userName.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel userLogin = new JLabel("@" + login);
        userLogin.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userLogin.setForeground(new Color(180, 180, 180));
        userLogin.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel statusBadge = new JPanel();
        statusBadge.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
        statusBadge.setOpaque(false);
        statusBadge.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel statusIcon = new JLabel("★");
        statusIcon.setForeground(accentColor);
        statusIcon.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JLabel statusText = new JLabel("Client #" + clientId);
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

        sidebar.add(profilePanel);

        String[] menuItems = {"Menu Gourmand", "Mes Commandes", "Suivi en Temps Réel", "Historique", "Paramètres", "Mon Profil"};
        String[] cardNames = {"menu", "orders", "tracking", "history", "settings", "profile"};

        menuButtons = new SidebarButton[menuItems.length];

        for (int i = 0; i < menuItems.length; i++) {
            menuButtons[i] = new SidebarButton(menuItems[i], menuIcons[i]);
            menuButtons[i].setAlignmentX(Component.LEFT_ALIGNMENT);

            final String cardName = cardNames[i];
            final int index = i;
            menuButtons[i].addActionListener(e -> {
                cardLayout.show(dynamicContent, cardName);
                
                Component comp = null;
                for (Component c : dynamicContent.getComponents()) {
                    if (c.isVisible()) {
                        comp = c;
                        break;
                    }
                }
                
                if (comp instanceof RefreshablePanel) {
                    ((RefreshablePanel)comp).refreshData();
                }
                
                for (SidebarButton btn : menuButtons) {
                    if (btn != null) btn.setActive(false);
                }
                menuButtons[index].setActive(true);
            });

            sidebar.add(menuButtons[i]);
            sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        if (menuButtons.length > 0) {
            menuButtons[0].setActive(true);
        }

        sidebar.add(Box.createVerticalGlue());

        SidebarButton logoutBtn = new SidebarButton("Déconnexion", logoutIcon);
        logoutBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        logoutBtn.setForeground(new Color(255, 120, 120));
        logoutBtn.addActionListener(e ->{ this.dispose();
        new LoginFrame1().setVisible(true);
        });
        sidebar.add(logoutBtn);

        return sidebar;
    }

    protected JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(darkColor);
        statusBar.setPreferredSize(new Dimension(getWidth(), 40));
        statusBar.setBorder(new EmptyBorder(0, 20, 0, 20));

        JLabel statusText = new JLabel("Prêt à commander • Dernière connexion: aujourd'hui à " + 
            java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")));
        statusText.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusText.setForeground(new Color(180, 180, 180));

        JLabel versionText = new JLabel("© 2023 GourmetGo • v2.0", SwingConstants.RIGHT);
        versionText.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        versionText.setForeground(new Color(180, 180, 180));

        statusBar.add(statusText, BorderLayout.WEST);
        statusBar.add(versionText, BorderLayout.EAST);

        return statusBar;
    }

    public void updateProfileInfo(String newNom, String newPrenom, String newLogin, 
                                Date newDateNaissance, String newAdresse, String newTelephone) {
        this.nom = newNom;
        this.prenom = newPrenom;
        this.login = newLogin;
        this.dateNaissance = newDateNaissance;
        this.adresse = newAdresse;
        this.telephone = newTelephone;
        refreshUI();
    }

    public void updateProfileInfoWithPassword(String newNom, String newPrenom, String newLogin, 
                                           Date selectedDate, String newAdresse, 
                                           String newTelephone, String newPassword) {
        try {
            Client client = new Client(
                clientId, newLogin, newPassword, newNom, newPrenom,
                selectedDate, newAdresse, newTelephone, false
            );
            
            ClientController controller = new ClientController();
            boolean success = controller.updateClient(client, true);
            
            if (success) {
                this.nom = newNom;
                this.prenom = newPrenom;
                this.login = newLogin;
                this.dateNaissance = selectedDate;
                this.adresse = newAdresse;
                this.telephone = newTelephone;
                
                refreshUI();
                
                JOptionPane.showMessageDialog(this, 
                    "Profil et mot de passe mis à jour avec succès",
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Échec de la mise à jour du profil",
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Erreur lors de la mise à jour: " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    protected void refreshUI() {
        JPanel mainPanel = (JPanel) getContentPane().getComponent(0);
        mainPanel.remove(1);
        
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        contentPanel.add(createModernSidebar(), BorderLayout.WEST);
        contentPanel.add(dynamicContent, BorderLayout.CENTER);
        
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    class ControlButton extends JButton {
        public ControlButton(String text) {
            super(text);
            setFont(new Font("Segoe UI", Font.PLAIN, 18));
            setForeground(lightColor);
            setBackground(new Color(0, 0, 0, 0));
            setBorder(new EmptyBorder(0, 15, 0, 15));
            setFocusPainted(false);
            setContentAreaFilled(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (getText().equals("×")) {
                        setBackground(new Color(255, 80, 80));
                    } else {
                        setBackground(new Color(80, 80, 80));
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    setBackground(new Color(0, 0, 0, 0));
                }
            });
        }
    }

    class SidebarButton extends JButton {
        private boolean active = false;

        public SidebarButton(String text, String iconUrl) {
            super(text);
            try {
                Image iconImage = ImageIO.read(new URL(iconUrl)).getScaledInstance(20, 20, Image.SCALE_SMOOTH);
                setIcon(new ImageIcon(iconImage));
            } catch (Exception e) {
                setText("• " + text);
            }

            setFont(new Font("Segoe UI", Font.PLAIN, 14));
            setForeground(lightColor);
            setBackground(new Color(0, 0, 0, 0));
            setBorder(new EmptyBorder(12, 20, 12, 20));
            setHorizontalAlignment(SwingConstants.LEFT);
            setFocusPainted(false);
            setContentAreaFilled(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setIconTextGap(15);

            updateStyle();

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (!active) {
                        setBackground(new Color(60, 60, 70));
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    if (!active) {
                        setBackground(new Color(0, 0, 0, 0));
                    }
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    if (!active) {
                        setBackground(new Color(80, 80, 90));
                    }
                }
            });
        }

        public void setActive(boolean active) {
            this.active = active;
            updateStyle();
        }

        private void updateStyle() {
            if (active) {
                setBackground(new Color(80, 80, 90));
                setForeground(accentColor);
            } else {
                setBackground(new Color(0, 0, 0, 0));
                setForeground(lightColor);
            }
        }
    }

    public interface RefreshablePanel {
        void refreshData();
    }
}