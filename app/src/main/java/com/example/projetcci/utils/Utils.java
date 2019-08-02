package com.example.projetcci.utils;

import android.support.annotation.NonNull;

import java.util.Locale;

public final class Utils {

    private Utils() {

    }

    /**
     * Search the Locale of the device
     * @return a string with language-country of the device
     */
    public static String getLocale() {
        String countryCode = Locale.getDefault().getCountry();
        String languageCode = Locale.getDefault().getLanguage();
        String localeCode = languageCode + "-" + countryCode;

        return localeCode;
    }

    /**
     *
     * @param date string of the date retrieved from API
     * @return reformatted date if needed
     */
    public static String formatDate(@NonNull String date) {
        //Format date if Locale if the device isn't US based
        if (!getLocale().equals("en-US")) {
            String tempDate = date.replaceAll("-","");
            String day = tempDate.substring(6, 8);
            String month = tempDate.substring(4, 6);
            String year = tempDate.substring(0, 4);
            String newDate = day + "/" + month + "/" + year;
            return newDate;
        } else {
            return date;
        }
    }
}
