package com.infotech4it.flare.views.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.UploadTask;
import com.infotech4it.flare.R;
import com.infotech4it.flare.databinding.ActivityRegistrationBinding;
import com.infotech4it.flare.googleplayservices.LocationProvider;
import com.infotech4it.flare.helpers.LoaderDialog;
import com.infotech4it.flare.helpers.UIHelper;
import com.infotech4it.flare.views.models.UserModel;

import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {

    private ActivityRegistrationBinding binding;
    private LoaderDialog loaderDialog;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = database.getReference("user_table");
    private FirebaseFirestore fireStore = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_registration);
        init();
    }

    private void init() {
        binding.setOnClick(this);
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
        loaderDialog.startLoadingDialog();
        mAuth.createUserWithEmailAndPassword(binding.edtEmail.getText().toString(),
                binding.edtPassword.getText().toString())
                .addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            loaderDialog.dismiss();
                            String errorMessage = task.getException().getMessage();
                            Toast.makeText(RegistrationActivity.this, "Error : " + errorMessage, Toast.LENGTH_LONG).show();

                        } else {
                            String firebaseID = task.getResult().getUser().getUid();
                            databaseReference.child(firebaseID).child("firebaseID").setValue(firebaseID);
                            databaseReference.child(firebaseID).child("name").setValue(binding.edtName.getText().toString());
                            databaseReference.child(firebaseID).child("email").setValue(binding.edtEmail.getText().toString());
                            databaseReference.child(firebaseID).child("number").setValue(binding.edtNumber.getText().toString());
                            databaseReference.child(firebaseID).child("password").setValue(binding.edtPassword.getText().toString());
                            databaseReference.child(firebaseID).child("profile").setValue("null");
//                            mAuth.getCurrentUser().sendEmailVerification();
                            saveToFirestore(binding.edtName.getText().toString(), firebaseID);
                        }
                    }
                });

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

    private void saveToFirestore(final String uName, final String user_id) {
        Map<String,String> userMap= new HashMap<>();
        userMap.put("name",uName);
        userMap.put("image","null");
        fireStore.collection("Users").document(user_id).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    loaderDialog.dismiss();
                    Toast.makeText(RegistrationActivity.this, "Successfully Registered", Toast.LENGTH_SHORT).show();
//                    Toast.makeText(RegistrationActivity.this, "Successfully Registered. Open your Email for verification", Toast.LENGTH_SHORT).show();
                    finish();
                }
                else {
                    loaderDialog.dismiss();
                    String error = task.getException().getMessage();
                    Toast.makeText(RegistrationActivity.this, " FireStore Error" + error, Toast.LENGTH_LONG).show();
                }
            }
        });

    }

}