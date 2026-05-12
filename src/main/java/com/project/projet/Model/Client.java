package com.project.projet.Model;

import java.time.LocalDate;

public class Client {

    private int id;
    private String nom;
    private String prenom;
    private String cin;
    private String email;
    private String telephone;
    private String adresse;
    private String numeroPermis;
    private LocalDate expirationPermis;

    public Client() {
    }

    public Client(int id, String nom, String prenom, String cin,
                  String email, String telephone, String adresse,
                  String numeroPermis, LocalDate expirationPermis) {

        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.cin = cin;
        this.email = email;
        this.telephone = telephone;
        this.adresse = adresse;
        this.numeroPermis = numeroPermis;
        this.expirationPermis = expirationPermis;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getCin() {
        return cin;
    }

    public void setCin(String cin) {
        this.cin = cin;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getNumeroPermis() {
        return numeroPermis;
    }

    public void setNumeroPermis(String numeroPermis) {
        this.numeroPermis = numeroPermis;
    }

    public LocalDate getExpirationPermis() {
        return expirationPermis;
    }

    public void setExpirationPermis(LocalDate expirationPermis) {
        this.expirationPermis = expirationPermis;
    }

    @Override
    public String toString() {
        return nom + " " + prenom;
    }
}
