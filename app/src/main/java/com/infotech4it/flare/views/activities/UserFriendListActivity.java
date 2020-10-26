package com.infotech4it.flare.views.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.infotech4it.flare.R;
import com.infotech4it.flare.databinding.ActivityUserFriendListBinding;
import com.infotech4it.flare.views.adapters.UserFriendListAdapter;
import com.infotech4it.flare.views.models.UserFriendsModel;

import java.util.ArrayList;

public class UserFriendListActivity extends AppCompatActivity {
    private ActivityUserFriendListBinding binding;
    private ArrayList<UserFriendsModel> data;
    private UserFriendListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_friend_list);

        init();
    }

    private void init() {
        data = new ArrayList<>();
        adapter = new UserFriendListAdapter(this);
        for (int i = 0; i <= 200; i++) {
            data.add(new UserFriendsModel("Bilal", "Lahore, Pakistan"));
        }
        adapter.setData(data);
        binding.recyclerview.setAdapter(adapter);
    }
}