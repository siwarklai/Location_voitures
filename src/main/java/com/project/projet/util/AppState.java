package com.project.projet.util;

import com.project.projet.Model.Utilisateur;

public class AppState {

    private static Utilisateur currentUser;

    public static Utilisateur getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(Utilisateur user) {
        currentUser = user;
    }

    public static String getRole() {
        return currentUser != null ? currentUser.getRole() : "";
    }
}
