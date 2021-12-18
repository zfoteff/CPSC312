package com.cs312.jumpshot;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.cs312.jumpshot.databinding.MapViewBinding;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapViewActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationClickListener {
    static final String TAG = "MapViewActivity";
    static final int LOCATION_REQUEST_CODE = 1;
    private GoogleMap mMap;
    private MapViewBinding binding;
    private EventDBHelper dbHelper;
    private List<Event> eventList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: Created map view");
        super.onCreate(savedInstanceState);
        dbHelper = new EventDBHelper(this);
        eventList = dbHelper.getEvents();
        binding = MapViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        addMarkers();
        enableMyLocation();
    }

    private LatLng getLatLng(String addrString) {
        LatLng coords = null;
        Geocoder geocoder = new Geocoder(this);
        try {
            List<Address> addressList = geocoder.getFromLocationName(addrString, 1);
            if (addressList != null && addressList.size() > 0) {
                Address address = addressList.get(0);
                coords = new LatLng(address.getLatitude(), address.getLongitude());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return coords;
    }

    private void addMarkers() {
        for (Event event : eventList)
            addMarker(event.getLocation(), event);
    }

    private void addMarker(String addrString, Event e) {
        LatLng newCoords = getLatLng(addrString);
        if (newCoords != null) {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.title(e.getEventName());
            markerOptions.snippet(e.getStartTime());
            markerOptions.position(newCoords);
            mMap.addMarker(markerOptions);
        }
    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            // if we have permission
            mMap.setMyLocationEnabled(true);
            mMap.setOnMyLocationClickListener(this);
        }
        else {
            // creates an alert dialog and prompts the user to choose grant or deny
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_REQUEST_CODE);
        }
    }
}
