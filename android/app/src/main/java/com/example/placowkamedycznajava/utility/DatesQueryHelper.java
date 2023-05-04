package com.example.placowkamedycznajava.utility;

import android.os.Build;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.stream.Stream;

public class DatesQueryHelper {

    public static final String[] appointmentArr = {"Dzisiaj", "Jutro", "Najbliższe 10 wizyt", "Najbliższe 20 wizyt", "Najbliższy miesiąc", "Najbliższe 3 miesiące", "Najbliższe pół roku", "Konkretna data"};
    public static final LinkedHashMap<String, String> dateQueryDict;
    static {
        dateQueryDict = new LinkedHashMap<>();
        dateQueryDict.put("Dzisiaj", "1");
        dateQueryDict.put("Jutro", tomorrowDateAsString());
        dateQueryDict.put("Najbliższe 10 wizyt", "10w");
        dateQueryDict.put("Najbliższe 20 wizyt", "20w");
        dateQueryDict.put("Najbliższy miesiąc", "30");
        dateQueryDict.put("Najbliższe 3 miesiące", "90");
        dateQueryDict.put("Najbliższe pół roku", "180");
    }

    private static String tomorrowDateAsString() {
        Date d = new Date(new Date().getTime() + 86400000);
        String pattern = "dd-MM-yyyy";
        SimpleDateFormat DateFor = new SimpleDateFormat(pattern, Locale.getDefault());
        return DateFor.format(d);
    }

    public static String todayDateAsString() {
        Date d = new Date();
        String pattern = "dd-MM-yyyy";
        SimpleDateFormat DateFor = new SimpleDateFormat(pattern, Locale.getDefault());
        return DateFor.format(d);
    }

    /**
     * Function takes database timezone datatime and converts to the current timezone without TZ info
     * @param datetime_arg db timezone datatime as String
     * @return formatted datatime of the current timezone
     */
    public static String formatDateTime(String datetime_arg) throws ParseException {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            java.text.SimpleDateFormat inputDateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.getDefault());
            java.text.SimpleDateFormat outputDateFormat = new java.text.SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());
            Date datetime = inputDateFormat.parse(datetime_arg);
            return outputDateFormat.format(datetime);
        }
        // return without changes if condition not met
        return datetime_arg;
    }
}
