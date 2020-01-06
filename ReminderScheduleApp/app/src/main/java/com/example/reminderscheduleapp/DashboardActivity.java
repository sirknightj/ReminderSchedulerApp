package com.example.reminderscheduleapp;

import android.app.Notification;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.example.reminderscheduleapp.persistence.EventRepository;

import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity implements eventAdapter.OnEventListener{

    private NotificationManagerCompat notificationManager;
    RecyclerView mRecyclerView;
    RecyclerView.Adapter eventAdapter;
    private ArrayList<event> mEvents = new ArrayList<>();
    private EventRepository eventRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mRecyclerView = findViewById(R.id.eventRecyclerViewDashboard);
        initializeRecyclerView();
        eventRepository = new EventRepository(this);
        retrieveEvents();
        notificationManager = NotificationManagerCompat.from(this);

    }

    private void retrieveEvents() {
        eventRepository.retrieveEventsTask().observe(this, new Observer<List<event>>() {
            @Override
            public void onChanged(@Nullable List<event> events) {
                if(mEvents.size() > 0) {
                    mEvents.clear();
                }
                if(mEvents != null) {
                    mEvents.addAll(events);
                }
                eventAdapter.notifyDataSetChanged();
            }
        });
    }

    public void onFloatingActionButtonClick(View view) {
        startActivity(new Intent(this, CreateEventActivity.class));
    }

    public void createNotifications() {
        Notification notification = new NotificationCompat.Builder(this, App.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_event_available_black_24dp)
                .setContentTitle("Hello")
                .setContentText("This is the text")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .build();

        notificationManager.notify(App.notificationID, notification);
        App.notificationID++;

//        Toast.makeText(this,"Notification was successfully created!",Toast.LENGTH_SHORT).show();
    }
    public void initializeRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        eventAdapter = new eventAdapter(mEvents, this);
        mRecyclerView.setAdapter(eventAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        initializeRecyclerView();
        eventAdapter.notifyDataSetChanged();
    }

    @Override
    public void onEventClick(int position) {
        Toast.makeText(this,"Tapped " + mEvents.get(position).getTitle(),Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, EditEventActivity.class);
        intent.putExtra("selected_event", mEvents.get(position));
        startActivity(intent);
    }
}
