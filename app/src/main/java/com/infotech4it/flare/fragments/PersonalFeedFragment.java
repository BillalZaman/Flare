package com.infotech4it.flare.fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
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


public class PersonalFeedFragment extends Fragment {
    private List<BlogPost> blogList;
    private List<BlogPost> blogListFilter;
    private FirebaseFirestore firebaseFirestore;
    private BlogRecycleAdapter blogRecycleAdapter;
    String currentUid;
    private LoaderDialog loaderDialog;
    Context context;
    RecyclerView recyclerView;


    public PersonalFeedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_personal_feed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recyclerview_user_post);
        init();
    }

    public void init(){
        context = getActivity();
        currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        loaderDialog = new LoaderDialog(getActivity());
        loadPosts();

    }

    public void loadPosts(){
        loaderDialog.startLoadingDialog();
        blogList = new ArrayList<>();
        blogListFilter = new ArrayList<>();
        blogRecycleAdapter = new BlogRecycleAdapter(blogList, getActivity());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(blogRecycleAdapter);
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


}