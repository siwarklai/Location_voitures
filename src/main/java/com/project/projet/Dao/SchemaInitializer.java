package com.project.projet.Dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SchemaInitializer {

    public static void initialize() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                return;
            }
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(
                        "CREATE TABLE IF NOT EXISTS utilisateurs (" +
                                "id INT AUTO_INCREMENT PRIMARY KEY," +
                                "username VARCHAR(50) UNIQUE NOT NULL," +
                                "password VARCHAR(100) NOT NULL," +
                                "role VARCHAR(30) NOT NULL" +
                                ")"
                );

                stmt.executeUpdate(
                        "CREATE TABLE IF NOT EXISTS vehicules (" +
                                "id INT AUTO_INCREMENT PRIMARY KEY," +
                                "marque VARCHAR(50) NOT NULL," +
                                "modele VARCHAR(50) NOT NULL," +
                                "immatriculation VARCHAR(30) UNIQUE NOT NULL," +
                                "categorie VARCHAR(30) NOT NULL," +
                                "carburant VARCHAR(30) NOT NULL," +
                                "boite_vitesse VARCHAR(20) NOT NULL," +
                                "nombre_places INT NOT NULL," +
                                "prix_jour DOUBLE NOT NULL," +
                                "statut VARCHAR(20) NOT NULL" +
                                ")"
                );

                stmt.executeUpdate(
                        "CREATE TABLE IF NOT EXISTS clients (" +
                                "id INT AUTO_INCREMENT PRIMARY KEY," +
                                "nom VARCHAR(50) NOT NULL," +
                                "prenom VARCHAR(50) NOT NULL," +
                                "cin VARCHAR(30) UNIQUE NOT NULL," +
                                "email VARCHAR(100) NOT NULL," +
                                "telephone VARCHAR(20) NOT NULL," +
                                "adresse VARCHAR(100) NOT NULL," +
                                "numero_permis VARCHAR(30) NOT NULL," +
                                "expiration_permis DATE NOT NULL" +
                                ")"
                );

                stmt.executeUpdate(
                        "CREATE TABLE IF NOT EXISTS reservations (" +
                                "id INT AUTO_INCREMENT PRIMARY KEY," +
                                "client_id INT NOT NULL," +
                                "vehicule_id INT NOT NULL," +
                                "date_debut DATE NOT NULL," +
                                "date_fin DATE NOT NULL," +
                                "nombre_jours INT NOT NULL," +
                                "prix_total DOUBLE NOT NULL," +
                                "prix_par_jour DOUBLE NOT NULL," +
                                "options VARCHAR(200)," +
                                "statut VARCHAR(30) NOT NULL," +
                                "FOREIGN KEY (client_id) REFERENCES clients(id)," +
                                "FOREIGN KEY (vehicule_id) REFERENCES vehicules(id)" +
                                ")"
                );

                stmt.executeUpdate(
                        "CREATE TABLE IF NOT EXISTS paiements (" +
                                "id INT AUTO_INCREMENT PRIMARY KEY," +
                                "reservation_id INT UNIQUE NOT NULL," +
                                "montant_total DOUBLE NOT NULL," +
                                "montant_paye DOUBLE NOT NULL," +
                                "reste_a_payer DOUBLE NOT NULL," +
                                "mode_paiement VARCHAR(30) NOT NULL," +
                                "statut_paiement VARCHAR(30) NOT NULL," +
                                "FOREIGN KEY (reservation_id) REFERENCES reservations(id)" +
                                ")"
                );

                stmt.executeUpdate(
                        "CREATE TABLE IF NOT EXISTS retours (" +
                                "id INT AUTO_INCREMENT PRIMARY KEY," +
                                "reservation_id INT NOT NULL," +
                                "date_retour DATE NOT NULL," +
                                "etat_vehicule VARCHAR(50) NOT NULL," +
                                "frais_supplementaires DOUBLE NOT NULL," +
                                "remarque VARCHAR(200)," +
                                "FOREIGN KEY (reservation_id) REFERENCES reservations(id)" +
                                ")"
                );
            }

            ensureDefaultAdmin(conn);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void ensureDefaultAdmin(Connection conn) throws SQLException {
        String countSql = "SELECT COUNT(*) FROM utilisateurs";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(countSql)) {
            if (rs.next() && rs.getInt(1) == 0) {
                String insertSql = "INSERT INTO utilisateurs (username, password, role) VALUES (?, ?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                    ps.setString(1, "admin");
                    ps.setString(2, "admin123");
                    ps.setString(3, "Administrateur");
                    ps.executeUpdate();
                }
            }
        }
    }
}
