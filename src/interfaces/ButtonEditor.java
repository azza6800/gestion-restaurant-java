package interfaces;

import entité.Client;
import Controller.ClientController;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;

public class ButtonEditor extends DefaultCellEditor {
    private final JPanel panel;
    private final JButton editButton;
    private final JButton deleteButton;
    private final GerantDashboard parent;
    private int currentRow;

    public ButtonEditor(GerantDashboard parent) {
        super(new JCheckBox());
        this.parent = parent;
        
        panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        panel.setOpaque(true);
        
        // Bouton Modifier
        editButton = createButton("Modifier", "https://cdn-icons-png.flaticon.com/512/2921/2921222.png", parent.primaryColor);
        editButton.addActionListener(e -> {
            fireEditingStopped();
            editClient();
        });
        
        // Bouton Supprimer
        deleteButton = createButton("Supprimer", "https://cdn-icons-png.flaticon.com/512/484/484662.png", parent.dangerColor);
        deleteButton.addActionListener(e -> {
            fireEditingStopped();
            deleteClient();
        });
        
        panel.add(editButton);
        panel.add(deleteButton);
    }

    private JButton createButton(String text, String iconUrl, Color color) {
        JButton button = new JButton(text);
        
        try {
            Image image = ImageIO.read(new URL(iconUrl));
            Image scaledImage = image.getScaledInstance(16, 16, Image.SCALE_SMOOTH);
            button.setIcon(new ImageIcon(scaledImage));
            button.setText(""); // Supprime le texte si l'icône est chargée
        } catch (IOException e) {
            System.err.println("Erreur de chargement de l'icône: " + e.getMessage());
            // Conserve le texte si l'icône ne peut pas être chargée
        }
        
        configureButton1(button, color);
        return button;
    }

    private void configureButton1(JButton button, Color color) {
        button.setOpaque(true);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(color.darker());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(color);
            }
        });
    }


    private void configureButton(JButton button, Color color) {
        button.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setForeground(color);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setOpaque(true);
                button.setBackground(new Color(color.getRed(), color.getGreen(), color.getBlue(), 30));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setOpaque(false);
            }
        });
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int row, int column) {
        currentRow = table.convertRowIndexToModel(row);
        panel.setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
        return panel;
    }

    @Override
    public Object getCellEditorValue() {
        return "";
    }

    private void editClient() {
        try {
            Client client = getClientFromTable(currentRow);
            if (client != null) {
                new EditClientDialog(parent, client, currentRow).setVisible(true);
            }
        } catch (Exception e) {
            handleError("Erreur lors de la modification du client", e);
        }
    }

    private void deleteClient() {
        try {
            Client client = getClientFromTable(currentRow);
            if (client == null) return;

            int confirm = JOptionPane.showConfirmDialog(panel,
                "Êtes-vous sûr de vouloir supprimer le client: " + client.getNom() + " " + client.getPrenom() + "?",
                "Confirmation de suppression",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
            
            if (confirm == JOptionPane.YES_OPTION) {
                ClientController controller = new ClientController();
                if (controller.deleteClient(client.getId())) {
                    JOptionPane.showMessageDialog(panel,
                        "Client supprimé avec succès",
                        "Succès",
                        JOptionPane.INFORMATION_MESSAGE);
                    parent.refreshClientTable();
                } else {
                    JOptionPane.showMessageDialog(panel,
                        "Erreur lors de la suppression du client",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception e) {
            handleError("Erreur lors de la suppression du client", e);
        }
    }

    private Client getClientFromTable(int row) throws Exception {
        // Méthode plus robuste pour trouver la table des clients
        JTable clientsTable = findClientsTable(parent);
        if (clientsTable != null) {
            DefaultTableModel model = (DefaultTableModel) clientsTable.getModel();
            int clientId = (int) model.getValueAt(row, 0);
            return new ClientController().getClientById(clientId);
        }
        return null;
    }
    
    private JTable findClientsTable(Container container) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JTable) {
                return (JTable) comp;
            } else if (comp instanceof JScrollPane) {
                JScrollPane scrollPane = (JScrollPane) comp;
                if (scrollPane.getViewport().getView() instanceof JTable) {
                    return (JTable) scrollPane.getViewport().getView();
                }
            } else if (comp instanceof Container) {
                JTable found = findClientsTable((Container) comp);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }
    

    private void handleError(String message, Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(panel,
            message,
            "Erreur",
            JOptionPane.ERROR_MESSAGE);
    }
}