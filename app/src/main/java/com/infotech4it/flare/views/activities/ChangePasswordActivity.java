package com.infotech4it.flare.views.activities;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.firebase.auth.FirebaseAuth;
import com.infotech4it.flare.R;
import com.infotech4it.flare.constant.Constant;
import com.infotech4it.flare.databinding.ActivityChangePasswordBinding;
import com.infotech4it.flare.helpers.LoaderDialog;
import com.infotech4it.flare.helpers.UIHelper;

public class ChangePasswordActivity extends AppCompatActivity {
    private ActivityChangePasswordBinding binding;
    private FirebaseAuth firebaseAuth;
    private LoaderDialog loaderDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_change_password);

        init();
    }

    private void init() {
        firebaseAuth = FirebaseAuth.getInstance();
        loaderDialog = new LoaderDialog(this);
        binding.setOnClick(this);
        if (getIntent().getStringExtra(Constant.CLASS_NAME) != null) {
            if (getIntent().getStringExtra(Constant.CLASS_NAME).equalsIgnoreCase("login")) {
                binding.textView.setText("Forgot Password");
                binding.btnLogin.setText("Forgot Password");
            }
        } else {
            binding.textView.setText("Change Password");
            binding.btnLogin.setText("Change Password");
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgBack: {
                finish();
                break;
            }
            case R.id.btnLogin: {
                if (UIHelper.isNetworkAvailable(this)) {
                    if (validation()) {
                        getYourNewPassword();
                    }
                } else {
                    UIHelper.showLongToastInCenter(this, getString(R.string.internet));
                }
                break;
            }
        }
    }

    private void getYourNewPassword() {
        firebaseAuth.sendPasswordResetEmail(binding.edtEmail.getText().toString()).addOnCompleteListener(
                task -> {
                    if (task.isSuccessful()) {

                            UIHelper.showLongToastInCenter(ChangePasswordActivity.this,
                                    "The Reset Password Link has been sent to the Register Email");
                            finish();
                    } else {
//                        loaderDialog.dismiss();
                        UIHelper.showLongToastInCenter(ChangePasswordActivity.this, task.getException().getMessage());
                    }
                }
        );
    }

    private boolean validation() {
        boolean check = true;
        binding.edtEmail.setError(null);

        if (binding.edtEmail.getText().toString().isEmpty()) {
            binding.textInputLayout.setError("Email Field cannot be Empty");
            check = false;
        }
        return check;
    }
}