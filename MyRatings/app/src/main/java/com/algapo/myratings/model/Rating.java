package com.algapo.myratings.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.UUID;

@Entity
public class Rating {

    // Attributes
    @PrimaryKey
    @NonNull
    private String id;
    @ColumnInfo
    private double rate;
    @ColumnInfo
    private long ratingDate;
    @ColumnInfo (name = "rateable_thing_fk")
    @NonNull
    private String rateableThingId;


    // Constructor
    public Rating(double rate, long ratingDate, RateableThing rateableThing) {
        this.id = UUID.randomUUID().toString();
        this.rate = rate;
        this.ratingDate = ratingDate;
        this.rateableThingId = rateableThing.getId();
    }

    // Constructor
    public Rating(String id, double rate, long ratingDate, RateableThing rateableThing) {
        this.id = id;
        this.rate = rate;
        this.ratingDate = ratingDate;
        this.rateableThingId = rateableThing.getId();
    }

    public Rating() {

    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public long getRatingDate() {
        return ratingDate;
    }

    public void setRatingDate(long ratingDate) {
        this.ratingDate = ratingDate;
    }

    public String getRateableThingId() {
        return rateableThingId;
    }

    public void setRateableThingId(String rateableThingId) {
        this.rateableThingId = rateableThingId;
    }
}
