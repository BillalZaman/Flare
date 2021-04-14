package com.infotech4it.flare.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.infotech4it.flare.R;
import com.infotech4it.flare.databinding.FragmentAllUsersListingBinding;
import com.infotech4it.flare.databinding.FragmentUserFriendListBinding;
import com.infotech4it.flare.helpers.FirebaseParser;
import com.infotech4it.flare.helpers.RecyclerItemClickListener;
import com.infotech4it.flare.views.activities.ChatDetailActivity;
import com.infotech4it.flare.views.activities.ProfileActivity;
import com.infotech4it.flare.views.adapters.SelectUserForChat;
import com.infotech4it.flare.views.adapters.UserFriendListAdapter;
import com.infotech4it.flare.views.models.MessageModelClass;
import com.infotech4it.flare.views.models.SelectStudentForChat;
import com.infotech4it.flare.views.models.UserFriendsModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AllUsersListingFragment extends Fragment {
    private ArrayList<UserFriendsModel> data;
    private UserFriendListAdapter adapter;
    private ArrayList<SelectStudentForChat>arrayList=new ArrayList<>();
    SelectUserForChat mAdapter;
    private int count=0;
    private String currentUserId;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReferenceUserTable = database.getReference("user_table");
    Context mContext;
    ProgressDialog progressDialog;
    private FragmentAllUsersListingBinding binding;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_all_users_listing, container, false);

        mContext=getContext();
        currentUserId = mAuth.getCurrentUser().getUid();
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setTitle("Loading ...");
        progressDialog.show();

        binding.recyclerview.addOnItemTouchListener(new RecyclerItemClickListener(getContext(),
                binding.recyclerview, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                SelectStudentForChat selectUserForChat = arrayList.get(position);
                String id = selectUserForChat.getFirebaseID();
                String name = selectUserForChat.getName();
                String email = selectUserForChat.getEmail();
                String password = selectUserForChat.getPassword();
                String phone = selectUserForChat.getNumber();
                String image = selectUserForChat.getProfile();
                JSONObject jsonObject = null;

                Intent intent = new Intent(getActivity(), ProfileActivity.class);
                intent.putExtra("visitUserId", id);
                intent.putExtra("mName", name);
                intent.putExtra("mEmail", email);
                intent.putExtra("mNumber", phone);
                intent.putExtra("mProfile", image);
                startActivity(intent);

            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("user_table");
        DatabaseReference tripsRef = rootRef.child("user_table");
        rootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (!ds.getKey().contains(currentUserId)){
                        SelectStudentForChat slct = ds.getValue(SelectStudentForChat.class);
                        arrayList.add(slct);
                    }



                }

                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                mAdapter = new SelectUserForChat(mContext, arrayList);
                binding.recyclerview.setAdapter(mAdapter);
                mAdapter.update(arrayList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });


    }
}