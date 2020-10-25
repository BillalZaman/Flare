package com.infotech4it.flare.views.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import com.infotech4it.flare.R;
import com.infotech4it.flare.databinding.ActivityChatDetailBinding;

public class ChatDetailActivity extends AppCompatActivity {
    private ActivityChatDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_chat_detail);
        init();
    }

    private void init() {

    }
}