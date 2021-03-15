package com.infotech4it.flare.views.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.infotech4it.flare.R;
import com.infotech4it.flare.helpers.AvatarGenerator;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private Button sendFriendRequest_Button, declineFriendRequest_Button;
    private TextView txtName, txtEmail, txtNumber;
    private ImageView profileImage;
    private DatabaseReference userDatabaseReference;
    private DatabaseReference friendRequestReference;
    private FirebaseAuth mAuth;
    private String CURRENT_STATE;
    public String receiver_userID;
    public String mName, mEmail, mNumber, mProfile;
    public String senderID;
    private DatabaseReference friendsDatabaseReference;
    private DatabaseReference notificationDatabaseReference;
    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        context = this;
        init();
        setViews();

    }

    public void init(){

        userDatabaseReference = FirebaseDatabase.getInstance().getReference().child("user_table");
        friendRequestReference = FirebaseDatabase.getInstance().getReference().child("friend_requests");
        friendRequestReference.keepSynced(true);
        mAuth = FirebaseAuth.getInstance();
        senderID = mAuth.getCurrentUser().getUid();
        friendsDatabaseReference = FirebaseDatabase.getInstance().getReference().child("friends");
        friendsDatabaseReference.keepSynced(true);
        notificationDatabaseReference = FirebaseDatabase.getInstance().getReference().child("notifications");
        notificationDatabaseReference.keepSynced(true);
        CURRENT_STATE = "not_friends";

        sendFriendRequest_Button = findViewById(R.id.visitUserFrndRqstSendButton);
        declineFriendRequest_Button = findViewById(R.id.visitUserFrndRqstDeclineButton);
        profileImage = findViewById(R.id.visit_user_profile_image);
        txtName = findViewById(R.id.name);
        txtEmail = findViewById(R.id.email);
        txtNumber = findViewById(R.id.phone);

        receiver_userID = getIntent().getExtras().get("visitUserId").toString();
        mName = getIntent().getExtras().get("mName").toString();
        mEmail = getIntent().getExtras().get("mEmail").toString();
        mNumber = getIntent().getExtras().get("mNumber").toString();
        mProfile = getIntent().getExtras().get("mProfile").toString();

        mToolbar = findViewById(R.id.single_profile_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(mName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("tag", "onClick : navigating back to back activity ");
                finish();
            }
        });

        txtName.setText(mName);
        txtEmail.setText(mEmail);
        txtNumber.setText(mNumber);

        if(mProfile!=null && !mProfile.trim().equals("")){

            Glide.with(profileImage.getContext()).
                    load(mProfile).
                    error(AvatarGenerator.Companion.avatarImage(context, 200,
                            AvatarGenerator.AvatarConstants.Companion.getCIRCLE(),
                            mName,false))
                    .placeholder(AvatarGenerator.Companion.avatarImage(context, 200,
                            AvatarGenerator.AvatarConstants.Companion.getCIRCLE(),
                            mName,false))
                    .into(profileImage);
        }
        else {
            Glide.with(profileImage.getContext())
                    .load(AvatarGenerator.Companion.avatarImage(context, 200,
                            AvatarGenerator.AvatarConstants.Companion.getCIRCLE(),
                            mName,false))
                    .into(profileImage);
        }


    }

    public void setViews(){

        userDatabaseReference.child(receiver_userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // for fixing dynamic cancel / friend / unfriend / accept button
                friendRequestReference.child(senderID)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                // if in database has these data then, execute conditions below
                                if (dataSnapshot.hasChild(receiver_userID)) {
                                    String requestType = dataSnapshot.child(receiver_userID)
                                            .child("request_type").getValue().toString();

                                    if (requestType.equals("sent")) {
                                        CURRENT_STATE = "request_sent";
                                        sendFriendRequest_Button.setText("Cancel Friend Request");

                                        declineFriendRequest_Button.setVisibility(View.INVISIBLE);
                                        declineFriendRequest_Button.setEnabled(false);

                                    } else if (requestType.equals("received")) {
                                        CURRENT_STATE = "request_received";
                                        sendFriendRequest_Button.setText("Accept Friend Request");

                                        declineFriendRequest_Button.setVisibility(View.VISIBLE);
                                        declineFriendRequest_Button.setEnabled(true);


                                        declineFriendRequest_Button.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                declineFriendRequest();
                                            }
                                        });

                                    }

                                } else {

                                    friendsDatabaseReference.child(senderID)
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.exists()){
                                                        if (dataSnapshot.hasChild(receiver_userID)){
                                                            CURRENT_STATE = "friends";
                                                            sendFriendRequest_Button.setText("Unfriend This Person");

                                                            declineFriendRequest_Button.setVisibility(View.INVISIBLE);
                                                            declineFriendRequest_Button.setEnabled(false);

                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        declineFriendRequest_Button.setVisibility(View.GONE);
        declineFriendRequest_Button.setEnabled(false);

        /** Send / Cancel / Accept / Unfriend >> request mechanism */
        if (!senderID.equals(receiver_userID)){ // condition for current owner / sender id
            sendFriendRequest_Button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendFriendRequest_Button.setEnabled(false);

                    if (CURRENT_STATE.equals("not_friends")){
                        sendFriendRequest();

                    } else if(CURRENT_STATE.equals("request_sent")){
                        cancelFriendRequest();

                    } else if (CURRENT_STATE.equals("request_received")){
                        acceptFriendRequest();

                    } else if (CURRENT_STATE.equals("friends")){
                        unfriendPerson();

                    }

                }
            });
        } else {
            sendFriendRequest_Button.setVisibility(View.INVISIBLE);
            declineFriendRequest_Button.setVisibility(View.INVISIBLE);
        }


    }

    private void declineFriendRequest() {
        //for declination, delete data from friends_request nodes
        // delete from, sender >> receiver > values
        friendRequestReference.child(senderID).child(receiver_userID).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            // delete from, receiver >> sender > values
                            friendRequestReference.child(receiver_userID).child(senderID).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                // after deleting data, just set button attributes
                                                sendFriendRequest_Button.setEnabled(true);
                                                CURRENT_STATE = "not_friends";
                                                sendFriendRequest_Button.setText("Send Friend Request");

                                                declineFriendRequest_Button.setVisibility(View.INVISIBLE);
                                                declineFriendRequest_Button.setEnabled(false);

                                            }
                                        }

                                    });

                        }
                    }

                });
    }

    private void unfriendPerson() {
        //for unfriend, delete data from friends nodes
        // delete from, sender >> receiver > values
        friendsDatabaseReference.child(senderID).child(receiver_userID).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()){
                            friendsDatabaseReference.child(receiver_userID).child(senderID).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            sendFriendRequest_Button.setEnabled(true);
                                            CURRENT_STATE = "not_friends";
                                            sendFriendRequest_Button.setText("Send Friend Request");

                                            declineFriendRequest_Button.setVisibility(View.INVISIBLE);
                                            declineFriendRequest_Button.setEnabled(false);

                                        }
                                    });
                        }
                    }
                });
    }

    private void acceptFriendRequest() {
        //
        Calendar myCalendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("EEEE, dd MMM, yyyy");
        final String friendshipDate = currentDate.format(myCalendar.getTime());

        friendsDatabaseReference.child(senderID).child(receiver_userID).child("date").setValue(friendshipDate)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        friendsDatabaseReference.child(receiver_userID).child(senderID).child("date").setValue(friendshipDate)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        /**
                                         *  because of accepting friend request,
                                         *  there have no more request them. So, for delete these node
                                         */
                                        friendRequestReference.child(senderID).child(receiver_userID).removeValue()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()){
                                                            // delete from users friend_requests node, receiver >> sender > values
                                                            friendRequestReference.child(receiver_userID).child(senderID).removeValue()
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful()){
                                                                                // after deleting data, just set button attributes
                                                                                sendFriendRequest_Button.setEnabled(true);
                                                                                CURRENT_STATE = "friends";
                                                                                sendFriendRequest_Button.setText("Unfriend This Person");

                                                                                declineFriendRequest_Button.setVisibility(View.INVISIBLE);
                                                                                declineFriendRequest_Button.setEnabled(false);
                                                                            }
                                                                        }

                                                                    });

                                                        }
                                                    }

                                                }); //

                                    }
                                });
                    }
                });
    }



    private void cancelFriendRequest() {
        //for cancellation, delete data from user nodes
        // delete from, sender >> receiver > values
        friendRequestReference.child(senderID).child(receiver_userID).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            // delete from, receiver >> sender > values
                            friendRequestReference.child(receiver_userID).child(senderID).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                // after deleting data, just set button attributes
                                                sendFriendRequest_Button.setEnabled(true);
                                                CURRENT_STATE = "not_friends";
                                                sendFriendRequest_Button.setText("Send Friend Request");

                                                declineFriendRequest_Button.setVisibility(View.INVISIBLE);
                                                declineFriendRequest_Button.setEnabled(false);

                                            }
                                        }

                                    });

                        }
                    }

                });

    }



    private void sendFriendRequest() {
        // insert or, put data to >> sender >> receiver >> request_type >> sent
        friendRequestReference.child(senderID).child(receiver_userID)
                .child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()){

                            // change or, put data to >> receiver >> sender>> request_type >> received
                            friendRequestReference.child(receiver_userID).child(senderID)
                                    .child("request_type").setValue("received")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()){

                                                //Request notification mechanism
                                                HashMap<String, String> notificationData = new HashMap<String, String>();
                                                notificationData.put("from", senderID);
                                                notificationData.put("type", "request");

                                                notificationDatabaseReference.child(receiver_userID).push().setValue(notificationData)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                if (task.isSuccessful()){
                                                                    // Request main mechanism
                                                                    sendFriendRequest_Button.setEnabled(true);
                                                                    CURRENT_STATE = "request_sent";
                                                                    sendFriendRequest_Button.setText("Cancel Friend Request");

                                                                    declineFriendRequest_Button.setVisibility(View.INVISIBLE);
                                                                    declineFriendRequest_Button.setEnabled(false);
                                                                }

                                                            }
                                                        });

                                            }
                                        }
                                    });


                        }



                    }
                });


    }


}
