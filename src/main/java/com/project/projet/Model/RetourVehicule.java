package com.project.projet.Model;
import java.time.LocalDate;

public class RetourVehicule {

    private int id;
    private Reservation reservation;
    private LocalDate dateRetour;
    private String etatVehicule;
    private double fraisSupplementaires;
    private String remarque;

    public RetourVehicule() {
    }

    public RetourVehicule(int id, Reservation reservation,
                          LocalDate dateRetour,
                          String etatVehicule,
                          double fraisSupplementaires,
                          String remarque) {

        this.id = id;
        this.reservation = reservation;
        this.dateRetour = dateRetour;
        this.etatVehicule = etatVehicule;
        this.fraisSupplementaires = fraisSupplementaires;
        this.remarque = remarque;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public LocalDate getDateRetour() {
        return dateRetour;
    }

    public void setDateRetour(LocalDate dateRetour) {
        this.dateRetour = dateRetour;
    }

    public String getEtatVehicule() {
        return etatVehicule;
    }

    public void setEtatVehicule(String etatVehicule) {
        this.etatVehicule = etatVehicule;
    }

    public double getFraisSupplementaires() {
        return fraisSupplementaires;
    }

    public void setFraisSupplementaires(double fraisSupplementaires) {
        this.fraisSupplementaires = fraisSupplementaires;
    }

    public String getRemarque() {
        return remarque;
    }

    public void setRemarque(String remarque) {
        this.remarque = remarque;
    }
}
