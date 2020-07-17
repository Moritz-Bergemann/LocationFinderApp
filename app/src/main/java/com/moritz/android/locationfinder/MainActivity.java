package com.moritz.android.locationfinder;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "activity_main";
    private static final int LOCATION_REQUEST_CODE = 0;
    AppViewModel model;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate() called");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Getting ViewModel
        model = new ViewModelProvider(this).get(AppViewModel.class);




//        model.getLocationEnabled().observe(this, new Observer<Boolean>() {
//            @Override
//            public void onChanged(Boolean locationEnabled) {
//                //TODO stuff about location & whatnot
//            }
//        });
//
//        //Creating listener for location changes
//        LocationListener locationListener = new LocationListener() {
//            @Override
//            public void onLocationChanged(@NonNull Location location) {
//                Log.v(TAG, "Location Changed");
//            }
//        };
//
//        TextView locationView = findViewById(R.id.location);
//
//        //Creating locationManager which will provide location info from operating system
//        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        //Getting location updates from the OS
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            //Requesting permissions if not already granted
//            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_COARSE_LOCATION }, LOCATION_REQUEST_CODE);
//        }
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case LOCATION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "User granted location permissions");
                    model.setHasLocationBeenDenied(false);
                } else {
                    Log.d(TAG, "User denied location permissions");
                    model.setHasLocationBeenDenied(true);
                }
        }
    }
}