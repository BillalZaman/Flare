package com.infotech4it.flare.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.infotech4it.flare.R;
import com.infotech4it.flare.databinding.FragmentFindFriendBinding;
import com.infotech4it.flare.helpers.AvatarGenerator;
import com.infotech4it.flare.helpers.FirebaseParser;
import com.infotech4it.flare.helpers.UIHelper;
import com.infotech4it.flare.views.activities.ChatDetailActivity;
import com.infotech4it.flare.views.adapters.FindFriendAdapter;
import com.infotech4it.flare.views.models.ChatModel;
import com.infotech4it.flare.views.models.Friends;
import com.infotech4it.flare.views.models.MessageModelClass;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindFriendActivity extends AppCompatActivity {
    private FragmentFindFriendBinding binding;
    private ArrayList<ChatModel> data;
    private FindFriendAdapter chatAdapter;
    private int count=0;
    private DatabaseReference friendsDatabaseReference;
    private DatabaseReference userDatabaseReference;
    private FirebaseAuth mAuth;
    private String currentUserId;
    String current_user_id;
    Context context;

    public FindFriendActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(
                this, R.layout.fragment_find_friend);

        init();
    }

    private void init() {

        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();
        currentUserId = current_user_id;
        friendsDatabaseReference = FirebaseDatabase.getInstance().getReference().child("friends").child(current_user_id);
        userDatabaseReference = FirebaseDatabase.getInstance().getReference().child("user_table");



//        chatAdapter = new FindFriendAdapter(this);
//        data = new ArrayList<>();
//        for (int i = 0; i <= 20; i++) {
//            data.add(new ChatModel("John Doe", "" + i + 12, "See you Tomorrow then!",
//                    "Lahore"));
//        }
//        chatAdapter.setData(data);
//        binding.recyclerview.setAdapter(chatAdapter);
//        binding.setOnClick(this);

//        UserFriendListFragment userFriendListFragment = new UserFriendListFragment();
//        AcceptedFriendsFragment userFriendListFragment = new AcceptedFriendsFragment();
//        UIHelper.replaceFragment(getContext(), R.id.frameLayout, userFriendListFragment);
//        binding.txtFriends.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
//        binding.txtFriends.setTextColor(getResources().getColor(R.color.white));
    }

//    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.txtFriends: {
////                UserFriendListFragment userFriendListFragment = new UserFriendListFragment();
//                AcceptedFriendsFragment userFriendListFragment = new AcceptedFriendsFragment();
//                UIHelper.replaceFragment(getContext(), R.id.frameLayout, userFriendListFragment);
//                binding.txtFriends.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
//                binding.txtFriends.setTextColor(getResources().getColor(R.color.white));
//                resetColorPreference(1);
//                break;
//            }
//            case R.id.txtFindFriends: {
//                AllUsersListingFragment allUsersListing = new AllUsersListingFragment();
//                UIHelper.replaceFragment(getContext(), R.id.frameLayout, allUsersListing);
//                binding.txtFindFriends.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
//                binding.txtFindFriends.setTextColor(getResources().getColor(R.color.white));
//                resetColorPreference(2);
//                break;
//            }
//            case R.id.txtRequests: {
//                RequestsFragment requestsFragment = new RequestsFragment();
//                UIHelper.replaceFragment(getContext(), R.id.frameLayout, requestsFragment);
//                binding.txtRequests.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
//                binding.txtRequests.setTextColor(getResources().getColor(R.color.white));
//                resetColorPreference(3);
//                break;
//            }
//        }
//    }

