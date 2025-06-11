package interfaces;

import entité.Produit;
import Controller.ProduitController;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.net.URL;

public class EditProductDialog extends JDialog {
    private final ProductCard parent;
    private final Produit produit;
    
    // Composants du formulaire
    private JTextField nomField;
    private JComboBox<String> categorieCombo;
    private JTextArea descriptionArea;
    private JTextField prixField;
    private JLabel imageLabel;
    private byte[] newImage;
    
    // URLs des images
    private static final String PRODUCT_ICON_URL = "https://cdn-icons-png.flaticon.com/512/3144/3144456.png";
    private static final String SAVE_ICON_URL = "https://cdn-icons-png.flaticon.com/512/3591/3591571.png";
    private static final String CANCEL_ICON_URL = "https://cdn-icons-png.flaticon.com/512/2732/2732657.png";
    private static final String IMAGE_UPLOAD_ICON_URL = "https://cdn-icons-png.flaticon.com/512/2950/2950052.png";

    public EditProductDialog(ProductCard parent, Produit produit) {
        super((JFrame)null, "Modifier Produit", true);
        this.parent = parent;
        this.produit = produit;
        initUI();
        setResizable(false);
    }

    private void initUI() {
        setSize(600, 500);
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

        JLabel title = new JLabel("Modification du produit #" + produit.getId());
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(new Color(60, 60, 60));

        ImageIcon productIcon = loadImageIcon(PRODUCT_ICON_URL, 40, 40);
        JLabel icon = new JLabel(productIcon);
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
        
        // Nom
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        addLabel("Nom*", form, gbc);
        gbc.gridx++;
        gbc.weightx = 0.7;
        nomField = addTextField(produit.getNom(), 20, form, gbc);
        
     // Catégorie
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.weightx = 0.3;
        addLabel("Catégorie*", form, gbc);
        gbc.gridx++;
        gbc.weightx = 0.7;
        String[] categories = {"Plat", "Sandwich", "Salade", "Dessert", "Viennoiserie", "Boisson"};
        categorieCombo = new JComboBox<>(categories);
        categorieCombo.setSelectedItem(null); // Aucune sélection par défaut
        styleComboBox(categorieCombo);
        form.add(categorieCombo, gbc);
        
        // Prix
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.weightx = 0.3;
        addLabel("Prix*", form, gbc);
        gbc.gridx++;
        gbc.weightx = 0.7;
        prixField = addTextField(String.format("%.2f", produit.getPrix()), 20, form, gbc);
        
        // Description
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.weightx = 0.3;
        addLabel("Description", form, gbc);
        gbc.gridx++;
        gbc.weightx = 0.7;
        descriptionArea = new JTextArea(produit.getDescription(), 4, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        styleTextArea(scrollPane);
        form.add(scrollPane, gbc);
        
        // Image
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.weightx = 0.3;
        addLabel("Image", form, gbc);
        gbc.gridx++;
        gbc.weightx = 0.7;
        imageLabel = new JLabel();
        imageLabel.setPreferredSize(new Dimension(150, 150));
        updateImageDisplay();
        
        JButton uploadBtn = new JButton("Changer l'image");
        uploadBtn.addActionListener(e -> uploadImage());
        uploadBtn.setIcon(loadImageIcon(IMAGE_UPLOAD_ICON_URL, 16, 16));
        styleButton(uploadBtn);
        
        JPanel imagePanel = new JPanel(new BorderLayout(10, 10));
        imagePanel.add(imageLabel, BorderLayout.CENTER);
        imagePanel.add(uploadBtn, BorderLayout.SOUTH);
        form.add(imagePanel, gbc);

        return form;
    }

    private void updateImageDisplay() {
        if (produit.getImage() != null && produit.getImage().length > 0) {
            ImageIcon icon = new ImageIcon(produit.getImage());
            Image img = icon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
            imageLabel.setIcon(new ImageIcon(img));
        } else {
            imageLabel.setIcon(new ImageIcon());
            imageLabel.setText("Aucune image");
            imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        }
    }

    private void uploadImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Sélectionner une image");
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.addChoosableFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(java.io.File f) {
                String name = f.getName().toLowerCase();
                return name.endsWith(".jpg") || name.endsWith(".jpeg") || 
                       name.endsWith(".png") || f.isDirectory();
            }
            public String getDescription() {
                return "Images (*.jpg, *.jpeg, *.png)";
            }
        });
        
        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            try {
                java.io.File file = fileChooser.getSelectedFile();
                ImageIcon icon = new ImageIcon(file.getAbsolutePath());
                Image img = icon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                imageLabel.setIcon(new ImageIcon(img));
                
                // Convertir en byte array pour sauvegarde
                java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
                javax.imageio.ImageIO.write(
                    javax.imageio.ImageIO.read(file), 
                    file.getName().substring(file.getName().lastIndexOf('.') + 1), 
                    baos
                );
                newImage = baos.toByteArray();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "Erreur lors du chargement de l'image", 
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void addLabel(String text, JPanel panel, GridBagConstraints gbc) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panel.add(label, gbc);
    }

    private JTextField addTextField(String text, int columns, JPanel panel, GridBagConstraints gbc) {
        JTextField field = new JTextField(text, columns);
        styleTextField(field);
        panel.add(field, gbc);
        return field;
    }

    private JPanel createFooterPanel() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        footer.setBackground(new Color(245, 245, 245));
        footer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)),
            new EmptyBorder(10, 0, 10, 20)
        ));

        ImageIcon cancelIcon = loadImageIcon(CANCEL_ICON_URL, 16, 16);
        JButton cancelBtn = createButton("Annuler", Color.RED, false, e -> dispose());
        cancelBtn.setIcon(cancelIcon);
        
        ImageIcon saveIcon = loadImageIcon(SAVE_ICON_URL, 16, 16);
        JButton saveBtn = createButton("Enregistrer", new Color(0, 150, 0), true, e -> updateProduct());
        saveBtn.setIcon(saveIcon);

        footer.add(cancelBtn);
        footer.add(saveBtn);

        return footer;
    }

    private JButton createButton(String text, Color color, boolean filled, ActionListener action) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(action);
        
        if (filled) {
            btn.setBackground(color);
            btn.setForeground(Color.WHITE);
            btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color.darker()),
                BorderFactory.createEmptyBorder(8, 20, 8, 20)
            ));
        } else {
            btn.setBackground(Color.WHITE);
            btn.setForeground(color);
            btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color),
                BorderFactory.createEmptyBorder(8, 20, 8, 20)
            ));
        }
        
        return btn;
    }

    private void styleTextField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        field.setBackground(new Color(252, 252, 252));
    }

    private void styleComboBox(JComboBox<String> combo) {
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        combo.setBackground(new Color(252, 252, 252));
        combo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
    }

    private void styleTextArea(JScrollPane scrollPane) {
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));
        scrollPane.setBackground(new Color(252, 252, 252));
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
    }

    private void updateProduct() {
        // Validation des champs obligatoires
        if (nomField.getText().trim().isEmpty()) {
            showError("Le nom du produit est obligatoire");
            nomField.requestFocus();
            return;
        }
        
        try {
            double prix = Double.parseDouble(prixField.getText().trim());
            if (prix <= 0) {
                showError("Le prix doit être supérieur à zéro");
                prixField.requestFocus();
                return;
            }
            
            // Mise à jour du produit
            produit.setNom(nomField.getText().trim());
            produit.setCategorie((String)categorieCombo.getSelectedItem());
            produit.setDescription(descriptionArea.getText().trim());
            produit.setPrix(prix);
            
            if (newImage != null) {
                produit.setImage(newImage);
            }
            
            ProduitController controller = new ProduitController();
            if (controller.modifierProduit(produit)) {
                JOptionPane.showMessageDialog(this,
                    "Produit mis à jour avec succès!",
                    "Succès",
                    JOptionPane.INFORMATION_MESSAGE);
                dispose();
                parent.cardManager.refreshProductCards(controller.getTousProduits());
            } else {
                showError("Erreur lors de la mise à jour du produit");
            }
        } catch (NumberFormatException e) {
            showError("Veuillez entrer un prix valide");
            prixField.requestFocus();
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, 
            message, 
            "Erreur de validation", 
            JOptionPane.ERROR_MESSAGE);
    }
}