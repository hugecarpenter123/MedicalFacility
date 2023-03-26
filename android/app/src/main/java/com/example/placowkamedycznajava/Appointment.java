package com.example.placowkamedycznajava;

public class Appointment {
    private int id;
    private String date;
    private String personel;
    private String speciality;

    public Appointment(int id, String date, String personel, String speciality) {
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
}