//    public void resetColorPreference(int position) {
//        if (position == 1) {
////                binding.txtFriends.setBackgroundColor(getResources().getColor(R.color.grey_light));
////                binding.txtFriends.setTextColor(getResources().getColor(R.color.black));
//
//            binding.txtFindFriends.setBackgroundColor(getResources().getColor(R.color.light_color));
//            binding.txtFindFriends.setTextColor(getResources().getColor(R.color.black));
//
//            binding.txtRequests.setBackgroundColor(getResources().getColor(R.color.light_color));
//            binding.txtRequests.setTextColor(getResources().getColor(R.color.black));
//
//        } else if (position == 2) {
//            binding.txtFriends.setBackgroundColor(getResources().getColor(R.color.light_color));
//            binding.txtFriends.setTextColor(getResources().getColor(R.color.black));
//
////                binding.txtFindFriends.setBackgroundColor(getResources().getColor(R.color.light_color));
////                binding.txtFindFriends.setTextColor(getResources().getColor(R.color.black));
//
//            binding.txtRequests.setBackgroundColor(getResources().getColor(R.color.light_color));
//            binding.txtRequests.setTextColor(getResources().getColor(R.color.black));
//
//        } else if (position == 3) {
//            binding.txtFriends.setBackgroundColor(getResources().getColor(R.color.light_color));
//            binding.txtFriends.setTextColor(getResources().getColor(R.color.black));
//
//            binding.txtFindFriends.setBackgroundColor(getResources().getColor(R.color.light_color));
//            binding.txtFindFriends.setTextColor(getResources().getColor(R.color.black));
//
////                binding.txtRequests.setBackgroundColor(getResources().getColor(R.color.light_color));
////                binding.txtRequests.setTextColor(getResources().getColor(R.color.black));
//
//        }
//    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Friends> recyclerOptions = new FirebaseRecyclerOptions.Builder<Friends>()
                .setQuery(friendsDatabaseReference, Friends.class)
                .build();

        FirebaseRecyclerAdapter<Friends, AcceptedFriendsFragment.ChatsVH> adapter = new FirebaseRecyclerAdapter<Friends, AcceptedFriendsFragment.ChatsVH>(recyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull final AcceptedFriendsFragment.ChatsVH holder, int position, @NonNull Friends model) {
                final String userID = getRef(position).getKey();
                userDatabaseReference.child(userID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            final String userName = dataSnapshot.child("name").getValue().toString();
                            final String userEmail = dataSnapshot.child("email").getValue().toString();
                            final String userProfile = dataSnapshot.child("profile").getValue().toString();

                            if(userProfile!=null && !userProfile.trim().equals("")){

                                Glide.with(holder.user_photo).
                                        load(userProfile).
                                        error(AvatarGenerator.Companion.avatarImage(FindFriendActivity.this, 200,
                                                AvatarGenerator.AvatarConstants.Companion.getCIRCLE(),
                                                userName,false))
                                        .placeholder(AvatarGenerator.Companion.avatarImage(FindFriendActivity.this,
                                                200,
                                                AvatarGenerator.AvatarConstants.Companion.getCIRCLE(),
                                                userName,false))
                                        .into(holder.user_photo);
                            }
                            else {
                                Glide.with(holder.user_photo)
                                        .load(AvatarGenerator.Companion.avatarImage(FindFriendActivity.this, 200,
                                                AvatarGenerator.AvatarConstants.Companion.getCIRCLE(),
                                                userName,false))
                                        .into(holder.user_photo);
                            }

                            holder.user_name.setText(userName);
                            holder.user_presence.setText(userEmail);

                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    loadChat(userID, userName, userEmail);

                                }
                            });
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @NonNull
            @Override
            public AcceptedFriendsFragment.ChatsVH onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.selectuserforchat, viewGroup,
                        false);
                return new AcceptedFriendsFragment.ChatsVH(view);
            }
        };

        binding.recyclerview.setAdapter(adapter);
        adapter.startListening();
    }

    public static class ChatsVH extends RecyclerView.ViewHolder{
        TextView user_name, user_presence;
        CircleImageView user_photo;
        public ChatsVH(View itemView) {
            super(itemView);
            user_name = itemView.findViewById(R.id.txtUsername);
            user_photo = itemView.findViewById(R.id.imgFriendUser);
            user_presence = itemView.findViewById(R.id.txtUserlocation);
        }
    }


    private void loadChat(String firebaseID, String Name, String Email) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReferenceUserTable = database.getReference("user_table");
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
                            if (firebaseID.contains(compare)) {
                                String chat_id = messageModelClass.getChatId();
                                Intent intent = new Intent(FindFriendActivity.this, ChatDetailActivity.class);
                                intent.putExtra("firebaseID", firebaseID);
                                intent.putExtra("Name", Name);
                                intent.putExtra("Email", Email);
                                intent.putExtra("chatId", chat_id);
                                startActivity(intent);
                                finish();
                                break;

                            }
//                            }else {
//                                Intent intent = new Intent(FindFriendActivity.this, ChatDetailActivity.class);
//                                intent.putExtra("firebaseID", firebaseID);
//                                intent.putExtra("Name", Name);
//                                intent.putExtra("Email", Email);
//                                startActivity(intent);
//                            }

//                            if (firebaseID.contains(messageModelClass.getuId())) {
//
//                                Intent intent = new Intent(FindFriendActivity.this, ChatDetailActivity.class);
//                                intent.putExtra("firebaseID", firebaseID);
//                                intent.putExtra("Name", Name);
//                                intent.putExtra("Email", Email);
//                                intent.putExtra("chatId", messageModelClass.getChatId());
//                                startActivity(intent);
//
//                                break;
//
//                            } else {
//
//                                if (count == snapshot.getChildrenCount()) {
//
//                                    Intent intent = new Intent(FindFriendActivity.this, ChatDetailActivity.class);
//                                    intent.putExtra("firebaseID", firebaseID);
//                                    intent.putExtra("Name", Name);
//                                    intent.putExtra("Email", Email);
//
//                                }
//
//
//                            }


                        } catch (Exception e) {
                            Log.e("Exception__", e.toString());
                        }
                    }

                }
                else {
                    Intent intent = new Intent(FindFriendActivity.this, ChatDetailActivity.class);
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