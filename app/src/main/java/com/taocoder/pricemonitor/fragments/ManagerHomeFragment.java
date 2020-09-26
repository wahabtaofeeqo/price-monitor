package com.taocoder.pricemonitor.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.database.Observable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageButton;

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
import com.google.firebase.auth.FirebaseAuth;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.taocoder.pricemonitor.R;
import com.taocoder.pricemonitor.helpers.Utils;
import com.taocoder.pricemonitor.models.Price;
import com.taocoder.pricemonitor.models.ResponseInfo;
import com.taocoder.pricemonitor.viewModels.MainViewModel;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class ManagerHomeFragment extends Fragment implements DatePickerDialog.OnDateSetListener, Validator.ValidationListener {

    private static final long UPDATE_INTERVAL = 60000;
    private static final long FASTEST_INTERVAL = 30000;
    private static final int REQUEST_CODE = 100;

    private LocationRequest locationRequest;

    @NotEmpty
    private TextInputEditText address;

    @NotEmpty
    private TextInputEditText volume;

    private MaterialButton add;

    private String date = null;

    // Form validator
    private Validator validator;

    private MainViewModel viewModel;

    private ProgressDialog progressDialog;

    private GoogleMap map;

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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        validator = new Validator(this);
        validator.setValidationListener(this);

        progressDialog = new ProgressDialog(getContext());

        providerClient = LocationServices.getFusedLocationProviderClient(getContext());
        startLocationUpdate();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manager_home, container, false);

        address = view.findViewById(R.id.address);
        volume  = view.findViewById(R.id.volume);
        add = view.findViewById(R.id.add);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validator.validate();
            }
        });

        ImageButton imageButton = view.findViewById(R.id.date);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

                DatePickerDialog dialog = new DatePickerDialog(getContext(), ManagerHomeFragment.this,
                        calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH));
                dialog.show();
            }
        });

        viewModel.getPriceResult().observe(requireActivity(), new Observer<ResponseInfo<Price>>() {
            @Override
            public void onChanged(ResponseInfo<Price> priceResponseInfo) {
                if (priceResponseInfo == null) return;

                if (priceResponseInfo.isError()) {
                    Utils.toastMessage(getContext(), priceResponseInfo.getMessage());
                }
                else {
                    address.setText("");
                    volume.setText("");
                    add.requestFocus();
                    Utils.toastMessage(getContext(), "Added Successfully");
                }

                progressDialog.dismiss();
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        date = dayOfMonth + "/" + month + "/" + year;
    }

    @Override
    public void onValidationSucceeded() {
        if (date == null) {
            Utils.toastMessage(getContext(), "Please set the Date.");
        }
        else {

            progressDialog.setTitle("Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            Price price = new Price();
            price.setVolume(volume.getText().toString().trim());
            price.setAddress(address.getText().toString().trim());
            price.setDate(date);
            price.setUserId(FirebaseAuth.getInstance().getCurrentUser().getUid());

            viewModel.setPrice(price);
        }
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error: errors) {
            View view = error.getView();
            String errorMessage = error.getCollatedErrorMessage(getContext());
            if (view instanceof TextInputEditText) {
                ((TextInputEditText) view).setError(errorMessage);
            }
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