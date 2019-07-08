package com.example.projetcci;

/**
 * Class that check different values typed by user
 */
public class Validator {

    private static final int MIN_LENGTH = 6;
    private static final int MAX_LENGTH = 15;

    /**
     * Check if email pattern and if EditText is empty
     * @param email typed by user
     * @return boolean
     */
    public static boolean checkEmail(String email) {
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
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
        if (password.isEmpty() || password.length() < MIN_LENGTH || password.length() > MAX_LENGTH
                || !(confirmPassword.equals(password))) {
            return false;
        }
        return true;
    }
}
