package interfaces;

import entité.Produit;
import Controller.ProduitController;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.nio.file.Files;

public class ProductFormManager {
    private final ProduitController controller;
    private final ProductCardManager cardManager;
    private JPanel formPanel;
    private JTextField nomField, prixField;
    private JTextArea descArea;
    private JComboBox<String> categorieCombo;
    private JLabel imageLabel;
    private byte[] imageBytes;
    private JTabbedPane tabbedPane;

    // Couleurs modernes
    private final Color PRIMARY_COLOR = new Color(59, 89, 152);
    private final Color SECONDARY_COLOR = new Color(246, 247, 249);
    private final Color ACCENT_COLOR = new Color(66, 133, 244);
    private final Color ERROR_COLOR = new Color(219, 68, 55);
    private final Color SUCCESS_COLOR = new Color(15, 157, 88);
    private final Color BORDER_COLOR = new Color(210, 210, 210);

    public ProductFormManager(ProduitController controller, ProductCardManager cardManager) {
        this.controller = controller;
        this.cardManager = cardManager;
    }

    public JPanel createFormPanel(JTabbedPane tabbedPane) {
        this.tabbedPane = tabbedPane;
        formPanel = new JPanel(new BorderLayout(10, 10));
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(SECONDARY_COLOR);

        // Header
        JPanel header = createHeaderPanel();
        formPanel.add(header, BorderLayout.NORTH);

        // Form content
        JPanel content = createFormContent();
        formPanel.add(content, BorderLayout.CENTER);

        // Footer with buttons
        JPanel footer = createFooterPanel();
        formPanel.add(footer, BorderLayout.SOUTH);

        return formPanel;
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(new CompoundBorder(
            new MatteBorder(0, 0, 1, 0, BORDER_COLOR),
            new EmptyBorder(15, 20, 15, 20)
        ));

        JLabel title = new JLabel("Ajouter un nouveau produit");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(PRIMARY_COLOR);

        header.add(title, BorderLayout.WEST);

        return header;
    }

    private JPanel createFormContent() {
        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(SECONDARY_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Première colonne (champs de formulaire)
        JPanel formFieldsPanel = new JPanel(new GridBagLayout());
        formFieldsPanel.setBackground(SECONDARY_COLOR);
        formFieldsPanel.setBorder(new EmptyBorder(0, 0, 0, 20));

        // Nom
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        addFormLabel(formFieldsPanel, "Nom du produit*", gbc);

        gbc.gridy++;
        nomField = new JTextField(20);
        styleTextField(nomField);
        formFieldsPanel.add(nomField, gbc);

        // Catégorie
        gbc.gridy++;
        addFormLabel(formFieldsPanel, "Catégorie*", gbc);

        gbc.gridy++;
        String[] categories = {"Plat", "Sandwich", "Salade", "Dessert", "Viennoiserie", "Boisson"};
        categorieCombo = new JComboBox<>(categories);
        styleComboBox(categorieCombo);
        formFieldsPanel.add(categorieCombo, gbc);

        // Prix
        gbc.gridy++;
        addFormLabel(formFieldsPanel, "Prix (€)*", gbc);

        gbc.gridy++;
        prixField = new JTextField(20);
        styleTextField(prixField);
        formFieldsPanel.add(prixField, gbc);

        // Description
        gbc.gridy++;
        addFormLabel(formFieldsPanel, "Description", gbc);

        gbc.gridy++;
        descArea = new JTextArea(4, 20);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(descArea);
        styleTextArea(scrollPane);
        formFieldsPanel.add(scrollPane, gbc);

        // Deuxième colonne (image)
        JPanel imageColumnPanel = new JPanel(new BorderLayout());
        imageColumnPanel.setBackground(SECONDARY_COLOR);
        
        JPanel imagePanel = createImagePanel();
        imageColumnPanel.add(imagePanel, BorderLayout.NORTH);
        
        // Ajout des deux colonnes au content
        JPanel columnsPanel = new JPanel(new GridLayout(1, 2, 30, 0));
        columnsPanel.setBackground(SECONDARY_COLOR);
        columnsPanel.add(formFieldsPanel);
        columnsPanel.add(imageColumnPanel);
        
        content.add(columnsPanel);

        return content;
    }

    private void addFormLabel(JPanel panel, String text, GridBagConstraints gbc) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(new Color(80, 80, 80));
        panel.add(label, gbc);
    }

