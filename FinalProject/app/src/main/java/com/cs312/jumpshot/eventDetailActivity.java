package com.cs312.jumpshot;

import static com.cs312.jumpshot.R.drawable.*;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class eventDetailActivity extends AppCompatActivity {

    static final String TAG = "EventDetail";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_view);

        ImageView photoDisplay = findViewById(R.id.photoDisplay);
        TextView eventName = findViewById(R.id.eventTitle);
        TextView eventLoc = findViewById(R.id.eventLocation2);
        TextView eventStartTime = findViewById(R.id.eventTime);

        Intent intent = getIntent();
        if (intent != null) {

            Log.d(TAG, "eventDetail");

            String name = intent.getStringExtra("name");
            String loc = intent.getStringExtra("location");
            String startTime = intent.getStringExtra("startTime");

            eventName.setText(name);
            eventLoc.setText(loc);
            eventStartTime.setText(startTime);

            byte[] byteArray = getIntent().getByteArrayExtra("photo");
            Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

            photoDisplay.setImageBitmap(bmp);

//            try {
//                byte[] byteArray = getIntent().getByteArrayExtra("photo");
//                Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
//
//                photoDisplay.setImageBitmap(bmp);
//            }
//            catch (Exception e) {
//                photoDisplay.setImageBitmap(null);
//            }
//            try {
//                byte[] bitmapArray;
//                bitmapArray = getIntent().getByteArrayExtra("photos");
//                Bitmap bmp = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
//                photoDisplay.setImageBitmap(bmp);
//            }
//            catch (Exception e) {
//                photoDisplay.setImageBitmap(null);
//            }
        }

    }
}
