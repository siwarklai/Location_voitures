package com.project.projet.controller;

import com.project.projet.Dao.ClientDao;
import com.project.projet.Model.Client;
import com.project.projet.util.ValidationUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;

public class ClientController {

    @FXML
    private TextField nomField;

    @FXML
    private TextField prenomField;

    @FXML
    private TextField cinField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField telephoneField;

    @FXML
    private TextField adresseField;

    @FXML
    private TextField permisField;

    @FXML
    private DatePicker expirationPicker;

    @FXML
    private TextField searchField;

    @FXML
    private TableView<Client> tableView;

    @FXML
    private TableColumn<Client, String> nomCol;

    @FXML
    private TableColumn<Client, String> prenomCol;

    @FXML
    private TableColumn<Client, String> cinCol;

    @FXML
    private TableColumn<Client, String> emailCol;

    @FXML
    private TableColumn<Client, String> telephoneCol;

    @FXML
    private TableColumn<Client, String> permisCol;

    @FXML
    private TableColumn<Client, LocalDate> expirationCol;

    private final ClientDao clientDao = new ClientDao();
    private final ObservableList<Client> clients = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        nomCol.setCellValueFactory(new PropertyValueFactory<>("nom"));
        prenomCol.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        cinCol.setCellValueFactory(new PropertyValueFactory<>("cin"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        telephoneCol.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        permisCol.setCellValueFactory(new PropertyValueFactory<>("numeroPermis"));
        expirationCol.setCellValueFactory(new PropertyValueFactory<>("expirationPermis"));

        tableView.setItems(clients);
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> fillForm(newV));

        searchField.textProperty().addListener((obs, oldV, newV) -> refreshTable());

        refreshTable();
    }

    @FXML
    private void onAdd() {
        if (!validateForm(null)) {
            return;
        }
        Client c = buildFromForm();
        clientDao.insert(c);
        refreshTable();
        clearForm();
        showAlert(Alert.AlertType.INFORMATION, "Client ajoute.");
    }

    @FXML
    private void onUpdate() {
        Client selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Selectionnez un client.");
            return;
        }
        if (!validateForm(selected.getId())) {
            return;
        }
        Client c = buildFromForm();
        c.setId(selected.getId());
        clientDao.update(c);
        refreshTable();
        clearForm();
        showAlert(Alert.AlertType.INFORMATION, "Client modifie.");
    }

    @FXML
    private void onDelete() {
        Client selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Selectionnez un client.");
            return;
        }
        clientDao.delete(selected.getId());
        refreshTable();
        clearForm();
        showAlert(Alert.AlertType.INFORMATION, "Client supprime.");
    }

    @FXML
    private void onReset() {
        clearForm();
        searchField.clear();
        refreshTable();
    }

    private void refreshTable() {
        clients.setAll(clientDao.search(searchField.getText()));
    }

    private void fillForm(Client c) {
        if (c == null) {
            return;
        }
        nomField.setText(c.getNom());
        prenomField.setText(c.getPrenom());
        cinField.setText(c.getCin());
        emailField.setText(c.getEmail());
        telephoneField.setText(c.getTelephone());
        adresseField.setText(c.getAdresse());
        permisField.setText(c.getNumeroPermis());
        expirationPicker.setValue(c.getExpirationPermis());
    }

    private Client buildFromForm() {
        Client c = new Client();
        c.setNom(nomField.getText());
        c.setPrenom(prenomField.getText());
        c.setCin(cinField.getText());
        c.setEmail(emailField.getText());
        c.setTelephone(telephoneField.getText());
        c.setAdresse(adresseField.getText());
        c.setNumeroPermis(permisField.getText());
        c.setExpirationPermis(expirationPicker.getValue());
        return c;
    }

    private boolean validateForm(Integer excludeId) {
        if (ValidationUtil.isBlank(nomField.getText()) || ValidationUtil.isBlank(prenomField.getText()) ||
                ValidationUtil.isBlank(cinField.getText()) || ValidationUtil.isBlank(emailField.getText()) ||
                ValidationUtil.isBlank(telephoneField.getText()) || ValidationUtil.isBlank(adresseField.getText()) ||
                ValidationUtil.isBlank(permisField.getText()) || expirationPicker.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Tous les champs sont obligatoires.");
            return false;
        }
        if (!ValidationUtil.isEmailValid(emailField.getText())) {
            showAlert(Alert.AlertType.ERROR, "Email invalide.");
            return false;
        }
        if (!ValidationUtil.isPhoneValid(telephoneField.getText())) {
            showAlert(Alert.AlertType.ERROR, "Telephone invalide.");
            return false;
        }
        if (!ValidationUtil.isLicenseValid(expirationPicker.getValue())) {
            showAlert(Alert.AlertType.ERROR, "Permis expire.");
            return false;
        }
        if (!clientDao.isCinUnique(cinField.getText(), excludeId)) {
            showAlert(Alert.AlertType.ERROR, "CIN deja utilise.");
            return false;
        }
        return true;
    }

    private void clearForm() {
        nomField.clear();
        prenomField.clear();
        cinField.clear();
        emailField.clear();
        telephoneField.clear();
        adresseField.clear();
        permisField.clear();
        expirationPicker.setValue(null);
        tableView.getSelectionModel().clearSelection();
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle("Clients");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
