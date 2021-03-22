package com.infotech4it.flare.fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.infotech4it.flare.R;
import com.infotech4it.flare.databinding.FragmentProfileBinding;
import com.infotech4it.flare.helpers.AvatarGenerator;
import com.infotech4it.flare.helpers.LoaderDialog;
import com.infotech4it.flare.interfaces.FireBaseResponseCallBack;
import com.infotech4it.flare.views.adapters.BlogRecycleAdapter;
import com.infotech4it.flare.views.models.BlogPost;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.app.Activity.RESULT_OK;


public class PersonalProfileFragment extends Fragment {
    private List<BlogPost> blogList;
    private List<BlogPost> blogListFilter;
    private FirebaseAuth mAuth= FirebaseAuth.getInstance();
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private StorageReference storageReference;
    private BlogRecycleAdapter blogRecycleAdapter;
    String currentUid;
    Context context;
    private String user_id, user_name, userImage_URL, user_email, user_number, user_password;
    ProgressDialog progressDialog;
    String imagePath;
    private String uploadedImageUrl;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri mImageUri;
    private ImageView imgProifle;
    private TextView mFirebaseID, btnUpdate;
    private TextInputLayout tName, tEmail, tPassword, tNumber;
    private TextInputEditText edtName, edtEmail, edtPassword, edtNumber;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = database.getReference("user_table");


    public PersonalProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_personal_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        init(view);
    }

    public void init(View view){
        context = getActivity();
        currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        storageReference = FirebaseStorage.getInstance().getReference();

        imgProifle = view.findViewById(R.id.imgUser);
        mFirebaseID = view.findViewById(R.id.flareid);
        tName = view.findViewById(R.id.text_name);
        tEmail = view.findViewById(R.id.text_email);
        tPassword = view.findViewById(R.id.text_Password);
        tNumber = view.findViewById(R.id.text_number);
        edtName = view.findViewById(R.id.edtName);
        edtNumber = view.findViewById(R.id.edtNumber);
        edtEmail = view.findViewById(R.id.edtEmail);
        edtPassword = view.findViewById(R.id.edtPassword);
        btnUpdate = view.findViewById(R.id.btnUpdate);

        setViews();

    }

    public void setViews(){
        getDataUser();
        requestStoragePermission();
        imgProifle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });

        mFirebaseID.setText("Your Flare ID: "+currentUid);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfile();
            }
        });
    }

    public void updateProfile(){
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Updating...");
        progressDialog.show();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (!edtName.getText().toString().trim().isEmpty()){
            databaseReference.child(uid).child("name").setValue(edtName.getText().toString());
            String firebaseID = mAuth.getCurrentUser().getUid();
            firebaseFirestore.collection("Users").document(firebaseID).update("name", edtName.getText().toString());
            progressDialog.dismiss();
            edtName.setText("");
        }

        if (!edtNumber.getText().toString().trim().isEmpty()){
            databaseReference.child(uid).child("number").setValue(edtName.getText().toString());
            progressDialog.dismiss();
            edtNumber.setText("");
        }

        if (!edtPassword.getText().toString().trim().isEmpty()){
            progressDialog.dismiss();
            if (edtPassword.getText().toString().length()<6){
                Toast.makeText(context, "Password length is too short", Toast.LENGTH_SHORT).show();
                return;
            }

            progressDialog = new ProgressDialog(context);
            progressDialog.setTitle("Updating Password...");
            progressDialog.show();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            AuthCredential credential = EmailAuthProvider.getCredential(user_email, user_password);

            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                user.updatePassword(edtPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            databaseReference.child(uid).child("password").setValue(edtPassword.getText().toString());
                                            Toast.makeText(context, "Password updated", Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                            edtPassword.setText("");
                                            UpdateEmail();

                                        } else {
                                            Toast.makeText(context, "Error password not updated", Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(context, "Error auth failed", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        }
                    });


        }else {
            UpdateEmail();
        }



//        getDataUser();
    }

    public void UpdateEmail(){
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (!edtEmail.getText().toString().trim().isEmpty()){
            progressDialog.dismiss();
            progressDialog = new ProgressDialog(context);
            progressDialog.setTitle("Updating Email...");
            progressDialog.show();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String mPassword;
            if (!edtPassword.getText().toString().trim().isEmpty()){
                mPassword = edtPassword.getText().toString().trim();
            }else {
                mPassword = user_password;
            }
            AuthCredential credential = EmailAuthProvider.getCredential(user_email, mPassword);

            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        user.updateEmail(edtEmail.getText().toString())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            databaseReference.child(uid).child("email").setValue(edtEmail.getText().toString());
                                            Toast.makeText(context, "Email updated", Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                            edtEmail.setText("");
                                            getDataUser();
                                        } else {
                                            Toast.makeText(context, "Error email not updated", Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                        }
                                    }
                                });
                    }else {
                        Toast.makeText(context, "Error auth failed", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }
            });

        }else {
            getDataUser();
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
                        tName.setHint("Your Flare Name: "+user_name);
                    }
                    if (data.getKey().equals("email")){
                        user_email = data.getValue().toString();
                        tEmail.setHint("Your Flare Email: "+user_email);
                    }
                    if (data.getKey().equals("number")){
                        user_number = data.getValue().toString();
                        tNumber.setHint("Your Flare Number: "+user_number);
                    }
                    if (data.getKey().equals("password")){
                        user_password = data.getValue().toString();
                        tPassword.setHint("Your Flare Password: "+user_password);
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
                                    .into(imgProifle);
                        }
                        else {
                            Glide.with(context)
                                    .load(AvatarGenerator.Companion.avatarImage(context, 200,
                                            AvatarGenerator.AvatarConstants.Companion.getCIRCLE(),
                                            user_name,false))
                                    .into(imgProifle);
                        }


                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
                    if (imagePath==null){
                        String mPath = getPathFromUri(context, mImageUri);
                        imagePath = mPath;
                    }

                    String mPath = getPathFromUri(context, mImageUri);
                    imagePath = mPath;

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
                                    .into(imgProifle);
                        }
                        else {
                            Glide.with(context)
                                    .load(AvatarGenerator.Companion.avatarImage(context, 200,
                                            AvatarGenerator.AvatarConstants.Companion.getCIRCLE(),
                                            user_name,false))
                                    .into(imgProifle);
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