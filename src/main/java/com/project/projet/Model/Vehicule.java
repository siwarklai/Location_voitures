package com.project.projet.Model;
public class Vehicule {

    private int id;
    private String marque;
    private String modele;
    private String immatriculation;
    private String categorie;
    private String carburant;
    private String boiteVitesse;
    private int nombrePlaces;
    private double prixParJour;
    private String statut;

    public Vehicule() {
    }

    public Vehicule(int id, String marque, String modele, String immatriculation,
                    String categorie, String carburant, String boiteVitesse,
                    int nombrePlaces, double prixParJour, String statut) {

        this.id = id;
        this.marque = marque;
        this.modele = modele;
        this.immatriculation = immatriculation;
        this.categorie = categorie;
        this.carburant = carburant;
        this.boiteVitesse = boiteVitesse;
        this.nombrePlaces = nombrePlaces;
        this.prixParJour = prixParJour;
        this.statut = statut;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMarque() {
        return marque;
    }

    public void setMarque(String marque) {
        this.marque = marque;
    }

    public String getModele() {
        return modele;
    }

    public void setModele(String modele) {
        this.modele = modele;
    }

    public String getImmatriculation() {
        return immatriculation;
    }

    public void setImmatriculation(String immatriculation) {
        this.immatriculation = immatriculation;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public String getCarburant() {
        return carburant;
    }

    public void setCarburant(String carburant) {
        this.carburant = carburant;
    }

    public String getBoiteVitesse() {
        return boiteVitesse;
    }

    public void setBoiteVitesse(String boiteVitesse) {
        this.boiteVitesse = boiteVitesse;
    }

    public int getNombrePlaces() {
        return nombrePlaces;
    }

    public void setNombrePlaces(int nombrePlaces) {
        this.nombrePlaces = nombrePlaces;
    }

    public double getPrixParJour() {
        return prixParJour;
    }

    public void setPrixParJour(double prixParJour) {
        this.prixParJour = prixParJour;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    @Override
    public String toString() {
        return marque + " " + modele;
    }
}