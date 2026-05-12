package com.project.projet.Dao;

import com.project.projet.Model.Reservation;
import com.project.projet.Model.RetourVehicule;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RetourVehiculeDao {

    public void insert(RetourVehicule retour) {
        String sql = "INSERT INTO retours (reservation_id, date_retour, etat_vehicule, frais_supplementaires, remarque) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, retour.getReservation().getId());
            ps.setDate(2, Date.valueOf(retour.getDateRetour()));
            ps.setString(3, retour.getEtatVehicule());
            ps.setDouble(4, retour.getFraisSupplementaires());
            ps.setString(5, retour.getRemarque());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<RetourVehicule> findAll(ReservationDao reservationDao) {
        List<RetourVehicule> result = new ArrayList<>();
        String sql = "SELECT id, reservation_id, date_retour, etat_vehicule, frais_supplementaires, remarque FROM retours ORDER BY id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Reservation> reservations = reservationDao.findAll();
            while (rs.next()) {
                RetourVehicule retour = new RetourVehicule();
                retour.setId(rs.getInt("id"));
                int reservationId = rs.getInt("reservation_id");
                for (Reservation reservation : reservations) {
                    if (reservation.getId() == reservationId) {
                        retour.setReservation(reservation);
                        break;
                    }
                }
                retour.setDateRetour(rs.getDate("date_retour").toLocalDate());
                retour.setEtatVehicule(rs.getString("etat_vehicule"));
                retour.setFraisSupplementaires(rs.getDouble("frais_supplementaires"));
                retour.setRemarque(rs.getString("remarque"));
                result.add(retour);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
}
