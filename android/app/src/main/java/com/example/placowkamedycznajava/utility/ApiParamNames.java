package com.example.placowkamedycznajava.utility;

public class ApiParamNames {
    // general
    public static final String ID = "id";

    // Login
    public static final String BOOLEAN_LOGIN_RESPONSE = "isAuthenticated";
    public static final String LOGIN = "login"; // PESEL or username

    // Register
    public static final String USERNAME = "username";
    public static final String FIRST_NAME = "first_name";
    public static final String LAST_NAME = "last_name";
    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";
    public static final String PESEL = "pesel";
    public static final String PHONE = "nr_telefonu";
    public static final String CITY = "miasto";
    public static final String CITY_CODE = "kod_pocztowy";
    public static final String STREET = "ulica";
    public static final String HOUSE_NUMBER = "nr_budynku";

    // Settings update
    public static final String UPDATE_OLD_PWD = "old_password";
    public static final String UPDATE_NEW_PWD = "new_password";

    // Appointment search
    public static final String SEARCH_PERSONEL_ID = "personel_id";
    public static final String SEARCH_DATE = "data";
    public static final String SEARCH_SPECIALITY_ID = "specjalnosc_id";
    public static final String SPECIALITY_ARRAY = "specjalnosc";
    public static final String PERSONEL_ARRAY = "personel";

    // List fragment GET & User Fragment GET
    public static final String APPOINTMENTS_NEXT_PAGE = "next";
    public static final String APPOINTMENTS_PREVIOUS_PAGE = "previous";
    public static final String APPOINTMENTS_RESULT_ARRAY = "results";
    public static final String APPOINTMENTS_DATE = "data";
    public static final String APPOINTMENTS_PERSONEL = "personel";
    public static final String APPOINTMENTS_SPECIALITY = "specjalnosc";

    // Lsit fragment POST
    public static final String BOOK_USER_ID = "uzytkownik";
    public static final String BOOK_APPOINTMENT_ID = "termin";

    // UserFragment
    public static final String USER_APPOINTMENTS_ARRAY = "wizyty";
    public static final String USER_APPOINTMENTS_DATATIME = "termin";

}
