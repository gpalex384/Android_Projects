package com.algapo.myratings.DAO;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.algapo.myratings.model.RateableThing;
import com.algapo.myratings.model.Rating;

@Database(entities = {Rating.class, RateableThing.class}, version = 1)
public abstract class AppDataBase extends RoomDatabase {
    public static final String DATABASE_NAME = "MYRATINGS";
    public abstract RatingDAO ratingDao();
    public abstract RateableThingDAO rateableThingDao();
    private static AppDataBase appDataBase;

    public static AppDataBase getAppDataBaseInstance(Context ctx) {
        if (appDataBase == null) {
            appDataBase = Room.databaseBuilder(ctx, AppDataBase.class, DATABASE_NAME).build();
        }
        return appDataBase;
    }
}

