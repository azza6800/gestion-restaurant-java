package entité;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Commande {
	 private int id;
	    private int clientId;
	    private Date dateCommande;
	    private String typeCommande;
	    private String statut;
	    private String adresseLivraison;
	    private List<Commande_produits> produits = new ArrayList<>(); 
	    public static final String EN_ATTENTE = "en_attente";
	    public static final String EN_PREPARATION = "en préparation";
	    public static final String PRETE = "prête";
	    public static final String EN_COURS_LIVRAISON = "en cours de livraison";
	    public static final String LIVREE = "livrée";
	    public static final String SERVIE = "servie";
	    public static final String PRETE_RECUPERER = "prête à récupérer";
	    public static final String RECUPEREE = "récupérée";
	    public static final String ANNULEE = "annulée";
	    public Commande(int id, int clientId, Date dateCommande, String typeCommande, 
	                   String statut, String adresseLivraison) {
	        this.id = id;
	        this.clientId = clientId;
	        this.dateCommande = dateCommande;
	        this.typeCommande = typeCommande;
	        this.statut = statut;
	        this.adresseLivraison = adresseLivraison;
	    }

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public int getClientId() {
			return clientId;
		}

		public void setClientId(int clientId) {
			this.clientId = clientId;
		}

		public Date getDateCommande() {
			return dateCommande;
		}

		public void setDateCommande(Date dateCommande) {
			this.dateCommande = dateCommande;
		}

		public String getTypeCommande() {
			return typeCommande;
		}

		public void setTypeCommande(String typeCommande) {
			this.typeCommande = typeCommande;
		}

		public String getStatut() {
			return statut;
		}

		public void setStatut(String statut) {
			this.statut = statut;
		}

		public String getAdresseLivraison() {
			return adresseLivraison;
		}

		public void setAdresseLivraison(String adresseLivraison) {
			this.adresseLivraison = adresseLivraison;
		}

		 public List<Commande_produits> getProduits() {
		        if (produits == null) {
		            produits = new ArrayList<>(); // Double protection
		        }
		        return produits;
		    }

		public void setProduits(List<Commande_produits> produits) {
			this.produits = produits;
		}
		
	    
	    

}
