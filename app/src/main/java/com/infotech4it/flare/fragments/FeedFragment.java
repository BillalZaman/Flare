package com.infotech4it.flare.fragments;

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

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.infotech4it.flare.databinding.FragmentFeedBinding;
import com.infotech4it.flare.googleplayservices.LocationProvider;
import com.infotech4it.flare.helpers.UIHelper;
import com.infotech4it.flare.views.activities.PostActivity;
import com.infotech4it.flare.views.adapters.NewsFeedAdapter;
import com.infotech4it.flare.views.models.MoreModel;
import com.infotech4it.flare.views.models.NewsFeedModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class FeedFragment extends Fragment {
    private FragmentFeedBinding binding;
    private ArrayList<NewsFeedModel> data;
    private NewsFeedAdapter adapter;


    public FeedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_feed, container, false);
        init();
        return binding.getRoot();
    }

    private void init() {
        setRecyclerview();

        binding.postConst.setOnClickListener(v->{
            UIHelper.openActivity((Activity) getContext(), PostActivity.class);
        });
    }

    private void setRecyclerview() {
        adapter = new NewsFeedAdapter(getContext());
        data = new ArrayList<>();
        for(int i=0;i<=10;i++) {
            data.add(new NewsFeedModel("Bilal Zaman", "6h ago",
                    "In publishing and graphic design, Lorem ipsum is a placeholder text commonly used to demonstrate "));
        }
        adapter.setData(data);
        binding.recyclerview.setAdapter(adapter);

    }
}