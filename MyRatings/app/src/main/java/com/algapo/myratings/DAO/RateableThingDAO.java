package com.algapo.myratings.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.algapo.myratings.model.RateableThing;

import java.util.List;

@Dao
public interface RateableThingDAO {

    @Query("SELECT * FROM rateablething")
    List<RateableThing> getAllRateableThings();

    @Query("SELECT * FROM rateablething WHERE id IN (:rateablethingsIds)")
    List<RateableThing> getAllRateableThingsByIds(String[] rateablethingsIds);

    @Insert
    void insertAll(RateableThing... rateableThings);

    @Delete
    void delete(RateableThing rateableThing);

}
