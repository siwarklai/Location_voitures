package com.project.projet.controller;

import com.project.projet.Dao.DatabaseConnection;
import com.project.projet.Dao.UtilisateurDao;
import com.project.projet.Model.Utilisateur;
import com.project.projet.util.AppState;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    private final UtilisateurDao utilisateurDao = new UtilisateurDao();

    @FXML
    private void onLogin() throws IOException {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            errorLabel.setText("Nom d'utilisateur et mot de passe requis.");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                errorLabel.setText("Connexion base de donnees impossible. Verifiez MySQL.");
                return;
            }
        } catch (Exception e) {
            errorLabel.setText("Connexion base de donnees impossible. Verifiez MySQL.");
            return;
        }

        Utilisateur user = utilisateurDao.findByCredentials(username, password);
        if (user == null) {
            errorLabel.setText("Identifiants invalides.");
            return;
        }

        AppState.setCurrentUser(user);
        openMainView();
    }

    private void openMainView() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/project/projet/main-view.fxml"));
        Scene scene = new Scene(loader.load(), 1200, 760);
        scene.getStylesheets().add(getClass().getResource("/com/project/projet/styles.css").toExternalForm());
        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("Agence de location");
    }
}
