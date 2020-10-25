package com.infotech4it.flare.views.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.view.View;

import com.infotech4it.flare.R;
import com.infotech4it.flare.databinding.ActivityLoginBinding;
import com.infotech4it.flare.helpers.UIHelper;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_login);
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
            case R.id.btnLogin:{
                UIHelper.openActivity(this, HomeActivity.class);
                break;
            }
            case R.id.txtForgotPassword:{
                UIHelper.openActivity(this, ChangePasswordActivity.class);
                break;
            }
            case R.id.txtRegister:{
                UIHelper.openActivity(this, RegistrationActivity.class);
                break;
            }
        }
    }
}