package com.infotech4it.flare.views.activities;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.infotech4it.flare.R;
import com.infotech4it.flare.databinding.ActivityLoginBinding;
import com.infotech4it.flare.helpers.LoaderDialog;
import com.infotech4it.flare.helpers.UIHelper;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class LoginActivity extends AppCompatActivity {
    private final int REQUEST_LOCATION_PERMISSION = 1;
    private ActivityLoginBinding binding;
//    private FirebaseAuth firebaseAuth;
    private LoaderDialog loaderDialog;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = database.getReference("user_table");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        init();
    }

    private void init() {
        binding.setOnClick(this);
//        firebaseAuth = FirebaseAuth.getInstance();
//        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
//            UIHelper.openActivity(LoginActivity.this, HomeActivity.class);
//        }
        if (mAuth.getCurrentUser() != null) {
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
//            UIHelper.openActivity(LoginActivity.this, HomeActivity.class);
        }
        loaderDialog = new LoaderDialog(this);
//        requestLocationPermission();
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
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
//                    UIHelper.showLongToastInCenter(this, getString(R.string.internet));
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
        //authenticate user
        mAuth.signInWithEmailAndPassword(binding.edtMobilePasswordText.getText().toString(),
                binding.edtPassword.getText().toString())
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (!task.isSuccessful()) {
                            // there was an error
                            loaderDialog.dismiss();
                            UIHelper.showLongToastInCenter(LoginActivity.this, "" + task.getException().getMessage());
                        } else {

                            if (mAuth.getCurrentUser().isEmailVerified()){
                                String firebaseID = task.getResult().getUser().getUid();
                                databaseReference.child(firebaseID).child("password").setValue(binding.edtPassword.getText().toString());
//                                loaderDialog.dismiss();
                                UIHelper.openActivity(LoginActivity.this, HomeActivity.class);
                            }else {
//                                loaderDialog.dismiss();
                                Toast.makeText(LoginActivity.this, "Please verify your Email Address", Toast.LENGTH_LONG).show();
                            }

                        }
                    }
                });

//        firebaseAuth.signInWithEmailAndPassword(binding.edtMobilePasswordText.getText().toString(),
//                binding.edtPassword.getText().toString()).addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                Handler handler = new Handler();
//                handler.postDelayed(() -> UIHelper.openActivity(LoginActivity.this, HomeActivity.class), 3000);
//            } else {
//                loaderDialog.dismiss();
//                Toast.makeText(LoginActivity.this, "" + task.getException().getMessage(),
//                        Toast.LENGTH_SHORT).show();
//            }
//        });
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