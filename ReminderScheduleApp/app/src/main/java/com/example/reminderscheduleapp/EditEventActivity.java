package com.example.reminderscheduleapp;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.inputmethodservice.Keyboard;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.reminderscheduleapp.persistence.EventRepository;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;

public class EditEventActivity extends AppCompatActivity implements TextWatcher {

    private EditText titleEditText, descriptionEditText, dateEditText, timeEditText;
    private TextView titleViewText, descriptionViewText, dateViewText, timeViewText;
    private RelativeLayout backArrowContainer, saveContainer;
    private ImageButton backArrowButton, saveButton;
    private LinearLayout viewLayout, editLayout;
    private Button deleteEventButton, previewNotificationButton;
    private event initialEvent;
    private NotificationManagerCompat notificationManager;
    private RadioButton everyDayRadioButton, everyMonthDayRadioButton, firstWeekDayOfEveryMonthButton, lastWeekDayOfEveryMonthButton, noneRadioButton;

    EventRepository eventRepository;

    private boolean isViewModeEnabled = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        isViewModeEnabled = true;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        // linking all of the variables
        titleEditText = findViewById(R.id.event_title_editor);
        titleViewText = findViewById(R.id.event_title_text);
        descriptionEditText = findViewById(R.id. description_edit_box);
        descriptionViewText = findViewById(R.id. description_text_box);
        dateEditText = findViewById(R.id.date_edit_box);
        dateViewText = findViewById(R.id.date_text_box);
        timeEditText = findViewById(R.id.time_edit_box);
        timeViewText = findViewById(R.id.time_text_box);
        backArrowContainer = findViewById(R.id.back_arrow_container);
        saveContainer = findViewById(R.id.save_image_container);
        backArrowButton = findViewById(R.id.toolbar_back_arrow);
        saveButton = findViewById(R.id.save_image);
        previewNotificationButton = findViewById(R.id.previewNotificationButton);
        viewLayout = findViewById(R.id.view_mode_layout);
        editLayout = findViewById(R.id.edit_mode_layout);
        deleteEventButton = findViewById(R.id.deleteEventButton);
        everyDayRadioButton = findViewById(R.id.radioButton);
        everyMonthDayRadioButton = findViewById(R.id.radioButton2);
        firstWeekDayOfEveryMonthButton = findViewById(R.id.radioButton3);
        lastWeekDayOfEveryMonthButton = findViewById(R.id.radioButton4);
        noneRadioButton = findViewById(R.id.radioButton5);


        eventRepository = new EventRepository(this);
        notificationManager = NotificationManagerCompat.from(this);

        if(getIntent().hasExtra("selected_event")) {
            initialEvent = getIntent().getParcelableExtra("selected_event");
        } else {
            Toast.makeText(this, "HUGE ERROR!!!!!!",Toast.LENGTH_SHORT);
        }
        setListeners();
        setEventFields();
        disableEditMode();

