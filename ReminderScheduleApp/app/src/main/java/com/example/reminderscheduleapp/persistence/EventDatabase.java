package com.example.reminderscheduleapp.persistence;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;

import com.example.reminderscheduleapp.event;

@Database(entities = {event.class}, version = 1)
public abstract class EventDatabase extends RoomDatabase {

    public static final String DATABASE_NAME = "databaseOfEvents";

    public static EventDatabase instance;

    public static EventDatabase getInstance(final Context context) {

        if(instance == null) {
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    EventDatabase.class,
                    DATABASE_NAME).build();
        }
        return instance;
    }

    public abstract EventDao getEventDao();
}
