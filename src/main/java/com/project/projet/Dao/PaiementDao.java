package com.project.projet.Dao;

import com.project.projet.Model.Paiement;
import com.project.projet.Model.Reservation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PaiementDao {

    public List<Paiement> findAll(ReservationDao reservationDao) {
        List<Paiement> result = new ArrayList<>();
        String sql = "SELECT p.id, p.reservation_id, p.montant_total, p.montant_paye, p.reste_a_payer, p.mode_paiement, p.statut_paiement " +
                "FROM paiements p ORDER BY p.id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Reservation> reservations = reservationDao.findAll();
            while (rs.next()) {
                Paiement p = new Paiement();
                p.setId(rs.getInt("id"));
                int reservationId = rs.getInt("reservation_id");
                for (Reservation reservation : reservations) {
                    if (reservation.getId() == reservationId) {
                        p.setReservation(reservation);
                        break;
                    }
                }
                p.setMontantTotal(rs.getDouble("montant_total"));
                p.setMontantPaye(rs.getDouble("montant_paye"));
                p.setResteAPayer(rs.getDouble("reste_a_payer"));
                p.setModePaiement(rs.getString("mode_paiement"));
                p.setStatutPaiement(rs.getString("statut_paiement"));
                result.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public void saveOrUpdate(Paiement paiement) {
        Integer existingId = findIdByReservation(paiement.getReservation().getId());
        if (existingId == null) {
            insert(paiement);
        } else {
            update(existingId, paiement);
        }
    }

    private Integer findIdByReservation(int reservationId) {
        String sql = "SELECT id FROM paiements WHERE reservation_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, reservationId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void insert(Paiement paiement) {
        String sql = "INSERT INTO paiements (reservation_id, montant_total, montant_paye, reste_a_payer, mode_paiement, statut_paiement) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            fillParams(ps, paiement);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void update(int id, Paiement paiement) {
        String sql = "UPDATE paiements SET reservation_id=?, montant_total=?, montant_paye=?, reste_a_payer=?, mode_paiement=?, statut_paiement=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            fillParams(ps, paiement);
            ps.setInt(7, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void fillParams(PreparedStatement ps, Paiement paiement) throws SQLException {
        ps.setInt(1, paiement.getReservation().getId());
        ps.setDouble(2, paiement.getMontantTotal());
        ps.setDouble(3, paiement.getMontantPaye());
        ps.setDouble(4, paiement.getResteAPayer());
        ps.setString(5, paiement.getModePaiement());
        ps.setString(6, paiement.getStatutPaiement());
    }
}
