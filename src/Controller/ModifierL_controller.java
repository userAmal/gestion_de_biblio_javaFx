package Controller;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;

import Models.Livre;
import application.ConnexionMysql;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.scene.control.ToggleGroup;

public class ModifierL_controller implements Initializable {

    @FXML
    private TextField isbnField;

    @FXML
    private TextField titreField;

    @FXML
    private TextField auteurField;

    @FXML
    private RadioButton dispoOui;

    @FXML
    private RadioButton dispoNon;

    @FXML
    private TextField prixField;

    @FXML
    private TextField genreField;

    private Connection connection;
    @FXML
    private Button btn_annuler;
    @FXML
    private Button btn_modifier;
    private Livre livreData;

    private ToggleGroup dispoToggleGroup;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        connection = ConnexionMysql.getConnection();
        dispoToggleGroup = new ToggleGroup();
        dispoOui.setToggleGroup(dispoToggleGroup);
        dispoNon.setToggleGroup(dispoToggleGroup);
        isbnField.setEditable(false);
    }

    @FXML
    private void modifierLivre(ActionEvent event) {
        String isbn = isbnField.getText();
        String titre = titreField.getText();
        String auteur = auteurField.getText();
        String genre = genreField.getText();
        float prix;

        try {
            prix = Float.parseFloat(prixField.getText());
        } catch (NumberFormatException e) {
            showAlert(AlertType.WARNING, "Veuillez entrer un prix valide!");
            return;
        }

        boolean dispo = dispoOui.isSelected();

        if (isbn.isEmpty() || titre.isEmpty() || auteur.isEmpty() || genre.isEmpty() || prix <= 0) {
            showAlert(AlertType.WARNING, "Veuillez remplir tous les champs correctement!");
            return;
        }

        String sqlUpdate = "UPDATE livres SET titre = ?, auteur = ?, genre = ?, dispo = ?, prixj = ? WHERE isbn = ?";

        try (PreparedStatement stUpdate = connection.prepareStatement(sqlUpdate)) {
            stUpdate.setString(1, titre);
            stUpdate.setString(2, auteur);
            stUpdate.setString(3, genre);
            stUpdate.setBoolean(4, dispo);
            stUpdate.setFloat(5, prix);
            stUpdate.setString(6, isbn);
            int rowsUpdated = stUpdate.executeUpdate();
            if (rowsUpdated > 0) {
                showAlert(AlertType.INFORMATION, "Livre modifié avec succès!");
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/livres.fxml"));
                    Parent root = loader.load();
                    LivresController controller = loader.getController();
                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    stage.setScene(new Scene(root));
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
           
            } else {
                showAlert(AlertType.ERROR, "Aucune mise à jour n'a été effectuée, veuillez vérifier l'ISBN.");
            }
        } catch (SQLException e) {
            showAlert(AlertType.ERROR, "Erreur lors de la modification du livre : " + e.getMessage());
            e.printStackTrace();
        }
    }


    private void showAlert(AlertType alertType, String message) {
        Alert alert = new Alert(alertType, message, ButtonType.OK);
        alert.showAndWait();
    }

    public void livresData(Livre selectedLivre) {
        this.livreData = selectedLivre;
        isbnField.setText(livreData.getIsbn());
        titreField.setText(livreData.getTitre());
        auteurField.setText(livreData.getAuteur());
        genreField.setText(livreData.getGenre());
        prixField.setText(String.valueOf(livreData.getPrixj()));
        if (livreData.isDispo()) {
            dispoOui.setSelected(true);
        } else {
            dispoNon.setSelected(true);
        }
    }
    @FXML
    protected void Annuler(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/livres.fxml"));
            Parent root = loader.load();
            LivresController controller = loader.getController();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
