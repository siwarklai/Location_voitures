package com.project.projet.controller;

import com.project.projet.Dao.PaiementDao;
import com.project.projet.Dao.ReservationDao;
import com.project.projet.Model.Paiement;
import com.project.projet.Model.Reservation;
import com.project.projet.util.ValidationUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class PaiementController {

    @FXML
    private ComboBox<Reservation> reservationCombo;

    @FXML
    private TextField totalField;

    @FXML
    private TextField paidField;

    @FXML
    private Label remainingLabel;

    @FXML
    private ComboBox<String> modeCombo;

    @FXML
    private Label statusLabel;

    @FXML
    private TableView<Paiement> tableView;

    @FXML
    private TableColumn<Paiement, String> reservationCol;

    @FXML
    private TableColumn<Paiement, Double> totalCol;

    @FXML
    private TableColumn<Paiement, Double> paidCol;

    @FXML
    private TableColumn<Paiement, Double> remainingCol;

    @FXML
    private TableColumn<Paiement, String> modeCol;

    @FXML
    private TableColumn<Paiement, String> statusCol;

    private final ReservationDao reservationDao = new ReservationDao();
    private final PaiementDao paiementDao = new PaiementDao();

    private final ObservableList<Paiement> paiements = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        reservationCol.setCellValueFactory(data -> new SimpleStringProperty("#" + data.getValue().getReservation().getId()));
        totalCol.setCellValueFactory(new PropertyValueFactory<>("montantTotal"));
        paidCol.setCellValueFactory(new PropertyValueFactory<>("montantPaye"));
        remainingCol.setCellValueFactory(new PropertyValueFactory<>("resteAPayer"));
        modeCol.setCellValueFactory(new PropertyValueFactory<>("modePaiement"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("statutPaiement"));

        tableView.setItems(paiements);

        modeCombo.setItems(FXCollections.observableArrayList("Especes", "Carte", "Virement", "Cheque"));
        reservationCombo.setItems(FXCollections.observableArrayList(reservationDao.findAll()));

        reservationCombo.valueProperty().addListener((obs, o, n) -> updateTotalFromReservation());
        paidField.textProperty().addListener((obs, o, n) -> recalcRemaining());

        refreshTable();
    }

    @FXML
    private void onSave() {
        Reservation reservation = reservationCombo.getValue();
        if (reservation == null || ValidationUtil.isBlank(paidField.getText()) || modeCombo.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Tous les champs sont obligatoires.");
            return;
        }
        double total = reservation.getPrixTotal();
        double paid;
        try {
            paid = Double.parseDouble(paidField.getText());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Montant invalide.");
            return;
        }
        if (paid < 0 || paid > total) {
            showAlert(Alert.AlertType.ERROR, "Montant paye invalide.");
            return;
        }

        Paiement paiement = new Paiement();
        paiement.setReservation(reservation);
        paiement.setMontantTotal(total);
        paiement.setMontantPaye(paid);
        paiement.setResteAPayer(total - paid);
        paiement.setModePaiement(modeCombo.getValue());
        paiement.setStatutPaiement(getStatus(total, paid));

        paiementDao.saveOrUpdate(paiement);
        refreshTable();
        clearForm();
        showAlert(Alert.AlertType.INFORMATION, "Paiement enregistre.");
    }

    private void refreshTable() {
        paiements.setAll(paiementDao.findAll(reservationDao));
    }

    private void updateTotalFromReservation() {
        Reservation reservation = reservationCombo.getValue();
        if (reservation != null) {
            totalField.setText(String.format("%.2f", reservation.getPrixTotal()));
        }
        recalcRemaining();
    }

    private void recalcRemaining() {
        Reservation reservation = reservationCombo.getValue();
        if (reservation == null) {
            remainingLabel.setText("0.00");
            statusLabel.setText("-");
            return;
        }
        double total = reservation.getPrixTotal();
        double paid = 0.0;
        try {
            paid = Double.parseDouble(paidField.getText());
        } catch (NumberFormatException ignored) {
        }
        double remaining = total - paid;
        remainingLabel.setText(String.format("%.2f", remaining));
        statusLabel.setText(getStatus(total, paid));
    }

    private String getStatus(double total, double paid) {
        if (paid <= 0) {
            return "Non paye";
        }
        if (paid < total) {
            return "Partiel";
        }
        return "Paye";
    }

    private void clearForm() {
        reservationCombo.setValue(null);
        totalField.clear();
        paidField.clear();
        remainingLabel.setText("0.00");
        statusLabel.setText("-");
        modeCombo.setValue(null);
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle("Paiements");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
