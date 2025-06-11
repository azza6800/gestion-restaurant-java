package entité;

public class Commande_produits {
    private int id; // Ajouté
    private Produit produit;
    private int quantite;
    private int commandeId; // Ajouté pour la relation

    // Constructeur par défaut ajouté
    public Commande_produits() {}

    public Commande_produits(Produit produit, int quantite) {
        this.produit = produit;
        this.quantite = quantite;
    }

    // Getters et setters pour les nouveaux champs
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getCommandeId() { return commandeId; }
    public void setCommandeId(int commandeId) { this.commandeId = commandeId; }

    // Getters et setters existants
    public Produit getProduit() { return produit; }
    public void setProduit(Produit produit) { this.produit = produit; }
    
    public int getQuantite() { return quantite; }
    public void setQuantite(int quantite) { this.quantite = quantite; }
}