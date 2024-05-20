package Controller;

import application.ConnexionMysql;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class SignInController implements Initializable {
   public Connection cnx;
   int x = 0;
   public PreparedStatement st;
   public ResultSet result;
   @FXML
   private Button btnForget;
   @FXML
   private Button btn_Go;
   @FXML
   private Button btn_SignUp; 
   @FXML
   private TextField txt_Password;
   @FXML
   private TextField txt_userName;
   @FXML
   private AnchorPane AnchorPane;
   private Parent fxml;

   public SignInController() {
   }

   @FXML
   void Click_Go() throws IOException, InterruptedException, SQLException {
      String nom = this.txt_userName.getText();
      String pass = this.txt_Password.getText();
      String sql = "select* from Admin";

      try {
         this.st = this.cnx.prepareStatement(sql);
         this.result = this.st.executeQuery();
      } catch (SQLException var7) {
         var7.printStackTrace();
      }

      while(this.result.next()) {
         if (nom.equals(this.result.getString("UserName")) && pass.equals(this.result.getString("Password"))) {
            this.x = 1;
            Stage stage = (Stage)this.btn_Go.getScene().getWindow();
            stage.close();
            Stage home = new Stage();
            this.fxml = (Parent)FXMLLoader.load(this.getClass().getResource("/Views/pageacc.fxml"));
            Scene scene = new Scene(this.fxml);
            home.setScene(scene);
            home.show();
         }
      }

      if (this.x == 0) {
         Alert alert = new Alert(AlertType.ERROR, "Le nom d'utilisateur ou le mot de passe est incorrect", new ButtonType[]{ButtonType.OK});
         alert.showAndWait();
      }

   }


   

   @FXML
   public void goToSignUp(ActionEvent event) {
       try {
           FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/signup.fxml"));
           Parent root = loader.load();
           Stage stage = new Stage();
           stage.setScene(new Scene(root));
           stage.show();
       } catch (IOException e) {
           e.printStackTrace();
       }}
   
   public void initialize(URL arg0, ResourceBundle arg1) {
      this.cnx = ConnexionMysql.getConnection();
   }
}
