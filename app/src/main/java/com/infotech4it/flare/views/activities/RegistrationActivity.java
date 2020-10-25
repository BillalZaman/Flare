package com.infotech4it.flare.views.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.view.View;

import com.infotech4it.flare.R;
import com.infotech4it.flare.databinding.ActivityRegistrationBinding;
import com.infotech4it.flare.helpers.UIHelper;

public class RegistrationActivity extends AppCompatActivity {
    private ActivityRegistrationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_registration);

        init();
    }

    private void init() {
        binding.setOnClick(this);
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.imgBack:{
                finish();
                break;
            }
            case R.id.btnRegister:{
                UIHelper.openActivity(this, HomeActivity.class);
                break;
            }
        }
    }
}