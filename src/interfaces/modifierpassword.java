package interfaces;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class modifierpassword extends JFrame {
    private JPasswordField currentPasswordField;
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;
    private int clientId;  

    public modifierpassword(int clientId) {
        this.clientId = clientId;
        initUI();
    }

    private void initUI() {
        setTitle("Changement de mot de passe");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        mainPanel.setBackground(new Color(245, 248, 250));

        // Titre
        JLabel titleLabel = new JLabel("Changer votre mot de passe", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(new Color(50, 100, 150));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Panel de formulaire
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(new Color(245, 248, 250));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // Champ mot de passe actuel - Version non modifiable
        JPanel currentPassPanel = new JPanel(new BorderLayout(5, 5));
        currentPassPanel.setBackground(new Color(245, 248, 250));
        
        JLabel currentPassLabel = new JLabel("Mot de passe actuel :");
        currentPassLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        currentPassLabel.setForeground(new Color(80, 80, 80));
        
        // Utilisation d'un JLabel avec des astérisques à la place du JPasswordField
        JLabel hiddenPasswordLabel = new JLabel("************");
        hiddenPasswordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        hiddenPasswordLabel.setPreferredSize(new Dimension(250, 35));
        hiddenPasswordLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 215, 230)),
            BorderFactory.createEmptyBorder(5, 12, 5, 12)
        ));
        hiddenPasswordLabel.setBackground(Color.WHITE);
        hiddenPasswordLabel.setOpaque(true);
        
        currentPassPanel.add(currentPassLabel, BorderLayout.NORTH);
        currentPassPanel.add(hiddenPasswordLabel, BorderLayout.CENTER);
        
        formPanel.add(currentPassPanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Champ nouveau mot de passe
        JPanel newPassPanel = createFieldPanel("Nouveau mot de passe :", newPasswordField = new JPasswordField());
        formPanel.add(newPassPanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Champ confirmation
        JPanel confirmPassPanel = createFieldPanel("Confirmer mot de passe :", confirmPasswordField = new JPasswordField());
        formPanel.add(confirmPassPanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 25)));

        // Panel boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 0));
        buttonPanel.setBackground(new Color(245, 248, 250));

        JButton submitButton = createStyledButton("Valider", new Color(76, 175, 80));
        submitButton.addActionListener(this::handlePasswordChange);
        buttonPanel.add(submitButton);

        JButton cancelButton = createStyledButton("Annuler", new Color(244, 67, 54));
        cancelButton.addActionListener(e -> dispose());
        buttonPanel.add(cancelButton);

        formPanel.add(buttonPanel);
        mainPanel.add(formPanel, BorderLayout.CENTER);

        add(mainPanel);
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Effet de survol
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }

    private JPanel createFieldPanel(String labelText, JPasswordField passwordField) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(new Color(245, 248, 250));

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(new Color(80, 80, 80));

        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setPreferredSize(new Dimension(250, 35));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 215, 230)),
            BorderFactory.createEmptyBorder(5, 12, 5, 12)
        ));
        passwordField.setBackground(Color.WHITE);

        panel.add(label, BorderLayout.NORTH);
        panel.add(passwordField, BorderLayout.CENTER);

        return panel;
    }

    private void handlePasswordChange(ActionEvent e) {
        // On ne récupère plus le mot de passe actuel depuis le champ
        String newPassword = new String(newPasswordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        // Validation des champs
        if (newPassword.isEmpty()) {
            showError("Veuillez entrer un nouveau mot de passe");
            newPasswordField.requestFocus();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            showError("Les nouveaux mots de passe ne correspondent pas");
            newPasswordField.setText("");
            confirmPasswordField.setText("");
            newPasswordField.requestFocus();
            return;
        }

        if (newPassword.length() < 6) {
            showError("Le mot de passe doit contenir au moins 6 caractères");
            newPasswordField.setText("");
            confirmPasswordField.setText("");
            newPasswordField.requestFocus();
            return;
        }

        // Vérification avec le contrôleur
        Controller.ClientController clientController = new Controller.ClientController();
        
        // On demande directement le changement de mot de passe sans vérification de l'ancien
        // (ou vous pourriez demander une autre méthode de vérification)
        if (clientController.changePassword(clientId, newPassword)) {
            JOptionPane.showMessageDialog(this, 
                "Mot de passe changé avec succès", 
                "Succès", 
                JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            showError("Erreur lors du changement de mot de passe");
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, 
            message, 
            "Erreur", 
            JOptionPane.ERROR_MESSAGE);
    }
}