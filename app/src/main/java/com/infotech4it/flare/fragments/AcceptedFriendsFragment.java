package com.infotech4it.flare.fragments;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
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
import com.infotech4it.flare.helpers.AvatarGenerator;
import com.infotech4it.flare.helpers.FirebaseParser;
import com.infotech4it.flare.views.activities.ChatDetailActivity;
import com.infotech4it.flare.views.models.Friends;
import com.infotech4it.flare.views.models.MessageModelClass;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class AcceptedFriendsFragment extends Fragment {

    String current_user_id;
    Context context;
    private View view;
    private RecyclerView chat_list;
    private int count = 0;
    private DatabaseReference friendsDatabaseReference;
    private DatabaseReference userDatabaseReference;
    private FirebaseAuth mAuth;
    private String currentUserId;

    public AcceptedFriendsFragment() {
        // Required empty public constructor
    }

    public static JSONObject getJSON(DataSnapshot dataSnapshot) {
        Gson gson = new Gson();
        JSONObject jsonObject = null;

        try {
            jsonObject = new JSONObject(gson.toJson(dataSnapshot.getValue()));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_user_friend_list, container, false);
        context = getActivity();
        chat_list = view.findViewById(R.id.recyclerview);

        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();
        currentUserId = current_user_id;

        friendsDatabaseReference = FirebaseDatabase.getInstance().getReference().child("friends").child(current_user_id);
        userDatabaseReference = FirebaseDatabase.getInstance().getReference().child("user_table");

        chat_list.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        chat_list.setLayoutManager(linearLayoutManager);


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Friends> recyclerOptions = new FirebaseRecyclerOptions.Builder<Friends>()
                .setQuery(friendsDatabaseReference, Friends.class)
                .build();

        FirebaseRecyclerAdapter<Friends, ChatsVH> adapter = new FirebaseRecyclerAdapter<Friends, ChatsVH>(recyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull final ChatsVH holder, int position, @NonNull Friends model) {
                final String userID = getRef(position).getKey();
                userDatabaseReference.child(userID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            final String userName = dataSnapshot.child("name").getValue().toString();
                            final String userEmail = dataSnapshot.child("email").getValue().toString();
                            final String userProfile = dataSnapshot.child("profile").getValue().toString();
                            final String latitude = dataSnapshot.child("latitude").getKey().toString();
                            final String longitude = dataSnapshot.child("longitude").getKey().toString();

                            if (userProfile != null && !userProfile.trim().equals("")) {

                                Glide.with(holder.user_photo.getContext()).
                                        load(userProfile).
                                        error(AvatarGenerator.Companion.avatarImage(context, 200,
                                                AvatarGenerator.AvatarConstants.Companion.getCIRCLE(),
                                                userName, false))
                                        .placeholder(AvatarGenerator.Companion.avatarImage(context, 200,
                                                AvatarGenerator.AvatarConstants.Companion.getCIRCLE(),
                                                userName, false))
                                        .into(holder.user_photo);
                            } else {
                                Glide.with(holder.user_photo.getContext())
                                        .load(AvatarGenerator.Companion.avatarImage(context, 200,
                                                AvatarGenerator.AvatarConstants.Companion.getCIRCLE(),
                                                userName, false))
                                        .into(holder.user_photo);
                            }

                            holder.user_name.setText(userName);
                            holder.user_presence.setText(userEmail);

                            holder.location.setOnClickListener(v -> {
                                String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?q=loc:%f,%f", latitude
                                        , longitude);
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                                startActivity(intent);
                            });

                            holder.itemView.setOnClickListener(v -> {
                                loadChat(userID, userName, userEmail);
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
            public ChatsVH onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.selectuserforchat, viewGroup, false);
                return new ChatsVH(view);
            }
        };

        chat_list.setAdapter(adapter);
        adapter.startListening();
    }

    private void loadChat(String firebaseID, String Name, String Email) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReferenceUserTable = database.getReference("user_table");
        count = 0;

        DatabaseReference databaseReferenceChat = databaseReferenceUserTable.child(currentUserId).child("chats");
        databaseReferenceChat.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Iterable<DataSnapshot> iterable = snapshot.getChildren();
                if (snapshot.getChildrenCount() > 0) {
                    while (iterable.iterator().hasNext()) {
                        DataSnapshot dataSnapshot = iterable.iterator().next();
                        try {
                            count += 1;
                            JSONObject jsonObject = getJSON(dataSnapshot);
                            MessageModelClass messageModelClass = FirebaseParser.INSTANCE.parseOneToOneChatParser(jsonObject);
                            String compare = String.valueOf(messageModelClass.getuId());
                            if (firebaseID.contains(compare)) {
                                String chat_id = messageModelClass.getChatId();
                                Intent intent = new Intent(getContext(), ChatDetailActivity.class);
                                intent.putExtra("firebaseID", firebaseID);
                                intent.putExtra("Name", Name);
                                intent.putExtra("Email", Email);
                                intent.putExtra("chatId", chat_id);
                                startActivity(intent);

                                break;

                            } else {
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

                } else {
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

    public static class ChatsVH extends RecyclerView.ViewHolder {
        TextView user_name, user_presence;
        ImageView location;
        CircleImageView user_photo;

        public ChatsVH(View itemView) {
            super(itemView);
            user_name = itemView.findViewById(R.id.txtUsername);
            user_photo = itemView.findViewById(R.id.imgFriendUser);
            user_presence = itemView.findViewById(R.id.txtUserlocation);
            location = itemView.findViewById(R.id.imgLocat);
        }
    }
}
