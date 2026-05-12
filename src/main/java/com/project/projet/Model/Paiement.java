package com.project.projet.Model;
public class Paiement {

    private int id;
    private Reservation reservation;
    private double montantTotal;
    private double montantPaye;
    private double resteAPayer;
    private String modePaiement;
    private String statutPaiement;

    public Paiement() {
    }

    public Paiement(int id, Reservation reservation,
                    double montantTotal, double montantPaye,
                    double resteAPayer, String modePaiement,
                    String statutPaiement) {

        this.id = id;
        this.reservation = reservation;
        this.montantTotal = montantTotal;
        this.montantPaye = montantPaye;
        this.resteAPayer = resteAPayer;
        this.modePaiement = modePaiement;
        this.statutPaiement = statutPaiement;
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

    public double getMontantTotal() {
        return montantTotal;
    }

    public void setMontantTotal(double montantTotal) {
        this.montantTotal = montantTotal;
    }

    public double getMontantPaye() {
        return montantPaye;
    }

    public void setMontantPaye(double montantPaye) {
        this.montantPaye = montantPaye;
    }

    public double getResteAPayer() {
        return resteAPayer;
    }

    public void setResteAPayer(double resteAPayer) {
        this.resteAPayer = resteAPayer;
    }

    public String getModePaiement() {
        return modePaiement;
    }

    public void setModePaiement(String modePaiement) {
        this.modePaiement = modePaiement;
    }

    public String getStatutPaiement() {
        return statutPaiement;
    }

    public void setStatutPaiement(String statutPaiement) {
        this.statutPaiement = statutPaiement;
    }
}