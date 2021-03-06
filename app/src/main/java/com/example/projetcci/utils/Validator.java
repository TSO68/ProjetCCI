package com.example.projetcci.utils;

import java.util.regex.Pattern;

/**
 * Class that check different values typed by user
 */
public final class Validator {

    private static final int MIN_LENGTH = 6;
    private static final int MAX_LENGTH = 15;

    private static final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(
            "[a-zA-Z0-9+._%\\-]{1,256}" +
                    "@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
    );

    private Validator() {

    }

    /**
     * Check if email pattern and if EditText is empty
     * @param email typed by user
     * @return boolean
     */
    public static boolean checkEmail(String email) {
        if (email.isEmpty() || !EMAIL_ADDRESS_PATTERN.matcher(email).matches()) {
            return false;
        }
        return true;
    }

    /**
     * Check password length and if EditText is empty
     * @param password typed by user
     * @return boolean
     */
    public static boolean checkPassword(String password) {
        if (password.isEmpty() || password.length() < MIN_LENGTH || password.length() > MAX_LENGTH) {
            return false;
        }
        return true;
    }

    /**
     * Check confirmed password length, equality with password and if EditText is empty
     * @param password typed by user
     * @param confirmPassword typed by user
     * @return boolean
     */
    public static boolean checkConfirmPassword(String password, String confirmPassword) {
        if (confirmPassword.isEmpty() || password.length() < MIN_LENGTH || password.length() > MAX_LENGTH
                || !(confirmPassword.equals(password))) {
            return false;
        }
        return true;
    }

    public static boolean checkContent(String content) {
        if (content.isEmpty()) {
            return false;
        }
        return true;
    }
}
