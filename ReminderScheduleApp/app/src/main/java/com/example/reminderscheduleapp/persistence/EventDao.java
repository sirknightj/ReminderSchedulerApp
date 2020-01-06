package com.example.reminderscheduleapp.persistence;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.reminderscheduleapp.event;

import java.util.List;

@Dao
public interface EventDao {
    @Query("SELECT * FROM event")
    LiveData<List<event>> getEvents();

    @Insert
    void insert(event... events);

    @Delete
    void delete(event... events);

    @Update
    void update(event... events);

}
