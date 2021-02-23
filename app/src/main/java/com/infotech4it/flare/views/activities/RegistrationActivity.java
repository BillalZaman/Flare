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
import com.infotech4it.flare.R;
import com.infotech4it.flare.databinding.ActivityRegistrationBinding;
import com.infotech4it.flare.googleplayservices.LocationProvider;
import com.infotech4it.flare.helpers.LoaderDialog;
import com.infotech4it.flare.helpers.UIHelper;
import com.infotech4it.flare.views.models.UserModel;

public class RegistrationActivity extends AppCompatActivity {
//        implements LocationListener {
//    private final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 8088;
//    protected LocationManager locationManager;
//    protected LocationListener locationListener;
    private ActivityRegistrationBinding binding;
//    private UserModel userModel;
//    private FirebaseDatabase firebaseDatabase;
//    private DatabaseReference databaseReference;
//    private FirebaseAuth firebaseAuth;
//    private LoaderDialog loaderDialog;
//    private LocationProvider locationProvider;
//    private double latitude, langitude;
//    private boolean mLocationPermissionGranted;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = database.getReference("user_table");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_registration);
        init();
    }

    private void init() {
        binding.setOnClick(this);
//        firebaseDatabase = FirebaseDatabase.getInstance();
//        firebaseAuth = FirebaseAuth.getInstance();
//        databaseReference = firebaseDatabase.getReference("User");
//        binding.setOnUserModel(userModel);
//        userModel = new UserModel();
//
//        loaderDialog = new LoaderDialog(this);

//        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
//        if (checkLocationPermission()) {
//            locationProvider = new LocationProvider(MapActivity.this, MapActivity.this);
//            locationProvider.connect();
//        }
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
        //create user
        mAuth.createUserWithEmailAndPassword(binding.edtEmail.getText().toString(),
                binding.edtPassword.getText().toString())
                .addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
//                            loaderDialog.dismiss();
                        } else {
                            String firebaseID = task.getResult().getUser().getUid();
                            databaseReference.child(firebaseID).child("firebaseID").setValue(firebaseID);
                            databaseReference.child(firebaseID).child("name").setValue(binding.edtName.getText().toString());
                            databaseReference.child(firebaseID).child("email").setValue(binding.edtEmail.getText().toString());
                            databaseReference.child(firebaseID).child("number").setValue(binding.edtNumber.getText().toString());
                            databaseReference.child(firebaseID).child("password").setValue(binding.edtPassword.getText().toString());
                            databaseReference.child(firebaseID).child("profile").setValue("null");
                            mAuth.getCurrentUser().sendEmailVerification();
                            Toast.makeText(RegistrationActivity.this, "Successfully Registered. Open your Email for verification", Toast.LENGTH_SHORT).show();

                            finish();
                        }
                    }
                });

//        firebaseAuth.createUserWithEmailAndPassword(binding.edtEmail.getText().toString(),
//                binding.edtPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//            @Override
//            public void onComplete(@NonNull Task<AuthResult> task) {
//                if (task.isSuccessful()) {
//                    Handler handler = new Handler();
//                    handler.postDelayed(() -> {
//                        SaveDataToDb();
////                        UIHelper.openActivity(RegistrationActivity.this, HomeActivity.class);
//                        UIHelper.showLongToastInCenter(RegistrationActivity.this, "" + task.getException().getMessage());
//                    }, 3000);
//                } else {
//                    loaderDialog.dismiss();
//                    UIHelper.showLongToastInCenter(RegistrationActivity.this, "" + task.getException().getMessage());
//                }
//            }
//        });
    }


//    private void SaveDataToDb() {
//        userModel = new UserModel(binding.edtName.getText().toString(), binding.edtEmail.getText().toString(),
//                binding.edtNumber.getText().toString(), binding.edtPassword.getText().toString(), latitude,langitude);
//        databaseReference.child(FirebaseAuth.getInstance().getUid()).setValue(userModel);
//    }

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

//    @Override
//    public void onLocationChanged(@NonNull Location location) {
//        latitude = location.getLatitude();
//        langitude = location.getLongitude();
//    }

//    private boolean checkLocationPermission() {
//        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
//                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
//    }
//
//    private void getLocationPermission() {
//        /*
//         * Request location permission, so that we can get the location of the
//         * device. The result of the permission request is handled by a callback,
//         * onRequestPermissionsResult.
//         */
//        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
//                android.Manifest.permission.ACCESS_FINE_LOCATION)
//                == PackageManager.PERMISSION_GRANTED) {
//            mLocationPermissionGranted = true;
//
//        } else {
//            ActivityCompat.requestPermissions(this,
//                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
//                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
//        }
//
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        mLocationPermissionGranted = false;
//
//        // If request is cancelled, the result arrays are empty.
//        if (grantResults.length > 0
//                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            mLocationPermissionGranted = true;
//            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//                openLocationSettingActivity();
//            } else {
//                if (checkLocationPermission()) {
//                    locationProvider = new LocationProvider(this, this);
//                    locationProvider.connect();
//                }
//            }
//        }
//    }
//
//    private void openLocationSettingActivity() {
//        new AlertDialog.Builder(this)
//                .setCancelable(false)
//                .setTitle(getResources().getString(R.string.alert))
//                .setMessage(getResources().getString(R.string.gps_enable_text))
//                .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                        startActivity(callGPSSettingIntent);
//                    }
//                })
//                .show();
//    }
//
//    @Override
//    public void handleNewLocation(Location location) {
//
//    }
}