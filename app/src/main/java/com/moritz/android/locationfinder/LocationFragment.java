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
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

    }

    private void checkLocationEnabled() {
        try {
            if (mViewModel.getLocationEnabled().getValue()) { //If location is enabled
                Log.d(TAG, "Location fragment found location to be enabled");

                mLocationInfoLayout.setVisibility(View.VISIBLE);
                mLocationDisabledInfoLayout.setVisibility(View.GONE);

                //Creating locationManager which will provide location info from operating system
                LocationManager locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);

                //Creating listener for location changes (that will update the model accordingly)
                LocationListener locationListener = mViewModel.createLocationListener();

                //Crashing in case model is wrong about having location permissions
                if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    throw new IllegalArgumentException("Did not have location permissions when model believed it did");
                }

                //Getting location updates (using listener tied to ViewModel)
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);

                //Initialising location value
                mViewModel.initialiseLocationData(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));

                //Setting up location connection stuff
                final TextView latitudeTextView = requireView().findViewById(R.id.latitudeTextView); //FIXME is the 'final' dodgy?
                final TextView longitudeTextView = requireView().findViewById(R.id.longitudeTextView);

                if (mViewModel.getLocationData().getValue() == null) {
                    latitudeTextView.setText(R.string.pending_location);
                    longitudeTextView.setText(R.string.pending_location);
                }

                //Updating location fields when location changes
                mViewModel.getLocationData().observe(requireActivity(), new Observer<Location>() {
                    @Override
                    public void onChanged(Location location) {
                        if (location != null) { //FIXME don't think this should be necessary
                            latitudeTextView.setText(String.format(Locale.US, "%.4f", location.getLatitude()));
                            longitudeTextView.setText(String.format(Locale.US, "%.4f", location.getLongitude()));
                        } else {
                            Toast.makeText(getContext(), R.string.failed_to_get_location, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                //Saving location functionality
                Button saveCurLocationButton = requireView().findViewById(R.id.saveCurLocationButton);
                TextView lastSavedPositionTextView = requireView().findViewById(R.id.lastSavedPositionTextView);

                showSavedLocation(mViewModel.getLocationData().getValue(), lastSavedPositionTextView); //Shows saved location if it already exists (e.g. after rotate)

                mViewModel.getSavedLocation().observe(getViewLifecycleOwner(), new Observer<Location>() {
                    @Override
                    public void onChanged(Location location) {
                        showSavedLocation(location, lastSavedPositionTextView);
                    }
                });

                saveCurLocationButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mViewModel.setSavedLocation(mViewModel.getLocationData().getValue());
                    }
                });

                //Show distance to saved location functionality
                Button getDistToSavedButton = requireView().findViewById(R.id.getDistToSavedButton);
                TextView distanceFromSavedPositionTextView = requireView().findViewById(R.id.distanceFromSavedPositionTextView);

                showDistance(mViewModel.getDistanceToSaved().getValue(), distanceFromSavedPositionTextView);

                mViewModel.getDistanceToSaved().observe(getViewLifecycleOwner(), new Observer<Float>() {
                    @Override
                    public void onChanged(Float aFloat) {
                        showDistance(aFloat, distanceFromSavedPositionTextView);
                    }
                });

                getDistToSavedButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Location curLocation = mViewModel.getLocationData().getValue();
                        Location savedLocation = mViewModel.getSavedLocation().getValue();

                        if (savedLocation != null) { //If a saved location has been set
                            //Setting distance value in ViewModel
                            mViewModel.setDistanceToSaved(curLocation.distanceTo(savedLocation));
                        } else {
                            Toast.makeText(requireActivity(), R.string.need_saved_location, Toast.LENGTH_SHORT).show();
                        }

                    }
                });

            } else { //If location access has been denied
                //Changing visibilities to show only the notice about options being unavailable
                Log.d(TAG, "Location fragment found location to be disabled");

                mLocationInfoLayout.setVisibility(View.GONE);
                mLocationDisabledInfoLayout.setVisibility(View.VISIBLE);

                mViewModel.getLocationData().removeObservers(getViewLifecycleOwner()); //FIXME what if there's other observers elsewhere?

                //FIXME does this clean up any previous location listeners properly?
            }
        } catch (NullPointerException np) {
            Log.e(TAG, "Value of \'LocationEnabled\' was NULL");
        }
    }

    private void showSavedLocation(Location location, TextView textView) {
        if (location != null) {
            String locationString = String.format(Locale.US, "%.4f N %.4f W",
                    location.getLatitude(), location.getLongitude());
            textView.setText(locationString);
        }
    }

    private void showDistance(Float aFloat, TextView textView) {
        if (aFloat != null) {
            String distanceString = String.format(Locale.US, "%.2f m", aFloat);
            textView.setText(distanceString);
        }
    }
}