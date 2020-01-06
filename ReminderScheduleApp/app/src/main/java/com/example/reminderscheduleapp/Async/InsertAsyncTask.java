package com.example.reminderscheduleapp.Async;

import android.os.AsyncTask;

import com.example.reminderscheduleapp.event;
import com.example.reminderscheduleapp.persistence.EventDao;

public class InsertAsyncTask extends AsyncTask<event, Void, Void> {

    private EventDao eventDao;

    public InsertAsyncTask(EventDao eventDao) {
        this.eventDao = eventDao;
    }

    @Override
    protected Void doInBackground(event... events) {
        eventDao.insert(events);
        return null;
    }
}
