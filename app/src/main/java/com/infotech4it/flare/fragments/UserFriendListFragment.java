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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.infotech4it.flare.R;
import com.infotech4it.flare.databinding.FragmentUserFriendListBinding;
import com.infotech4it.flare.helpers.FirebaseParser;
import com.infotech4it.flare.helpers.RecyclerItemClickListener;
import com.infotech4it.flare.views.activities.ChatDetailActivity;
import com.infotech4it.flare.views.adapters.SelectUserForChat;
import com.infotech4it.flare.views.adapters.UserFriendListAdapter;
import com.infotech4it.flare.views.models.MessageModelClass;
import com.infotech4it.flare.views.models.SelectStudentForChat;
import com.infotech4it.flare.views.models.UserFriendsModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UserFriendListFragment extends Fragment {
    private FragmentUserFriendListBinding binding;
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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_friend_list, container, false);
        mContext=getContext();
        currentUserId = mAuth.getCurrentUser().getUid();
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setTitle("Loading ...");
        progressDialog.show();

        mAdapter = new SelectUserForChat(mContext, arrayList);
        binding.recyclerview.setAdapter(mAdapter);

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference tripsRef = rootRef.child("user_table");
        ValueEventListener valueEventListener = new ValueEventListener() {
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

                mAdapter.update(arrayList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        tripsRef.addListenerForSingleValueEvent(valueEventListener);

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

                loadChat(jsonObject, id, name, email, password, phone, image);


            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));
        return binding.getRoot();
    }

    private void loadChat(JSONObject jsonObjectPlayer, String firebaseID, String Name, String Email, String Password, String Phone, String image) {

        count= 0;

        DatabaseReference databaseReferenceChat=databaseReferenceUserTable.child(currentUserId).child("chats");
        databaseReferenceChat.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Iterable<DataSnapshot> iterable=snapshot.getChildren();
                if (snapshot.getChildrenCount()>0) {
                    while (iterable.iterator().hasNext()) {
                        DataSnapshot dataSnapshot = iterable.iterator().next();
                        try {
                            count += 1;
                            JSONObject jsonObject = getJSON(dataSnapshot);
                            MessageModelClass messageModelClass = FirebaseParser.INSTANCE.parseOneToOneChatParser(jsonObject);
                            String compare = String.valueOf(messageModelClass.getuId());
                            if (firebaseID.contains(compare)){
                                String chat_id = messageModelClass.getChatId();
                                Intent intent = new Intent(getContext(), ChatDetailActivity.class);
                                intent.putExtra("firebaseID", firebaseID);
                                intent.putExtra("Name", Name);
                                intent.putExtra("Email", Email);
                                intent.putExtra("chatId", chat_id);
                                startActivity(intent);

                                break;

                            }else {
                                Intent intent = new Intent(getContext(), ChatDetailActivity.class);
                                intent.putExtra("firebaseID", firebaseID);
                                intent.putExtra("Name", Name);
                                intent.putExtra("Email", Email);
                                startActivity(intent);
                            }

                            if (firebaseID.contains(messageModelClass.getuId())) {

                                Intent intent = new Intent(getContext(), ChatDetailActivity.class);
                                intent.putExtra("firebaseID", firebaseID);
                                intent.putExtra("Name", Name);
                                intent.putExtra("Email", Email);
                                intent.putExtra("chatId", messageModelClass.getChatId());
                                startActivity(intent);

                                break;

                            } else {

                                if (count == snapshot.getChildrenCount()) {

                                    Intent intent = new Intent(getContext(), ChatDetailActivity.class);
                                    intent.putExtra("firebaseID", firebaseID);
                                    intent.putExtra("Name", Name);
                                    intent.putExtra("Email", Email);

                                }


                            }


                        } catch (Exception e) {
                            Log.e("Exception__", e.toString());
                        }
                    }

                }else {
                    Intent intent = new Intent(getContext(), ChatDetailActivity.class);
                    intent.putExtra("firebaseID", firebaseID);
                    intent.putExtra("Name", Name);
                    intent.putExtra("Email", Email);
                    startActivity(intent);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        System.out.println("int count= (int) snapshot.getChildrenCount();");

    }

    public static JSONObject getJSON(DataSnapshot dataSnapshot) {
        Gson gson=new Gson();
        JSONObject jsonObject=null;

        try {
            jsonObject = new JSONObject(gson.toJson(dataSnapshot.getValue()));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

}