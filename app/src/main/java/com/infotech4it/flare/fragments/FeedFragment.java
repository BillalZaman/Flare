package com.infotech4it.flare.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.infotech4it.flare.R;
import com.infotech4it.flare.databinding.FragmentFeedBinding;
import com.infotech4it.flare.helpers.UIHelper;
import com.infotech4it.flare.views.activities.PostActivity;
import com.infotech4it.flare.views.adapters.NewsFeedAdapter;
import com.infotech4it.flare.views.models.NewsFeedModel;

import java.util.ArrayList;


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