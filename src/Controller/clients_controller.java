package Controller;

import application.ConnexionMysql;
import Models.Livre;
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
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class clients_controller implements Initializable {
    @FXML
    private Button btn_ajout;

    @FXML
    private Button btn_editer;

    @FXML
    private Button btn_supp;
    @FXML
    private Button btn_rechercher;
    @FXML
    private Button btn_retour;
    @FXML
    private TableColumn<client, String> vcin;

    @FXML
    private TableColumn<client, String> vnom;

    @FXML
    private TableColumn<client, String> vprenom;

    @FXML
    private TableColumn<client, String> vadr;

    @FXML
    private TableView<client> table;

    @FXML
    private TextField txt_rechercher;
    private Parent fxml;
    @FXML
    private AnchorPane parent;
    
    
    
    private ObservableList<client> clientsData = FXCollections.observableArrayList();
    private Connection connection;
    private PreparedStatement statement;
    private ResultSet resultSet;

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
    public void rechercher(ActionEvent event) {
        table.getItems().clear();
        clientsData.clear();

        String cin = txt_rechercher.getText();
        if (!isValidCIN(cin)) {
            showAlert(Alert.AlertType.ERROR, "CIN invalide");
            return;
        }

        String sql = "SELECT cin, nom, prenom, adresse FROM clients WHERE cin = ?";
        boolean clientFound = false;

        try {
            statement = connection.prepareStatement(sql);
            statement.setString(1, cin);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                clientFound = true;
                clientsData.add(new client(
                        resultSet.getString("cin"),
                        resultSet.getString("nom"),
                        resultSet.getString("prenom"),
                        resultSet.getString("adresse")
                ));
            }

            if (clientFound) {
                table.setItems(clientsData);
            } else {
                showAlert(Alert.AlertType.ERROR, "Aucun client trouvé avec le CIN = " + cin);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean isValidCIN(String cin) {
        return cin.matches("\\d{8}");
    }

    @FXML
    public void supprimerClient(ActionEvent event) {
        client selectedClient = table.getSelectionModel().getSelectedItem();
        if (selectedClient != null) {
            table.getItems().remove(selectedClient);
            try {
                String sql = "DELETE FROM clients WHERE cin=?";
                PreparedStatement pstmt = connection.prepareStatement(sql);
                pstmt.setString(1, selectedClient.getCin());
                pstmt.executeUpdate();
                showAlert(Alert.AlertType.INFORMATION, "Client supprimé avec succès!");
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur lors de la suppression du client");
                e.printStackTrace();
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Sélectionnez un client!");
        }
    }

    private void showClients() {
        clientsData.clear();
        String query = "SELECT * FROM clients";
        try {
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                clientsData.add(new client(
                        resultSet.getString("cin"),
                        resultSet.getString("nom"),
                        resultSet.getString("prenom"),
                        resultSet.getString("adresse")));
            }
            table.setItems(clientsData);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType alertType, String message) {
        Alert alert = new Alert(alertType, message, ButtonType.OK);
        alert.showAndWait();
    }
    @FXML
    public void ajouterClient(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Ajouterclient.fxml"));
            Parent root = loader.load();
            AjouterC_controller controller = loader.getController();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void modifierClient(ActionEvent event) {
        client selectedClient = table.getSelectionModel().getSelectedItem();
        if (selectedClient != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/ModifierClient.fxml"));
                Parent root = loader.load();
                ModifierC_controller controller = loader.getController();
                controller.setClientData(selectedClient);
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Sélectionnez un client!");
        }
    }
    @FXML
    public void retour(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/pageacc.fxml"));
            Parent root = loader.load();
            pageAcc_controller controller = loader.getController();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void supprimerclient(ActionEvent event) {
        client selectedClient = table.getSelectionModel().getSelectedItem();
        if (selectedClient != null) {
            table.getItems().remove(selectedClient);
            try {
                String sql = "DELETE FROM clients WHERE cin=?";
                PreparedStatement pstmt = connection.prepareStatement(sql);
                pstmt.setString(1, selectedClient.getCin());
                pstmt.executeUpdate();
                showAlert(Alert.AlertType.INFORMATION, "client supprimé avec succès!");
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur lors de la suppression du client");
                e.printStackTrace();
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Sélectionnez un client!");
        }
    }
}
