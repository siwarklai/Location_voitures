package com.project.projet.controller;

import com.project.projet.util.AppState;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class MainController {

    @FXML
    private Label userLabel;

    @FXML
    private StackPane contentPane;

    @FXML
    private Button vehiclesButton;

    @FXML
    private Button clientsButton;

    @FXML
    private Button reservationsButton;

    @FXML
    private Button paymentsButton;

    @FXML
    private Button returnsButton;

    @FXML
    private Button statsButton;

    @FXML
    private void initialize() {
        String role = AppState.getRole();
        if (role != null && !role.isBlank()) {
            userLabel.setText(AppState.getCurrentUser().getUsername() + " - " + role);
        }
        applyRoleAccess(role);
        loadView("/com/project/projet/vehicles-view.fxml");
    }

    @FXML
    private void showVehicles() {
        loadView("/com/project/projet/vehicles-view.fxml");
    }

    @FXML
    private void showClients() {
        loadView("/com/project/projet/clients-view.fxml");
    }

    @FXML
    private void showReservations() {
        loadView("/com/project/projet/reservations-view.fxml");
    }

    @FXML
    private void showPayments() {
        loadView("/com/project/projet/payments-view.fxml");
    }

    @FXML
    private void showReturns() {
        loadView("/com/project/projet/returns-view.fxml");
    }

    @FXML
    private void showStats() {
        loadView("/com/project/projet/stats-view.fxml");
    }

    private void loadView(String fxmlPath) {
        try {
            Node view = FXMLLoader.load(getClass().getResource(fxmlPath));
            contentPane.getChildren().setAll(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void applyRoleAccess(String role) {
        if (role == null) {
            return;
        }
        if (role.equalsIgnoreCase("Agent")) {
            statsButton.setDisable(true);
        } else if (role.equalsIgnoreCase("Responsable")) {
            vehiclesButton.setDisable(true);
            clientsButton.setDisable(true);
            paymentsButton.setDisable(true);
            returnsButton.setDisable(true);
        }
    }
}
