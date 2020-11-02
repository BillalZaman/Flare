package com.infotech4it.flare.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.infotech4it.flare.R;
import com.infotech4it.flare.databinding.FragmentUserFriendListBinding;
import com.infotech4it.flare.views.adapters.UserFriendListAdapter;
import com.infotech4it.flare.views.models.UserFriendsModel;

import java.util.ArrayList;

public class UserFriendListFragment extends Fragment {
    private FragmentUserFriendListBinding binding;
    private ArrayList<UserFriendsModel> data;
    private UserFriendListAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_friend_list, container, false);
        init();
        return binding.getRoot();
    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        binding = DataBindingUtil.setContentView(this, R.layout.fragment_user_friend_list);
//
//        init();
//    }

    private void init() {
        data = new ArrayList<>();
        adapter = new UserFriendListAdapter(getContext());
        for (int i = 0; i <= 200; i++) {
            data.add(new UserFriendsModel("Bilal", "Lahore, Pakistan"));
        }
        adapter.setData(data);
        binding.recyclerview.setAdapter(adapter);
    }
}