/**
 * This program executes the PlaceSearchActivity of NearMe. It allows for searching
 * of nearby places to the user's location, and displays them in a recycler view.
 *
 * Rie implemented the design of the PlaceSearchActivity (minus the RecyclerView),
 * getting the user's location, calling the PlacesAPI search, and parsing the search data
 * into Place objects.
 *
 * Zac implemented the Recycler view and the logic for populating the view with cards, as well as
 * displaying the results in PLaceDetailActivity after asynchronously calling the Google Place
 * Detail API
 *
 * Sources:
 * https://developers.google.com/maps/documentation/places/web-service/place-id
 * https://stackoverflow.com/questions/13135447/setting-onclicklistener-for-the-drawable-right-of-an-edittext
 *
 * CPSC 312, Fall 2021
 * Programming Assignment #8
 *
 * @author Rie Durnil and Zac Foteff
 * @version v1 12/9/21
 */

package com.durnil.rie.nearme;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

// This takes the place of MainActivity, declared in the manifest

public class PlaceSearchActivity extends AppCompatActivity {
    private static final String URI = "https://maps.googleapis.com/maps/api/place/nearbysearch";
    private static final String API_KEY = "AIzaSyAO5ikhEmbJgIlT0Taw0Os94-XjctylvaY";
    private static final int LOCATION_REQUEST_CODE = 1;
    static final String TAG = "PlaceSearchActivityTag";

    private FusedLocationProviderClient fusedLocationClient;
    private FetchLocationsNearMeAsyncTask asyncTask;
    private double latitude;
    private double longitude;
    private List<Place> placeList;
    Place selected;
    CustomAdaptor adaptor;

