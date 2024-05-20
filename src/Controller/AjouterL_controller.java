package Controller;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import Models.Livre;
import application.ConnexionMysql;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.scene.Node;

public class AjouterL_controller implements Initializable {

    Connection connection;
    PreparedStatement st;
    ResultSet result;

    @FXML
    private Button btnAjouter;

    @FXML
    private Button btn_annuler;

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
    private TextField txt_visbn;

    @FXML
    private TextField txt_titre;

    @FXML
    private TextField txt_auteur;

    @FXML
    private TextField txt_genre;

    @FXML
    private TextField txt_prix;

    private ObservableList<Livre> livresData = FXCollections.observableArrayList();

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
        livresData.clear();

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

    @FXML
    void ajouterLivre() {
        String isbn = txt_visbn.getText();
        String titre = txt_titre.getText();
        String auteur = txt_auteur.getText();
        String genre = txt_genre.getText();
        float prix;

        try {
            prix = Float.parseFloat(txt_prix.getText());
        } catch (NumberFormatException e) {
            showAlert(AlertType.WARNING, "Veuillez entrer un prix valide!");
            return;
        }

        boolean dispo = true;
        String sqlCheck = "SELECT * FROM livres WHERE isbn = ?";
        String sqlInsert = "INSERT INTO livres (isbn, titre, auteur, genre, dispo, prixj) VALUES (?, ?, ?, ?, ?, ?)";

        if (isbn.isEmpty() || titre.isEmpty() || auteur.isEmpty() || genre.isEmpty() || prix <= 0) {
            showAlert(AlertType.WARNING, "Veuillez remplir tous les champs correctement!");
            return;
        }

        if (isbn.length() != 10) {
            showAlert(AlertType.WARNING, "L'ISBN doit comporter 10 caractères!");
            return;
        }

        try (PreparedStatement stCheck = connection.prepareStatement(sqlCheck)) {
            stCheck.setString(1, isbn);
            try (ResultSet result = stCheck.executeQuery()) {
                if (result.next()) {
                    showAlert(AlertType.WARNING, "Le livre existe déjà");
                    return;
                }
            }
        } catch (SQLException e) {
            showAlert(AlertType.ERROR, "Erreur lors de la vérification du livre");
            e.printStackTrace();
            System.err.println("Erreur de vérification SQL: " + e.getMessage());
            return;
        }

        try (PreparedStatement stInsert = connection.prepareStatement(sqlInsert)) {
            stInsert.setString(1, isbn);
            stInsert.setString(2, titre);
            stInsert.setString(3, auteur);
            stInsert.setString(4, genre);
            stInsert.setBoolean(5, dispo);
            stInsert.setFloat(6, prix);
            stInsert.executeUpdate();
            showAlert(AlertType.INFORMATION, "Livre ajouté avec succès!");
            showLivres();
            clearFields();
        } catch (SQLException e) {
            showAlert(AlertType.ERROR, "Erreur lors de l'ajout du livre");
            e.printStackTrace();
            System.err.println("Erreur d'insertion SQL: " + e.getMessage());
        }
    }

    private void clearFields() {
        txt_genre.clear();
        txt_auteur.clear();
        txt_visbn.clear();
        txt_titre.clear();
        txt_prix.clear();
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

    private void showAlert(AlertType alertType, String message) {
        Alert alert = new Alert(alertType, message, ButtonType.OK);
        alert.showAndWait();
    }
}