    private JPanel createImagePanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(SECONDARY_COLOR);
        panel.setBorder(new EmptyBorder(0, 0, 0, 0));

        // Conteneur pour l'image avec ombre
        JPanel imageContainer = new JPanel(new BorderLayout());
        imageContainer.setBackground(Color.WHITE);
        imageContainer.setBorder(new CompoundBorder(
            new LineBorder(BORDER_COLOR, 1),
            new EmptyBorder(10, 10, 10, 10)
        ));

        imageLabel = new JLabel("Aucune image sélectionnée", SwingConstants.CENTER);
        imageLabel.setPreferredSize(new Dimension(280, 280));
        imageLabel.setOpaque(true);
        imageLabel.setBackground(new Color(245, 245, 245));
        imageLabel.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        imageLabel.setForeground(new Color(150, 150, 150));
        imageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setVerticalAlignment(SwingConstants.CENTER);
        
        imageContainer.add(imageLabel, BorderLayout.CENTER);

        JButton uploadBtn = new JButton("Choisir une image");
        styleUploadButton(uploadBtn);
        uploadBtn.addActionListener(this::handleImageUpload);
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        btnPanel.setBackground(SECONDARY_COLOR);
        btnPanel.add(uploadBtn);

        panel.add(imageContainer, BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createFooterPanel() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        footer.setBackground(SECONDARY_COLOR);
        footer.setBorder(new EmptyBorder(10, 0, 0, 0));

        JButton cancelBtn = new JButton("Annuler");
        styleCancelButton(cancelBtn);
        cancelBtn.addActionListener(e -> {
            resetForm();
            if (tabbedPane != null) {
                tabbedPane.setSelectedIndex(0);
            }
        });

        JButton saveBtn = new JButton("Enregistrer le produit");
        styleSaveButton(saveBtn);
        saveBtn.addActionListener(this::saveProduct);

        footer.add(cancelBtn);
        footer.add(saveBtn);

        return footer;
    }

