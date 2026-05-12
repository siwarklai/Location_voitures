package com.project.projet.util;

import java.time.LocalDate;

public class ValidationUtil {

    public static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static boolean isEmailValid(String email) {
        return email != null && email.contains("@");
    }

    public static boolean isPhoneValid(String phone) {
        return phone != null && phone.matches("\\d+");
    }

    public static boolean isPriceValid(double price) {
        return price > 0;
    }

    public static boolean isDateRangeValid(LocalDate start, LocalDate end) {
        return start != null && end != null && !end.isBefore(start);
    }

    public static boolean isLicenseValid(LocalDate expiration) {
        return expiration != null && !expiration.isBefore(LocalDate.now());
    }
}
