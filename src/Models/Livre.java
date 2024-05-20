package Models;

public class Livre {
    private String isbn;
    private String titre;
    private String auteur;
    private String genre;
    private boolean dispo;
    private float prixj;

    public Livre(String isbn, String titre, String auteur, String genre, boolean dispo, float prixj) {
        this.isbn = isbn;
        this.titre = titre;
        this.auteur = auteur;
        this.genre = genre;
        this.dispo = dispo;
        this.prixj = prixj;
    }
    public Livre(String isbn, String titre, String auteur, float d) {
        this.isbn = isbn;
        this.titre = titre;
        this.auteur = auteur;
        this.prixj = d;
    }
    public Livre(String string, String string2, String string3, double double1, boolean boolean1) {
		// TODO Auto-generated constructor stub
	}
	public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getAuteur() {
        return auteur;
    }

    public void setAuteur(String auteur) {
        this.auteur = auteur;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public boolean isDispo() {
        return dispo;
    }

    public void setDispo(boolean dispo) {
        this.dispo = dispo;
    }

    public float getPrixj() {
        return prixj;
    }

    public void setPrixj(float prixj) {
        this.prixj = prixj;
    }

    @Override
    public String toString() {
        return "Livre{" +
                "isbn='" + isbn + '\'' +
                ", titre='" + titre + '\'' +
                ", auteur='" + auteur + '\'' +
                ", genre='" + genre + '\'' +
                ", dispo=" + dispo +
                ", prixj=" + prixj +
                '}';
    }
}