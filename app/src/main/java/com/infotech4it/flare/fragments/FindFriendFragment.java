package com.infotech4it.flare.fragments;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.infotech4it.flare.R;
import com.infotech4it.flare.databinding.FragmentFindFriendBinding;
import com.infotech4it.flare.views.adapters.ChatAdapter;
import com.infotech4it.flare.views.adapters.FindFriendAdapter;
import com.infotech4it.flare.views.models.ChatModel;

import java.util.ArrayList;

public class FindFriendFragment extends Fragment {
    private FragmentFindFriendBinding binding;
    private ArrayList<ChatModel> data;
    private FindFriendAdapter chatAdapter;

    public FindFriendFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_find_friend, container, false);
        init();
        return binding.getRoot();
    }

    private void init() {
        chatAdapter = new FindFriendAdapter(getContext());
        data = new ArrayList<>();
        for(int i=0; i<= 20; i++) {
            data.add(new ChatModel("John Doe", ""+i+12, "See you Tomorrow then!",
                    "Lahore"));
        }
        chatAdapter.setData(data);
        binding.recyclerview.setAdapter(chatAdapter);
    }
}