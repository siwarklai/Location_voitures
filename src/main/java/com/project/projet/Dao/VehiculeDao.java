package com.project.projet.Dao;

import com.project.projet.Model.Vehicule;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class VehiculeDao {

    public List<Vehicule> findAll() {
        List<Vehicule> result = new ArrayList<>();
        String sql = "SELECT * FROM vehicules ORDER BY id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                result.add(map(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public List<Vehicule> search(String keyword, String status) {
        if ((keyword == null || keyword.isBlank()) && (status == null || status.isBlank())) {
            return findAll();
        }
        List<Vehicule> result = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM vehicules WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (keyword != null && !keyword.isBlank()) {
            sql.append(" AND (marque LIKE ? OR modele LIKE ? OR immatriculation LIKE ? OR categorie LIKE ? OR statut LIKE ?)");
            String like = "%" + keyword + "%";
            params.add(like);
            params.add(like);
            params.add(like);
            params.add(like);
            params.add(like);
        }
        if (status != null && !status.isBlank()) {
            sql.append(" AND statut = ?");
            params.add(status);
        }
        sql.append(" ORDER BY id DESC");

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

    public boolean isImmatriculationUnique(String immatriculation, Integer excludeId) {
        String sql = "SELECT COUNT(*) FROM vehicules WHERE immatriculation = ?";
        if (excludeId != null) {
            sql += " AND id <> ?";
        }
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, immatriculation);
            if (excludeId != null) {
                ps.setInt(2, excludeId);
            }
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) == 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void insert(Vehicule vehicule) {
        String sql = "INSERT INTO vehicules (marque, modele, immatriculation, categorie, carburant, boite_vitesse, nombre_places, prix_jour, statut) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            fillVehiculeParams(ps, vehicule);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(Vehicule vehicule) {
        String sql = "UPDATE vehicules SET marque=?, modele=?, immatriculation=?, categorie=?, carburant=?, boite_vitesse=?, nombre_places=?, prix_jour=?, statut=? " +
                "WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            fillVehiculeParams(ps, vehicule);
            ps.setInt(10, vehicule.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM vehicules WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateStatus(int id, String status) {
        String sql = "UPDATE vehicules SET statut = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void fillVehiculeParams(PreparedStatement ps, Vehicule vehicule) throws SQLException {
        ps.setString(1, vehicule.getMarque());
        ps.setString(2, vehicule.getModele());
        ps.setString(3, vehicule.getImmatriculation());
        ps.setString(4, vehicule.getCategorie());
        ps.setString(5, vehicule.getCarburant());
        ps.setString(6, vehicule.getBoiteVitesse());
        ps.setInt(7, vehicule.getNombrePlaces());
        ps.setDouble(8, vehicule.getPrixParJour());
        ps.setString(9, vehicule.getStatut());
    }

    private Vehicule map(ResultSet rs) throws SQLException {
        Vehicule v = new Vehicule();
        v.setId(rs.getInt("id"));
        v.setMarque(rs.getString("marque"));
        v.setModele(rs.getString("modele"));
        v.setImmatriculation(rs.getString("immatriculation"));
        v.setCategorie(rs.getString("categorie"));
        v.setCarburant(rs.getString("carburant"));
        v.setBoiteVitesse(rs.getString("boite_vitesse"));
        v.setNombrePlaces(rs.getInt("nombre_places"));
        v.setPrixParJour(rs.getDouble("prix_jour"));
        v.setStatut(rs.getString("statut"));
        return v;
    }
}
