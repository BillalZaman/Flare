package com.infotech4it.flare.views.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_post);
        context = this;
        init();
    }

    public void init() {
        binding.setOnClick(this);
        mauth = FirebaseAuth.getInstance();
        user_id = mauth.getCurrentUser().getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        requestStoragePermission();
        getDataUser();

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
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
                break;
            }
            case R.id.btnPost: {
                if (imagePath!=null){
                    setUpGroup();
                }else {
                    PostWithoutImage();
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

        Map<String,Object> postMap = new HashMap<>();
        postMap.put("image_url","null");
        postMap.put("desc",binding.edtStatusDesc.getText().toString().trim());
        postMap.put("user_id",user_id);
        postMap.put("user_name",user_name);
        postMap.put("image_thumb","null");
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
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            Toast.makeText(getApplicationContext(), "All permissions are granted!", Toast.LENGTH_SHORT).show();
                        }

                        if (report.isAnyPermissionPermanentlyDenied()) {
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
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

        if (resultCode == RESULT_OK) {
            if (reqCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {

                String fileExtension = getFileExtension(data.getData(), this);

                if (isItImage(fileExtension)) {
                    mImageUri = data.getData();
                    String path = getRealPathFromUri(context, mImageUri);
                    imagePath = path;
                    Glide.with(binding.imgStatus.getContext())
                            .load(mImageUri)
                            .error(R.drawable.ic_profile)
                            .into(binding.imgStatus);

                }  else {
                    Toast.makeText(this, "Select Valid File", Toast.LENGTH_LONG).show();
                }

            }
        }
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


            uploadImageToStorage(new FireBaseResponseCallBack() {
                @Override
                public void onCompleteCallBack(boolean isOk, String message) {

                    if (isOk) {
                        uploadedImageUrl=message;

                        Map<String,Object> postMap = new HashMap<>();
                        postMap.put("image_url",uploadedImageUrl);
                        postMap.put("desc",binding.edtStatusDesc.getText().toString().trim());
                        postMap.put("user_id",user_id);
                        postMap.put("user_name",user_name);
                        postMap.put("image_thumb",uploadedImageUrl);
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
}