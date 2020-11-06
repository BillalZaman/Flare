package com.infotech4it.flare.views.activities;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;

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
import com.infotech4it.flare.databinding.ActivityRegistrationBinding;
import com.infotech4it.flare.helpers.LoaderDialog;
import com.infotech4it.flare.helpers.UIHelper;
import com.infotech4it.flare.views.models.UserModel;

public class RegistrationActivity extends AppCompatActivity {
    private ActivityRegistrationBinding binding;
    private UserModel userModel;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private LoaderDialog loaderDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_registration);
        init();
    }

    private void init() {
        binding.setOnClick(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = firebaseDatabase.getReference("User");
        binding.setOnUserModel(userModel);
        userModel = new UserModel();

        loaderDialog = new LoaderDialog(this);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgBack: {
                finish();
                break;
            }
            case R.id.btnRegister: {
                if (UIHelper.isNetworkAvailable(this)) {
                    if (validation()) {
                        registerUser();
                    }
                } else {
                    UIHelper.showLongToastInCenter(this, getString(R.string.internet));
                }
                break;
            }
        }
    }

    public void registerUser() {
        firebaseAuth.createUserWithEmailAndPassword(binding.edtEmail.getText().toString(),
                binding.edtPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Handler handler = new Handler();
                    handler.postDelayed(() -> {
                        SaveDataToDb();
                        UIHelper.openActivity(RegistrationActivity.this, HomeActivity.class);
                        UIHelper.showLongToastInCenter(RegistrationActivity.this, "" + task.getException().getMessage());
                    }, 3000);
                } else {
                    loaderDialog.dismiss();
                    UIHelper.showLongToastInCenter(RegistrationActivity.this, "" + task.getException().getMessage());
                }
            }
        });
    }


    private void SaveDataToDb() {
        userModel = new UserModel(binding.edtName.getText().toString(), binding.edtEmail.getText().toString(),
                binding.edtNumber.getText().toString(), binding.edtPassword.getText().toString());
        databaseReference.child(FirebaseAuth.getInstance().getUid()).setValue(userModel);
    }

    private boolean validation() {
        boolean check = true;
        binding.edtName.setError(null);
        binding.edtEmail.setError(null);
        binding.edtNumber.setError(null);
        binding.edtPassword.setError(null);
        binding.edtConfirmPassword.setError(null);

        if (binding.edtName.getText().toString().isEmpty()) {
            binding.textName.setError("Name Field cannot be Empty");
            check = false;
        } else if (binding.edtEmail.getText().toString().isEmpty()) {
            binding.textInputLayout.setError("Email Field cannot be Empty");
            check = false;
        } else if (binding.edtNumber.getText().toString().isEmpty()) {
            binding.textInputNumber.setError("Number Field cannot be Empty");
            check = false;
        } else if (binding.edtPassword.getText().toString().isEmpty()) {
            binding.textPasword.setError("Password Field cannot be Empty");
            check = false;
        } else if (binding.edtConfirmPassword.getText().toString().isEmpty()) {
            binding.textConfirmPasword.setError("Confirm Password Field cannot be empty");
            check = false;
        } else if (binding.edtPassword.getText().toString().length() < 6) {
            binding.textPasword.setError("Password cannot be less then 6 character");
            check = false;
        } else if (binding.edtConfirmPassword.getText().toString().length() < 6) {
            binding.textConfirmPasword.setError("Password cannot be less then 6 character");
            check = false;
        } else if (!binding.edtPassword.getText().toString().equalsIgnoreCase(binding.edtConfirmPassword.getText().toString())) {
            binding.textConfirmPasword.setError("Password Mismatch");
            check = false;
        }
        return check;
    }
}