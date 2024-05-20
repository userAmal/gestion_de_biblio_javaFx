package Controller;

import application.ConnexionMysql;
import Models.Livre;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class LivresController implements Initializable {
    @FXML
    private Button btnRetour;
    @FXML
    private Button btnAjouter;

    @FXML
    private Button btnModifier;

    @FXML
    private Button btn_supprimer;

    @FXML
    private Button btnRechercher;

    @FXML
    private TableColumn<Livre, String> visbn;

    @FXML
    private TableColumn<Livre, String> vtitre;

    @FXML
    private TableColumn<Livre, String> vauteur;

    @FXML
    private TableColumn<Livre, String> vgenre;

    @FXML
    private TableColumn<Livre, Integer> vdisp;

    @FXML
    private TableColumn<Livre, Float> vprix;

    @FXML
    private TableView<Livre> table;

    @FXML
    private TextField txtRechercher;

    private ObservableList<Livre> livresData = FXCollections.observableArrayList();

    private Connection connection;
    private PreparedStatement statement;
    private ResultSet resultSet;
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        connection = ConnexionMysql.getConnection();
        initializeColumns();
        showLivres();
    }

    private void initializeColumns() {
        visbn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        vtitre.setCellValueFactory(new PropertyValueFactory<>("titre"));
        vauteur.setCellValueFactory(new PropertyValueFactory<>("auteur"));
        vgenre.setCellValueFactory(new PropertyValueFactory<>("genre"));
        vdisp.setCellValueFactory(new PropertyValueFactory<>("dispo"));
        vprix.setCellValueFactory(new PropertyValueFactory<>("prixj"));
    }

    private void showLivres() {
        String query = "SELECT * FROM livres";
        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                livresData.add(new Livre(
                        resultSet.getString("ISBN"),
                        resultSet.getString("titre"),
                        resultSet.getString("auteur"),
                        resultSet.getString("genre"),
                        resultSet.getBoolean("dispo"),
                        resultSet.getFloat("prixj")
                ));
            }
            table.setItems(livresData);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    


    private void showAlert(Alert.AlertType alertType, String message) {
        Alert alert = new Alert(alertType, message, ButtonType.OK);
        alert.showAndWait();
    }


    @FXML
    public void rechercher(ActionEvent event) {
        if (txtRechercher.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Veuillez entrer un titre de recherche!");
            return;
        }
        table.getItems().clear();
        livresData.clear();

        String titre = txtRechercher.getText();

        String sql = "SELECT * FROM livres WHERE titre LIKE ?";
        boolean livreFound = false;

        try {
            statement = connection.prepareStatement(sql);
            statement.setString(1, "%" + titre + "%");
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                livreFound = true;
                livresData.add(new Livre(
                        resultSet.getString("ISBN"),
                        resultSet.getString("titre"),
                        resultSet.getString("auteur"),
                        resultSet.getString("genre"),
                        resultSet.getBoolean("dispo"),
                        resultSet.getFloat("prixj")
                ));
            }

            if (livreFound) {
                table.setItems(livresData);
            } else {
                showAlert(Alert.AlertType.ERROR, "Aucun livre trouvé avec le titre contenant: " + titre);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    @FXML
    public void ajouter(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Ajouterlivre.fxml"));
            Parent root = loader.load();
            AjouterL_controller controller = loader.getController();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void supprimerLivre(ActionEvent event) {
        Livre selectedLivre = table.getSelectionModel().getSelectedItem();
        if (selectedLivre != null) {
            if (selectedLivre.isDispo()) { 
                table.getItems().remove(selectedLivre);
                try {
                    String sql = "DELETE FROM livres WHERE ISBN=?";
                    PreparedStatement pstmt = connection.prepareStatement(sql);
                    pstmt.setString(1, selectedLivre.getIsbn());
                    pstmt.executeUpdate();
                    showAlert(Alert.AlertType.INFORMATION, "Livre supprimé avec succès!");
                } catch (SQLException e) {
                    showAlert(Alert.AlertType.ERROR, "Erreur lors de la suppression du Livre");
                    e.printStackTrace();
                }
            } else {
                showAlert(Alert.AlertType.WARNING, "Ce livre n'est pas disponible et ne peut pas être supprimé!");
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Sélectionnez un Livre!");
        }
    }

    @FXML
    public void Modifier(ActionEvent event) {
        Livre selectedLivre = table.getSelectionModel().getSelectedItem();
        if (selectedLivre != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/ModifierLivre.fxml"));
                Parent root = loader.load();
                ModifierL_controller controller = loader.getController();
                controller.livresData(selectedLivre); 
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Sélectionnez un Livre!");
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
}
