package Controller;

import application.ConnexionMysql;
import Models.client;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;

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
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ModifierC_controller implements Initializable {
    Connection connection;

    @FXML
    private Button btn_modifier;
    @FXML
    private Button btn_annuler;
    @FXML
    private TextField txt_cin;

    @FXML
    private TextField txt_nom;

    @FXML
    private TextField txt_prenom;

    @FXML
    private TextField txt_adresse;

    private client clientData; 

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        connection = ConnexionMysql.getConnection();
    }
    
    private void showAlert(Alert.AlertType alertType, String message) {
        Alert alert = new Alert(alertType, message, ButtonType.OK);
        alert.showAndWait();
    }
    
    public void setClientData(client clientData) {
        this.clientData = clientData;
        txt_cin.setText(clientData.getCin());
        txt_nom.setText(clientData.getNom());
        txt_prenom.setText(clientData.getPrenom());
        txt_adresse.setText(clientData.getAdresse());
    }
    
    @FXML
    private void modifierClient(ActionEvent event) {
        String cin = txt_cin.getText();
        String nom = txt_nom.getText();
        String prenom = txt_prenom.getText();
        String adresse = txt_adresse.getText();

        if (cin.isEmpty() || nom.isEmpty() || prenom.isEmpty() || adresse.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Veuillez remplir tous les champs.");
            return;
        }

        try {
            String sql = "UPDATE clients SET nom = ?, prenom = ?, adresse = ? WHERE cin = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, nom);
            statement.setString(2, prenom);
            statement.setString(3, adresse);
            statement.setString(4, cin);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Client modifié avec succès.");
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/clients.fxml"));
                    Parent root = loader.load();
                    clients_controller controller = loader.getController();
                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    stage.setScene(new Scene(root));
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "La modification du client a échoué.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur lors de la mise à jour du client : " + e.getMessage());
        }
    }
    @FXML
    protected void Annuler(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/clients.fxml"));
            Parent root = loader.load();
            clients_controller controller = loader.getController();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
