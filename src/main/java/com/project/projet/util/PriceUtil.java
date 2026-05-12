package com.project.projet.util;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class PriceUtil {

    public static int calculateDays(LocalDate start, LocalDate end) {
        if (start == null || end == null) {
            return 0;
        }
        long days = ChronoUnit.DAYS.between(start, end);
        if (days < 0) {
            return 0;
        }
        if (days == 0) {
            return 1;
        }
        return (int) days;
    }

    public static double calculateTotal(int days, double prixParJour, double optionsTotal) {
        return (days * prixParJour) + optionsTotal;
    }
}
