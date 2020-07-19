package com.moritz.android.locationfinder;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LocationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LocationFragment extends Fragment {
    private static final int LOCATION_REQUEST_CODE = 0;

    private static final String TAG = "location_fragment";

    private AppViewModel mViewModel;
    private LinearLayout mLocationInfoLayout;
    private LinearLayout mLocationDisabledInfoLayout;

    public LocationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment LocationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LocationFragment newInstance() {
        LocationFragment fragment = new LocationFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_location, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewModel = new ViewModelProvider(requireActivity()).get(AppViewModel.class);

        mLocationInfoLayout = view.findViewById(R.id.locationInfoLayout);
        mLocationDisabledInfoLayout = view.findViewById(R.id.locationDisabledInfoLayout);

        checkLocationEnabled();

        //Resetting location-related parts of view if location settings change
        mViewModel.getLocationEnabled().observe(getActivity(), new Observer<Boolean>() { //FIXME I'm not sure if the usage of getActivity() is appropriate
            @Override
            public void onChanged(Boolean aBoolean) {
                checkLocationEnabled();
            }
        });

        //Setting up location connection stuff
        final TextView latitudeTextView = view.findViewById(R.id.latitudeTextView); //FIXME is the 'final' dodgy?
        final TextView longitudeTextView = view.findViewById(R.id.longitudeTextView);

        latitudeTextView.setText("Pending...");
        longitudeTextView.setText("Pending...");

        //Updating location fields when location changes
        mViewModel.getLocationData().observe(getActivity(), new Observer<Location>() {
            @Override
            public void onChanged(Location location) {
                latitudeTextView.setText(String.format(Locale.US, ".4%f", location.getLatitude()));
                longitudeTextView.setText(String.format(Locale.US, ".4%f", location.getLongitude()));
            }
        });
    }

    private void checkLocationEnabled() {
        try {
            if (mViewModel.getLocationEnabled().getValue()) { //If location is enabled
                //TODO
            }
            else { //If location access has been denied
                //Changing visibilities to show the right thi
                mLocationInfoLayout.setVisibility(View.GONE);
                mLocationDisabledInfoLayout.setVisibility(View.VISIBLE);
            }
        } catch (NullPointerException np) {
            Log.e(TAG, "Value of \'LocationEnabled\' was NULL"); //FIXME this is always raised
        }
    }
}