    private void handleImageUpload(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Sélectionner une image");
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.addChoosableFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(File f) {
                String name = f.getName().toLowerCase();
                return name.endsWith(".jpg") || name.endsWith(".jpeg") || 
                       name.endsWith(".png") || f.isDirectory();
            }
            public String getDescription() {
                return "Images (JPG, PNG)";
            }
        });

        if (fileChooser.showOpenDialog(formPanel) == JFileChooser.APPROVE_OPTION) {
            try {
                File file = fileChooser.getSelectedFile();
                imageBytes = Files.readAllBytes(file.toPath());
                
                ImageIcon icon = new ImageIcon(imageBytes);
                Image img = icon.getImage();
                
                int labelWidth = imageLabel.getWidth();
                int labelHeight = imageLabel.getHeight();
                int imgWidth = icon.getIconWidth();
                int imgHeight = icon.getIconHeight();
                
                double widthRatio = (double)labelWidth / imgWidth;
                double heightRatio = (double)labelHeight / imgHeight;
                double ratio = Math.min(widthRatio, heightRatio);
                
                int scaledWidth = (int)(imgWidth * ratio);
                int scaledHeight = (int)(imgHeight * ratio);
                
                Image scaledImg = img.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
                imageLabel.setIcon(new ImageIcon(scaledImg));
                imageLabel.setText("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(formPanel, 
                    "Erreur lors du chargement de l'image", 
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    private void styleUploadButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(new CompoundBorder(
            new RoundBorder(20, ACCENT_COLOR),
            new EmptyBorder(12, 24, 12, 24)
        ));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        button.setContentAreaFilled(false);
        button.setOpaque(true);
        
        button.setUI(new BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                AbstractButton b = (AbstractButton) c;
                ButtonModel model = b.getModel();
                
                // Dessiner le fond avec dégradé
                GradientPaint gp;
                if (model.isPressed()) {
                    gp = new GradientPaint(0, 0, ACCENT_COLOR.darker(), 0, c.getHeight(), ACCENT_COLOR.darker().darker());
                } else if (model.isRollover()) {
                    gp = new GradientPaint(0, 0, ACCENT_COLOR.brighter(), 0, c.getHeight(), ACCENT_COLOR.darker());
                } else {
                    gp = new GradientPaint(0, 0, ACCENT_COLOR, 0, c.getHeight(), ACCENT_COLOR.darker());
                }
                
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 20, 20);
                
                // Dessiner l'ombre
                if (c.isEnabled()) {
                    g2.setColor(new Color(ACCENT_COLOR.getRed(), ACCENT_COLOR.getGreen(), ACCENT_COLOR.getBlue(), 50));
                    g2.fillRoundRect(1, 3, c.getWidth()-2, c.getHeight()-2, 20, 20);
                }
                
                super.paint(g, c);
                g2.dispose();
            }
        });
    }

    private void styleCancelButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        button.setForeground(new Color(100, 100, 100));
        button.setBorder(new CompoundBorder(
            new RoundBorder(20, BORDER_COLOR),
            new EmptyBorder(12, 24, 12, 24)
        ));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setContentAreaFilled(false);
        button.setOpaque(true);
        
        button.setUI(new BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                AbstractButton b = (AbstractButton) c;
                ButtonModel model = b.getModel();
                
                // Dessiner le fond
                Color bgColor;
                if (model.isPressed()) {
                    bgColor = new Color(240, 240, 240);
                } else if (model.isRollover()) {
                    bgColor = new Color(248, 248, 248);
                } else {
                    bgColor = Color.WHITE;
                }
                
                g2.setColor(bgColor);
                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 20, 20);
                
                // Dessiner la bordure
                Color borderColor;
                if (model.isPressed()) {
                    borderColor = BORDER_COLOR.darker().darker();
                } else if (model.isRollover()) {
                    borderColor = BORDER_COLOR.darker();
                } else {
                    borderColor = BORDER_COLOR;
                }
                
                g2.setColor(borderColor);
                g2.drawRoundRect(0, 0, c.getWidth()-1, c.getHeight()-1, 20, 20);
                
                super.paint(g, c);
                g2.dispose();
            }
        });
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setForeground(new Color(70, 70, 70));
                button.repaint();
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setForeground(new Color(100, 100, 100));
                button.repaint();
            }
        });
    }

    private void styleSaveButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(new CompoundBorder(
            new RoundBorder(20, SUCCESS_COLOR),
            new EmptyBorder(12, 24, 12, 24)
        ));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setContentAreaFilled(false);
        button.setOpaque(true);
        
        button.setUI(new BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                AbstractButton b = (AbstractButton) c;
                ButtonModel model = b.getModel();
                
                // Dessiner le fond avec dégradé
                GradientPaint gp;
                if (model.isPressed()) {
                    gp = new GradientPaint(0, 0, SUCCESS_COLOR.darker(), 0, c.getHeight(), SUCCESS_COLOR.darker().darker());
                } else if (model.isRollover()) {
                    gp = new GradientPaint(0, 0, SUCCESS_COLOR.brighter(), 0, c.getHeight(), SUCCESS_COLOR.darker());
                } else {
                    gp = new GradientPaint(0, 0, SUCCESS_COLOR, 0, c.getHeight(), SUCCESS_COLOR.darker());
                }
                
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 20, 20);
                
                // Dessiner l'ombre
                if (c.isEnabled()) {
                    g2.setColor(new Color(SUCCESS_COLOR.getRed(), SUCCESS_COLOR.getGreen(), SUCCESS_COLOR.getBlue(), 50));
                    g2.fillRoundRect(1, 3, c.getWidth()-2, c.getHeight()-2, 20, 20);
                }
                
                super.paint(g, c);
                g2.dispose();
            }
        });
    }

    private void saveProduct(ActionEvent e) {
        try {
            // Validation
            if (nomField.getText().trim().isEmpty()) {
                showError("Le nom du produit est obligatoire");
                nomField.setBorder(BorderFactory.createLineBorder(ERROR_COLOR, 1));
                return;
            } else {
                nomField.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
            }

            double prix;
            try {
                prix = Double.parseDouble(prixField.getText().trim());
                if (prix <= 0) {
                    showError("Le prix doit être supérieur à zéro");
                    prixField.setBorder(BorderFactory.createLineBorder(ERROR_COLOR, 1));
                    return;
                }
                prixField.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
            } catch (NumberFormatException ex) {
                showError("Veuillez entrer un prix valide");
                prixField.setBorder(BorderFactory.createLineBorder(ERROR_COLOR, 1));
                return;
            }

            // Création du produit
            Produit produit = new Produit(
                0,
                nomField.getText().trim(),
                descArea.getText().trim(),
                prix,
                (String)categorieCombo.getSelectedItem(),
                imageBytes
            );

            // Sauvegarde
            if (controller.ajouterProduit(produit)) {
                JOptionPane.showMessageDialog(formPanel,
                    "Produit ajouté avec succès!",
                    "Succès",
                    JOptionPane.INFORMATION_MESSAGE);
                
                resetForm();
                cardManager.refreshProductCards(controller.getTousProduits());
                
                if (tabbedPane != null) {
                    tabbedPane.setSelectedIndex(0);
                }
            } else {
                showError("Erreur lors de l'ajout du produit");
            }
        } catch (Exception ex) {
            showError("Une erreur inattendue est survenue");
            ex.printStackTrace();
        }
    }

    public void resetForm() {
        nomField.setText("");
        descArea.setText("");
        prixField.setText("");
        categorieCombo.setSelectedIndex(0);
        imageLabel.setIcon(null);
        imageLabel.setText("Aucune image sélectionnée");
        imageBytes = null;
        
        nomField.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        prixField.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(formPanel, 
            message, 
            "Erreur", 
            JOptionPane.ERROR_MESSAGE);
    }

    // Styles modernes pour les composants
    private void styleTextField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(new CompoundBorder(
            new LineBorder(BORDER_COLOR, 1),
            new EmptyBorder(10, 12, 10, 12)
        ));
        field.setBackground(Color.WHITE);
    }

    private void styleComboBox(JComboBox<String> combo) {
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        combo.setBackground(Color.WHITE);
        combo.setBorder(new CompoundBorder(
            new LineBorder(BORDER_COLOR, 1),
            new EmptyBorder(8, 12, 8, 12)
        ));
        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBorder(new EmptyBorder(5, 10, 5, 10));
                return this;
            }
        });
    }

    private void styleTextArea(JScrollPane scrollPane) {
        scrollPane.setBorder(new CompoundBorder(
            new LineBorder(BORDER_COLOR, 1),
            new EmptyBorder(0, 0, 0, 0)
        ));
        JTextArea textArea = ((JTextArea)scrollPane.getViewport().getView());
        textArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textArea.setBackground(Color.WHITE);
    }

    // Styles améliorés pour les boutons
   
   
    // Classe pour les bordures arrondies
    static class RoundBorder extends AbstractBorder {
        private int radius;
        private Color color;
        
        public RoundBorder(int radius, Color color) {
            this.radius = radius;
            this.color = color;
        }
        
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.drawRoundRect(x, y, width-1, height-1, radius, radius);
            g2.dispose();
        }
        
        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(radius/2, radius/2, radius/2, radius/2);
        }
        
        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.left = insets.right = radius/2;
            insets.top = insets.bottom = radius/2;
            return insets;
        }
    }
}