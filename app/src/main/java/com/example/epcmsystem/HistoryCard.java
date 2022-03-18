package com.example.epcmsystem;

import android.net.Uri;

import java.util.Date;

public class HistoryCard {
    private String date;
    private String degree;
    private int health;
    private String location;
    private Uri image;

    public HistoryCard(String date, String degree, int health, String location, Uri image) {
        this.date = date;
        this.degree = degree;
        this.health = health;
        this.location = location;
        this.image = image;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Uri getImage() {
        return image;
    }

    public void setImage(Uri image) {
        this.image = image;
    }
}
