package Models;

import java.time.LocalDate;

public class Emprunt {
	 private int id;
	    private String isbn;
	    private String cin;
	    private LocalDate dateEmprunt;
	    private LocalDate dateRetour;
	    private double total;

	    public Emprunt(int id, String isbn, String cin, LocalDate dateEmprunt, LocalDate dateRetour, double total) {
	        this.id = id;
	        this.isbn = isbn;
	        this.cin = cin;
	        this.dateEmprunt = dateEmprunt;
	        this.dateRetour = dateRetour;
	        this.total = total;
	    }

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getIsbn() {
			return isbn;
		}

		public void setIsbn(String isbn) {
			this.isbn = isbn;
		}

		public String getCin() {
			return cin;
		}

		public void setCin(String cin) {
			this.cin = cin;
		}

		public LocalDate getDateEmprunt() {
			return dateEmprunt;
		}

		public void setDateEmprunt(LocalDate dateEmprunt) {
			this.dateEmprunt = dateEmprunt;
		}

		public LocalDate getDateRetour() {
			return dateRetour;
		}

		public void setDateRetour(LocalDate dateRetour) {
			this.dateRetour = dateRetour;
		}

		public double getTotal() {
			return total;
		}

		public void setTotal(double total) {
			this.total = total;
		}
	    
}
