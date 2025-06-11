package interfaces;

import javax.swing.*;
import java.awt.*;
import java.io.File;

import entité.Produit;
import Controller.ProduitController;

public class DialogManager {
    private static ProduitController produitController;
    
    public static void initialize(ProduitController controller) {
        produitController = controller;
    }

    /**
     * Affiche un dialogue de modification de produit
     * @param produit Le produit à modifier
     * @param parent Le composant parent pour le positionnement
     */
    public static void showEditDialog(Produit produit, Component parent) {
        JDialog editDialog = new JDialog(
            JOptionPane.getFrameForComponent(parent), 
            "Modifier Produit", 
            true
        );
        editDialog.setLayout(new BorderLayout());
        editDialog.setSize(500, 400);
        editDialog.setLocationRelativeTo(parent);

        // Création du formulaire
        JPanel formPanel = createEditForm(produit);
        editDialog.add(formPanel, BorderLayout.CENTER);
        
        // Bouton de sauvegarde
        JButton saveButton = new JButton("Enregistrer");
        saveButton.addActionListener(e -> handleSaveAction(produit, editDialog, formPanel));
        editDialog.add(saveButton, BorderLayout.SOUTH);

        editDialog.setVisible(true);
    }

    private static JPanel createEditForm(Produit produit) {
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Champs du formulaire
        JTextField nameField = new JTextField(produit.getNom());
        JComboBox<String> categoryCombo = new JComboBox<>(
            new String[]{"Plat", "Sandwich", "Salade", "Dessert", "Viennoiserie", "Boisson"});
        categoryCombo.setSelectedItem(produit.getCategorie());
        
        JTextField priceField = new JTextField(String.valueOf(produit.getPrix()));
        JTextArea descArea = new JTextArea(produit.getDescription());
        
        // Ajout des champs
        formPanel.add(new JLabel("Nom:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Catégorie:"));
        formPanel.add(categoryCombo);
        formPanel.add(new JLabel("Prix (€):"));
        formPanel.add(priceField);
        formPanel.add(new JLabel("Description:"));
        formPanel.add(new JScrollPane(descArea));
        
        return formPanel;
    }

    private static void handleSaveAction(Produit produit, JDialog dialog, JPanel formPanel) {
        // Validation et sauvegarde...
        dialog.dispose();
    }

    /**
     * Affiche une boîte de dialogue de confirmation de suppression
     * @param parent Le composant parent pour le positionnement
     * @return true si l'utilisateur a confirmé la suppression
     */
    public static boolean confirmDelete(Component parent) {
        int result = JOptionPane.showConfirmDialog(
            parent,
            "Voulez-vous vraiment supprimer ce produit?",
            "Confirmation",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        return result == JOptionPane.YES_OPTION;
    }

    /**
     * Affiche un message d'information
     * @param parent Le composant parent
     * @param message Le message à afficher
     * @param title Le titre de la fenêtre
     */
    public static void showInfoMessage(Component parent, String message, String title) {
        JOptionPane.showMessageDialog(
            parent,
            message,
            title,
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    /**
     * Affiche un message d'erreur
     * @param parent Le composant parent
     * @param message Le message d'erreur
     * @param title Le titre de la fenêtre
     */
    public static void showErrorMessage(Component parent, String message, String title) {
        JOptionPane.showMessageDialog(
            parent,
            message,
            title,
            JOptionPane.ERROR_MESSAGE
        );
    }

    /**
     * Affiche un dialogue de sélection de fichier image
     * @param parent Le composant parent
     * @return Le fichier sélectionné ou null
     */
    public static File showImageChooser(Component parent) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Sélectionner une image");
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.addChoosableFileFilter(
            new javax.swing.filechooser.FileNameExtensionFilter(
                "Images", "jpg", "jpeg", "png", "gif")
        );
        
        if (fileChooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();
        }
        return null;
    }
}