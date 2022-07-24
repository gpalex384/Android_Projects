package com.algapo.myratings.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.algapo.myratings.model.Rating;

import java.util.List;

@Dao
public interface RatingDAO {

    @Query("SELECT * FROM rating")
    List<Rating> getAllRatings();

    @Query("SELECT * FROM rating WHERE id IN (:ratingIds)")
    List<Rating> getAllRatingsByIds(String[] ratingIds);

    @Insert
    void insertAll(Rating... ratings);

    @Delete
    void delete(Rating rating);

}
