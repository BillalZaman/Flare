package com.infotech4it.flare.fragments;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.infotech4it.flare.R;
import com.infotech4it.flare.databinding.FragmentFeedBinding;
import com.infotech4it.flare.googleplayservices.GetAddressIntentService;
import com.infotech4it.flare.helpers.LoaderDialog;
import com.infotech4it.flare.views.activities.PostActivity;
import com.infotech4it.flare.views.adapters.BlogRecycleAdapter;
import com.infotech4it.flare.views.models.BlogPost;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


public class LocationFeedFragment extends Fragment {
    private FragmentFeedBinding binding;
    private List<BlogPost> blogList;
    private List<BlogPost> blogListFilter;
    private DocumentSnapshot lastVisible;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth mauth;
    private BlogRecycleAdapter blogRecycleAdapter;
    private boolean firstPageLoaded = true;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String user_id, currentUid;
    Context context;
    private LoaderDialog loaderDialog;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 2;
    private LocationAddressResultReceiver addressResultReceiver;
    private Location currentLocation;
    private LocationCallback locationCallback;
    double longitude, latitude;
    boolean isFirst=true, isSecond=false;


    public LocationFeedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_feed, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getActivity();
        loaderDialog = new LoaderDialog(getActivity());
//        turnOnLocation();
        addressResultReceiver = new LocationAddressResultReceiver(new Handler());
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                currentLocation = locationResult.getLocations().get(0);
                getAddress();
            }
        };
        startLocationUpdates();
        init();
        currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public void turnOnLocation(){

        LocationManager lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        if(!gps_enabled && !network_enabled) {
            // notify user
            new AlertDialog.Builder(context)
                    .setMessage("Turn On GPS Locations")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            getActivity().startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            getActivity().finish();
                        }
                    })
                    .show();
        }else {
//            loadPosts();
        }

    }

    private void init() {
        binding.postConst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser != null) {
                    user_id = mAuth.getCurrentUser().getUid();
                    firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.getResult().exists()) {
                                Intent addPost = new Intent(getActivity(), PostActivity.class);
                                startActivity(addPost);
                            } else {
                                Toast.makeText(getContext(), "Please set up Name and Profile", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(getContext(), "No Record Found", Toast.LENGTH_LONG).show();
                }


            }
        });

        binding.imgFilter.setOnClickListener(v->{
            binding.postConst.setVisibility(View.VISIBLE);
        });

        binding.txtEmerAlert.setOnClickListener(v->{
            binding.postConst.setVisibility(View.GONE);
        });

        binding.edtFindFriend.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                blogRecycleAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void loadPosts(){
        loaderDialog.startLoadingDialog();
        blogList = new ArrayList<>();
        blogListFilter = new ArrayList<>();
        blogRecycleAdapter = new BlogRecycleAdapter(blogList, getActivity());
        binding.recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerview.setAdapter(blogRecycleAdapter);
        firebaseFirestore = FirebaseFirestore.getInstance();

        firebaseFirestore.collection("Posts").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        if (documentSnapshots.isEmpty()) {
                            loaderDialog.dismiss();
                            String TAG="CurrentUserPosts";
                            Log.d(TAG, "onSuccess: LIST EMPTY");
                            Toast.makeText(getContext(), "Current User Posts List is Empty", Toast.LENGTH_LONG).show();
                            return;
                        } else {

                            for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                String BlogPostId = doc.getDocument().getId();
                                BlogPost blogPost = doc.getDocument().toObject(BlogPost.class).withId(BlogPostId);
                                blogList.add(blogPost);
                            }

                            for (int i=0; i<blogList.size(); i++){

                                if (blogList.get(i).getKilometer()!=-1){

                                    double distance = getdistance(latitude, longitude,
                                            blogList.get(i).getLatitude(), blogList.get(i).getLongitude());

                                    double kmdistance = distance / 0.62137;

                                    int a = (int) Math.round(kmdistance);

                                    int b = (int) Math.round(blogList.get(i).getKilometer());

                                    if (b>=a){
                                        BlogPost blogPost = blogList.get(i);
                                        blogListFilter.add(blogPost);
                                    }

                                }

                            }

//                            blogList.clear();
//                            blogList.addAll(blogListFilter);
                            blogRecycleAdapter.updateAdapter(blogListFilter);
//                            blogRecycleAdapter.notifyDataSetChanged();
                            loaderDialog.dismiss();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loaderDialog.dismiss();
                Toast.makeText(getContext(), "Error getting Current User Posts", Toast.LENGTH_LONG).show();
            }
        });


    }

    private double getdistance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }


    @SuppressWarnings("MissingPermission")
    private void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new
                            String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
        else {
            LocationRequest locationRequest = new LocationRequest();
            locationRequest.setInterval(2000);
            locationRequest.setFastestInterval(1000);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        }
    }

    @SuppressWarnings("MissingPermission")
    private void getAddress() {
        if (!Geocoder.isPresent()) {
            Toast.makeText(context, "Can't find current address, ",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(getActivity(), GetAddressIntentService.class);
        intent.putExtra("add_receiver", addressResultReceiver);
        intent.putExtra("add_location", currentLocation);
        getActivity().startService(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull
            int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            }
            else {
                Toast.makeText(context, "Location permission not granted, " + "restart the app if you want the feature", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class LocationAddressResultReceiver extends ResultReceiver {
        LocationAddressResultReceiver(Handler handler) {
            super(handler);
        }
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            if (resultCode == 0) {
                Log.d("Address", "Location null retrying");
                getAddress();
            }
            if (resultCode == 1) {
                Toast.makeText(context, "Address not found", Toast.LENGTH_SHORT).show();
            }

            if (resultCode == 2) {
//                Toast.makeText(PostActivity.this, "Address found Successfully", Toast.LENGTH_SHORT).show();
                String currentAdd = resultData.getString("address_result");
                latitude = resultData.getDouble("latitude");
                longitude = resultData.getDouble("longitude");
//                Toast.makeText(PostActivity.this, latitude+" -- "+longitude, Toast.LENGTH_SHORT).show();
                if (isFirst){
                    loadPosts();
                    isFirst=false;
                    isSecond=true;
                }
            }

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        startLocationUpdates();
        turnOnLocation();
        if (getUserVisibleHint()){
            if (isSecond){
                loadPosts();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

}