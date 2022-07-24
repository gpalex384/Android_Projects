package com.algapo.myratings.model;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.List;
import java.util.UUID;

@Entity
public class RateableThing {

    // Attributes
    @PrimaryKey
    @NonNull
    private String id;
    @ColumnInfo
    private String name;
    @ColumnInfo
    private double latitude;
    @ColumnInfo
    private double longitude;
    @Ignore
    private List<Rating> ratingList;

    // Constructor
    public RateableThing(String name, double latitude, double longitude) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Constructor
    public RateableThing(String id, String name, double latitude, double longitude) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public RateableThing() {

    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public List<Rating> getRatingList() {
        return ratingList;
    }

    public void setRatingList(List<Rating> ratingList) {
        this.ratingList = ratingList;
    }

}
