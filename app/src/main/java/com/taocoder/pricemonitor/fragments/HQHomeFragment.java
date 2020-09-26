package com.taocoder.pricemonitor.fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.taocoder.pricemonitor.R;
import com.taocoder.pricemonitor.helpers.Utils;
import com.taocoder.pricemonitor.models.ResponseInfo;
import com.taocoder.pricemonitor.models.StationAddress;
import com.taocoder.pricemonitor.viewModels.MainViewModel;

import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HQHomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HQHomeFragment extends Fragment {

    private static final long UPDATE_INTERVAL = 60000;
    private static final long FASTEST_INTERVAL = 30000;
    private static final int REQUEST_CODE = 100;

    private LocationRequest locationRequest;

    @NotEmpty
    private TextInputEditText address;

    private MaterialButton add;

    private MainViewModel viewModel;

    private ProgressDialog progressDialog;

    private GoogleMap map;

    private LatLng mLatLng;

    //Location info provider
    private FusedLocationProviderClient providerClient;

    private Geocoder geocoder;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        @Override
        public void onMapReady(GoogleMap googleMap) {
            map = googleMap;

            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                providerClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null)
                            updateMap(location);
                    }
                });
            }

            map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    getAddress(latLng);
                    mLatLng = latLng;
                }
            });

            startLocationUpdate();
        }
    };

    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            if (locationResult.getLocations().size() > 0) {
                updateMap(locationResult.getLastLocation());
            }
        }
    };

    public HQHomeFragment() {
        // Required empty public constructor
    }

    public static HQHomeFragment newInstance(String param1, String param2) {
        return new HQHomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        progressDialog = new ProgressDialog(getContext());
        providerClient = LocationServices.getFusedLocationProviderClient(getContext());

        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        startLocationUpdate();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_h_q_home, container, false);

        address = rootView.findViewById(R.id.address);
        add = rootView.findViewById(R.id.add);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (address.getText().toString().trim().isEmpty()) {
                    address.setError("Enter Address");
                }
                else {
                    StationAddress station = new StationAddress();
                    station.setAddress(address.getText().toString().trim());
                    station.setLatitude(mLatLng.latitude);
                    station.setLongitude(mLatLng.longitude);

                    progressDialog.setTitle("Please wait...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    viewModel.addAddress(station);
                }
            }
        });

        viewModel.getAddressResult().observe(requireActivity(), new Observer<ResponseInfo<StationAddress>>() {
            @Override
            public void onChanged(ResponseInfo<StationAddress> stationAddressResponseInfo) {
                if (stationAddressResponseInfo ==  null) return;

                if (stationAddressResponseInfo.isError()) {
                    Utils.toastMessage(getContext(), stationAddressResponseInfo.getMessage());
                }
                else {
                    Utils.toastMessage(getContext(), "Address added");
                    address.setText("");
                    add.requestFocus();
                }
                progressDialog.dismiss();
            }
        });

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    private void startLocationUpdate() {

        createLocationRequest();
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            providerClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
            if (map != null) {
                map.setMyLocationEnabled(true);
            }
        }
        else {
            askPermission();
        }
    }

    private void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }

    private void askPermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
    }

    private void updateMap(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title("My Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker());
        markerOptions.position(latLng);

        if (map != null) {
            map.clear();
            map.addMarker(markerOptions);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (providerClient != null)
            providerClient.removeLocationUpdates(locationCallback);
    }

    private void getAddress(LatLng latLng) {
        if (geocoder == null) {
            geocoder = new Geocoder(getContext(), Locale.getDefault());
        }

        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            address.setText(addresses.get(0).getAddressLine(0));
        }
        catch (Exception e) {

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length == 0) {
                Utils.toastMessage(getContext(), "Location Permission is required");
            }
            else {
                startLocationUpdate();
            }
        }
    }
}