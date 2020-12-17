package com.infotech4it.flare.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.infotech4it.flare.R;
import com.infotech4it.flare.constant.Constant;
import com.infotech4it.flare.databinding.FragmentProfileBinding;
import com.infotech4it.flare.helpers.PreferenceHelper;
import com.infotech4it.flare.helpers.UIHelper;
import com.infotech4it.flare.interfaces.ImgClickInterface;
import com.infotech4it.flare.views.activities.PostActivity;
import com.infotech4it.flare.views.adapters.NewsFeedAdapter;
import com.infotech4it.flare.views.adapters.UserFriendAdapter;
import com.infotech4it.flare.views.models.NewsFeedModel;
import com.infotech4it.flare.views.models.UserFriendsModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import pl.aprilapps.easyphotopicker.ChooserType;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import pl.aprilapps.easyphotopicker.MediaFile;
import pl.aprilapps.easyphotopicker.MediaSource;

import static android.Manifest.permission.CAMERA;


public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;
    private ArrayList<UserFriendsModel> data;
    private UserFriendAdapter adapter;
    private ArrayList<NewsFeedModel> newsData;
    private NewsFeedAdapter newsAdapter;
    private ImgClickInterface imgClickInterface;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);
        init();
        return binding.getRoot();
    }

    private void init() {
        binding.setOnClick(this);
        imgClickInterface = (ImgClickInterface) getContext();
        adapter = new UserFriendAdapter(getContext());
        data = new ArrayList<>();
        adapter.setData(data);
        binding.recyclerview.setAdapter(adapter);
        setNewsRecyclerview();
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.imgUser:{
//                selectImage();
                imgClickInterface.ImgClick("user");
                break;
            }
            case R.id.imgcover:{

                break;
            }
        }
    }


    private void setNewsRecyclerview() {
        newsAdapter = new NewsFeedAdapter(getContext());
        newsData = new ArrayList<>();
        for (int i = 0; i <= 10; i++) {
            newsData.add(new NewsFeedModel("Bilal Zaman", "6h ago",
                    "In publishing and graphic design, Lorem ipsum is a placeholder text commonly used to demonstrate "));
        }
        newsAdapter.setData(newsData);
        binding.recyclerviewUserPost.setAdapter(newsAdapter);
    }
}