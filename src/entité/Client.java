package entité;

import java.util.Date;

public class Client {
	 private int id;
	    private String login;
	    private String password;
	    private String nom;
	    private String prenom;
	    private Date dateNaissance;
	    private String adresse;
	    private String telephone;
	    private boolean firstLogin;
	    
	    public Client() {}
	    
	    // Constructeurs, getters et setters
	    public Client(int id, String login, String password, String nom, String prenom, 
	                 Date dateNaissance, String adresse, String telephone, boolean firstLogin) {
	        this.id = id;
	        this.login = login;
	        this.password = password;
	        this.nom = nom;
	        this.prenom = prenom;
	        this.dateNaissance = dateNaissance;
	        this.adresse = adresse;
	        this.telephone = telephone;
	        this.firstLogin = firstLogin;
	    }

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getLogin() {
			return login;
		}

		public void setLogin(String login) {
			this.login = login;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public String getNom() {
			return nom;
		}

		public void setNom(String nom) {
			this.nom = nom;
		}

		public String getPrenom() {
			return prenom;
		}

		public void setPrenom(String prenom) {
			this.prenom = prenom;
		}

		public Date getDateNaissance() {
			return dateNaissance;
		}

		public void setDateNaissance(Date dateNaissance) {
			this.dateNaissance = dateNaissance;
		}

		public String getAdresse() {
			return adresse;
		}

		public void setAdresse(String adresse) {
			this.adresse = adresse;
		}

		public String getTelephone() {
			return telephone;
		}

		public void setTelephone(String telephone) {
			this.telephone = telephone;
		}

		public boolean isFirstLogin() {
			return firstLogin;
		}

		public void setFirstLogin(boolean firstLogin) {
			this.firstLogin = firstLogin;
		}
	    

}
