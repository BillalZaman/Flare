package com.infotech4it.flare.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.infotech4it.flare.R;
import com.infotech4it.flare.databinding.FragmentProfileBinding;
import com.infotech4it.flare.helpers.UIHelper;
import com.infotech4it.flare.views.activities.PostActivity;
import com.infotech4it.flare.views.adapters.NewsFeedAdapter;
import com.infotech4it.flare.views.adapters.UserFriendAdapter;
import com.infotech4it.flare.views.models.NewsFeedModel;
import com.infotech4it.flare.views.models.UserFriendsModel;

import java.util.ArrayList;


public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;
    private ArrayList<UserFriendsModel> data;
    private UserFriendAdapter adapter;

    private ArrayList<NewsFeedModel> newsData;
    private NewsFeedAdapter newsAdapter;

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
        adapter = new UserFriendAdapter(getContext());
        data = new ArrayList<>();
        adapter.setData(data);
        binding.recyclerview.setAdapter(adapter);

        binding.postConst.setOnClickListener(v->{
            UIHelper.openActivity((Activity) getContext(), PostActivity.class);
        });

        setNewsRecyclerview();
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