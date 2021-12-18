package com.cs312.jumpshot;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class EventBrowserActivity extends AppCompatActivity {
    static final String TAG = "EventBrowser";
    private EventDBHelper dbHelper;
    private RecyclerView recyclerView;
    private List<Event> eventList;
    CustomAdaptor adaptor;
    Bitmap bmp;
    byte[] byteArray;
    ActivityResultLauncher<Intent> eventDetailLauncher;

    @SuppressLint("WrongThread")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate called for EventBrowser Activity: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_browser);
        recyclerView = (RecyclerView) findViewById(R.id.eventList);
        eventList = new ArrayList<>();
        dbHelper = new EventDBHelper(EventBrowserActivity.this);
        eventList = dbHelper.getEvents();


        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adaptor = new CustomAdaptor();
        recyclerView.setAdapter(adaptor);

        Intent intent = getIntent();
        if (intent != null) {

            try {
                byte[] byteArray = getIntent().getByteArrayExtra("photo");
                bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            }
            catch (Exception e) {

            }

        }


        eventDetailLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Log.d(TAG, "OKAY");
                        }
                    }
                });


        Button openMapView = findViewById(R.id.openInMap);
        openMapView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EventBrowserActivity.this, MapViewActivity.class);
                startActivity(intent);
            }
        });

    }

    class CustomAdaptor extends RecyclerView.Adapter<CustomAdaptor.CustomViewHolder> {
         class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
            TextView name;
            TextView address;
            TextView startTime;
            CardView cards;

            public CustomViewHolder(@NonNull View itemView) {
                super(itemView);
                cards = itemView.findViewById(R.id.myCardView);
                name = itemView.findViewById(R.id.cardEventName);
                address = itemView.findViewById(R.id.cardEventAddress);
                startTime = itemView.findViewById(R.id.cardEventStarttime);
                itemView.setOnClickListener(this);
            }

            public void updateView(Event e) {
                Log.d(TAG, "updateView: ");
                name.setText(e.getEventName());
                address.setText(e.getLocation());
                startTime.setText(e.getStartTime());
            }

            @Override
            public void onClick(View view) {

                Log.d(TAG, "clicked");

                int pos = getAdapterPosition();
                Event clickedEvent = eventList.get(pos);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();



                Intent intent = new Intent(EventBrowserActivity.this, eventDetailActivity.class);
                intent.putExtra("from", "notPhoto");
                intent.putExtra("name", clickedEvent.getEventName());
                intent.putExtra("location", clickedEvent.getLocation());
                intent.putExtra("startTime", clickedEvent.getStartTime());
                intent.putExtra("photo", byteArray);
                eventDetailLauncher.launch(intent);


//                Intent intent = new Intent(EventBrowserActivity.this, eventDetailActivity.class);
//                intent.putExtra("name", clickedEvent.getEventName());
//                intent.putExtra("location", clickedEvent.getLocation());
//                intent.putExtra("startTime", clickedEvent.getStartTime());
//                intent.putExtra("photos", clickedEvent.getBitmapArray());
//                eventDetailLauncher.launch(intent);

            }

             @Override
             public boolean onLongClick(View view) {

                Log.d(TAG, "longClick");

                try {
                    int pos = getAdapterPosition();
                    Event clickedEvent = eventList.get(pos);
                    clickedEvent.addToBitmapArray(bmp);
                }
                catch(Exception e) {

                }
                 return true;
             }
         }

        @NonNull
        @Override
        public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(EventBrowserActivity.this)
                    .inflate(R.layout.card_view, parent, false);
            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
            Event event = eventList.get(position);
            holder.updateView(event);
        }

        @Override
        public int getItemCount() {
            return eventList.size();
        }
    }
}
