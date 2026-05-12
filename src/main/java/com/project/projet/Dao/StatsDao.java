package com.project.projet.Dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class StatsDao {

    public int countVehicules() {
        return singleInt("SELECT COUNT(*) FROM vehicules");
    }

    public int countVehiculesDisponibles() {
        return singleInt("SELECT COUNT(*) FROM vehicules WHERE statut = 'Disponible'");
    }

    public int countReservations() {
        return singleInt("SELECT COUNT(*) FROM reservations");
    }

    public double totalRevenue() {
        String sql = "SELECT COALESCE(SUM(montant_paye), 0) FROM paiements";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public String mostRentedVehicle() {
        String sql = "SELECT v.marque, v.modele, COUNT(*) AS total " +
                "FROM reservations r JOIN vehicules v ON r.vehicule_id = v.id " +
                "GROUP BY v.id, v.marque, v.modele ORDER BY total DESC LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getString("marque") + " " + rs.getString("modele");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "-";
    }

    public Map<String, Integer> reservationsByStatus() {
        Map<String, Integer> result = new HashMap<>();
        String sql = "SELECT statut, COUNT(*) FROM reservations GROUP BY statut";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.put(rs.getString(1), rs.getInt(2));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    private int singleInt(String sql) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
