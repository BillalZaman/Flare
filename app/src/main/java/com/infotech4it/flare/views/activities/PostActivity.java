package com.infotech4it.flare.views.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.infotech4it.flare.R;
import com.infotech4it.flare.databinding.ActivityPostBinding;
import com.infotech4it.flare.googleplayservices.GetAddressIntentService;
import com.infotech4it.flare.helpers.AvatarGenerator;
import com.infotech4it.flare.helpers.UIHelper;
import com.infotech4it.flare.interfaces.FireBaseResponseCallBack;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import id.zelory.compressor.Compressor;
import pl.aprilapps.easyphotopicker.ChooserType;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import pl.aprilapps.easyphotopicker.MediaFile;
import pl.aprilapps.easyphotopicker.MediaSource;

import static android.Manifest.permission.CAMERA;

public class PostActivity extends AppCompatActivity {
    private ActivityPostBinding binding;
    private String user_id, user_name, userImage_URL;
    private FirebaseAuth mauth;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri mImageUri;
    Context context;
    String imagePath;
    private String uploadedImageUrl;
    ProgressDialog progressDialog;
    private FirebaseAuth mAuth= FirebaseAuth.getInstance();
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 2;
    private LocationAddressResultReceiver addressResultReceiver;
    private Location currentLocation;
    private LocationCallback locationCallback;
    double longitude, latitude;
    boolean isImage=false;
    private EasyImage easyImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_post);
        context = this;
        init();

        addressResultReceiver = new LocationAddressResultReceiver(new Handler());
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                currentLocation = locationResult.getLocations().get(0);
                getAddress();
            }
        };
        startLocationUpdates();
        turnOnLocation();
        easyImage = new EasyImage.Builder(this)
                .setChooserTitle("Pick media")
                .setCopyImagesToPublicGalleryFolder(false)
                .setChooserType(ChooserType.CAMERA_AND_GALLERY)
                .setFolderName("EasyImage sample")
                .allowMultiple(false)
                .build();

    }

    public void init() {
        binding.setOnClick(this);
        mauth = FirebaseAuth.getInstance();
        user_id = mauth.getCurrentUser().getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        getDataUser();

    }

    public void turnOnLocation(){

        LocationManager lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        if(!gps_enabled && !network_enabled) {
            // notify user
            new AlertDialog.Builder(context)
                    .setMessage("Turn On GPS Locations")
                    .setPositiveButton("Yes", (paramDialogInterface, paramInt) -> context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    })
                    .show();
        }

    }

    @SuppressWarnings("MissingPermission")
    private void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new
                            String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
        else {
            LocationRequest locationRequest = new LocationRequest();
            locationRequest.setInterval(2000);
            locationRequest.setFastestInterval(1000);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        }
    }

    @SuppressWarnings("MissingPermission")
    private void getAddress() {
        if (!Geocoder.isPresent()) {
            Toast.makeText(PostActivity.this, "Can't find current address, ",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this, GetAddressIntentService.class);
        intent.putExtra("add_receiver", addressResultReceiver);
        intent.putExtra("add_location", currentLocation);
        startService(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull
            int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            }
            else {
                Toast.makeText(this, "Location permission not granted, " + "restart the app if you want the feature", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class LocationAddressResultReceiver extends ResultReceiver {
        LocationAddressResultReceiver(Handler handler) {
            super(handler);
        }
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            if (resultCode == 0) {
                Log.d("Address", "Location null retrying");
                getAddress();
            }
            if (resultCode == 1) {
                Toast.makeText(PostActivity.this, "Address not found", Toast.LENGTH_SHORT).show();
            }

            if (resultCode == 2) {
//                Toast.makeText(PostActivity.this, "Address found Successfully", Toast.LENGTH_SHORT).show();
                String currentAdd = resultData.getString("address_result");
                latitude = resultData.getDouble("latitude");
                longitude = resultData.getDouble("longitude");
//                Toast.makeText(PostActivity.this, latitude+" -- "+longitude, Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }


    public void getDataUser(){

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("user_table");

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final DatabaseReference orders_Reference = myRef.child(uid);
        orders_Reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    if(data.getKey().equals("name")){
                        user_name = data.getValue().toString();
                        binding.textView3.setText(user_name);
                    }
                    if (data.getKey().equals("profile")){
                        userImage_URL = data.getValue().toString();

                        if(userImage_URL!=null){

                            Glide.with(context).
                                    load(userImage_URL).
                                    error(AvatarGenerator.Companion.avatarImage(context, 200,
                                            AvatarGenerator.AvatarConstants.Companion.getCIRCLE(),
                                            user_name,false))
                                    .placeholder(AvatarGenerator.Companion.avatarImage(context, 200,
                                            AvatarGenerator.AvatarConstants.Companion.getCIRCLE(),
                                            user_name,false))
                                    .into(binding.imgUser);
                        }
                        else {
                            Glide.with(context)
                                    .load(AvatarGenerator.Companion.avatarImage(context, 200,
                                            AvatarGenerator.AvatarConstants.Companion.getCIRCLE(),
                                            user_name,false))
                                    .into(binding.imgUser);
                        }


                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgBack: {
                finish();
                break;
            }
            case R.id.imgAddPic: {
//                Intent intent = new Intent();
//                intent.setType("image/*");
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                startActivityForResult(intent, PICK_IMAGE_REQUEST);

                requestStoragePermission();
                break;
            }
            case R.id.btnPost: {
//                Toast.makeText(context, binding.location.getProgress()+"--this is range--", Toast.LENGTH_SHORT).show();
                if (latitude==0 && longitude==0){
                    Toast.makeText(context, "Unable to get your Location. Kindly Check Your Internet and GPS Connection", Toast.LENGTH_SHORT).show();
                    return;
                }else {

                    if (isImage){
                        setUpGroup();
                    }else {
                        PostWithoutImage();
                    }
//                    if (imagePath!=null){
//                        setUpGroup();
//                    }else {
//                        PostWithoutImage();
//                    }

                }

                break;
            }
            case R.id.imgLocation: {
                UIHelper.openActivityAndSendActivityName(this, MapActivity.class, "PostActivity");
                break;
            }
        }
    }

    public void PostWithoutImage(){
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Uploading Post...");
        progressDialog.show();

        Calendar c = Calendar.getInstance();
        System.out.println("Current time => "+c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(c.getTime());
        // formattedDate have current date/time
        Toast.makeText(this, formattedDate, Toast.LENGTH_SHORT).show();


        Map<String,Object> postMap = new HashMap<>();
        postMap.put("image_url","null");
        postMap.put("desc",binding.edtStatusDesc.getText().toString().trim());
        postMap.put("user_id",user_id);
        postMap.put("user_name",user_name);
        postMap.put("image_thumb","null");
        postMap.put("latitude", latitude);
        postMap.put("longitude", longitude);
        postMap.put("kilometer", binding.location.getProgress());
        postMap.put("timeStamp", FieldValue.serverTimestamp());

        firebaseFirestore.collection("Posts").add(postMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()) {
                    if (progressDialog!=null){
                        progressDialog.dismiss();
                    }

                    Toast.makeText(PostActivity.this, "Successfully Posted", Toast.LENGTH_LONG).show();
//                    finish();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            try {
                                Intent intent = new Intent(PostActivity.this, HomeActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }finally {
                                finish();
                            }

                        }
                    }, 300);

                } else {
                    if (progressDialog!=null){
                        progressDialog.dismiss();
                    }

                    Toast.makeText(PostActivity.this, "Error" + task.getException().toString(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void requestStoragePermission() {
        String[] permission = {Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA};

        if (ContextCompat.checkSelfPermission(this, permission[0]) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, permission[1]) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, permission[2]) == PackageManager.PERMISSION_GRANTED) {
            //Toast.makeText(activity, "permission granted", Toast.LENGTH_SHORT).show();
            selectImage();
        } else {
            ActivityCompat.requestPermissions(this, permission, 123);
        }
    }

    public void selectImage() {
        try {
            PackageManager pm = getPackageManager();
            int hasPerm = pm.checkPermission(CAMERA, getPackageName());
            final CharSequence[] options = {getResources().getString(R.string.take_photo),
                    getResources().getString(R.string.chhose_from_gallery),
                    getResources().getString(R.string.cancel_btn)};
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
            builder.setTitle(getResources().getString(R.string.select_option));
            builder.setItems(options, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int item) {
                    if (options[item].equals(getResources().getString(R.string.take_photo))) {
                        dialog.dismiss();
                        easyImage.openCameraForImage(PostActivity.this);
                    } else if (options[item].equals(getResources().getString(R.string.chhose_from_gallery))) {
                        dialog.dismiss();
                        easyImage.openGallery(PostActivity.this);
                    } else if (options[item].equals(getResources().getString(R.string.cancel_btn))) {
                        dialog.dismiss();
                    }
                }
            });
            builder.show();

        } catch (Exception e) {
            Toast.makeText(this, getResources().getString(R.string.camera_permission_error), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PostActivity.this);
        builder.setTitle("Need Storage Permission");
        builder.setMessage("This app needs storage permission to upload image for user Profile. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

//        if (resultCode == RESULT_OK) {
//            if (reqCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
//
//                String fileExtension = getFileExtension(data.getData(), this);
//                isImage=true;
//                if (isItImage(fileExtension)) {
//                    mImageUri = data.getData();
//                    String path = getRealPathFromUri(context, mImageUri);
//                    imagePath = path;
//                    if (imagePath==null){
//                        String mPath = getPathFromUri(context, mImageUri);
//                        imagePath = mPath;
//                    }
//
//                    String mPath = getPathFromUri(context, mImageUri);
//                    imagePath = mPath;
//
//                    Glide.with(binding.imgStatus.getContext())
//                            .load(mImageUri)
//                            .error(R.drawable.ic_profile)
//                            .into(binding.imgStatus);
//
//                }  else {
//                    Toast.makeText(this, "Select Valid File", Toast.LENGTH_LONG).show();
//                }
//
//            }
//        }


        easyImage.handleActivityResult(reqCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onMediaFilesPicked(@NotNull MediaFile[] mediaFiles, @NotNull MediaSource mediaSource) {
                if (mediaFiles[0].getFile().exists()) {
                   imagePath = String.valueOf(mediaFiles[0].getFile());
                   isImage = true;
                   Glide.with(PostActivity.this).load(mediaFiles[0].getFile()).into(binding.imgStatus);
                }
            }
        });
    }

    private void setUpGroup() {
        createGroup(new FireBaseResponseCallBack() {
            @Override
            public void onCompleteCallBack(boolean isOk, String message) {

                if (isOk) {

                }

                else Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void createGroup(final FireBaseResponseCallBack callBack) {

        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Uploading Image...");
        progressDialog.show();

        if (imagePath == null) {
            if (progressDialog!=null){
                progressDialog.dismiss();
            }
            Toast.makeText(context, "image path is null", Toast.LENGTH_SHORT).show();

        } else {


            uploadImageToStorage((isOk, message) -> {

                if (isOk) {
                    uploadedImageUrl=message;

                    Map<String,Object> postMap = new HashMap<>();
                    postMap.put("image_url",uploadedImageUrl);
                    postMap.put("desc",binding.edtStatusDesc.getText().toString().trim());
                    postMap.put("user_id",user_id);
                    postMap.put("user_name",user_name);
                    postMap.put("image_thumb",uploadedImageUrl);
                    postMap.put("latitude", latitude);
                    postMap.put("longitude", longitude);
                    postMap.put("kilometer", binding.location.getProgress());
                    postMap.put("timeStamp", FieldValue.serverTimestamp());

                    firebaseFirestore.collection("Posts").add(postMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (task.isSuccessful()) {
                                if (progressDialog!=null){
                                    progressDialog.dismiss();
                                }

                                Toast.makeText(PostActivity.this, "Successfully Posted", Toast.LENGTH_LONG).show();
//                                    finish();
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    public void run() {
                                        try {
                                            Intent intent = new Intent(PostActivity.this, HomeActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                        }finally {
                                            finish();
                                        }
                                    }
                                }, 300);


                            } else {
                                if (progressDialog!=null){
                                    progressDialog.dismiss();
                                }

                                Toast.makeText(PostActivity.this, "Error" + task.getException().toString(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                } else {
                    if (progressDialog!=null){
                        progressDialog.dismiss();
                    }
                    callBack.onCompleteCallBack(false, message);
                }
            });


        }


    }


    private void uploadImageToStorage(final FireBaseResponseCallBack callBack) {

        InputStream stream;
        StorageReference imageStorageRef = storageReference.child("post_images/" +
                System.currentTimeMillis() +
                ".jpg");

        try {

            stream = new FileInputStream(new File(imagePath));
            UploadTask uploadTask = imageStorageRef.putStream(stream);

            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {

                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                    if (!task.isSuccessful()) {
                        throw task.getException();

                    }

                    return imageStorageRef.getDownloadUrl();
                }

            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {

                    if (task.isSuccessful()) {
                        callBack.onCompleteCallBack(true, task.getResult().toString());

                    } else {

                        if (callBack != null) {
                            if (task.getException() != null) {
                                callBack.onCompleteCallBack(false, task.getException().getMessage());
                            } else {
                                callBack.onCompleteCallBack(false, "Unknow Error Occured");
                            }
                        }

                    }

                }
            });


        } catch (FileNotFoundException e) {
            e.printStackTrace();
            if (progressDialog!=null){
                progressDialog.dismiss();
            }
            Toast.makeText(context, "Image Uploading Failed", Toast.LENGTH_SHORT).show();
        }


    }

    public static String getFileExtension(Uri uri, Context context) {
        String extension;
        ContentResolver contentResolver = context.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        extension= mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
        return extension;
    }

    public static boolean isItImage(String extension){

        if(extension.equalsIgnoreCase("jpg")){
            return true;
        }

        else if(extension.equalsIgnoreCase("jpeg")){
            return true;
        }
        else return extension.equalsIgnoreCase("png");

    }

    public static String getRealPathFromUri(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try {
            Intent intent = new Intent(PostActivity.this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }finally {
            finish();
        }
    }

    public static String getPathFromUri(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }
}