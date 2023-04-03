package com.example.placowkamedycznajava;

import java.util.Locale;

/**
 * The same class as Appointment, but this class refers to different table in database and holds
 * different id's commpared to Appointment Table, technically one of this classes could hold info for both,
 * but the distinction makes it clearer.
 */

public class UserAppointment {
    private int id;
    private String date;
    private String personel;
    private String speciality;

    public UserAppointment(int id, String date, String personel, String speciality) {
        this.id = id;
        this.date = date;
        this.personel = personel;
        this.speciality = speciality;
    }

    public int getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public String getPersonel() {
        return personel;
    }

    public String getSpeciality() {
        return speciality;
    }

    @Override
    public String toString() {
        return String.format(Locale.getDefault(), "%d. %s, %s - %s", id, personel, speciality, date);
    }
}
