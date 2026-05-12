package com.project.projet.controller;

import com.project.projet.Dao.ClientDao;
import com.project.projet.Dao.ReservationDao;
import com.project.projet.Dao.VehiculeDao;
import com.project.projet.Model.Client;
import com.project.projet.Model.Reservation;
import com.project.projet.Model.Vehicule;
import com.project.projet.util.PriceUtil;
import com.project.projet.util.ValidationUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReservationController {

    private static final double ASSURANCE_PRICE = 50.0;
    private static final double GPS_PRICE = 20.0;
    private static final double BEBE_PRICE = 15.0;
    private static final double CONDUCTEUR_PRICE = 30.0;

    @FXML
    private ComboBox<Client> clientCombo;

    @FXML
    private ComboBox<Vehicule> vehiculeCombo;

    @FXML
    private DatePicker dateDebutPicker;

    @FXML
    private DatePicker dateFinPicker;

    @FXML
    private CheckBox assuranceCheck;

    @FXML
    private CheckBox gpsCheck;

    @FXML
    private CheckBox bebeCheck;

    @FXML
    private CheckBox conducteurCheck;

    @FXML
    private Label daysLabel;

    @FXML
    private Label prixJourLabel;

    @FXML
    private Label totalLabel;

    @FXML
    private ComboBox<String> statusCombo;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> filterStatusCombo;

    @FXML
    private TableView<Reservation> tableView;

    @FXML
    private TableColumn<Reservation, String> clientCol;

    @FXML
    private TableColumn<Reservation, String> vehiculeCol;

    @FXML
    private TableColumn<Reservation, LocalDate> debutCol;

    @FXML
    private TableColumn<Reservation, LocalDate> finCol;

    @FXML
    private TableColumn<Reservation, Integer> joursCol;

    @FXML
    private TableColumn<Reservation, Double> totalCol;

    @FXML
    private TableColumn<Reservation, String> statutCol;

    private final ClientDao clientDao = new ClientDao();
    private final VehiculeDao vehiculeDao = new VehiculeDao();
    private final ReservationDao reservationDao = new ReservationDao();

    private final ObservableList<Reservation> reservations = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        clientCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getClient().toString()));
        vehiculeCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getVehicule().toString()));
        debutCol.setCellValueFactory(new PropertyValueFactory<>("dateDebut"));
        finCol.setCellValueFactory(new PropertyValueFactory<>("dateFin"));
        joursCol.setCellValueFactory(new PropertyValueFactory<>("nombreJours"));
        totalCol.setCellValueFactory(new PropertyValueFactory<>("prixTotal"));
        statutCol.setCellValueFactory(new PropertyValueFactory<>("statut"));

        tableView.setItems(reservations);
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> fillForm(newV));

        statusCombo.setItems(FXCollections.observableArrayList("En attente", "Confirmee", "Annulee", "Terminee"));
        filterStatusCombo.setItems(FXCollections.observableArrayList("", "En attente", "Confirmee", "Annulee", "Terminee"));

        loadCombos();
        setupListeners();
        refreshTable();
    }

    private void loadCombos() {
        clientCombo.setItems(FXCollections.observableArrayList(clientDao.findAll()));
        vehiculeCombo.setItems(FXCollections.observableArrayList(vehiculeDao.findAll()));
    }

    private void setupListeners() {
        dateDebutPicker.valueProperty().addListener((obs, o, n) -> recalcPrice());
        dateFinPicker.valueProperty().addListener((obs, o, n) -> recalcPrice());
        vehiculeCombo.valueProperty().addListener((obs, o, n) -> recalcPrice());
        assuranceCheck.selectedProperty().addListener((obs, o, n) -> recalcPrice());
        gpsCheck.selectedProperty().addListener((obs, o, n) -> recalcPrice());
        bebeCheck.selectedProperty().addListener((obs, o, n) -> recalcPrice());
        conducteurCheck.selectedProperty().addListener((obs, o, n) -> recalcPrice());

        searchField.textProperty().addListener((obs, o, n) -> refreshTable());
        filterStatusCombo.valueProperty().addListener((obs, o, n) -> refreshTable());
    }

    @FXML
    private void onAdd() {
        if (!validateForm(null)) {
            return;
        }
        Reservation reservation = buildReservation();
        reservationDao.insert(reservation);
        updateVehiculeStatus(reservation);
        refreshTable();
        clearForm();
        showAlert(Alert.AlertType.INFORMATION, "Reservation ajoutee.");
    }

    @FXML
    private void onUpdate() {
        Reservation selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Selectionnez une reservation.");
            return;
        }
        if (!validateForm(selected.getId())) {
            return;
        }
        Reservation reservation = buildReservation();
        reservation.setId(selected.getId());
        reservationDao.update(reservation);
        updateVehiculeStatus(reservation);
        refreshTable();
        clearForm();
        showAlert(Alert.AlertType.INFORMATION, "Reservation modifiee.");
    }

    @FXML
    private void onDelete() {
        Reservation selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Selectionnez une reservation.");
            return;
        }
        reservationDao.delete(selected.getId());
        vehiculeDao.updateStatus(selected.getVehicule().getId(), "Disponible");
        refreshTable();
        clearForm();
        showAlert(Alert.AlertType.INFORMATION, "Reservation supprimee.");
    }

    @FXML
    private void onReset() {
        clearForm();
        searchField.clear();
        filterStatusCombo.setValue("");
        refreshTable();
    }

    private void refreshTable() {
        reservations.setAll(reservationDao.search(searchField.getText(), filterStatusCombo.getValue()));
    }

    private void recalcPrice() {
        Vehicule vehicule = vehiculeCombo.getValue();
        int days = PriceUtil.calculateDays(dateDebutPicker.getValue(), dateFinPicker.getValue());
        double prixJour = vehicule != null ? vehicule.getPrixParJour() : 0.0;
        double optionsTotal = getOptionsTotal();
        double total = PriceUtil.calculateTotal(days, prixJour, optionsTotal);

        daysLabel.setText(String.valueOf(days));
        prixJourLabel.setText(String.format("%.2f", prixJour));
        totalLabel.setText(String.format("%.2f", total));
    }

    private double getOptionsTotal() {
        double total = 0.0;
        if (assuranceCheck.isSelected()) {
            total += ASSURANCE_PRICE;
        }
        if (gpsCheck.isSelected()) {
            total += GPS_PRICE;
        }
        if (bebeCheck.isSelected()) {
            total += BEBE_PRICE;
        }
        if (conducteurCheck.isSelected()) {
            total += CONDUCTEUR_PRICE;
        }
        return total;
    }

    private Reservation buildReservation() {
        Reservation reservation = new Reservation();
        reservation.setClient(clientCombo.getValue());
        reservation.setVehicule(vehiculeCombo.getValue());
        reservation.setDateDebut(dateDebutPicker.getValue());
        reservation.setDateFin(dateFinPicker.getValue());
        reservation.setNombreJours(PriceUtil.calculateDays(dateDebutPicker.getValue(), dateFinPicker.getValue()));
        reservation.setOptions(String.join(", ", getOptionsSelected()));
        reservation.setStatut(statusCombo.getValue());
        reservation.setPrixTotal(Double.parseDouble(totalLabel.getText()));
        return reservation;
    }

    private List<String> getOptionsSelected() {
        List<String> options = new ArrayList<>();
        if (assuranceCheck.isSelected()) {
            options.add("Assurance");
        }
        if (gpsCheck.isSelected()) {
            options.add("GPS");
        }
        if (bebeCheck.isSelected()) {
            options.add("Siege bebe");
        }
        if (conducteurCheck.isSelected()) {
            options.add("Conducteur sup");
        }
        return options;
    }

    private boolean validateForm(Integer excludeId) {
        if (clientCombo.getValue() == null || vehiculeCombo.getValue() == null ||
                dateDebutPicker.getValue() == null || dateFinPicker.getValue() == null ||
                statusCombo.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Tous les champs sont obligatoires.");
            return false;
        }
        if (!ValidationUtil.isDateRangeValid(dateDebutPicker.getValue(), dateFinPicker.getValue())) {
            showAlert(Alert.AlertType.ERROR, "Date fin invalide.");
            return false;
        }
        if (!ValidationUtil.isLicenseValid(clientCombo.getValue().getExpirationPermis())) {
            showAlert(Alert.AlertType.ERROR, "Permis du client expire.");
            return false;
        }
        Vehicule vehicule = vehiculeCombo.getValue();
        if (vehicule.getStatut().equalsIgnoreCase("Maintenance")) {
            showAlert(Alert.AlertType.ERROR, "Vehicule en maintenance.");
            return false;
        }
        if (!reservationDao.isVehiculeAvailable(vehicule.getId(), dateDebutPicker.getValue(), dateFinPicker.getValue(), excludeId)) {
            showAlert(Alert.AlertType.ERROR, "Vehicule indisponible sur la periode.");
            return false;
        }
        return true;
    }

    private void updateVehiculeStatus(Reservation reservation) {
        String status = reservation.getStatut();
        if (status.equalsIgnoreCase("Confirmee") || status.equalsIgnoreCase("En attente")) {
            vehiculeDao.updateStatus(reservation.getVehicule().getId(), "Reserve");
        } else if (status.equalsIgnoreCase("Terminee") || status.equalsIgnoreCase("Annulee")) {
            vehiculeDao.updateStatus(reservation.getVehicule().getId(), "Disponible");
        }
    }

    private void fillForm(Reservation reservation) {
        if (reservation == null) {
            return;
        }
        clientCombo.setValue(reservation.getClient());
        vehiculeCombo.setValue(reservation.getVehicule());
        dateDebutPicker.setValue(reservation.getDateDebut());
        dateFinPicker.setValue(reservation.getDateFin());
        statusCombo.setValue(reservation.getStatut());

        assuranceCheck.setSelected(reservation.getOptions() != null && reservation.getOptions().contains("Assurance"));
        gpsCheck.setSelected(reservation.getOptions() != null && reservation.getOptions().contains("GPS"));
        bebeCheck.setSelected(reservation.getOptions() != null && reservation.getOptions().contains("Siege bebe"));
        conducteurCheck.setSelected(reservation.getOptions() != null && reservation.getOptions().contains("Conducteur sup"));

        recalcPrice();
    }

    private void clearForm() {
        clientCombo.setValue(null);
        vehiculeCombo.setValue(null);
        dateDebutPicker.setValue(null);
        dateFinPicker.setValue(null);
        assuranceCheck.setSelected(false);
        gpsCheck.setSelected(false);
        bebeCheck.setSelected(false);
        conducteurCheck.setSelected(false);
        statusCombo.setValue(null);
        daysLabel.setText("0");
        prixJourLabel.setText("0.00");
        totalLabel.setText("0.00");
        tableView.getSelectionModel().clearSelection();
        loadCombos();
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle("Reservations");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
