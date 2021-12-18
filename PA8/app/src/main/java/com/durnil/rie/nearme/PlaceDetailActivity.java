/**
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

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

public class PlaceDetailActivity extends AppCompatActivity {
    private static final String URI = "https://maps.googleapis.com/maps/api/place";
    private static final String API_KEY = "AIzaSyAO5ikhEmbJgIlT0Taw0Os94-XjctylvaY";
    private TextView title;
    private TextView address;
    private TextView phone;
    private TextView review;
    private FetchReviewsAndPhonesTask asyncTask;
    static final String TAG = "PlaceSearchDetailTag";
    private List<String> detailsList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail);

        title = (TextView) findViewById(R.id.name);
        address = (TextView) findViewById(R.id.address);
        phone = (TextView) findViewById(R.id.phone);
        review = (TextView) findViewById(R.id.review);
        detailsList = new ArrayList<>();

        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        String newTitle = intent.getStringExtra("title");
        String newAddress = intent.getStringExtra("address");
        boolean isOpenNow = intent.getBooleanExtra("isOpen", false);

        asyncTask = new FetchReviewsAndPhonesTask();
        asyncTask.execute(constructDetailURL(id));

        if (isOpenNow)
            title.setText(String.format("%s(Open)", newTitle));
        else
            title.setText(String.format("%s (Closed)", newTitle));
        address.setText(newAddress);
    }

    class FetchReviewsAndPhonesTask extends AsyncTask<String, Void, List<String>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            detailsList.clear();
        }

        /**
         Makes the API call and gets the resulting JSON.
         * @return the list of reviews and phone numbers
         */
        @Override
        protected List<String> doInBackground(String... strings) {
            String url = strings[0];
            List<String> result = new ArrayList<>();

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

                JSONObject obj = new JSONObject(jsonResult);
                Log.d(TAG, "doInBackground: " + obj);
                result = parseResults(obj);
                Log.d(TAG, "doInBackground: "+ result);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return result;
        }

        /**
         After results are received from the API calls, the phone and review fields are set
         * @param result the resulting list of places
         */
        @Override
        protected void onPostExecute(List<String> result) {
            super.onPostExecute(result);

            PlaceDetailActivity.this.phone.setText(result.get(0));
            PlaceDetailActivity.this.review.setText(result.get(1));
        }
    }

    class FetchPhoto extends AsyncTask<String, Void, List<String>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /**
         Makes the API call and gets the resulting JSON.
         * @return the list of reviews and phone numbers
         */
        @Override
        protected List<String> doInBackground(String... strings) {
            String url = strings[0];
            List<String> result = new ArrayList<>();

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

                JSONObject obj = new JSONObject(jsonResult);
                Log.d(TAG, "doInBackground: " + obj);
                result = parseResults(obj);
                Log.d(TAG, "doInBackground: "+ result);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return result;
        }

        /**
         After results are received from the API calls, the phone and review fields are set
         * @param result the resulting list of places
         */
        @Override
        protected void onPostExecute(List<String> result) {
            super.onPostExecute(result);

            PlaceDetailActivity.this.phone.setText(result.get(0));
            PlaceDetailActivity.this.review.setText(result.get(1));
        }
    }

    private String constructDetailURL(String id) {
        String url = URI;
        url += "/details/json?place_id=";
        url += id;
        url += "&fields=review%2Cformatted_phone_number";
        url += "&key="+API_KEY;
        Log.d(TAG, "constructSearchURL: " + url);
        return url;
    }

    private String constructPhotoURL(String id) {
        String url = URI;
        url += "/photo$place_id="+id;
        url += "&key="+API_KEY;
        return url;
    }

    private List<String> parseResults (JSONObject json) {
        List<String> result = new ArrayList<>();
        try {
            //  Get phone number
            JSONObject jsonResult = json.getJSONObject("result");
            String phoneNumber = jsonResult.getString("formatted_phone_number");
            result.add(phoneNumber);

            //  Get first review
            JSONArray reviews = jsonResult.getJSONArray("reviews");
            String reviewText = reviews.getJSONObject(0).getString("text");
            result.add(reviewText);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Method creates menu item to return the user to the search activity
     *
     * @param menu new menu resource
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_place_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.back:
                Intent intent = new Intent(PlaceDetailActivity.this, PlaceSearchActivity.class);
                startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}

