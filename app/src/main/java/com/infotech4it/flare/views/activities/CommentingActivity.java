package com.infotech4it.flare.views.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import com.infotech4it.flare.R;
import com.infotech4it.flare.databinding.ActivityCommentingBinding;
import com.infotech4it.flare.views.adapters.CommentingAdapter;
import com.infotech4it.flare.views.models.CommentingModel;

import java.util.ArrayList;

public class CommentingActivity extends AppCompatActivity {
    private ActivityCommentingBinding binding;
    private ArrayList<CommentingModel> data;
    private CommentingAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_commenting);
        init();
    }

    private void init() {
        recyclerview();
        binding.imgBack.setOnClickListener(v->{
            finish();
        });
    }

    private void recyclerview() {
        data = new ArrayList<>();
        adapter = new CommentingAdapter(this);
        for (int i=0 ; i<=10; i++){
            data.add(new CommentingModel("Ali Haider","Please add me on your flare account. I am waiting for you to accept lets chat on inbox"));
        }
        adapter.setData(data);
        binding.recyclerview.setAdapter(adapter);
    }
}