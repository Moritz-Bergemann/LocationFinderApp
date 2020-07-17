package com.moritz.android.locationfinder;

import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "activity_main";
    private static final int LOCATION_REQUEST_CODE = 0;

    private static final int NUM_PAGES = 2;

    private AppViewModel mModel;
    private ViewPager2 mPager;
    private int mPreviousPagerItem;

    private PagerAdapter mPagerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate() called");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Getting ViewModel
        mModel = new ViewModelProvider(this).get(AppViewModel.class);

        //Setting up pager
        mPager = findViewById(R.id.pager);
        mPreviousPagerItem = mPager.getCurrentItem();

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
    }

    @Override
    public void onBackPressed() {
        //Make back press move back one page (unless on first page, then just do what you'd normally do)
        if (mPager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case LOCATION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "User granted location permissions");
                    mModel.setHasLocationBeenDenied(false);
                } else {
                    Log.d(TAG, "User denied location permissions");
                    mModel.setHasLocationBeenDenied(true);
                }
        }
    }

    private class PagerAdapter extends FragmentStateAdapter {
        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return new
        }

        @Override
        public int getItemCount() {
            return 0;
        }
    }
}