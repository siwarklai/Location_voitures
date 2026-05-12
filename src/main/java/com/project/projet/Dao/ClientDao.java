package com.project.projet.Dao;

import com.project.projet.Model.Client;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ClientDao {

    public List<Client> findAll() {
        List<Client> result = new ArrayList<>();
        String sql = "SELECT * FROM clients ORDER BY id DESC";
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

    public List<Client> search(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return findAll();
        }
        List<Client> result = new ArrayList<>();
        String sql = "SELECT * FROM clients WHERE nom LIKE ? OR prenom LIKE ? OR cin LIKE ? OR telephone LIKE ? OR numero_permis LIKE ? ORDER BY id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String like = "%" + keyword + "%";
            ps.setString(1, like);
            ps.setString(2, like);
            ps.setString(3, like);
            ps.setString(4, like);
            ps.setString(5, like);
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

    public boolean isCinUnique(String cin, Integer excludeId) {
        String sql = "SELECT COUNT(*) FROM clients WHERE cin = ?";
        if (excludeId != null) {
            sql += " AND id <> ?";
        }
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cin);
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

    public void insert(Client client) {
        String sql = "INSERT INTO clients (nom, prenom, cin, email, telephone, adresse, numero_permis, expiration_permis) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            fillClientParams(ps, client);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(Client client) {
        String sql = "UPDATE clients SET nom=?, prenom=?, cin=?, email=?, telephone=?, adresse=?, numero_permis=?, expiration_permis=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            fillClientParams(ps, client);
            ps.setInt(9, client.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM clients WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void fillClientParams(PreparedStatement ps, Client client) throws SQLException {
        ps.setString(1, client.getNom());
        ps.setString(2, client.getPrenom());
        ps.setString(3, client.getCin());
        ps.setString(4, client.getEmail());
        ps.setString(5, client.getTelephone());
        ps.setString(6, client.getAdresse());
        ps.setString(7, client.getNumeroPermis());
        ps.setDate(8, Date.valueOf(client.getExpirationPermis()));
    }

    private Client map(ResultSet rs) throws SQLException {
        Client c = new Client();
        c.setId(rs.getInt("id"));
        c.setNom(rs.getString("nom"));
        c.setPrenom(rs.getString("prenom"));
        c.setCin(rs.getString("cin"));
        c.setEmail(rs.getString("email"));
        c.setTelephone(rs.getString("telephone"));
        c.setAdresse(rs.getString("adresse"));
        c.setNumeroPermis(rs.getString("numero_permis"));
        c.setExpirationPermis(rs.getDate("expiration_permis").toLocalDate());
        return c;
    }
}
