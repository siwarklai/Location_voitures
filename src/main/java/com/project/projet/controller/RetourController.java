package com.project.projet.controller;

import com.project.projet.Dao.ReservationDao;
import com.project.projet.Dao.RetourVehiculeDao;
import com.project.projet.Dao.VehiculeDao;
import com.project.projet.Model.Reservation;
import com.project.projet.Model.RetourVehicule;
import com.project.projet.util.ValidationUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;

public class RetourController {

    @FXML
    private ComboBox<Reservation> reservationCombo;

    @FXML
    private DatePicker dateRetourPicker;

    @FXML
    private ComboBox<String> etatCombo;

    @FXML
    private TextField fraisField;

    @FXML
    private TextArea remarqueArea;

    @FXML
    private TableView<RetourVehicule> tableView;

    @FXML
    private TableColumn<RetourVehicule, String> reservationCol;

    @FXML
    private TableColumn<RetourVehicule, LocalDate> dateCol;

    @FXML
    private TableColumn<RetourVehicule, String> etatCol;

    @FXML
    private TableColumn<RetourVehicule, Double> fraisCol;

    private final ReservationDao reservationDao = new ReservationDao();
    private final RetourVehiculeDao retourDao = new RetourVehiculeDao();
    private final VehiculeDao vehiculeDao = new VehiculeDao();
    private final ObservableList<RetourVehicule> retours = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        reservationCol.setCellValueFactory(data -> new SimpleStringProperty("#" + data.getValue().getReservation().getId()));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("dateRetour"));
        etatCol.setCellValueFactory(new PropertyValueFactory<>("etatVehicule"));
        fraisCol.setCellValueFactory(new PropertyValueFactory<>("fraisSupplementaires"));

        tableView.setItems(retours);

        etatCombo.setItems(FXCollections.observableArrayList("OK", "Maintenance"));
        reservationCombo.setItems(FXCollections.observableArrayList(reservationDao.findAll()));

        refreshTable();
    }

    @FXML
    private void onSave() {
        if (reservationCombo.getValue() == null || dateRetourPicker.getValue() == null || etatCombo.getValue() == null ||
                ValidationUtil.isBlank(fraisField.getText())) {
            showAlert(Alert.AlertType.ERROR, "Tous les champs sont obligatoires.");
            return;
        }
        double frais;
        try {
            frais = Double.parseDouble(fraisField.getText());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Frais invalides.");
            return;
        }
        if (frais < 0) {
            showAlert(Alert.AlertType.ERROR, "Frais invalides.");
            return;
        }

        RetourVehicule retour = new RetourVehicule();
        retour.setReservation(reservationCombo.getValue());
        retour.setDateRetour(dateRetourPicker.getValue());
        retour.setEtatVehicule(etatCombo.getValue());
        retour.setFraisSupplementaires(frais);
        retour.setRemarque(remarqueArea.getText());
        retourDao.insert(retour);

        reservationDao.updateStatus(retour.getReservation().getId(), "Terminee");
        if (etatCombo.getValue().equalsIgnoreCase("OK")) {
            vehiculeDao.updateStatus(retour.getReservation().getVehicule().getId(), "Disponible");
        } else {
            vehiculeDao.updateStatus(retour.getReservation().getVehicule().getId(), "Maintenance");
        }

        refreshTable();
        clearForm();
        showAlert(Alert.AlertType.INFORMATION, "Retour enregistre.");
    }

    private void refreshTable() {
        retours.setAll(retourDao.findAll(reservationDao));
    }

    private void clearForm() {
        reservationCombo.setValue(null);
        dateRetourPicker.setValue(null);
        etatCombo.setValue(null);
        fraisField.clear();
        remarqueArea.clear();
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle("Retours");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
