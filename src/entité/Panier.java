package entité;

import java.util.ArrayList;
import java.util.List;

public class Panier {
    private List<PanierItem> items;
    private String typeCommande;
    private String adresseLivraison;
    
    public Panier() {
        this.items = new ArrayList<>();
    }
    
    public void ajouterProduit(Produit produit, int quantite) {
        for (PanierItem item : items) {
            if (item.getProduit().getId() == produit.getId()) {
                item.setQuantite(item.getQuantite() + quantite);
                return;
            }
        }
        items.add(new PanierItem(produit, quantite));
    }
    
    public void retirerProduit(int produitId) {
        items.removeIf(item -> item.getProduit().getId() == produitId);
    }
    
    public void modifierQuantite(int produitId, int nouvelleQuantite) {
        for (PanierItem item : items) {
            if (item.getProduit().getId() == produitId) {
                item.setQuantite(nouvelleQuantite);
                return;
            }
        }
    }
    
    public List<PanierItem> getItems() {
        return items;
    }
    
    public double getTotal() {
        double total = 0;
        for (PanierItem item : items) {
            total += item.getProduit().getPrix() * item.getQuantite();
        }
        return total;
    }
    
    public void vider() {
        items.clear();
        typeCommande = null;
        adresseLivraison = null;
    }
    
    // Getters et setters pour typeCommande et adresseLivraison
    public String getTypeCommande() { return typeCommande; }
    public void setTypeCommande(String typeCommande) { this.typeCommande = typeCommande; }
    
    public String getAdresseLivraison() { return adresseLivraison; }
    public void setAdresseLivraison(String adresseLivraison) { this.adresseLivraison = adresseLivraison; }
}