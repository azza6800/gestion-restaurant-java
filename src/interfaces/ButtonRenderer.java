package interfaces;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.net.URL;
import javax.imageio.ImageIO;

public class ButtonRenderer extends JPanel implements TableCellRenderer {
    private JButton editButton;
    private JButton deleteButton;

    public ButtonRenderer(Color editColor, Color deleteColor) {
        setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
        setOpaque(true);
        
        // Chargement des icônes
        ImageIcon editIcon = loadIcon("https://cdn-icons-png.flaticon.com/512/2921/2921222.png", 16, 16);
        ImageIcon deleteIcon = loadIcon("https://cdn-icons-png.flaticon.com/512/484/484662.png", 16, 16);
        
        editButton = new JButton(editIcon);
        styleActionButton(editButton, editColor, "Modifier");
        
        deleteButton = new JButton(deleteIcon);
        styleActionButton(deleteButton, deleteColor, "Supprimer");
        
        add(editButton);
        add(deleteButton);
    }

    private ImageIcon loadIcon(String url, int width, int height) {
        try {
            Image image = ImageIO.read(new URL(url));
            return new ImageIcon(image.getScaledInstance(width, height, Image.SCALE_SMOOTH));
        } catch (Exception e) {
            System.err.println("Erreur de chargement de l'icône: " + e.getMessage());
            return null;
        }
    }

    private void styleActionButton(JButton button, Color color, String tooltip) {
        button.setToolTipText(tooltip);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setForeground(color);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(color.getRed(), color.getGreen(), color.getBlue(), 30));
                button.setOpaque(true);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(null);
                button.setOpaque(false);
            }
        });
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
        return this;
    }
}