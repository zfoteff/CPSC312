package com.cs312.jumpshot;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddEventActivity extends AppCompatActivity {

    static final String TAG = "addEventActivity";
    static EventDBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        dbHelper = new EventDBHelper(this);
        Log.d(TAG, "addEventActivity called");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_event);

        EditText eventName = findViewById(R.id.cardEventName);
        EditText eventLocation = findViewById(R.id.eventLocation);
        EditText eventTime = findViewById(R.id.eventStartTime);

        Intent intent = getIntent();
        if (intent != null) {
            Toast.makeText(this, "Made a new event!", Toast.LENGTH_LONG).show();
        }

        Button saveButton = findViewById(R.id.saveEvent);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();

                //  Add event to db
                Event newEvent = new Event(1, eventName.getText().toString(), eventTime.getText().toString(), eventLocation.getText().toString(), "placeholder");
                dbHelper.insertEvent(newEvent);

                intent.putExtra("newEventName", newEvent.getEventName());
                intent.putExtra("newEventLocation", newEvent.getLocation());
                intent.putExtra("newEventTime", newEvent.getStartTime());

                AddEventActivity.this.setResult(Activity.RESULT_OK, intent);
                AddEventActivity.this.finish();
            }
        });
    }

}
