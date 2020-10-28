package com.infotech4it.flare.views.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.infotech4it.flare.R;
import com.infotech4it.flare.databinding.ActivityMapBinding;
import com.infotech4it.flare.googleplayservices.LocationProvider;
import com.infotech4it.flare.helpers.UIHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapActivity extends AppCompatActivity implements LocationProvider.LocationCallback, OnMapReadyCallback {
    private ActivityMapBinding binding;
    private double mLatitude = 33.6160373;
    private double mLongitude = 72.9460223;
    private GoogleMap mMap;
    private Marker marker;
    private LocationManager mLocationManager;
    private boolean mLocationPermissionGranted;
    private final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 8088;
    private static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 2;
    private LocationProvider locationProvider;
    private  String userAddress = "Islamabad, Pakistan";
    private TextView tvLat;
    private MarkerOptions markerOptions;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_map);
        init();
    }

    private void init() {
        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        getLocationPermission();
        checkAndRequestPermissions();
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.fragmentMap);
        mapFragment.getMapAsync(this);
        if (checkLocationPermission()) {
            locationProvider = new LocationProvider(this,this);
            locationProvider.connect();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.clear();
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if (mMap.isIndoorEnabled()) {
            mMap.setIndoorEnabled(false);
        }

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            // Use default InfoWindow frame
            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            // Defines the contents of the InfoWindow
            @Override
            public View getInfoContents(Marker arg0) {
                View v = getLayoutInflater().inflate(R.layout.info_window_layout, null);

                // Getting the position from the marker
                LatLng latLng = arg0.getPosition();
                tvLat = v.findViewById(R.id.address);
                tvLat.setText(userAddress);
                return v;

            }
        });
        mMap.getUiSettings().setZoomControlsEnabled(true);

        mMap.setOnMyLocationChangeListener(myLocationChangeListener);
        // Setting a click event handler for the map

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {
                mMap.clear();
                mLatitude = latLng.latitude;
                mLongitude = latLng.longitude;
                Log.e("latlng", latLng + "");
                addMarker(latLng);
                new getAddressForLocation(mLatitude, mLongitude).execute();
            }
        });

        if (checkLocationPermission()) {
            checkAndRequestPermissions();
        } else {
            return;
        }
        TrackUser(mLatitude,mLongitude,userAddress);
    }

    @Override
    public void handleNewLocation(Location location) {

    }

    private GoogleMap.OnMyLocationChangeListener myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
        @Override
        public void onMyLocationChange(Location location) {
            LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());

            mMap.clear();
            mLatitude = location.getLatitude();
            mLongitude = location.getLongitude();
            marker = mMap.addMarker(new MarkerOptions()
                    .position(loc)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.location)));
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(loc, 14);
            mMap.animateCamera(cameraUpdate);

        }

    };

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;

        } else {
            ActivityCompat.requestPermissions((Activity) this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    private boolean checkAndRequestPermissions() {
        int locationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int coarsePermision = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();

        if (locationPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (coarsePermision != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),
                    REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;

    }

    private boolean checkLocationPermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void addMarker(LatLng coordinate) {
        CameraPosition cameraPosition;
        mLatitude = coordinate.latitude;
        mLongitude = coordinate.longitude;
        if (mMap != null) {

            mMap.addMarker(new MarkerOptions().position(coordinate).icon(BitmapDescriptorFactory.
                    fromResource(R.drawable.location)));
            cameraPosition = new CameraPosition.Builder().target(coordinate).zoom(18).build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        }
    }

    @SuppressLint("StaticFieldLeak")
    private class getAddressForLocation extends AsyncTask<Void, Void, Void> {
        Double latitude, longitude;

        getAddressForLocation(Double latitude, Double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Geocoder geocoder;
                List<Address> addresses;
                geocoder = new Geocoder(MapActivity.this, Locale.getDefault());
                StringBuilder sb = new StringBuilder();

                addresses = geocoder.getFromLocation(latitude, longitude, 1);

                if (addresses != null && addresses.size() > 0) {

                    String address = addresses.get(0).getAddressLine(0);
//                    if (address != null)
//                        sb.append(address).append(" ");
//                    city = addresses.get(0).getLocality();
//                    if (city != null)
//                        sb.append(city).append(" ");
//
//                    state = addresses.get(0).getAdminArea();
//                    if (state != null)
//                        sb.append(state).append(" ");
//                    country = addresses.get(0).getCountryName();
//                    if (country != null)
//                        sb.append(country).append(" ");
//
//                    String postalCode = addresses.get(0).getPostalCode();
//                    if (postalCode != null)
//                        sb.append(postalCode).append(" ");
//                    userAddress = sb.toString();

                }
            } catch (IOException e) {
                e.printStackTrace();
//                showLocation((new LatLng(latitude, longitude)));

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (userAddress == null || userAddress.equalsIgnoreCase("")) {
//                showLocation((new LatLng(latitude, longitude)));
            } else {
                MarkerOptions markerOptions;
                try {
                    mMap.clear();
                    // binding.edtSearch.setHint(""+userAddress);

                    markerOptions = new MarkerOptions().position(new LatLng(latitude, longitude))
                            .title(userAddress).icon(BitmapDescriptorFactory.fromResource(R.drawable.location));
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 14);
                    mMap.animateCamera(cameraUpdate);
                    mMap.addMarker(markerOptions).showInfoWindow();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public void TrackUser(Double latitude, Double longitude, final String addressTitle) {
//        mMap.clear();
        mLatitude = latitude;
        mLongitude = longitude;
        userAddress = addressTitle;
        try {
            mMap.clear();
            markerOptions = new MarkerOptions().position(new LatLng(mLatitude, mLongitude)).title(userAddress)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.location));
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(mLatitude, mLongitude),
                    14);
            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker marker) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {
                    View v = getLayoutInflater().inflate(R.layout.info_window_layout, null);

                    // Getting the position from the marker
                    LatLng latLng = marker.getPosition();
                    tvLat = v.findViewById(R.id.address);
                    tvLat.setText(userAddress);
                    return v;
                }
            });
            mMap.animateCamera(cameraUpdate);
            mMap.addMarker(markerOptions).showInfoWindow();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}