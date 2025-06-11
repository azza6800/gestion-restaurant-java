package interfaces;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Gerer_profil extends JPanel implements ClientDashboard.RefreshablePanel {
    private final ClientDashboard dashboard;
    private JTextField nomField, prenomField, loginField, adresseField, telephoneField;
    private JPasswordField passwordField, confirmPasswordField;
    private DatePicker datePicker;
    private JLabel photoLabel;

    private final Color DEFAULT_PRIMARY_COLOR = new Color(70, 130, 180);
    private final Color DEFAULT_LIGHT_COLOR = new Color(240, 240, 240);
    private final Color DEFAULT_DARK_COLOR = new Color(60, 60, 60);
    private final Color DEFAULT_SUCCESS_COLOR = new Color(70, 160, 70);
    private final Color DEFAULT_ERROR_COLOR = new Color(220, 80, 80);

    public Gerer_profil(ClientDashboard dashboard) {
        this.dashboard = dashboard;
        initializeComponents();
        setupUI();
        refreshData();
    }

    private void initializeComponents() {
        nomField = createModernTextField();
        prenomField = createModernTextField();
        loginField = createModernTextField();
        adresseField = createModernTextField();
        telephoneField = createModernTextField();
        passwordField = createModernPasswordField();
        confirmPasswordField = createModernPasswordField();
        
        Color primaryColor = dashboard.primaryColor != null ? dashboard.primaryColor : DEFAULT_PRIMARY_COLOR;
        datePicker = new DatePicker(primaryColor);
        
        photoLabel = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setClip(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 100, 100));
                super.paintComponent(g2);
            }
        };
        photoLabel.setPreferredSize(new Dimension(120, 120));
        photoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        photoLabel.setVerticalAlignment(SwingConstants.CENTER);
        photoLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(primaryColor, 3),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
    }

    private JTextField createModernTextField() {
        JTextField field = new JTextField(20);
        Color primaryColor = dashboard.primaryColor != null ? dashboard.primaryColor : DEFAULT_PRIMARY_COLOR;
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, primaryColor),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return field;
    }

    private JPasswordField createModernPasswordField() {
        JPasswordField field = new JPasswordField(20);
        Color primaryColor = dashboard.primaryColor != null ? dashboard.primaryColor : DEFAULT_PRIMARY_COLOR;
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, primaryColor),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return field;
    }

    private void setupUI() {
        setLayout(new BorderLayout());
        Color lightColor = dashboard.lightColor != null ? dashboard.lightColor : DEFAULT_LIGHT_COLOR;
        setBackground(lightColor);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        JPanel photoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        photoPanel.setOpaque(false);
        photoPanel.add(photoLabel);
        
        JPanel photoContainer = new JPanel(new BorderLayout());
        photoContainer.setOpaque(false);
        photoContainer.add(photoPanel, BorderLayout.CENTER);
        
        headerPanel.add(photoContainer, BorderLayout.WEST);
        
        JLabel titleLabel = new JLabel("Mon Profil");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        Color darkColor = dashboard.darkColor != null ? dashboard.darkColor : DEFAULT_DARK_COLOR;
        titleLabel.setForeground(darkColor);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        add(headerPanel, BorderLayout.NORTH);
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        addFormField(formPanel, gbc, "Nom:", nomField);
        addFormField(formPanel, gbc, "Prénom:", prenomField);
        addFormField(formPanel, gbc, "Login:", loginField);
        addFormField(formPanel, gbc, "Date de naissance:", datePicker);
        addFormField(formPanel, gbc, "Adresse:", adresseField);
        addFormField(formPanel, gbc, "Téléphone:", telephoneField);
        addFormField(formPanel, gbc, "Nouveau mot de passe:", passwordField);
        addFormField(formPanel, gbc, "Confirmer mot de passe:", confirmPasswordField);
        
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        add(scrollPane, BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, String label, JComponent field) {
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.3;
        
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        Color darkColor = dashboard.darkColor != null ? dashboard.darkColor : DEFAULT_DARK_COLOR;
        lbl.setForeground(darkColor);
        panel.add(lbl, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        panel.add(field, gbc);
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        JButton cancelBtn = new JButton("Annuler");
        styleButton(cancelBtn, DEFAULT_ERROR_COLOR);
        cancelBtn.addActionListener(e -> refreshData());
        
        JButton saveBtn = new JButton("Enregistrer");
        Color successColor = dashboard.successColor != null ? dashboard.successColor : DEFAULT_SUCCESS_COLOR;
        styleButton(saveBtn, successColor);
        saveBtn.addActionListener(e -> saveProfileChanges());
        
        panel.add(cancelBtn);
        panel.add(saveBtn);
        
        return panel;
    }

    private void styleButton(JButton button, Color color) {
        if (color == null) {
            color = DEFAULT_PRIMARY_COLOR;
        }
        
        final Color finalColor = color;
        
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(finalColor);
        button.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(finalColor.brighter());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(finalColor);
            }
        });
    }

    private void saveProfileChanges() {
        try {
            if (nomField.getText().trim().isEmpty() || 
                prenomField.getText().trim().isEmpty() || 
                loginField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Veuillez remplir tous les champs obligatoires (Nom, Prénom, Login)", 
                    "Erreur", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String newPassword = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());
            
            if (!newPassword.isEmpty() && !newPassword.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this, 
                    "Les mots de passe ne correspondent pas", 
                    "Erreur", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            Date selectedDate = datePicker.getDate();
            
            if (newPassword.isEmpty()) {
                dashboard.updateProfileInfo(
                    nomField.getText().trim(),
                    prenomField.getText().trim(),
                    loginField.getText().trim(),
                    selectedDate,
                    adresseField.getText().trim(),
                    telephoneField.getText().trim()
                );
            } else {
                dashboard.updateProfileInfoWithPassword(
                    nomField.getText().trim(),
                    prenomField.getText().trim(),
                    loginField.getText().trim(),
                    selectedDate,
                    adresseField.getText().trim(),
                    telephoneField.getText().trim(),
                    newPassword
                );
            }
            
            JOptionPane.showMessageDialog(this, "Profil mis à jour avec succès!", 
                "Succès", JOptionPane.INFORMATION_MESSAGE);
            
            passwordField.setText("");
            confirmPasswordField.setText("");
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Erreur lors de la mise à jour du profil: " + ex.getMessage(), 
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void refreshData() {
        if (datePicker != null && dashboard.dateNaissance != null) {
            datePicker.setDate(dashboard.dateNaissance);
        }
        
        nomField.setText(dashboard.nom != null ? dashboard.nom : "");
        prenomField.setText(dashboard.prenom != null ? dashboard.prenom : "");
        loginField.setText(dashboard.login != null ? dashboard.login : "");
        adresseField.setText(dashboard.adresse != null ? dashboard.adresse : "");
        telephoneField.setText(dashboard.telephone != null ? dashboard.telephone : "");
        passwordField.setText("");
        confirmPasswordField.setText("");
        
        photoLabel.setIcon(new ImageIcon(createDefaultProfileImage()));
    }
    
    private BufferedImage createDefaultProfileImage() {
        BufferedImage img = new BufferedImage(120, 120, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        
        Color primaryColor = dashboard.primaryColor != null ? dashboard.primaryColor : DEFAULT_PRIMARY_COLOR;
        g2d.setColor(primaryColor);
        g2d.fillRoundRect(0, 0, 120, 120, 100, 100);
        
        String initials = "";
        if (dashboard.prenom != null && !dashboard.prenom.isEmpty()) {
            initials += dashboard.prenom.substring(0, 1).toUpperCase();
        }
        if (dashboard.nom != null && !dashboard.nom.isEmpty()) {
            initials += dashboard.nom.substring(0, 1).toUpperCase();
        }
        
        if (!initials.isEmpty()) {
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Segoe UI", Font.BOLD, 48));
            FontMetrics fm = g2d.getFontMetrics();
            int x = (120 - fm.stringWidth(initials)) / 2;
            int y = ((120 - fm.getHeight()) / 2) + fm.getAscent();
            g2d.drawString(initials, x, y);
        }
        
        g2d.dispose();
        return img;
    }
}