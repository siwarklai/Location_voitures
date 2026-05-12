package com.project.projet.Dao;

import com.project.projet.Model.Client;
import com.project.projet.Model.Reservation;
import com.project.projet.Model.Vehicule;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReservationDao {

    public List<Reservation> findAll() {
        List<Reservation> result = new ArrayList<>();
        String sql = baseQuery() + " ORDER BY r.id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(map(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public List<Reservation> search(String keyword, String status) {
        if ((keyword == null || keyword.isBlank()) && (status == null || status.isBlank())) {
            return findAll();
        }
        List<Reservation> result = new ArrayList<>();
        StringBuilder sql = new StringBuilder(baseQuery() + " WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (keyword != null && !keyword.isBlank()) {
            sql.append(" AND (c.nom LIKE ? OR c.prenom LIKE ? OR v.marque LIKE ? OR v.modele LIKE ? OR v.immatriculation LIKE ?)");
            String like = "%" + keyword + "%";
            params.add(like);
            params.add(like);
            params.add(like);
            params.add(like);
            params.add(like);
        }
        if (status != null && !status.isBlank()) {
            sql.append(" AND r.statut = ?");
            params.add(status);
        }
        sql.append(" ORDER BY r.id DESC");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(map(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public boolean isVehiculeAvailable(int vehiculeId, java.time.LocalDate start, java.time.LocalDate end, Integer excludeReservationId) {
        String sql = "SELECT COUNT(*) FROM reservations WHERE vehicule_id = ? AND statut IN ('En attente', 'Confirmee') " +
                "AND NOT (date_fin < ? OR date_debut > ?)";
        if (excludeReservationId != null) {
            sql += " AND id <> ?";
        }
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, vehiculeId);
            ps.setDate(2, Date.valueOf(start));
            ps.setDate(3, Date.valueOf(end));
            if (excludeReservationId != null) {
                ps.setInt(4, excludeReservationId);
            }
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) == 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void insert(Reservation reservation) {
        String sql = "INSERT INTO reservations (client_id, vehicule_id, date_debut, date_fin, nombre_jours, prix_total, prix_par_jour, options, statut) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            fillReservationParams(ps, reservation);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(Reservation reservation) {
        String sql = "UPDATE reservations SET client_id=?, vehicule_id=?, date_debut=?, date_fin=?, nombre_jours=?, prix_total=?, prix_par_jour=?, options=?, statut=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            fillReservationParams(ps, reservation);
            ps.setInt(10, reservation.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateStatus(int reservationId, String status) {
        String sql = "UPDATE reservations SET statut=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, reservationId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM reservations WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void fillReservationParams(PreparedStatement ps, Reservation reservation) throws SQLException {
        ps.setInt(1, reservation.getClient().getId());
        ps.setInt(2, reservation.getVehicule().getId());
        ps.setDate(3, Date.valueOf(reservation.getDateDebut()));
        ps.setDate(4, Date.valueOf(reservation.getDateFin()));
        ps.setInt(5, reservation.getNombreJours());
        ps.setDouble(6, reservation.getPrixTotal());
        ps.setDouble(7, reservation.getVehicule().getPrixParJour());
        ps.setString(8, reservation.getOptions());
        ps.setString(9, reservation.getStatut());
    }

    private String baseQuery() {
        return "SELECT " +
                "r.id AS r_id, r.date_debut, r.date_fin, r.nombre_jours, r.prix_total, r.prix_par_jour, r.options, r.statut, " +
                "c.id AS c_id, c.nom AS c_nom, c.prenom AS c_prenom, c.cin AS c_cin, c.email AS c_email, c.telephone AS c_telephone, " +
                "c.adresse AS c_adresse, c.numero_permis AS c_numero_permis, c.expiration_permis AS c_expiration_permis, " +
                "v.id AS v_id, v.marque AS v_marque, v.modele AS v_modele, v.immatriculation AS v_immatriculation, v.categorie AS v_categorie, " +
                "v.carburant AS v_carburant, v.boite_vitesse AS v_boite_vitesse, v.nombre_places AS v_nombre_places, v.prix_jour AS v_prix_jour, v.statut AS v_statut " +
                "FROM reservations r " +
                "JOIN clients c ON r.client_id = c.id " +
                "JOIN vehicules v ON r.vehicule_id = v.id";
    }

    private Reservation map(ResultSet rs) throws SQLException {
        Client client = new Client();
        client.setId(rs.getInt("c_id"));
        client.setNom(rs.getString("c_nom"));
        client.setPrenom(rs.getString("c_prenom"));
        client.setCin(rs.getString("c_cin"));
        client.setEmail(rs.getString("c_email"));
        client.setTelephone(rs.getString("c_telephone"));
        client.setAdresse(rs.getString("c_adresse"));
        client.setNumeroPermis(rs.getString("c_numero_permis"));
        client.setExpirationPermis(rs.getDate("c_expiration_permis").toLocalDate());

        Vehicule vehicule = new Vehicule();
        vehicule.setId(rs.getInt("v_id"));
        vehicule.setMarque(rs.getString("v_marque"));
        vehicule.setModele(rs.getString("v_modele"));
        vehicule.setImmatriculation(rs.getString("v_immatriculation"));
        vehicule.setCategorie(rs.getString("v_categorie"));
        vehicule.setCarburant(rs.getString("v_carburant"));
        vehicule.setBoiteVitesse(rs.getString("v_boite_vitesse"));
        vehicule.setNombrePlaces(rs.getInt("v_nombre_places"));
        vehicule.setPrixParJour(rs.getDouble("v_prix_jour"));
        vehicule.setStatut(rs.getString("v_statut"));

        Reservation reservation = new Reservation();
        reservation.setId(rs.getInt("r_id"));
        reservation.setClient(client);
        reservation.setVehicule(vehicule);
        reservation.setDateDebut(rs.getDate("date_debut").toLocalDate());
        reservation.setDateFin(rs.getDate("date_fin").toLocalDate());
        reservation.setNombreJours(rs.getInt("nombre_jours"));
        reservation.setPrixTotal(rs.getDouble("prix_total"));
        reservation.setOptions(rs.getString("options"));
        reservation.setStatut(rs.getString("statut"));
        return reservation;
    }
}
