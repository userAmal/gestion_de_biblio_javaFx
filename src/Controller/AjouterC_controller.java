package Controller;

import application.ConnexionMysql;
import Models.client;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.scene.Node;

public class AjouterC_controller implements Initializable {

    Connection connection;
    PreparedStatement st;
    ResultSet result;

    @FXML
    private Button btn_annuler;
    @FXML
    private Button btn_ajouter;

    @FXML
    private TableColumn<client, String> vcin;
    @FXML
    private TableColumn<client, Integer> vid;
    @FXML
    private TableColumn<client, String> vnom;
    @FXML
    private TableColumn<client, String> vprenom;
    @FXML
    private TableColumn<client, String> vadr;
    @FXML
    private TableView<client> table;

    @FXML
    private TextField txt_cin;
    @FXML
    private TextField txt_nom;
    @FXML
    private TextField txt_prenom;
    @FXML
    private TextField txt_adresse;

    private ObservableList<client> clientsData = FXCollections.observableArrayList();

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        connection = ConnexionMysql.getConnection();
        initializeColumns();
        showClients();
    }

    private void initializeColumns() {
        vcin.setCellValueFactory(new PropertyValueFactory<>("cin"));
        vnom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        vprenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        vadr.setCellValueFactory(new PropertyValueFactory<>("adresse"));
    }

    @FXML
    void ajouterC() {
        String nom = txt_nom.getText();
        String prenom = txt_prenom.getText();
        String cin = txt_cin.getText();
        String adresse = txt_adresse.getText();
        String sqlCheck = "SELECT * FROM clients WHERE cin = ?";
        String sqlInsert = "INSERT INTO clients (cin, nom, prenom, adresse) VALUES (?, ?, ?, ?)";

        // Validate CIN
        if (cin.length() != 8 || !cin.matches("\\d+")) {
            showAlert(AlertType.WARNING, "Le CIN doit comporter exactement 8 chiffres!");
            return;
        }

        try {
            st = connection.prepareStatement(sqlCheck);
            st.setString(1, cin);
            ResultSet result = st.executeQuery();
            if (result.next()) {
                showAlert(AlertType.WARNING, "Le client existe déjà");
                return;
            }
        } catch (SQLException e) {
            showAlert(AlertType.ERROR, "Erreur lors de la vérification du client");
            e.printStackTrace();
            return;
        }

        if (!nom.isEmpty() && !prenom.isEmpty() && !cin.isEmpty() && !adresse.isEmpty()) {
            try {
                st = connection.prepareStatement(sqlInsert);
                st.setString(1, cin);
                st.setString(2, nom);
                st.setString(3, prenom);
                st.setString(4, adresse);
                st.executeUpdate();
                showAlert(AlertType.INFORMATION, "Client ajouté avec succès!");
                clearFields();
                showClients();
            } catch (SQLException e) {
                showAlert(AlertType.ERROR, "Erreur lors de l'ajout du client");
                e.printStackTrace();
            }
        } else {
            showAlert(AlertType.WARNING, "Veuillez remplir tous les champs!");
        }
    }

    private void clearFields() {
        txt_cin.clear();
        txt_nom.clear();
        txt_prenom.clear();
        txt_adresse.clear();
    }

    private void showClients() {
        clientsData.clear();
        String query = "SELECT * FROM clients";
        try {
            st = connection.prepareStatement(query);
            ResultSet resultSet = st.executeQuery();
            while (resultSet.next()) {
                clientsData.add(new client(
                        resultSet.getString("cin"),
                        resultSet.getString("nom"),
                        resultSet.getString("prenom"),
                        resultSet.getString("adresse")));
            }
            table.setItems(clientsData);
        } catch (SQLException e) {
            showAlert(AlertType.ERROR, "Erreur lors de l'affichage des clients");
            e.printStackTrace();
        }
    }

    private void showAlert(AlertType alertType, String message) {
        Alert alert = new Alert(alertType, message, ButtonType.OK);
        alert.showAndWait();
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
