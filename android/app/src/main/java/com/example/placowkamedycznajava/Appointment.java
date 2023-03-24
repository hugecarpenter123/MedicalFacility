package com.example.placowkamedycznajava;

public class Appointment {
    private int id;
    private String date;
    private String personel;

    public Appointment(int id, String date, String personel) {
        this.id = id;
        this.date = date;
        this.personel = personel;
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
}
