package com.project.projet.controller;

import com.project.projet.Dao.StatsDao;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.util.Map;

public class StatsController {

    @FXML
    private Label totalVehiculesLabel;

    @FXML
    private Label disponiblesLabel;

    @FXML
    private Label totalReservationsLabel;

    @FXML
    private Label revenueLabel;

    @FXML
    private Label topVehiculeLabel;

    @FXML
    private Label statusLabel;

    private final StatsDao statsDao = new StatsDao();

    @FXML
    private void initialize() {
        refresh();
    }

    @FXML
    private void onRefresh() {
        refresh();
    }

    private void refresh() {
        totalVehiculesLabel.setText(String.valueOf(statsDao.countVehicules()));
        disponiblesLabel.setText(String.valueOf(statsDao.countVehiculesDisponibles()));
        totalReservationsLabel.setText(String.valueOf(statsDao.countReservations()));
        revenueLabel.setText(String.format("%.2f", statsDao.totalRevenue()));
        topVehiculeLabel.setText(statsDao.mostRentedVehicle());

        Map<String, Integer> byStatus = statsDao.reservationsByStatus();
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, Integer> entry : byStatus.entrySet()) {
            if (builder.length() > 0) {
                builder.append(" | ");
            }
            builder.append(entry.getKey()).append(": ").append(entry.getValue());
        }
        statusLabel.setText(builder.length() > 0 ? builder.toString() : "-");
    }
}