    /**
     onCreate method for PlaceSeearchActivity.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_search);

        placeList = new ArrayList<>();
        EditText searchEditText = findViewById(R.id.searchEditText);

        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            /**
             onEditorAction method for the search button on the keyboard.
             */
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    String searchUrl = constructSearchURL(searchEditText.getText().toString());
                    asyncTask = new FetchLocationsNearMeAsyncTask();
                    asyncTask.execute(searchUrl);
                    return true;
                }
                return false;
            }
        });

        searchEditText.setOnTouchListener(new View.OnTouchListener() {
            /**
             onTouch method for the x button on the search bar. Clears the
             EditText and the list of Place results.
             */
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if (motionEvent.getRawX() >= searchEditText.getCompoundDrawables()[2].getBounds().left) {
                        searchEditText.setText("");
                        // hides the recyclerView
                        placeList.clear();
                    }
                }
                return false;
            }
        });

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        enableMyLocation();

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adaptor = new CustomAdaptor();
        recyclerView.setAdapter(adaptor);
    }

    class CustomAdaptor extends RecyclerView.Adapter<CustomAdaptor.CustomViewHolder> {
        class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView title;
            TextView rating;
            TextView address;
            CardView cards;

            public CustomViewHolder(@NonNull View itemView) {
                super(itemView);

                cards = itemView.findViewById(R.id.myCardView);
                title = itemView.findViewById(R.id.placeName);
                rating = itemView.findViewById(R.id.rating);
                address = itemView.findViewById(R.id.card_address);

                itemView.setOnClickListener(this);
            }

            public void updateView(Place p) {
                Log.d(TAG, "updateView: "+p.toString());
                title.setText(p.getName());
                rating.setText(Double.toString(p.getRating()));
                address.setText(p.getVicinity());
            }

            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: ");
                Intent intent = new Intent(PlaceSearchActivity.this, PlaceDetailActivity.class);
                selected = placeList.get(getAdapterPosition());
                String title = selected.getName();
                String address = selected.getVicinity();
                intent.putExtra("id", selected.getId());
                intent.putExtra("title", title);
                intent.putExtra("address", address);
                intent.putExtra("isOpen", selected.isOpen());
                startActivity(intent);
            }
        }

        @NonNull
        @Override
        public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(PlaceSearchActivity.this)
                    .inflate(R.layout.card_view, parent, false);
            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
            Place place = placeList.get(position);
            holder.updateView(place);
        }

        @Override
        public int getItemCount() {
            return placeList.size();

        }
    }

    /**
     Method to inflate the menu.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_place_search, menu);
        return true;
    }

    /**
     Method to handle clicks of menu items, both the search button, and the location
     button.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.back:
                EditText searchEditText = findViewById(R.id.searchEditText);
                String searchUrl = constructSearchURL(searchEditText.getText().toString());
                asyncTask = new FetchLocationsNearMeAsyncTask();
                asyncTask.execute(searchUrl);
                break;
            case R.id.locationMenuButton:
                enableMyLocation();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     Gets the user's permission to access their fine location. If permission has already been
     granted, gets the user's last known location.
     */
    private void enableMyLocation() {
        // get the user's permission to access their fine location
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            // get the users location
            fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                /**
                 Method for when getting the user's location is successful
                 *
                 * @param location the user's last known location
                 */
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        Toast.makeText(PlaceSearchActivity.this, "Current Location Updated", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            // need to request permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_REQUEST_CODE);
        }
    }

    /**
     Gets the result of the location permission request.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableMyLocation();
            } else {
                Toast.makeText(this, "Location Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     Constructor for the url for the API call. Adds in values entered by user.
     *
     * @param keyword the string the user typed into the search bar
     * @return the String for the url
     */
    private String constructSearchURL(String keyword) {
        String searchUrl = URI;
        searchUrl += "/json?location=";
        searchUrl += latitude + "," + longitude;
        searchUrl += "&radius=1500&rankBy=distance";
        searchUrl += "&keyword=" + keyword;
        searchUrl += "&key=" + API_KEY;
        Log.d(TAG, "constructSearchURL: " + searchUrl);
        return searchUrl;
    }

    class FetchLocationsNearMeAsyncTask extends AsyncTask<String, Void, List<Place>> {

        /**
         While the task is running, sets the progress bar to visible.
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            ProgressBar progressBar = findViewById(R.id.progressBar);
            progressBar.setVisibility(View.VISIBLE);
        }

        /**
         Makes the API call and gets the resulting JSON.
         *
         * @return the list of Places resulting from the search
         */
        @Override
        protected List<Place> doInBackground(String... strings) {
            String url = strings[0];
            List<Place> placeList = new ArrayList<>();

            try {
                URL urlObject = new URL(url);
                HttpsURLConnection urlConnection = (HttpsURLConnection) urlObject.openConnection();

                String jsonResult = "";
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while (data != -1) {
                    jsonResult += (char) data;
                    data = reader.read();
                }

                Log.d(TAG, "doInBackground: " + jsonResult);

                JSONObject jsonObject = new JSONObject(jsonResult);
                JSONArray placesArray = jsonObject.getJSONArray("results");

                for (int i = 0; i < placesArray.length(); i++) {
                    JSONObject singlePlaceObject = placesArray.getJSONObject(i);
                    Place place = parsePlace(singlePlaceObject);
                    if (place != null) {
                        placeList.add(place);
                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return placeList;
        }

        /**
         After results are received from the API calls, the place list is set
         and the progress bar disappears.
         *
         * @param placeList the resulting list of places
         */
        @Override
        protected void onPostExecute(List<Place> placeList) {
            super.onPostExecute(placeList);
            PlaceSearchActivity.this.placeList = placeList;
            ProgressBar progressBar = findViewById(R.id.progressBar);
            progressBar.setVisibility(View.INVISIBLE);
            adaptor.notifyDataSetChanged();
        }
    }

    /**
     Parses data from a single place's JSONObject. Creates a new Place object.
     *
     * @param placeJSON a single place JSONObject
     * @return the Place associated
     */
    private Place parsePlace(JSONObject placeJSON) {
        Place place = null;
        try {
            String id = placeJSON.getString("place_id");
            String name = placeJSON.getString("name");
            String vicinity = placeJSON.getString("vicinity");
            Double rating = placeJSON.getDouble("rating");

            JSONObject hours = placeJSON.getJSONObject("opening_hours");
            boolean isOpenNow = hours.getBoolean("open_now");

            Log.d(TAG, "parsePlace: " + id + " " + name + " " + vicinity + " " + rating);
            place = new Place(id,name, vicinity, isOpenNow, rating);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return place;
    }
}
