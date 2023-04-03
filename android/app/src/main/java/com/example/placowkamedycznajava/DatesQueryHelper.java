package com.example.placowkamedycznajava;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class DatesQueryHelper {

    private final String[] appointmentArr = {"Dzisiaj", "Jutro", "Najbliższe 10 wizyt", "Najbliższe 20 wizyt", "Najbliższy miesiąc", "Najbliższe 3 miesiące", "Najbliższe pół roku", "Konkretna data"};
    private HashMap<String, String> dateQueryDict;

    public DatesQueryHelper() {
        fillDict();
    }

    public void fillDict() {
        dateQueryDict = new HashMap<>();
        dateQueryDict.put(appointmentArr[0], "1");
        dateQueryDict.put(appointmentArr[1], tomorrowDateAsString());
        dateQueryDict.put(appointmentArr[2], "10w");
        dateQueryDict.put(appointmentArr[3], "20w");
        dateQueryDict.put(appointmentArr[4], "30");
        dateQueryDict.put(appointmentArr[5], "90");
        dateQueryDict.put(appointmentArr[6], "150");
        dateQueryDict.put(appointmentArr[7], String.valueOf(30 * 6));
    }

    public String[] getAppointmentArr() {
        return appointmentArr;
    }

    public HashMap<String, String> getDateQueryDict() {
        return dateQueryDict;
    }

    private String tomorrowDateAsString() {
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
