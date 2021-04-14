package com.infotech4it.flare.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.infotech4it.flare.R;
import com.infotech4it.flare.databinding.FragmentRequestsBinding;
import com.infotech4it.flare.helpers.AvatarGenerator;
import com.infotech4it.flare.helpers.UIHelper;
import com.infotech4it.flare.views.models.Requests;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestsActivity extends AppCompatActivity {

    public FragmentRequestsBinding binding;
    String user_UId;
    private View view;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private DatabaseReference userDatabaseReference;
    // for accept and cancel mechanism
    private DatabaseReference friendsDatabaseReference;
    private DatabaseReference friendReqDatabaseReference;


    public RequestsActivity() {
        // Required empty public constructor
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.fragment_requests);
        binding.requestList.setHasFixedSize(true);

        mAuth = FirebaseAuth.getInstance();
        user_UId = mAuth.getCurrentUser().getUid();
        userDatabaseReference = FirebaseDatabase.getInstance().getReference().child("user_table");
        databaseReference = FirebaseDatabase.getInstance().getReference().child("friend_requests").child(user_UId);

        friendsDatabaseReference = FirebaseDatabase.getInstance().getReference().child("friends");
        friendReqDatabaseReference = FirebaseDatabase.getInstance().getReference().child("friend_requests");


//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(tg);
//        //linearLayoutManager.setStackFromEnd(true);
//        binding.requestList.setHasFixedSize(true);
//        binding.requestList.setLayoutManager(linearLayoutManager);

    }


    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Requests> recyclerOptions = new FirebaseRecyclerOptions.Builder<Requests>()
                .setQuery(databaseReference, Requests.class)
                .build();

        FirebaseRecyclerAdapter<Requests, RequestsVH> adapter = new FirebaseRecyclerAdapter<Requests, RequestsVH>(recyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull final RequestsVH holder, int position, @NonNull Requests model) {
                final String userID = getRef(position).getKey();
                // handling accept/cancel button
                DatabaseReference getTypeReference = getRef(position).child("request_type").getRef();
                getTypeReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String requestType = dataSnapshot.getValue().toString();

                            if (requestType.equals("received")) {
                                userDatabaseReference.child(userID).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        final String userName = dataSnapshot.child("name").getValue().toString();
                                        final String userThumbPhoto = dataSnapshot.child("profile").getValue().toString();
                                        final String user_status = dataSnapshot.child("email").getValue().toString();

                                        holder.name.setText(userName);
                                        holder.status.setText(user_status);

                                        if (userThumbPhoto != null && !userThumbPhoto.trim().equals("")) {

                                            Glide.with(holder.user_photo).
                                                    load(userThumbPhoto).
                                                    error(AvatarGenerator.Companion.avatarImage(RequestsActivity.this, 200,
                                                            AvatarGenerator.AvatarConstants.Companion.getCIRCLE(),
                                                            userName, false))
                                                    .placeholder(AvatarGenerator.Companion.avatarImage(RequestsActivity.this, 200,
                                                            AvatarGenerator.AvatarConstants.Companion.getCIRCLE(),
                                                            userName, false))
                                                    .into(holder.user_photo);
                                        } else {
                                            Glide.with(holder.user_photo)
                                                    .load(AvatarGenerator.Companion.avatarImage(RequestsActivity.this, 200,
                                                            AvatarGenerator.AvatarConstants.Companion.getCIRCLE(),
                                                            userName, false))
                                                    .into(holder.user_photo);
                                        }


                                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                CharSequence[] options = new CharSequence[]{"Accept Request", "Cancel Request"};

                                                AlertDialog.Builder builder = new AlertDialog.Builder(RequestsActivity.this);

                                                builder.setItems(options, (dialog, which) -> {

                                                    if (which == 0) {
                                                        Calendar myCalendar = Calendar.getInstance();
                                                        SimpleDateFormat currentDate = new SimpleDateFormat("EEEE, dd MMM, yyyy");
                                                        final String friendshipDate = currentDate.format(myCalendar.getTime());

                                                        friendsDatabaseReference.child(user_UId).child(userID).child("date").setValue(friendshipDate)
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {

                                                                        friendsDatabaseReference.child(userID).child(user_UId).child("date").setValue(friendshipDate)
                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                        /**
                                                                                         *  because of accepting friend request,
                                                                                         *  there have no more request them. So, for delete these node
                                                                                         */
                                                                                        friendReqDatabaseReference.child(user_UId).child(userID).removeValue()
                                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                    @Override
                                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                                        if (task.isSuccessful()) {
                                                                                                            // delete from users friend_requests node, receiver >> sender > values
                                                                                                            friendReqDatabaseReference.child(userID).child(user_UId).removeValue()
                                                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                        @Override
                                                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                                                            if (task.isSuccessful()) {
                                                                                                                                // after deleting data
                                                                                                                                UIHelper.showLongToastInCenter(RequestsActivity.this, "This person is your friend now");
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


                                                    if (which == 1) {
                                                        //for cancellation, delete data from user nodes
                                                        // delete from, sender >> receiver > values
                                                        friendReqDatabaseReference.child(user_UId).child(userID).removeValue()
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {
                                                                            // delete from, receiver >> sender > values
                                                                            friendReqDatabaseReference.child(userID).child(user_UId).removeValue()
                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                            if (task.isSuccessful()) {
                                                                                                //Toast.makeText(getActivity(), "Cancel Request", Toast.LENGTH_SHORT).show();
                                                                                                Snackbar snackbar = Snackbar
                                                                                                        .make(view, "Canceled Request", Snackbar.LENGTH_LONG);
                                                                                                // Changing message text color
                                                                                                View sView = snackbar.getView();
                                                                                                sView.setBackgroundColor(ContextCompat.getColor(RequestsActivity.this, R.color.colorPrimary));
                                                                                                TextView textView = sView.findViewById(R.id.snackbar_text);
                                                                                                textView.setTextColor(Color.WHITE);
                                                                                                snackbar.show();

                                                                                            }
                                                                                        }

                                                                                    });

                                                                        }
                                                                    }

                                                                });
                                                    }
                                                    if (which == 2) {
//                                                            Intent profileIntent = new Intent(getContext(), ProfileActivity.class);
//                                                            profileIntent.putExtra("visitUserId", userID);
//                                                            startActivity(profileIntent);
                                                    }

                                                });
                                                builder.show();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                            if (requestType.equals("sent")) {
                                userDatabaseReference.child(userID).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        final String userName = dataSnapshot.child("name").getValue().toString();
                                        final String userThumbPhoto = dataSnapshot.child("profile").getValue().toString();
                                        final String user_status = dataSnapshot.child("email").getValue().toString();

                                        holder.name.setText(userName);
                                        holder.status.setText(user_status);

                                        if (userThumbPhoto != null && !userThumbPhoto.trim().equals("")) {

                                            Glide.with(holder.user_photo).
                                                    load(userThumbPhoto).
                                                    error(AvatarGenerator.Companion.avatarImage(RequestsActivity.this, 200,
                                                            AvatarGenerator.AvatarConstants.Companion.getCIRCLE(),
                                                            userName, false))
                                                    .placeholder(AvatarGenerator.Companion.avatarImage(RequestsActivity.this, 200,
                                                            AvatarGenerator.AvatarConstants.Companion.getCIRCLE(),
                                                            userName, false))
                                                    .into(holder.user_photo);
                                        } else {
                                            Glide.with(holder.user_photo)
                                                    .load(AvatarGenerator.Companion.avatarImage(RequestsActivity.this, 200,
                                                            AvatarGenerator.AvatarConstants.Companion.getCIRCLE(),
                                                            userName, false))
                                                    .into(holder.user_photo);
                                        }

                                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                CharSequence[] options = new CharSequence[]{"Cancel Sent Request"};

                                                AlertDialog.Builder builder = new AlertDialog.Builder(RequestsActivity.this);

                                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        if (which == 0) {
                                                            //for cancellation, delete data from user nodes
                                                            // delete from, sender >> receiver > values
                                                            friendReqDatabaseReference.child(user_UId).child(userID).removeValue()
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful()) {
                                                                                // delete from, receiver >> sender > values
                                                                                friendReqDatabaseReference.child(userID).child(user_UId).removeValue()
                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                if (task.isSuccessful()) {
                                                                                                    Snackbar snackbar = Snackbar
                                                                                                            .make(view, "Cancel Sent Request", Snackbar.LENGTH_LONG);
                                                                                                    // Changing message text color
                                                                                                    View sView = snackbar.getView();
                                                                                                    sView.setBackgroundColor(ContextCompat.getColor(RequestsActivity.this, R.color.colorPrimary));
                                                                                                    TextView textView = sView.findViewById(R.id.snackbar_text);
                                                                                                    textView.setTextColor(Color.WHITE);
                                                                                                    snackbar.show();
                                                                                                }
                                                                                            }

                                                                                        });

                                                                            }
                                                                        }

                                                                    });
                                                        }
                                                        if (which == 1) {
//                                                            Intent profileIntent = new Intent(getContext(), ProfileActivity.class);
//                                                            profileIntent.putExtra("visitUserId", userID);
//                                                            startActivity(profileIntent);
                                                        }

                                                    }
                                                });
                                                builder.show();
                                            }

                                        });
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                    }
                                });
                            }

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @NonNull
            @Override
            public RequestsVH onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.selectuserforchat, viewGroup, false);
                return new RequestsVH(view);
            }
        };
        binding.requestList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class RequestsVH extends RecyclerView.ViewHolder {
        TextView name, status;
        CircleImageView user_photo;

        public RequestsVH(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.txtUsername);
            user_photo = itemView.findViewById(R.id.imgFriendUser);
            status = itemView.findViewById(R.id.txtUserlocation);
        }
    }

}
