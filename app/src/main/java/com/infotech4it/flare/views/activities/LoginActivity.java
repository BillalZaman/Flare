package com.infotech4it.flare.views.activities;

import android.Manifest;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.firebase.auth.FirebaseAuth;
import com.infotech4it.flare.R;
import com.infotech4it.flare.databinding.ActivityLoginBinding;
import com.infotech4it.flare.helpers.LoaderDialog;
import com.infotech4it.flare.helpers.UIHelper;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class LoginActivity extends AppCompatActivity {
    private final int REQUEST_LOCATION_PERMISSION = 1;
    private ActivityLoginBinding binding;
    private FirebaseAuth firebaseAuth;
    private LoaderDialog loaderDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        init();
    }

    private void init() {
        binding.setOnClick(this);
        firebaseAuth = FirebaseAuth.getInstance();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            UIHelper.openActivity(LoginActivity.this, HomeActivity.class);
        }
        loaderDialog = new LoaderDialog(this);
        requestLocationPermission();
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
                        LoggedIn();
                    }
                } else {
                    UIHelper.showLongToastInCenter(this, getString(R.string.internet));
                }
                break;
            }
            case R.id.txtForgotPassword: {
                UIHelper.openActivityAndSendActivityName(this, ChangePasswordActivity.class, "login");
                break;
            }
            case R.id.txtRegister: {
                UIHelper.openActivity(this, RegistrationActivity.class);
                break;
            }
        }
    }

    private void LoggedIn() {
        firebaseAuth.signInWithEmailAndPassword(binding.edtMobilePasswordText.getText().toString(),
                binding.edtPassword.getText().toString()).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Handler handler = new Handler();
                handler.postDelayed(() -> UIHelper.openActivity(LoginActivity.this, HomeActivity.class), 3000);
            } else {
                loaderDialog.dismiss();
                Toast.makeText(LoginActivity.this, "" + task.getException().getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validation() {
        boolean check = true;
        binding.edtMobilePasswordText.setError(null);
        binding.edtPassword.setError(null);

        if (binding.edtMobilePasswordText.getText().toString().isEmpty()) {
            binding.textInputLayout.setError("Email Field cannot be Empty");
            check = false;
        } else if (binding.edtPassword.getText().toString().isEmpty()) {
            binding.textInputLayout.setError("Password Field cannot be Empty");
            check = false;
        }
        return check;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @AfterPermissionGranted(REQUEST_LOCATION_PERMISSION)
    public void requestLocationPermission() {
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION};
        if (EasyPermissions.hasPermissions(this, perms)) {
            Toast.makeText(this, "Permission already granted", Toast.LENGTH_SHORT).show();
        } else {
            EasyPermissions.requestPermissions(this, "Please grant the location permission", REQUEST_LOCATION_PERMISSION, perms);
        }
    }
}