        // this brings up the time picker dialog whenever the time EditText is selected
        timeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                TimePickerDialog timePickerDialog = new TimePickerDialog(EditEventActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                        // this is setting the correct format of the output text
                        String formattedHour, formattedMinute, amPM;
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
                        timeEditText.setText(formattedHour+ ":" + formattedMinute + " " + amPM);
                    }
                }, calendar.get(calendar.HOUR_OF_DAY), calendar.get(calendar.MINUTE), false);
                timePickerDialog.show();
            }
        });

        // this brings up the calendar dialog box whenever the date EditText is tapped
        dateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(EditEventActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        dateEditText.setText((month+1) + "/" + (dayOfMonth) + "/" + year);
                    }
                }, calendar.get(calendar.YEAR), calendar.get(calendar.MONTH), calendar.get(calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });

        // this is the onClick method for the delete event button
        deleteEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(EditEventActivity.this);
                builder.setMessage("Are you sure you want to delete this event?")
                        .setPositiveButton("Delete This Event", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                eventRepository.deleteEvent(initialEvent);
                                Toast.makeText(EditEventActivity.this,"Event was successfully deleted",Toast.LENGTH_SHORT).show();
                                Thread thread = new Thread() {
                                    @Override
                                    public void run() {
                                        try { // toasts need a little bit of time to show up before exiting this screen
                                            Thread.sleep(Toast.LENGTH_SHORT);
                                            finish();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                };
                                thread.start();
                                finish();
                            }
                        }).setNegativeButton("Cancel", null);
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

    private void setListeners() {
        // sets up the watchers to watch these specific EditTexts for edits, to update the TextViews whenever this is changed
        titleEditText.addTextChangedListener(this);
        descriptionEditText.addTextChangedListener(this);
        dateEditText.addTextChangedListener(this);
        timeEditText.addTextChangedListener(this);
    }

    private void setEventFields() {
        // this sets all of the text to appear in the TextViews
        titleViewText.setText(initialEvent.getTitle());
        titleEditText.setText(initialEvent.getTitle());
        descriptionEditText.setText(initialEvent.getDescription());
        descriptionViewText.setText(initialEvent.getDescription());
        dateEditText.setText(initialEvent.getDate());
        dateViewText.setText(initialEvent.getDate());
        timeEditText.setText(initialEvent.getTime());
        timeViewText.setText(initialEvent.getTime());

        everyDayRadioButton.setText("Every Day");

        //since dates are determined by the dateCalendar, dates will always be in this format
        Scanner dateScanner = new Scanner(initialEvent.getDate());
        dateScanner.useDelimiter("/");
        int month = Integer.parseInt(dateScanner.next());
        int day = Integer.parseInt(dateScanner.next());
        int year = Integer.parseInt(dateScanner.next().substring(0,4));

        Calendar calendar = Calendar.getInstance();
        try {
            calendar.set(Calendar.MONTH, month-1);
            calendar.set(Calendar.DAY_OF_MONTH, day);
            calendar.set(Calendar.YEAR, year);
        } catch (Exception e) {
            Toast.makeText(this, "HUGE ERROR!" , Toast.LENGTH_SHORT).show();
        }

        String dayOfWeek = "monday";
        try {
            dayOfWeek = new SimpleDateFormat("EEEE").format(calendar.getTime());
        } catch (Exception e) {
            Toast.makeText(this, "HUGE ERROR!" , Toast.LENGTH_SHORT).show();
        }

        firstWeekDayOfEveryMonthButton.setText("First " + dayOfWeek + " of every month");
        lastWeekDayOfEveryMonthButton.setText("Last " + dayOfWeek + " of every month");
        String suffix;
        switch (day % 10) {
            case 1: suffix = "st"; break;
            case 2: suffix = "nd"; break;
            case 3: suffix = "rd"; break;
            default: suffix = "th"; break;
        }
        everyMonthDayRadioButton.setText("Every " + day + suffix + " of the month");

         if (initialEvent.getRepeats().equals("every_day")) {
            everyDayRadioButton.setChecked(true);
        } else if (initialEvent.getRepeats().equals("every_first_week_day")) {
            firstWeekDayOfEveryMonthButton.setChecked(true);
        } else if (initialEvent.getRepeats().equals("every_last_week_day")) {
            lastWeekDayOfEveryMonthButton.setChecked(true);
        } else if (initialEvent.getRepeats().equals("every_month_day")) {
            everyMonthDayRadioButton.setChecked(true);
        } else {
            noneRadioButton.setChecked(true);
        }
    }

    public void enableEditMode() {
        isViewModeEnabled = false;
        backArrowContainer.setVisibility(View.GONE);
        saveContainer.setVisibility(View.VISIBLE);

        titleViewText.setVisibility(View.GONE);
        titleEditText.setVisibility(View.VISIBLE);
        viewLayout.setVisibility(View.GONE);
        editLayout.setVisibility(View.VISIBLE);
        previewNotificationButton.setEnabled(false);
        everyMonthDayRadioButton.setEnabled(true);
        everyDayRadioButton.setEnabled(true);
        lastWeekDayOfEveryMonthButton.setEnabled(true);
        firstWeekDayOfEveryMonthButton.setEnabled(true);
        noneRadioButton.setEnabled(true);
        setEventFields();
    }

    // triggered whenever the event is saved
    public void disableEditMode() {
        isViewModeEnabled = true;
        backArrowContainer.setVisibility(View.VISIBLE);
        saveContainer.setVisibility(View.GONE);
        // replaces all of the EditTexts with TextViews
        titleViewText.setVisibility(View.VISIBLE);
        titleEditText.setVisibility(View.GONE);
        viewLayout.setVisibility(View.VISIBLE);
        editLayout.setVisibility(View.GONE);
        // disables the buttons, and re-enables the previewNotificationButton
        everyDayRadioButton.setEnabled(false);
        everyMonthDayRadioButton.setEnabled(false);
        lastWeekDayOfEveryMonthButton.setEnabled(false);
        firstWeekDayOfEveryMonthButton.setEnabled(false);
        noneRadioButton.setEnabled(false);
        previewNotificationButton.setEnabled(true);
        setEventFields();
    }

    public void onTitleClick(View view) {
        enableEditMode();
        // sets the cursor at the right place when the title bar is pressed
        titleEditText.requestFocus();
        titleEditText.setSelection(titleEditText.length());
    }

    public void onSaveButtonClick(View view) {
        // hides the keyboard
        InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View v = this.getCurrentFocus();
        if(v == null) {
            v = new View(this);
        }
        inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(),0);
        saveAndUpdateEvent();
    }

    // whenever any TextViews are clicked, the activity will switch between edit mode and view mode
    public void onViewLayoutTextClick(View view) {
        enableEditMode();
    }

    // this saves the event when the back button in the bottom is pressed
    @Override
    public void onBackPressed() {
        if(!isViewModeEnabled) {
            // hides the keyboard
            InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
            View v = this.getCurrentFocus();
            if(v == null) {
                v = new View(this);
            }
            inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(),0);
            saveAndUpdateEvent();
        } else {
            super.onBackPressed();
        }

    }

    // this is triggered when the back button in the top left is pressed
    public void onBackArrowPressed(View view) {
        onBackPressed();
    }

    public void saveAndUpdateEvent() {
        // checking to see if the new inputs are still valid
        // title is the only thing checked, because it is impossible to make the other fields empty
        if(titleEditText.getText().toString().trim().length() > 0) {
            initialEvent.setTitle(titleEditText.getText().toString());
            initialEvent.setDescription(descriptionEditText.getText().toString());
            initialEvent.setDate((dateEditText).getText().toString());
            initialEvent.setTime(timeEditText.getText().toString());
            if (everyDayRadioButton.isChecked()) {
                initialEvent.setRepeats("every_day");
            } else if (everyMonthDayRadioButton.isChecked()) {
                initialEvent.setRepeats("every_month_day");
            } else if (firstWeekDayOfEveryMonthButton.isChecked()) {
                initialEvent.setRepeats("every_first_week_day");
            } else if (lastWeekDayOfEveryMonthButton.isChecked()) {
                initialEvent.setRepeats("every_last_week_day");
            } else {
                initialEvent.setRepeats("none");
            }
            disableEditMode();
            eventRepository.updateEvent(initialEvent);
            Toast.makeText(this, "Event successfully updated!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "You need a title!", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        titleViewText.setText(titleEditText.getText().toString());
        descriptionViewText.setText(descriptionEditText.getText().toString());
        dateViewText.setText(dateViewText.getText().toString());
        timeViewText.setText(timeEditText.getText().toString());
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    public void createNotification(View view) {
        Notification notification = new NotificationCompat.Builder(this, App.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_event_available_black_24dp)
                .setContentTitle("Reminder For " + initialEvent.getTitle())
                .setContentText("You created a reminder to remind yourself on " + initialEvent.getDate() + " " +
                        initialEvent.getTime() + " to " + initialEvent.getTitle() + " " + initialEvent.getDescription())
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("You created a reminder to remind yourself on " + initialEvent.getDate() + " " +
                                initialEvent.getTime() + " to " + initialEvent.getTitle() + " " + initialEvent.getDescription()))
                .build();

        notificationManager.notify(initialEvent.getId(), notification);


        Scanner dateScanner = new Scanner(initialEvent.getDate());
        dateScanner.useDelimiter("/");
        int month = Integer.parseInt(dateScanner.next());
        int day = Integer.parseInt(dateScanner.next());
        int year = Integer.parseInt(dateScanner.next().substring(0,4));

        Calendar calendar = Calendar.getInstance();
        try {
            calendar.set(Calendar.MONTH, month-1);
            calendar.set(Calendar.DAY_OF_MONTH, day);
            calendar.set(Calendar.YEAR, year);
        } catch (Exception e) {
            Toast.makeText(this, "HUGE ERROR!" , Toast.LENGTH_SHORT).show();
        }

        Scanner scanner1 = new Scanner(initialEvent.getTime());
        String time = scanner1.next();
        int hour = Integer.parseInt(time.substring(0,2));
        int minute = Integer.parseInt(time.substring(3,5));
        String amPm = scanner1.next();
        if(amPm.equals("PM")) {
            hour = hour + 12;
        }
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        if(calendar.before(Calendar.getInstance())) {
            Toast.makeText(this, "Warning: time is in the past", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Remember to " + initialEvent.getTitle() + " on " + calendar.getTime().toString(), Toast.LENGTH_LONG);
        }
    }

}
