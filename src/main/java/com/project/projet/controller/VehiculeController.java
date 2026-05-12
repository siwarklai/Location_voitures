package com.project.projet.controller;

import com.project.projet.Dao.VehiculeDao;
import com.project.projet.Model.Vehicule;
import com.project.projet.util.ValidationUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class VehiculeController {

    @FXML
    private TextField marqueField;

    @FXML
    private TextField modeleField;

    @FXML
    private TextField immatriculationField;

    @FXML
    private ComboBox<String> categorieCombo;

    @FXML
    private ComboBox<String> carburantCombo;

    @FXML
    private ComboBox<String> boiteCombo;

    @FXML
    private TextField placesField;

    @FXML
    private TextField prixField;

    @FXML
    private ComboBox<String> statutCombo;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> filterStatutCombo;

    @FXML
    private TableView<Vehicule> tableView;

    @FXML
    private TableColumn<Vehicule, String> marqueCol;

    @FXML
    private TableColumn<Vehicule, String> modeleCol;

    @FXML
    private TableColumn<Vehicule, String> immatCol;

    @FXML
    private TableColumn<Vehicule, String> categorieCol;

    @FXML
    private TableColumn<Vehicule, String> carburantCol;

    @FXML
    private TableColumn<Vehicule, String> boiteCol;

    @FXML
    private TableColumn<Vehicule, Integer> placesCol;

    @FXML
    private TableColumn<Vehicule, Double> prixCol;

    @FXML
    private TableColumn<Vehicule, String> statutCol;

    private final VehiculeDao vehiculeDao = new VehiculeDao();
    private final ObservableList<Vehicule> vehicules = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        categorieCombo.setItems(FXCollections.observableArrayList("Citadine", "Berline", "SUV", "Utilitaire", "Luxe"));
        carburantCombo.setItems(FXCollections.observableArrayList("Essence", "Diesel", "Hybride", "Electrique"));
        boiteCombo.setItems(FXCollections.observableArrayList("Manuelle", "Automatique"));
        statutCombo.setItems(FXCollections.observableArrayList("Disponible", "Reserve", "Loue", "Maintenance"));
        filterStatutCombo.setItems(FXCollections.observableArrayList("", "Disponible", "Reserve", "Loue", "Maintenance"));

        marqueCol.setCellValueFactory(new PropertyValueFactory<>("marque"));
        modeleCol.setCellValueFactory(new PropertyValueFactory<>("modele"));
        immatCol.setCellValueFactory(new PropertyValueFactory<>("immatriculation"));
        categorieCol.setCellValueFactory(new PropertyValueFactory<>("categorie"));
        carburantCol.setCellValueFactory(new PropertyValueFactory<>("carburant"));
        boiteCol.setCellValueFactory(new PropertyValueFactory<>("boiteVitesse"));
        placesCol.setCellValueFactory(new PropertyValueFactory<>("nombrePlaces"));
        prixCol.setCellValueFactory(new PropertyValueFactory<>("prixParJour"));
        statutCol.setCellValueFactory(new PropertyValueFactory<>("statut"));

        tableView.setItems(vehicules);
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> fillForm(newV));

        searchField.textProperty().addListener((obs, oldV, newV) -> refreshTable());
        filterStatutCombo.valueProperty().addListener((obs, oldV, newV) -> refreshTable());

        refreshTable();
    }

    @FXML
    private void onAdd() {
        if (!validateForm(null)) {
            return;
        }
        Vehicule v = buildFromForm();
        vehiculeDao.insert(v);
        refreshTable();
        clearForm();
        showAlert(Alert.AlertType.INFORMATION, "Vehicule ajoute.");
    }

    @FXML
    private void onUpdate() {
        Vehicule selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Selectionnez un vehicule.");
            return;
        }
        if (!validateForm(selected.getId())) {
            return;
        }
        Vehicule v = buildFromForm();
        v.setId(selected.getId());
        vehiculeDao.update(v);
        refreshTable();
        clearForm();
        showAlert(Alert.AlertType.INFORMATION, "Vehicule modifie.");
    }

    @FXML
    private void onDelete() {
        Vehicule selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Selectionnez un vehicule.");
            return;
        }
        vehiculeDao.delete(selected.getId());
        refreshTable();
        clearForm();
        showAlert(Alert.AlertType.INFORMATION, "Vehicule supprime.");
    }

    @FXML
    private void onReset() {
        clearForm();
        searchField.clear();
        filterStatutCombo.setValue("");
        refreshTable();
    }

    private void refreshTable() {
        vehicules.setAll(vehiculeDao.search(searchField.getText(), filterStatutCombo.getValue()));
    }

    private void fillForm(Vehicule v) {
        if (v == null) {
            return;
        }
        marqueField.setText(v.getMarque());
        modeleField.setText(v.getModele());
        immatriculationField.setText(v.getImmatriculation());
        categorieCombo.setValue(v.getCategorie());
        carburantCombo.setValue(v.getCarburant());
        boiteCombo.setValue(v.getBoiteVitesse());
        placesField.setText(String.valueOf(v.getNombrePlaces()));
        prixField.setText(String.valueOf(v.getPrixParJour()));
        statutCombo.setValue(v.getStatut());
    }

    private Vehicule buildFromForm() {
        Vehicule v = new Vehicule();
        v.setMarque(marqueField.getText());
        v.setModele(modeleField.getText());
        v.setImmatriculation(immatriculationField.getText());
        v.setCategorie(categorieCombo.getValue());
        v.setCarburant(carburantCombo.getValue());
        v.setBoiteVitesse(boiteCombo.getValue());
        v.setNombrePlaces(Integer.parseInt(placesField.getText()));
        v.setPrixParJour(Double.parseDouble(prixField.getText()));
        v.setStatut(statutCombo.getValue());
        return v;
    }

    private boolean validateForm(Integer excludeId) {
        if (ValidationUtil.isBlank(marqueField.getText()) || ValidationUtil.isBlank(modeleField.getText()) ||
                ValidationUtil.isBlank(immatriculationField.getText()) || categorieCombo.getValue() == null ||
                carburantCombo.getValue() == null || boiteCombo.getValue() == null ||
                ValidationUtil.isBlank(placesField.getText()) || ValidationUtil.isBlank(prixField.getText()) ||
                statutCombo.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Tous les champs sont obligatoires.");
            return false;
        }

        int places;
        double prix;
        try {
            places = Integer.parseInt(placesField.getText());
            prix = Double.parseDouble(prixField.getText());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Places et prix doivent etre numeriques.");
            return false;
        }
        if (places <= 0 || !ValidationUtil.isPriceValid(prix)) {
            showAlert(Alert.AlertType.ERROR, "Places et prix doivent etre superieurs a 0.");
            return false;
        }
        if (!vehiculeDao.isImmatriculationUnique(immatriculationField.getText(), excludeId)) {
            showAlert(Alert.AlertType.ERROR, "Immatriculation deja utilisee.");
            return false;
        }
        return true;
    }

    private void clearForm() {
        marqueField.clear();
        modeleField.clear();
        immatriculationField.clear();
        categorieCombo.setValue(null);
        carburantCombo.setValue(null);
        boiteCombo.setValue(null);
        placesField.clear();
        prixField.clear();
        statutCombo.setValue(null);
        tableView.getSelectionModel().clearSelection();
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle("Vehicules");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
