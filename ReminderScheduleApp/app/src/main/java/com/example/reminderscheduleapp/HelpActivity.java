package com.example.reminderscheduleapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class HelpActivity extends AppCompatActivity {

    private int screenNumber = 1;
    private TextView helpTextView;
    private Button nextButton, previousButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        previousButton = findViewById(R.id.button);
        nextButton = findViewById(R.id.button2);
        helpTextView = findViewById(R.id.helpText);
        updateText();
    }

    public void onNextButtonClick(View view) {
        screenNumber++;
        updateText();
    }

    public void onPreviousButtonClick(View view) {
        screenNumber--;
        updateText();
    }

    private void updateText() {
        switch(screenNumber) {
            case(1):
                helpTextView.setText("To create a new event, tap the + button on the dashboard. Then, enter all the required fields.");
                previousButton.setEnabled(false);
                break;
            case(2):
                helpTextView.setText("To edit an existing event, tap the event you wish to edit on the dashboard.");
                previousButton.setEnabled(true);
                break;
            case(3):
                helpTextView.setText("Once you're on the edit event screen, you can tap on any text to enter edit mode");
                break;
            case(4):
                helpTextView.setText("Once in edit mode, you can edit whatever you like");
                break;
            case(5):
                helpTextView.setText("Once you're done editing, you can save using either the back soft navigation button or use the save button in the top left corner");
                break;
            case(6):
                helpTextView.setText("You can tap the Delete Event button to delete the event you're looking at. It will ask you to confirm whether you want to delete the event or not.");
                break;
            case(7):
                helpTextView.setText("The Preview Notification button lets you send a test notification to yourself so you know what your reminder will look like.");
                nextButton.setText("Next");
                break;
            case(8):
                helpTextView.setText("Thanks for reading the tutorial!");
                nextButton.setText("Finish");
                break;
            case(9):
                finish();
                break;
            default:
                if(screenNumber < 1) {
                    screenNumber = 1;
                } else {
                    screenNumber = 8;
                }
                break;
        }

    }
}
