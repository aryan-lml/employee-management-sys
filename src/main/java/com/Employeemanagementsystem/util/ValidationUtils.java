package com.Employeemanagementsystem.util;

import java.util.regex.Pattern;

public final class ValidationUtils {

    private static final Pattern EMAIL = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE = Pattern.compile("^\\d{7,15}$"); // allow 7-15 digits international

    private ValidationUtils() {}

    public static boolean isValidEmail(String email) {
        if (email == null) return false;
        return EMAIL.matcher(email).matches();
    }

    public static boolean isValidPhone(String phone) {
        if (phone == null) return false;
        String trimmed = phone.replaceAll("[^0-9]", "");
        return PHONE.matcher(trimmed).matches();
    }

    public static boolean isValidSalary(Double salary) {
        if (salary == null) return false;
        return salary >= 0.0;
    }
}
