package interfaces;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ImageUtils {
    private static final Map<String, String> CATEGORY_ICONS = new HashMap<>();
    
    static {
        // Initialisation des icônes par catégorie
        CATEGORY_ICONS.put("Plat", "🍽️");
        CATEGORY_ICONS.put("Sandwich", "🥪");
        CATEGORY_ICONS.put("Salade", "🥗");
        CATEGORY_ICONS.put("Dessert", "🍰");
        CATEGORY_ICONS.put("Viennoiserie", "🥐");
        CATEGORY_ICONS.put("Boisson", "🥤");
    }

    /**
     * Crée une icône redimensionnée à partir de données d'image brutes
     * @param imageData Les données binaires de l'image
     * @param width La largeur souhaitée
     * @param height La hauteur souhaitée
     * @return ImageIcon redimensionnée ou null si les données sont invalides
     */
    public static ImageIcon createResizedIcon(byte[] imageData, int width, int height) {
        if (imageData == null || imageData.length == 0) {
            return null;
        }
        
        try {
            ImageIcon icon = new ImageIcon(imageData);
            Image scaledImage = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
        } catch (Exception e) {
            System.err.println("Erreur lors du traitement de l'image: " + e.getMessage());
            return null;
        }
    }

    /**
     * Obtient l'emoji correspondant à une catégorie de produit
     * @param category La catégorie du produit
     * @return L'emoji représentant la catégorie
     */
    public static String getCategoryIcon(String category) {
        return CATEGORY_ICONS.getOrDefault(category, "🍴"); // Icône par défaut
    }

    /**
     * Charge une image à partir d'un fichier
     * @param file Le fichier image à charger
     * @return Les données binaires de l'image ou null en cas d'erreur
     */
    public static byte[] loadImageFile(File file) {
        // Implémentation à compléter avec java.nio.file.Files
        return null;
    }

    /**
     * Vérifie si un fichier est une image valide
     * @param file Le fichier à vérifier
     * @return true si le fichier est une image valide
     */
    public static boolean isValidImageFile(File file) {
        if (file == null || !file.exists()) {
            return false;
        }
        
        String name = file.getName().toLowerCase();
        return name.endsWith(".jpg") || name.endsWith(".jpeg") || 
               name.endsWith(".png") || name.endsWith(".gif");
    }

    /**
     * Crée un ImageIcon à partir d'un fichier avec redimensionnement
     * @param file Le fichier image
     * @param width Largeur souhaitée
     * @param height Hauteur souhaitée
     * @return ImageIcon redimensionné ou null en cas d'erreur
     */
    public static ImageIcon createIconFromFile(File file, int width, int height) {
        if (!isValidImageFile(file)) {
            return null;
        }
        
        try {
            ImageIcon icon = new ImageIcon(file.getAbsolutePath());
            Image scaledImage = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de l'image: " + e.getMessage());
            return null;
        }
    }
}