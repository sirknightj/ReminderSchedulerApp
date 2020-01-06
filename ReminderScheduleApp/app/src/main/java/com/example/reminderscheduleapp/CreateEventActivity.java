package com.example.reminderscheduleapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.arch.persistence.room.Room;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.reminderscheduleapp.persistence.EventDatabase;
import com.example.reminderscheduleapp.persistence.EventRepository;

import java.util.Calendar;

public class CreateEventActivity extends AppCompatActivity {

    EditText eventTitle;
    EditText eventDescription;
    EditText eventDate;
    EditText eventTime;
    Button createButton;
    EventRepository mEventRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        eventTitle = findViewById(R.id.titleUserInputBox);
        eventDescription = findViewById(R.id.descriptionUserInputBox);
        eventDate = findViewById(R.id.dateUserInputBox);
        eventTime = findViewById(R.id.timeUserInputBox);
        createButton = findViewById(R.id.createEventButton);
        mEventRepository = new EventRepository(this);

        eventTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                TimePickerDialog timePickerDialog = new TimePickerDialog(CreateEventActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                        String formattedTime, formattedHour, formattedMinute, amPM;
                        if(hourOfDay >= 12) {
                            amPM = "PM";
                        } else {
                            amPM = "AM";
                        }
                        if(hourOfDay%12 < 10) {
                            if(hourOfDay%12 == 0) {
                                formattedHour = "12";
                            } else {
                                formattedHour = "0" + (hourOfDay%12);
                            }
                        } else {
                            formattedHour = (hourOfDay%12) + "";
                        }
                        if(minute < 10) {
                            if(minute == 0) {
                                formattedMinute = "00";
                            } else {
                                formattedMinute = "0" + minute;
                            }
                        } else {
                            formattedMinute = minute +"";
                        }
                        eventTime.setText(formattedHour+ ":" + formattedMinute + " " + amPM);
                    }
                }, calendar.get(calendar.HOUR_OF_DAY), calendar.get(calendar.MINUTE), false);
                timePickerDialog.show();
            }
        });

        eventDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(CreateEventActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        eventDate.setText((month+1) + "/" + (dayOfMonth) + "/" + year);
                    }
                }, calendar.get(calendar.YEAR), calendar.get(calendar.MONTH), calendar.get(calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });

        eventTitle.addTextChangedListener(createEventTextWatcher);
        eventDate.addTextChangedListener(createEventTextWatcher);
        eventTime.addTextChangedListener(createEventTextWatcher);
    }


    public void onCreateEventButtonClick(View view) {
        EventDatabase db = Room.databaseBuilder(getApplicationContext(), EventDatabase.class, "databaseOfEvents")
                .allowMainThreadQueries()
                .build();

        boolean isInputCorrect = true;
        int errorMessageType = 0;
        if (eventTitle.getText().toString().trim().length() == 0) {
            isInputCorrect = false;
            errorMessageType = 1;
        }


        if(isInputCorrect) {
            mEventRepository.insertEventTask(new event(
                    eventTitle.getText().toString().trim(),
                    eventDescription.getText().toString().trim(),
                    eventDate.getText().toString().trim(),
                    eventTime.getText().toString().trim(),
                    "never"));
            Toast.makeText(this,"Event was successfully created",Toast.LENGTH_SHORT).show();
            Thread thread = new Thread() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(Toast.LENGTH_SHORT);
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            thread.start();
        } else {
            String errorMessage = "Error";
            if(errorMessageType == 1) {
                errorMessage = "Name of event is required";
            } else {
                errorMessage = "Huge Error!";
            }
            Toast.makeText(this,errorMessage,Toast.LENGTH_SHORT).show();
        }
    }

    private TextWatcher createEventTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            createButton.setEnabled(eventTitle.getText().toString().trim().length() > 0 && eventDate.getText().toString().trim().length() > 0 && eventTime.getText().toString().trim().length() > 0);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
}
