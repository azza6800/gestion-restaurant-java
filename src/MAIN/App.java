package MAIN;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;



import interfaces.LoginFrame1;

public class App {
    public static void main(String[] args) {
        // Appliquer un look and feel moderne
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Afficher la fenêtre de connexion
        SwingUtilities.invokeLater(() -> {
            new LoginFrame1().setVisible(true);  // Fixed: added parentheses after LoginFrame
        });
    }
}