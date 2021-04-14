package com.infotech4it.flare.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.infotech4it.flare.R;
import com.infotech4it.flare.databinding.FragmentFeedBinding;
import com.infotech4it.flare.helpers.UIHelper;
import com.infotech4it.flare.views.activities.PostActivity;
import com.infotech4it.flare.views.adapters.BlogRecycleAdapter;
import com.infotech4it.flare.views.adapters.NewsFeedAdapter;
import com.infotech4it.flare.views.models.BlogPost;
import com.infotech4it.flare.views.models.NewsFeedModel;

import java.util.ArrayList;
import java.util.List;


public class FeedFragment extends Fragment {
    private FragmentFeedBinding binding;
    private List<BlogPost> blogList;
    private DocumentSnapshot lastVisible;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth mauth;
    private BlogRecycleAdapter blogRecycleAdapter;
    private boolean firstPageLoaded = true;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String user_id;

    public FeedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_feed, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        blogList = new ArrayList<>();
        blogRecycleAdapter = new BlogRecycleAdapter(blogList, getActivity());
        binding.recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerview.setAdapter(blogRecycleAdapter);

        if(FirebaseAuth.getInstance().getCurrentUser() != null) {

            firebaseFirestore = FirebaseFirestore.getInstance();
            mauth = FirebaseAuth.getInstance();

            binding.recyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    boolean lastItem = !recyclerView.canScrollVertically(1);

                    if (lastItem) {
                        loadMorePosts();
                    }
                }
            });


            if (mauth.getCurrentUser() != null) {
                Query firstQuery = firebaseFirestore.collection("Posts")
                        .orderBy("timeStamp", Query.Direction.DESCENDING)
                        .limit(3);

                firstQuery.addSnapshotListener((documentSnapshots, e) -> {

                    if (documentSnapshots.size()>0){
                        if (firstPageLoaded) {
                            lastVisible = documentSnapshots.getDocuments()
                                    .get(documentSnapshots.size() - 1);
                        }
                    }


                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                        if (doc.getType() == DocumentChange.Type.ADDED) {
                            String BlogPostId = doc.getDocument().getId();
                            BlogPost blogPost = doc.getDocument().toObject(BlogPost.class).withId(BlogPostId);

                            if (firstPageLoaded) {
                                blogList.add(blogPost);
                            }
                            else {
                                blogList.add(0, blogPost);
                            }


                            blogRecycleAdapter.notifyDataSetChanged();
                        }
                    }

                });

            }
        }

    }

    private void loadMorePosts() {

        if (mauth.getCurrentUser() != null) {
            Query nextQuery = firebaseFirestore.collection("Posts")
                    .orderBy("timeStamp", Query.Direction.DESCENDING)
                    .startAfter(lastVisible)
                    .limit(3);

            nextQuery.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                    if (!documentSnapshots.isEmpty()) {
                        lastVisible = documentSnapshots.getDocuments()
                                .get(documentSnapshots.size() - 1);

                        for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                            if (doc.getType() == DocumentChange.Type.ADDED) {
                                String BlogPostId = doc.getDocument().getId();
                                BlogPost blogPost = doc.getDocument().toObject(BlogPost.class).withId(BlogPostId);
                                blogList.add(blogPost);
                                blogRecycleAdapter.notifyDataSetChanged();
                            }
                        }

                    }

                }
            });
        }
    }

    private void init() {
        binding.postConst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser != null) {
                    user_id = mAuth.getCurrentUser().getUid();
                    firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.getResult().exists()) {
                                Intent addPost = new Intent(getActivity(), PostActivity.class);
                                startActivity(addPost);
                            } else {
                                Toast.makeText(getContext(), "Please set up Name and Profile", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(getContext(), "No Record Found", Toast.LENGTH_LONG).show();
                }


            }
        });

        binding.imgFilter.setOnClickListener(v->{
            binding.postConst.setVisibility(View.VISIBLE);
        });

        binding.txtEmerAlert.setOnClickListener(v->{
            binding.postConst.setVisibility(View.GONE);
        });

        binding.edtFindFriend.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                blogRecycleAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

}