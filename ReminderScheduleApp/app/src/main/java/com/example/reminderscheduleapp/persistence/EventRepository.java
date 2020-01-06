package com.example.reminderscheduleapp.persistence;

import android.arch.lifecycle.LiveData;
import android.content.Context;

import com.example.reminderscheduleapp.Async.DeleteAsyncTask;
import com.example.reminderscheduleapp.Async.InsertAsyncTask;
import com.example.reminderscheduleapp.Async.UpdateAsyncTask;
import com.example.reminderscheduleapp.event;

import java.util.List;

public class EventRepository {
    private EventDatabase eventDatabase;

    public EventRepository(Context context) {
        eventDatabase = EventDatabase.getInstance(context);
    }

    public void insertEventTask(event event) {
        new InsertAsyncTask(eventDatabase.getEventDao()).execute(event);
    }

    public void updateEvent(event event) {
        new UpdateAsyncTask(eventDatabase.getEventDao()).execute(event);
    }

    public LiveData<List<event>> retrieveEventsTask() {

        return eventDatabase.getEventDao().getEvents();
    }

    public void deleteEvent(event event) {
        new DeleteAsyncTask(eventDatabase.getEventDao()).execute(event);
    }
}
