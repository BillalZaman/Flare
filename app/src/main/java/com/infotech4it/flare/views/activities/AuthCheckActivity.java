package com.infotech4it.flare.views.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.view.View;

import com.infotech4it.flare.R;
import com.infotech4it.flare.databinding.ActivityAuthCheckBinding;
import com.infotech4it.flare.helpers.UIHelper;

public class AuthCheckActivity extends AppCompatActivity {
    private ActivityAuthCheckBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_auth_check);

        init();
    }

    private void init() {
        binding.setOnClick(this);
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.btnSignin:{
                UIHelper.openActivity(this, LoginActivity.class);
                break;
            }
            case R.id.btnSignup:{

                break;
            }
        }
    }
}