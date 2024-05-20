package Models;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class Facture{

    public static void genererFacture(Emprunt emprunt) {
        String facture = "facture_" + emprunt.getId() + ".txt";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(facture))) {
            writer.write("Facture d'emprunt\n\n");
            writer.write("ID de l'emprunt: " + emprunt.getId() + "\n");
            writer.write("ISBN du livre: " + emprunt.getIsbn() + "\n");
            writer.write("CIN du client: " + emprunt.getCin() + "\n");
            writer.write("Date d'emprunt: " + emprunt.getDateEmprunt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "\n");
            writer.write("Date de retour: " + emprunt.getDateRetour().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "\n");
            writer.write("Montant total: " + emprunt.getTotal() + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}