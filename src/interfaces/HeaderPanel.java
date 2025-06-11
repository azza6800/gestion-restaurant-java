package interfaces;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.imageio.ImageIO;
import java.net.URL;

public class HeaderPanel extends JPanel {
    private final Color darkColor = new Color(45, 45, 55);
    private final Color lightColor = new Color(250, 250, 252);
    private final Color accentColor = new Color(100, 220, 180);
    
    private final String logoUrl = "https://cdn-icons-png.flaticon.com/512/732/732247.png";
    private final String avatarUrl = "https://cdn-icons-png.flaticon.com/512/3135/3135715.png";

    public HeaderPanel(GerantDashboard parent) {
        setLayout(new BorderLayout());
        setBackground(darkColor);
        setPreferredSize(new Dimension(parent.getWidth(), 70));
        setBorder(new EmptyBorder(0, 20, 0, 20));

        // Partie gauche avec logo et titre
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        leftPanel.setOpaque(false);

        try {
            Image logoImage = ImageIO.read(new URL(logoUrl)).getScaledInstance(40, 40, Image.SCALE_SMOOTH);
            JLabel logoIcon = new JLabel(new ImageIcon(logoImage));
            leftPanel.add(logoIcon);
        } catch (Exception e) {
            leftPanel.add(new JLabel("🍴"));
        }

        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setOpaque(false);

        JLabel title = new JLabel("GourmetGo - Gérant");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(lightColor);

        JLabel slogan = new JLabel("Gestion complète de votre restaurant");
        slogan.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        slogan.setForeground(new Color(180, 180, 180));

        textPanel.add(title, BorderLayout.NORTH);
        textPanel.add(slogan, BorderLayout.SOUTH);
        leftPanel.add(textPanel);

        add(leftPanel, BorderLayout.WEST);

        // Partie droite avec contrôles
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);

        // Bouton de déconnexion (conservé comme dans l'original)
        JButton logoutButton = new JButton("Déconnexion");
        logoutButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.WHITE),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        logoutButton.setContentAreaFilled(false);
        logoutButton.setFocusPainted(false);
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutButton.addActionListener(e -> {
            parent.dispose();
            new LoginFrame1().setVisible(true);
        });

        // Bouton profil (style ClientDashboard)
        JButton profileBtn = new JButton();
        profileBtn.setContentAreaFilled(false);
        profileBtn.setBorder(new EmptyBorder(5, 15, 5, 5));
        profileBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        try {
            Image avatarImage = ImageIO.read(new URL(avatarUrl)).getScaledInstance(30, 30, Image.SCALE_SMOOTH);
            profileBtn.setIcon(new ImageIcon(avatarImage));
        } catch (Exception e) {
            profileBtn.setText("👤");
        }

        // Boutons de contrôle (style ClientDashboard)
        JButton minBtn = new ControlButton("−");
        JButton closeBtn = new ControlButton("×");

        minBtn.addActionListener(e -> parent.setState(Frame.ICONIFIED));
        closeBtn.addActionListener(e -> System.exit(0));

        rightPanel.add(logoutButton);
        rightPanel.add(profileBtn);
        rightPanel.add(minBtn);
        rightPanel.add(closeBtn);

        add(rightPanel, BorderLayout.EAST);
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

                public void mouseExited(MouseEvent e) {
                    setBackground(new Color(0, 0, 0, 0));
                }
            });
        }
    }
}