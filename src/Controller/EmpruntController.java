package Controller;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import Models.Emprunt;
import Models.Facture;
import Models.Livre;
import Models.client;
import application.ConnexionMysql;

public class EmpruntController implements Initializable {

    @FXML
    private ComboBox<String> isbnComboBox;

    @FXML
    private ComboBox<String> cinComboBox;

    @FXML
    private DatePicker dateRetourPicker;

    @FXML
    private TextField totalField;

    @FXML
    private Button calculerButton;

    @FXML
    private Button ajouterButton;

    @FXML
    private Button supprimerButton;

 
    @FXML
    private Button btn_retour;

    @FXML
    private TableView<Emprunt> empruntTableView;

    @FXML
    private TableColumn<Emprunt, Integer> idColumn;

    @FXML
    private TableColumn<Emprunt, String> isbnColumn;

    @FXML
    private TableColumn<Emprunt, String> cinColumn;

    @FXML
    private TableColumn<Emprunt, LocalDate> dateEmpruntColumn;

    @FXML
    private TableColumn<Emprunt, LocalDate> dateRetourColumn;

    @FXML
    private TableColumn<Emprunt, Double> totalColumn;

    private ObservableList<Emprunt> empruntList = FXCollections.observableArrayList();
    private Connection connection;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        connection = ConnexionMysql.getConnection();
        initializeComboBoxes();
        initializeTableView();
        loadEmprunts();
    }

    private void initializeComboBoxes() {
        try (PreparedStatement statement = connection.prepareStatement("SELECT isbn FROM livres WHERE dispo = TRUE");
             ResultSet resultSet = statement.executeQuery()) {
            ObservableList<String> isbnList = FXCollections.observableArrayList();
            while (resultSet.next()) {
                isbnList.add(resultSet.getString("isbn"));
            }
            isbnComboBox.setItems(isbnList);

            try (PreparedStatement cinStatement = connection.prepareStatement("SELECT cin FROM clients");
                 ResultSet cinResultSet = cinStatement.executeQuery()) {
                ObservableList<String> cinList = FXCollections.observableArrayList();
                while (cinResultSet.next()) {
                    cinList.add(cinResultSet.getString("cin"));
                }
                cinComboBox.setItems(cinList);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void initializeTableView() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        isbnColumn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        cinColumn.setCellValueFactory(new PropertyValueFactory<>("cin"));
        dateEmpruntColumn.setCellValueFactory(new PropertyValueFactory<>("dateEmprunt"));
        dateRetourColumn.setCellValueFactory(new PropertyValueFactory<>("dateRetour"));
        totalColumn.setCellValueFactory(new PropertyValueFactory<>("total"));

        empruntTableView.setItems(empruntList);
    }

    private void loadEmprunts() {
        empruntList.clear();
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM emprunts");
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Emprunt emprunt = new Emprunt(resultSet.getInt("id"), resultSet.getString("isbn"), resultSet.getString("cin"), resultSet.getDate("dateE").toLocalDate(), resultSet.getDate("dateR").toLocalDate(), resultSet.getDouble("totalP"));
                empruntList.add(emprunt);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Double getPrixjByIsbn(String isbn) {
        try (PreparedStatement statement = connection.prepareStatement("SELECT prixj FROM livres WHERE isbn = ?")) {
            statement.setString(1, isbn);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getDouble("prixj");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur SQL", e.getMessage());
        }
        return null;
    }

    @FXML
    private void calculerButtonClicked() {
        if (isbnComboBox.getValue() == null || dateRetourPicker.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Données incomplètes", "Veuillez sélectionner un livre et une date de retour.");
            return;
        }

        LocalDate currentDate = LocalDate.now();

        System.out.println("Date actuelle : " + currentDate);

        LocalDate returnDate = dateRetourPicker.getValue();
        long daysBetween = ChronoUnit.DAYS.between(currentDate, returnDate);
        System.out.println("Jours entre les dates : " + daysBetween);
        if (daysBetween < 0) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Date invalide", "La date de retour ne peut pas être antérieure à aujourd'hui.");
            return;
        }

        String selectedIsbn = isbnComboBox.getValue();
        Double prixj = getPrixjByIsbn(selectedIsbn);

        if (prixj != null) {
            System.out.println(prixj);
            double totalPrice = daysBetween * prixj;
            System.out.println("Prix total : " + totalPrice);
            totalField.setText(String.valueOf(totalPrice));
        } else {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Livre introuvable", "Impossible de trouver le livre avec l'ISBN : " + selectedIsbn);
        }
    }


    private Livre getLivreByIsbn(String isbn) {
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM livres WHERE isbn = ?")) {
            statement.setString(1, isbn);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new Livre(resultSet.getString("isbn"), resultSet.getString("titre"), resultSet.getString("auteur"), resultSet.getDouble("prixj"), resultSet.getBoolean("dispo"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur SQL", e.getMessage());
        }
        return null;
    }




    @FXML
    private void ajouterButtonClicked() {
        if (isbnComboBox.getValue() == null || cinComboBox.getValue() == null || dateRetourPicker.getValue() == null || totalField.getText().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Données incomplètes", "Veuillez remplir tous les champs.");
            return;
        }

        LocalDate dateEmprunt = LocalDate.now(); 
        LocalDate dateRetour = dateRetourPicker.getValue();

        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO emprunts (isbn, cin, dateE, dateR, totalP) VALUES (?, ?, ?, ?, ?)", 
                PreparedStatement.RETURN_GENERATED_KEYS)) {
            
            statement.setString(1, isbnComboBox.getSelectionModel().getSelectedItem());
            statement.setString(2, cinComboBox.getSelectionModel().getSelectedItem());
            statement.setDate(3, Date.valueOf(dateEmprunt));
            statement.setDate(4, Date.valueOf(dateRetour));
            statement.setDouble(5, Double.parseDouble(totalField.getText()));
            
            statement.executeUpdate();
            
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    Emprunt emprunt = new Emprunt(id, isbnComboBox.getSelectionModel().getSelectedItem(), cinComboBox.getSelectionModel().getSelectedItem(), dateEmprunt, dateRetour, Double.parseDouble(totalField.getText()));
                    empruntList.add(emprunt);

                    updateLivreDisponibilite(isbnComboBox.getSelectionModel().getSelectedItem(), false);
                    Facture.genererFacture(emprunt);

                    updateISBNComboBox();

                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Emprunt ajouté", "Emprunt ajouté avec succès. La facture est prête.");

                    clearFields();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Échec de l'insertion", "Impossible de récupérer l'identifiant généré.");
                }
                
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur SQL", e.getMessage());
        }
    }

    private void updateISBNComboBox() {
        try (PreparedStatement statement = connection.prepareStatement("SELECT isbn FROM livres WHERE dispo = TRUE");
             ResultSet resultSet = statement.executeQuery()) {
            ObservableList<String> isbnList = FXCollections.observableArrayList();
            while (resultSet.next()) {
                isbnList.add(resultSet.getString("isbn"));
            }
            isbnComboBox.setItems(isbnList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateLivreDisponibilite(String isbn, boolean dispo) {
        try (PreparedStatement statement = connection.prepareStatement("UPDATE livres SET dispo = ? WHERE isbn = ?")) {
            statement.setBoolean(1, dispo);
            statement.setString(2, isbn);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur SQL", e.getMessage());
        }
    }

    private void clearFields() {
        isbnComboBox.setValue(null);
        cinComboBox.setValue(null);
        dateRetourPicker.setValue(null);
        totalField.clear();
    }

    @FXML
    private void handleSupprimer() {
        Emprunt selectedEmprunt = empruntTableView.getSelectionModel().getSelectedItem();
        if (selectedEmprunt != null) {
            try (PreparedStatement statement = connection.prepareStatement("DELETE FROM emprunts WHERE id = ?")) {
                statement.setInt(1, selectedEmprunt.getId());
                statement.executeUpdate();
                empruntList.remove(selectedEmprunt);
                Livre livre = getLivreByIsbn(selectedEmprunt.getIsbn());
                if (livre != null && !livre.isDispo()) {
                    updateLivreDisponibilite(selectedEmprunt.getIsbn(), true);
                }
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Emprunt supprimé", "L'emprunt a été supprimé avec succès. Le livre est maintenant disponible.");

                clearFields();
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur SQL", e.getMessage());
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Attention", "Aucune sélection", "Veuillez sélectionner un élément à supprimer.");
        }
    }



    @FXML
    private void handleRetour() {
        Emprunt selectedEmprunt = empruntTableView.getSelectionModel().getSelectedItem();
        if (selectedEmprunt != null) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "UPDATE emprunts SET dateR = ?, totalP = ? WHERE id = ?")) {

                LocalDate newDateRetour = dateRetourPicker.getValue();
                if (newDateRetour == null) {
                    showAlert(Alert.AlertType.WARNING, "Attention", "Données incomplètes", "Veuillez sélectionner une nouvelle date de retour.");
                    return;
                }
                
                long daysBetween = ChronoUnit.DAYS.between(selectedEmprunt.getDateEmprunt(), newDateRetour);
                Livre livre = getLivreByIsbn(selectedEmprunt.getIsbn());
                if (livre != null) {
                    double newTotal = daysBetween * livre.getPrixj();
                    totalField.setText(String.valueOf(newTotal));

                    statement.setDate(1, Date.valueOf(newDateRetour));
                    statement.setDouble(2, newTotal);
                    statement.setInt(3, selectedEmprunt.getId());

                    statement.executeUpdate();

                    selectedEmprunt.setDateRetour(newDateRetour);
                    selectedEmprunt.setTotal(newTotal);

                    empruntTableView.refresh();
                    clearFields();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Livre introuvable", "Impossible de trouver le livre avec l'ISBN : " + selectedEmprunt.getIsbn());
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur SQL", e.getMessage());
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Attention", "Aucune sélection", "Veuillez sélectionner un élément à retourner.");
        }
    }




    private void showAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
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
