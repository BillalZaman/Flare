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

    private ActivityLoginBinding binding;
    private LoaderDialog loaderDialog;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = database.getReference("user_table");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        init();
    }

    private void init() {
        binding.setOnClickLogin(this);
        loaderDialog = new LoaderDialog(this);
        if (mAuth.getCurrentUser() != null) {
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }

    }

    public void onClickLogin(View view) {
        switch (view.getId()) {
            case R.id.imgBack: {
                finish();
                break;
            }
            case R.id.btnLogin: {
                if (validation()) {
                    LoggedIn();
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
        loaderDialog.startLoadingDialog();
        mAuth.signInWithEmailAndPassword(binding.edtMobilePasswordText.getText().toString(),
                binding.edtPassword.getText().toString())
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (!task.isSuccessful()) {
                            loaderDialog.dismiss();
                            Toast.makeText(LoginActivity.this, "There is no Record of this User. Please Sign Up", Toast.LENGTH_LONG).show();

                        } else {

                            if (mAuth.getCurrentUser().isEmailVerified()){
                                String firebaseID = task.getResult().getUser().getUid();
                                databaseReference.child(firebaseID).child("password").setValue(binding.edtPassword.getText().toString());
                                loaderDialog.dismiss();
                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }else {
                                loaderDialog.dismiss();
                                Toast.makeText(LoginActivity.this, "Please verify your Email Address", Toast.LENGTH_LONG).show();
                            }

                        }
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

}