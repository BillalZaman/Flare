package com.infotech4it.flare.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.infotech4it.flare.R;
import com.infotech4it.flare.databinding.FragmentFindFriendBinding;
import com.infotech4it.flare.helpers.UIHelper;
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_find_friend, container, false);
        init();
        return binding.getRoot();
    }

    private void init() {
        chatAdapter = new FindFriendAdapter(getContext());
        data = new ArrayList<>();
        for (int i = 0; i <= 20; i++) {
            data.add(new ChatModel("John Doe", "" + i + 12, "See you Tomorrow then!",
                    "Lahore"));
        }
        chatAdapter.setData(data);
        binding.recyclerview.setAdapter(chatAdapter);
        binding.setOnClick(this);

        UserFriendListFragment userFriendListFragment = new UserFriendListFragment();
        UIHelper.replaceFragment(getContext(), R.id.frameLayout, userFriendListFragment);
        binding.txtFriends.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        binding.txtFriends.setTextColor(getResources().getColor(R.color.white));
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtFriends: {
                UserFriendListFragment userFriendListFragment = new UserFriendListFragment();
                UIHelper.replaceFragment(getContext(), R.id.frameLayout, userFriendListFragment);
                binding.txtFriends.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                binding.txtFriends.setTextColor(getResources().getColor(R.color.white));
                resetColorPreference(1);
                break;
            }
            case R.id.txtFindFriends: {
                UserFriendListFragment userFriendListFragment = new UserFriendListFragment();
                UIHelper.replaceFragment(getContext(), R.id.frameLayout, userFriendListFragment);
                binding.txtFindFriends.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                binding.txtFindFriends.setTextColor(getResources().getColor(R.color.white));
                resetColorPreference(2);
                break;
            }
            case R.id.txtRequests: {
                UserFriendListFragment userFriendListFragment = new UserFriendListFragment();
                UIHelper.replaceFragment(getContext(), R.id.frameLayout, userFriendListFragment);
                binding.txtRequests.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                binding.txtRequests.setTextColor(getResources().getColor(R.color.white));
                resetColorPreference(3);
                break;
            }
        }
    }

    public void resetColorPreference(int position) {
        if (position == 1) {
//                binding.txtFriends.setBackgroundColor(getResources().getColor(R.color.grey_light));
//                binding.txtFriends.setTextColor(getResources().getColor(R.color.black));

            binding.txtFindFriends.setBackgroundColor(getResources().getColor(R.color.light_color));
            binding.txtFindFriends.setTextColor(getResources().getColor(R.color.black));

            binding.txtRequests.setBackgroundColor(getResources().getColor(R.color.light_color));
            binding.txtRequests.setTextColor(getResources().getColor(R.color.black));

        } else if (position == 2) {
            binding.txtFriends.setBackgroundColor(getResources().getColor(R.color.light_color));
            binding.txtFriends.setTextColor(getResources().getColor(R.color.black));

//                binding.txtFindFriends.setBackgroundColor(getResources().getColor(R.color.light_color));
//                binding.txtFindFriends.setTextColor(getResources().getColor(R.color.black));

            binding.txtRequests.setBackgroundColor(getResources().getColor(R.color.light_color));
            binding.txtRequests.setTextColor(getResources().getColor(R.color.black));

        } else if (position == 3) {
            binding.txtFriends.setBackgroundColor(getResources().getColor(R.color.light_color));
            binding.txtFriends.setTextColor(getResources().getColor(R.color.black));

            binding.txtFindFriends.setBackgroundColor(getResources().getColor(R.color.light_color));
            binding.txtFindFriends.setTextColor(getResources().getColor(R.color.black));

//                binding.txtRequests.setBackgroundColor(getResources().getColor(R.color.light_color));
//                binding.txtRequests.setTextColor(getResources().getColor(R.color.black));

        }
    }
}