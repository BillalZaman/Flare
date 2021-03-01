package com.infotech4it.flare.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.infotech4it.flare.R;
import com.infotech4it.flare.constant.Constant;
import com.infotech4it.flare.databinding.FragmentFeedBinding;
import com.infotech4it.flare.databinding.FragmentProfileBinding;
import com.infotech4it.flare.helpers.AvatarGenerator;
import com.infotech4it.flare.helpers.LoaderDialog;
import com.infotech4it.flare.helpers.PreferenceHelper;
import com.infotech4it.flare.helpers.UIHelper;
import com.infotech4it.flare.interfaces.FireBaseResponseCallBack;
import com.infotech4it.flare.interfaces.ImgClickInterface;
import com.infotech4it.flare.views.activities.HomeActivity;
import com.infotech4it.flare.views.activities.PostActivity;
import com.infotech4it.flare.views.adapters.BlogRecycleAdapter;
import com.infotech4it.flare.views.adapters.NewsFeedAdapter;
import com.infotech4it.flare.views.adapters.UserFriendAdapter;
import com.infotech4it.flare.views.models.BlogPost;
import com.infotech4it.flare.views.models.NewsFeedModel;
import com.infotech4it.flare.views.models.UserFriendsModel;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.aprilapps.easyphotopicker.ChooserType;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import pl.aprilapps.easyphotopicker.MediaFile;
import pl.aprilapps.easyphotopicker.MediaSource;

import static android.Manifest.permission.CAMERA;
import static android.app.Activity.RESULT_OK;


public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;
    private List<BlogPost> blogList;
    private List<BlogPost> blogListFilter;
    private FirebaseAuth mAuth= FirebaseAuth.getInstance();
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    private BlogRecycleAdapter blogRecycleAdapter;
    String currentUid;
    private LoaderDialog loaderDialog;
    Context context;
    private String user_id, user_name, userImage_URL;
    ProgressDialog progressDialog;
    String imagePath;
    private String uploadedImageUrl;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri mImageUri;


    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    public void init(){
        context = getActivity();
        currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        storageReference = FirebaseStorage.getInstance().getReference();
        loaderDialog = new LoaderDialog(getActivity());
        getDataUser();
        loadPosts();
        requestStoragePermission();

        binding.imgUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });

        binding.flareid.setText("Your Flare ID: "+currentUid);
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.imgUser:{

                break;
            }
            case R.id.imgcover:{

                break;
            }
        }
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
                        binding.txtUserName.setText(user_name);
                    }
                    if (data.getKey().equals("email")){
                        String email = data.getValue().toString();
                        binding.flareemail.setText("Your Flare Email: "+email);
                    }
                    if (data.getKey().equals("number")){
                        String num = data.getValue().toString();
                        binding.flarenumber.setText("Your Flare Number: "+num);
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

    public void loadPosts(){
        loaderDialog.startLoadingDialog();
        blogList = new ArrayList<>();
        blogListFilter = new ArrayList<>();
        blogRecycleAdapter = new BlogRecycleAdapter(blogList);
        binding.recyclerviewUserPost.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerviewUserPost.setAdapter(blogRecycleAdapter);
        firebaseFirestore = FirebaseFirestore.getInstance();

        firebaseFirestore.collection("Posts").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        if (documentSnapshots.isEmpty()) {
                            loaderDialog.dismiss();
                            String TAG="CurrentUserPosts";
                            Log.d(TAG, "onSuccess: LIST EMPTY");
                            Toast.makeText(getContext(), "Current User Posts List is Empty", Toast.LENGTH_LONG).show();
                            return;
                        } else {

                            for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                String BlogPostId = doc.getDocument().getId();
                                BlogPost blogPost = doc.getDocument().toObject(BlogPost.class).withId(BlogPostId);
                                blogList.add(blogPost);
                            }

                            for (int i=0; i<blogList.size(); i++){
                                if (blogList.get(i).getUser_id().contains(currentUid)){
                                    BlogPost blogPost = blogList.get(i);
                                    blogListFilter.add(blogPost);
                                }
                            }

                            blogList.clear();
                            blogList.addAll(blogListFilter);

                            blogRecycleAdapter.notifyDataSetChanged();
                            loaderDialog.dismiss();
                        }
                    }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            loaderDialog.dismiss();
                            Toast.makeText(getContext(), "Error getting Current User Posts", Toast.LENGTH_LONG).show();
                        }
                    });


    }


    private void requestStoragePermission() {
        Dexter.withActivity(getActivity())
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            Toast.makeText(getActivity(), "All permissions are granted!", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getActivity(), "Error occurred! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
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
        Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (reqCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {

                String fileExtension = getFileExtension(data.getData(), getActivity());

                if (isItImage(fileExtension)) {
                    mImageUri = data.getData();
                    String path = getRealPathFromUri(context, mImageUri);
                    imagePath = path;

                    setUpGroup();

                }  else {
                    Toast.makeText(context, "Select Valid File", Toast.LENGTH_LONG).show();
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
                        String firebaseID = mAuth.getCurrentUser().getUid();
                        DatabaseReference databaseReference;
                        databaseReference = FirebaseDatabase.getInstance().getReference().child("user_table").child(firebaseID);
                        HashMap<String, Object> result = new HashMap<>();
                        result.put("profile", uploadedImageUrl);
                        databaseReference.updateChildren(result);

                        firebaseFirestore.collection("Users").document(firebaseID).update("image", uploadedImageUrl);

                        if (progressDialog!=null){
                            progressDialog.dismiss();
                        }

                        Toast.makeText(getActivity(), "Profile Successfully Updated", Toast.LENGTH_LONG).show();

                        if(uploadedImageUrl!=null){

                            Glide.with(context).
                                    load(uploadedImageUrl).
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
        StorageReference imageStorageRef = storageReference.child("profile_images/" +
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



}