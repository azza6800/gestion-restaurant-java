package interfaces;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import Controller.ClientController;
import Controller.LoginController;
import entité.Client;

import java.awt.event.ActionEvent;

public class LoginFrame1 extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel messageLabel;
    private JLabel avatarLabel;
    protected String logoUrl = "https://cdn-icons-png.flaticon.com/512/732/732247.png";
    protected String avatarUrl = "https://cdn-icons-png.flaticon.com/512/3135/3135715.png";

    public LoginFrame1() {
        initUI();
    }

    private void initUI() {
        // Configuration de la fenêtre
        setTitle("GourmetGo - Connexion");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setUndecorated(true);
        setShape(new RoundRectangle2D.Double(0, 0, 1000, 600, 30, 30));

        // Panel principal avec fond noir
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setColor(new Color(20, 20, 20)); // Noir profond
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new BorderLayout());

        // Panel gauche pour le logo et le slogan
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setOpaque(false);
        leftPanel.setPreferredSize(new Dimension(400, 600));
        leftPanel.setBorder(new EmptyBorder(50, 50, 50, 50));

        // Logo
        try {
            Image logoImage = ImageIO.read(new URL(logoUrl)).getScaledInstance(150, 150, Image.SCALE_SMOOTH);
            JLabel logoLabel = new JLabel(new ImageIcon(logoImage));
            logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
            leftPanel.add(logoLabel, BorderLayout.NORTH);
        } catch (IOException e) {
            e.printStackTrace();
            // Fallback si l'image ne charge pas
            JLabel titleLabel = new JLabel("GOURMETGO", SwingConstants.CENTER);
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
            titleLabel.setForeground(new Color(0, 150, 0));
            leftPanel.add(titleLabel, BorderLayout.NORTH);
        }

        // Slogan
        JLabel sloganLabel = new JLabel("<html><div style='text-align: center;'>" +
                "<h1 style='color: #00cc66; font-size: 24px;'>GourmetGo</h1>" +
                "<p style='color: #ffffff; font-size: 16px;'>Votre solution complète<br>de gestion de restaurant</p>" +
                "<p style='color: #aaaaaa; font-size: 14px; margin-top: 20px;'>Optimisez vos commandes,<br>gérez votre inventaire,<br>et simplifiez votre service</p></div></html>");
        sloganLabel.setHorizontalAlignment(SwingConstants.CENTER);
        leftPanel.add(sloganLabel, BorderLayout.CENTER);

        // Panel de formulaire à droite
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setOpaque(false);
        rightPanel.setBorder(new EmptyBorder(50, 50, 50, 50));

        // Panel de formulaire
        JPanel formPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setColor(new Color(30, 30, 30, 220)); // Fond noir légèrement transparent
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                g2d.setColor(new Color(0, 150, 0)); // Bordure verte
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 25, 25);
            }
        };
        formPanel.setLayout(null);
        formPanel.setPreferredSize(new Dimension(450, 500));
        formPanel.setOpaque(false);

        // Titre
        JLabel titleLabel = new JLabel("CONNEXION", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(0, 180, 0)); // Vert clair
        titleLabel.setBounds(0, 30, 450, 40);
        formPanel.add(titleLabel);

        // Avatar
        avatarLabel = new JLabel();
        try {
            Image avatarImage = ImageIO.read(new URL(avatarUrl)).getScaledInstance(100, 100, Image.SCALE_SMOOTH);
            avatarLabel.setIcon(new ImageIcon(avatarImage));
        } catch (IOException e) {
            e.printStackTrace();
            avatarLabel.setIcon(createDefaultAvatar());
        }
        avatarLabel.setBounds(175, 80, 100, 100);
        formPanel.add(avatarLabel);

        // Champ nom d'utilisateur
        JLabel userLabel = new JLabel("Nom d'utilisateur:");
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userLabel.setForeground(Color.WHITE);
        userLabel.setBounds(75, 200, 300, 20);
        formPanel.add(userLabel);

        usernameField = new JTextField();
        usernameField.setBounds(75, 220, 300, 40);
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        usernameField.setForeground(Color.WHITE);
        usernameField.setBackground(new Color(50, 50, 50));
        usernameField.setCaretColor(Color.WHITE);
        usernameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 150, 0)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10))
        );
        formPanel.add(usernameField);

        // Champ mot de passe
        JLabel passLabel = new JLabel("Mot de passe:");
        passLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passLabel.setForeground(Color.WHITE);
        passLabel.setBounds(75, 280, 300, 20);
        formPanel.add(passLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(75, 300, 300, 40);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setForeground(Color.WHITE);
        passwordField.setBackground(new Color(50, 50, 50));
        passwordField.setCaretColor(Color.WHITE);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 150, 0)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10))
        );
        formPanel.add(passwordField);

        // Bouton de connexion
        JButton loginButton = new JButton("SE CONNECTER");
        loginButton.setBounds(75, 370, 300, 45);
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        loginButton.setForeground(Color.WHITE);
        loginButton.setBackground(new Color(0, 120, 0)); // Vert foncé
        loginButton.setBorderPainted(false);
        loginButton.setFocusPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.addActionListener(this::handleLogin);

        // Effet de survol pour le bouton
        loginButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                loginButton.setBackground(new Color(0, 180, 0)); // Vert plus clair
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                loginButton.setBackground(new Color(0, 120, 0)); // Vert foncé
            }
        });

        // Message d'erreur
        messageLabel = new JLabel("", SwingConstants.CENTER);
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        messageLabel.setForeground(new Color(255, 80, 80)); // Rouge clair
        messageLabel.setBounds(75, 430, 300, 20);
        formPanel.add(messageLabel);

        // Bouton de fermeture
        JButton closeButton = new JButton("X");
        closeButton.setBounds(400, 10, 40, 40);
        closeButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        closeButton.setForeground(Color.WHITE);
        closeButton.setBackground(new Color(80, 80, 80)); // Gris foncé
        closeButton.setBorderPainted(false);
        closeButton.setFocusPainted(false);
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeButton.addActionListener(e -> System.exit(0));
        
        // Effet de survol pour le bouton fermer
        closeButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                closeButton.setBackground(new Color(120, 0, 0)); // Rouge foncé
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                closeButton.setBackground(new Color(80, 80, 80)); // Gris foncé
            }
        });
        formPanel.add(closeButton);

        formPanel.add(loginButton);
        rightPanel.add(formPanel);
        
        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(rightPanel, BorderLayout.CENTER);
        add(mainPanel);
    }

    private void handleLogin(ActionEvent e) {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Veuillez remplir tous les champs");
            return;
        }

        LoginController authController = new LoginController();
        boolean isAuthenticated = authController.authenticate(username, password);

        if (isAuthenticated) {
            boolean isAdmin = authController.isAdmin(username);
            
            if (isAdmin) {
                
                new GerantDashboard().setVisible(true);
                dispose();
            } else {
                int clientId = authController.getClientId(username);
                boolean firstLogin = authController.isFirstLogin(username);
                
                if (firstLogin) {
                    dispose();
                    SwingUtilities.invokeLater(() -> {
                        modifierpassword passwordChangeFrame = new modifierpassword(clientId);
                        passwordChangeFrame.setVisible(true);
                    });
                } else {
                    ClientController clientController = new ClientController();
                    Client client = clientController.getClientById(clientId);

                    ClientDashboard dashboard = new ClientDashboard(
                        client.getId(),
                        client.getNom(),
                        client.getPrenom(),
                        client.getLogin(),
                        client.getDateNaissance(),
                        client.getAdresse(),
                        client.getTelephone()
                    );
                    dashboard.setVisible(true);
                    this.dispose();
                }
            }
        } else {
            messageLabel.setText("Identifiants incorrects");
            passwordField.setText("");
            usernameField.requestFocus();
        }
    }

    private Icon createDefaultAvatar() {
        BufferedImage img = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        
        // Cercle de fond
        g2d.setColor(new Color(0, 120, 0)); // Vert foncé
        g2d.fillOval(0, 0, 100, 100);
        
        // Visage
        g2d.setColor(new Color(255, 218, 185));
        g2d.fillOval(20, 20, 60, 60);
        
        // Yeux
        g2d.setColor(Color.WHITE);
        g2d.fillOval(35, 45, 15, 15);
        g2d.fillOval(65, 45, 15, 15);
        
        // Pupilles
        g2d.setColor(Color.BLACK);
        g2d.fillOval(40, 50, 5, 5);
        g2d.fillOval(70, 50, 5, 5);
        
        // Bouche (sourire)
        g2d.setStroke(new BasicStroke(2));
        g2d.drawArc(40, 55, 30, 20, 0, -180);
        
        g2d.dispose();
        return new ImageIcon(img);
    }
}