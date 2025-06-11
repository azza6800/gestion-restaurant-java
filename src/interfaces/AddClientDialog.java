package interfaces;

import entité.Client;
import Controller.ClientController;
import interfaces.DatePicker;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.Date;

public class AddClientDialog extends JDialog {
    private final GerantDashboard parent;
    private final ClientsPanel clientsPanel;
    
    // Composants du formulaire
    private JTextField emailField;
    private JPasswordField passwordField;
    private JTextField nomField;
    private JTextField prenomField;
    private DatePicker datePicker;  // Notre nouveau DatePicker
    private JTextField adresseField;
    private JTextField telephoneField;

    // URLs des images
    private static final String USER_ICON_URL = "https://cdn-icons-png.flaticon.com/512/3177/3177440.png";
    private static final String SAVE_ICON_URL = "https://cdn-icons-png.flaticon.com/512/3591/3591571.png";
    private static final String CANCEL_ICON_URL = "https://cdn-icons-png.flaticon.com/512/2732/2732657.png";

    public AddClientDialog(GerantDashboard parent, ClientsPanel clientsPanel) {
        super(parent, "Ajouter un nouveau client", true);
        this.parent = parent;
        this.clientsPanel = clientsPanel;
        initUI();
        setResizable(false);
    }

    private void initUI() {
        setSize(750, 650);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(245, 245, 245));
        
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createFormPanel(), BorderLayout.CENTER);
        add(createFooterPanel(), BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
            new EmptyBorder(15, 25, 15, 25)
        ));

        JLabel title = new JLabel("Ajout d'un nouveau client");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(new Color(60, 60, 60));

        ImageIcon userIcon = loadImageIcon(USER_ICON_URL, 40, 40);
        JLabel icon = new JLabel(userIcon);
        icon.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

        header.add(title, BorderLayout.WEST);
        header.add(icon, BorderLayout.EAST);
        return header;
    }

    private ImageIcon loadImageIcon(String imageUrl, int width, int height) {
        try {
            URL url = new URL(imageUrl);
            Image image = new ImageIcon(url).getImage();
            Image scaledImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
        } catch (Exception e) {
            e.printStackTrace();
            return new ImageIcon();
        }
    }

    private JPanel createFormPanel() {
        JPanel form = new JPanel();
        form.setBackground(Color.WHITE);
        form.setBorder(new EmptyBorder(20, 30, 20, 30));
        form.setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Email
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        addLabel("Email*", form, gbc);
        gbc.gridx++;
        gbc.weightx = 0.7;
        emailField = addTextField(20, form, gbc);
        
        // Mot de passe
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.weightx = 0.3;
        addLabel("Mot de passe*", form, gbc);
        gbc.gridx++;
        gbc.weightx = 0.7;
        passwordField = addPasswordField(20, form, gbc);
        
        // Nom
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.weightx = 0.3;
        addLabel("Nom*", form, gbc);
        gbc.gridx++;
        gbc.weightx = 0.7;
        nomField = addTextField(20, form, gbc);
        
        // Prénom
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.weightx = 0.3;
        addLabel("Prénom*", form, gbc);
        gbc.gridx++;
        gbc.weightx = 0.7;
        prenomField = addTextField(20, form, gbc);
        
        // Date de naissance (avec notre DatePicker personnalisé)
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.weightx = 0.3;
        addLabel("Date de naissance", form, gbc);
        gbc.gridx++;
        gbc.weightx = 0.7;
        datePicker = new DatePicker(parent.primaryColor);
        form.add(datePicker, gbc);
        
        // Adresse
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.weightx = 0.3;
        addLabel("Adresse", form, gbc);
        gbc.gridx++;
        gbc.weightx = 0.7;
        adresseField = addTextField(20, form, gbc);
        
        // Téléphone
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.weightx = 0.3;
        addLabel("Téléphone*", form, gbc);
        gbc.gridx++;
        gbc.weightx = 0.7;
        telephoneField = addTextField(20, form, gbc);
        
        // Note
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(20, 10, 0, 10);
        JLabel note = new JLabel("* Champs obligatoires");
        note.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        note.setForeground(new Color(120, 120, 120));
        form.add(note, gbc);

        return form;
    }

    private void addLabel(String text, JPanel panel, GridBagConstraints gbc) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panel.add(label, gbc);
    }

    private JTextField addTextField(int columns, JPanel panel, GridBagConstraints gbc) {
        JTextField field = new JTextField(columns);
        styleTextField(field);
        panel.add(field, gbc);
        return field;
    }

    private JPasswordField addPasswordField(int columns, JPanel panel, GridBagConstraints gbc) {
        JPasswordField passwordField = new JPasswordField(columns);
        styleTextField(passwordField);
        panel.add(passwordField, gbc);
        
        gbc.gridy++;
        gbc.gridx = 1;
        gbc.insets = new Insets(0, 10, 10, 10);
        JLabel hint = new JLabel("Minimum 6 caractères");
        hint.setFont(new Font("Segoe UI", Font.ITALIC, 10));
        hint.setForeground(new Color(150, 150, 150));
        panel.add(hint, gbc);
        
        gbc.gridy--;
        gbc.insets = new Insets(10, 10, 10, 10);
        
        return passwordField;
    }

    private JPanel createFooterPanel() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        footer.setBackground(new Color(245, 245, 245));
        footer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)),
            new EmptyBorder(10, 0, 10, 20)
        ));

        ImageIcon cancelIcon = loadImageIcon(CANCEL_ICON_URL, 16, 16);
        JButton cancelBtn = createButton("Annuler", parent.dangerColor, false, e -> dispose());
        cancelBtn.setIcon(cancelIcon);
        
        ImageIcon saveIcon = loadImageIcon(SAVE_ICON_URL, 16, 16);
        JButton saveBtn = createButton("Enregistrer", parent.successColor, true, e -> processForm());
        saveBtn.setIcon(saveIcon);

        footer.add(cancelBtn);
        footer.add(saveBtn);

        return footer;
    }

   
    private void styleTextField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        field.setBackground(new Color(252, 252, 252));
    }

    private void processForm() {
        if (validateForm()) {
            createClient();
        }
    }

    private boolean validateForm() {
        if (emailField.getText().trim().isEmpty() ||
            passwordField.getPassword().length == 0 ||
            nomField.getText().trim().isEmpty() ||
            prenomField.getText().trim().isEmpty() ||
            telephoneField.getText().trim().isEmpty()) {
            
            showError("Veuillez remplir tous les champs obligatoires");
            return false;
        }
        
        if (!emailField.getText().matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            showError("Veuillez entrer une adresse email valide");
            emailField.requestFocus();
            return false;
        }
        
        if (passwordField.getPassword().length < 6) {
            showError("Le mot de passe doit contenir au moins 6 caractères");
            passwordField.requestFocus();
            return false;
        }
        
        if (!telephoneField.getText().matches("^\\d{8,15}$")) {
            showError("Numéro de téléphone invalide (8-15 chiffres)");
            telephoneField.requestFocus();
            return false;
        }
        
        return true;
    }
    private JButton createButton(String text, Color color, boolean filled, ActionListener action) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Fond du bouton
                if (filled) {
                    if (getModel().isPressed()) {
                        g2.setColor(color.darker());
                    } else if (getModel().isRollover()) {
                        g2.setColor(color.brighter());
                    } else {
                        g2.setColor(color);
                    }
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                } else {
                    if (getModel().isPressed()) {
                        g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 20));
                        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    } else if (getModel().isRollover()) {
                        g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 10));
                        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    }
                }
                g2.dispose();
                
                super.paintComponent(g);
            }
            
            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
                g2.dispose();
            }
        };
        
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setForeground(filled ? Color.WHITE : color);
        btn.setContentAreaFilled(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 24, 10, 24));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(action);
        
        // Style spécifique pour le bouton rempli
        if (filled) {
            btn.setBorder(BorderFactory.createEmptyBorder(10, 24, 10, 24));
        }
        
        return btn;
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, 
            message, 
            "Erreur de validation", 
            JOptionPane.ERROR_MESSAGE);
    }

    private void createClient() {
        Client newClient = new Client(
            0,
            emailField.getText().trim(),
            new String(passwordField.getPassword()),
            nomField.getText().trim(),
            prenomField.getText().trim(),
            datePicker.getDate(),  // Récupération de la date depuis notre DatePicker
            adresseField.getText().trim(),
            telephoneField.getText().trim(),
            true
        );

        ClientController controller = new ClientController();
        if (controller.addClient(newClient)) {
            JOptionPane.showMessageDialog(this,
                "Client ajouté avec succès!",
                "Succès",
                JOptionPane.INFORMATION_MESSAGE);
            dispose();
            clientsPanel.refreshTable();
        } else {
            showError("Erreur lors de l'ajout du client. L'email existe peut-être déjà.");
        }
    }
}