/**
 * Program handles back end logic of Video Details screen for the WatchList application. Contains
 * logic for creating and editing elements in the watchlist
 * CPSC 312-01, Fall 2021
 * Programming Assignment #7
 *
 * @author Zac Foteff
 * @version v2.0 11/23/21
 */

package com.example.watchlist;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class VideoDetailsActivity extends AppCompatActivity {
    private static final String TAG = "VideoDetailsActivity";
    private String type;
    private int drawableId = R.drawable.placeholderimage;
    ActivityResultLauncher<Intent> photoChooserLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: Opened create new entry screen");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_details_activity);

        List<String> videoTags = new ArrayList<>();
        videoTags.add("Sports");
        videoTags.add("Action");
        videoTags.add("Mystery");
        videoTags.add("Thriller");
        videoTags.add("Crime");
        videoTags.add("Reality TV");
        videoTags.add("Documentary");
        videoTags.add("YouTube Video");

        EditText titleField = findViewById(R.id.entryName);
        Button saveButton = findViewById(R.id.save_button);
        ImageView imageButton = findViewById(R.id.image_button);
        CheckBox watchedCheckbox = findViewById(R.id.watched);
        Spinner options = findViewById(R.id.videoTypes);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, videoTags);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        options.setAdapter(adapter);

        Intent intent = getIntent();
        if (intent != null) {
            String title = intent.getStringExtra("title");
            boolean watched = intent.getBooleanExtra("watched", false);
            int drawable = intent.getIntExtra("drawable_id", R.drawable.placeholderimage);

            titleField.setText(title);
            watchedCheckbox.setChecked(watched);
            imageButton.setImageResource(drawable);
        }

        options.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                type = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //  Check if edittext has anything in it
                String titleValue = titleField.getText().toString();

                if (titleValue.isEmpty()) {
                    //create toast
                    Toast.makeText(VideoDetailsActivity.this, "Enter a title to save new entry", Toast.LENGTH_LONG).show();
                } else {
                    Log.d(TAG, "onClick: Clicked save");
                    Intent intent = new Intent();
                    intent.putExtra("title", titleValue);
                    intent.putExtra("type", type);
                    intent.putExtra("watched", watchedCheckbox.isChecked());
                    intent.putExtra("drawable_id", drawableId);
                    VideoDetailsActivity.this.setResult(RESULT_OK, intent);
                    VideoDetailsActivity.this.finish();
                }
            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
                imageButton.setImageResource(drawableId);
            }
        });

        photoChooserLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK) {
                            Intent data = result.getData();
                            if (data != null) {
                                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                                imageButton.setImageBitmap(bitmap);
                            }
                        }
                    }
                });
    }

    private void selectImage() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        photoChooserLauncher.launch(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.saveVideo) {
            Log.d(TAG, "onClick: Clicked create new video");
            Intent intent = new Intent(VideoDetailsActivity.this, MainActivity.class);
            VideoDetailsActivity.this.setResult(RESULT_CANCELED, intent);
            VideoDetailsActivity.this.finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
