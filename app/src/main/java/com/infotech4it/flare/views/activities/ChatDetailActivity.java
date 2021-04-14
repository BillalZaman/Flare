package com.infotech4it.flare.views.activities;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.infotech4it.flare.R;
import com.infotech4it.flare.databinding.ActivityChatDetailBinding;
import com.infotech4it.flare.helpers.AAppGlobal;
import com.infotech4it.flare.helpers.LoaderDialog;
import com.infotech4it.flare.views.adapters.MessageDetailAdapter;
import com.infotech4it.flare.views.models.FirebaseUserTableModal;
import com.infotech4it.flare.views.models.MessageDetailClass;
import com.infotech4it.flare.views.models.MessageModelClass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatDetailActivity extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReferenceUserTable = database.getReference("user_table");
    DatabaseReference databaseReferenceOneToOneChat = database.getReference("one_to_one_chat");
    List<MessageDetailClass> mChats = new ArrayList<>();
    MessageDetailClass messageDetailClass = new MessageDetailClass();
    String chatId = null;
    private ActivityChatDetailBinding binding;
    private Context context;
    private MessageDetailAdapter mAdapter;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final FirebaseUserTableModal firebaseUserTableModal = new FirebaseUserTableModal();
    private MessageModelClass messageModelClass = new MessageModelClass();
    private int unReadMessageCount = 1;
    private boolean loadChatFirstTime = true;
    private String currentUserId;
    private long sendMessageTime = 0;
    private String mUserId, mUserName, mUserEmail, mPhotoURL;
    private LoaderDialog loaderDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentUserId = mAuth.getCurrentUser().getUid();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat_detail);
        init();
        loaderDialog = new LoaderDialog(this);

        if (getIntent().hasExtra("existing")) {
            messageModelClass = (MessageModelClass) getIntent().getSerializableExtra("modal");
            chatId = messageModelClass.getChatId();
            mUserId = String.valueOf(messageModelClass.getuId());
            mPhotoURL = messageModelClass.getProfile();
            mUserName = messageModelClass.getTvName();
            binding.textView.setText(mUserName);
            mAdapter.setData(messageModelClass, true);
        } else {
            getUserData();
            if (mUserId != null) {
                databaseReferenceUserTable.child(mUserId).addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if (!dataSnapshot.exists()) {
                                    firebaseUserTableModal.setId(Integer.parseInt(mUserId));
                                    firebaseUserTableModal.setName(mUserName);
                                    firebaseUserTableModal.setProfile(mPhotoURL);
                                    databaseReferenceUserTable.child(mUserId).setValue(firebaseUserTableModal);

                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
            }

        }

    }

    private void init() {

        context = this;
        currentUserId = mAuth.getCurrentUser().getUid();

        mAdapter = new MessageDetailAdapter(mChats, context, currentUserId);
        binding.rvChat.setAdapter(mAdapter);

        binding.imgBack.setOnClickListener(view -> finish());

        binding.edittextChatbox.setOnFocusChangeListener((view, isFocused) -> {
            if (!isFocused) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(binding.edittextChatbox.getWindowToken(), 0);
            }
        });

        binding.buttonChatboxSend.setOnClickListener(v -> {
            sendMessage();
        });

        binding.delete.setVisibility(View.GONE);

        binding.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                DeleteChat();

                try {

                    DatabaseReference db_node = FirebaseDatabase.getInstance().getReference().child("user_table").child(currentUserId).child("chats").child(chatId);
                    db_node.removeValue();

                    DatabaseReference db_node_first = FirebaseDatabase.getInstance().getReference().child("user_table").child(mUserId).child("chats").child(chatId);
                    db_node_first.removeValue();

                    DatabaseReference db_node_second = FirebaseDatabase.getInstance().getReference().child("one_to_one_chat").child(chatId);
                    db_node_second.removeValue();

                } finally {
                    finish();
                }
            }
        });

    }

    private void getUserData() {

        mUserId = getIntent().getExtras().getString("firebaseID");
        mUserName = getIntent().getExtras().getString("Name");
        chatId = getIntent().getStringExtra("chatId");
        mUserEmail = getIntent().getExtras().getString("mUserEmail");
        mPhotoURL = getIntent().getExtras().getString("mPhotoURL");

        binding.textView.setText(mUserName);
        mAdapter.setNameandProfile(mUserName, mPhotoURL, false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        currentUserId = mAuth.getCurrentUser().getUid();
        if (chatId != null) {
            loadChat();
        }
        StopChat();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (chatId != null) {
            DatabaseReference referenceSender = null;
            referenceSender = databaseReferenceUserTable.child(currentUserId).child("chats").child(String.valueOf(chatId));
            referenceSender.child("unread_message_count").setValue(0);

        }

    }

    private void sendMessage() {

        sendMessageTime = Long.parseLong(AAppGlobal.Companion.getUniTime());
        updateFrontScreen(binding.edittextChatbox.getText().toString());

        messageDetailClass = new MessageDetailClass();
        messageDetailClass.setMessage_type(AAppGlobal.Companion.TYPE_SEND);
        messageDetailClass.setView_type(AAppGlobal.Companion.TEXT_TYPE);
        messageDetailClass.setTime(String.valueOf(sendMessageTime));
        messageDetailClass.setMessage(binding.edittextChatbox.getText().toString());
        messageDetailClass.setSenderName(currentUserId);

        databaseReferenceOneToOneChat.child(chatId).push().setValue(messageDetailClass);
        binding.edittextChatbox.setText("");
    }

    private void updateFrontScreen(String message) {

        DatabaseReference referenceSender = null;
        DatabaseReference referenceReceiver = null;

        if (mChats.size() == 0) {

            chatId = "_";
            chatId += AAppGlobal.Companion.getUniTime();
            chatId += "_";

            referenceSender = databaseReferenceUserTable.child(String.valueOf(currentUserId)).child("chats").
                    child(String.valueOf(chatId));

            Map<String, Object> map = new HashMap<>();
            map.put("u_id", String.valueOf(mUserId));
            map.put("chat_id", String.valueOf(chatId));
            map.put("recent_message", String.valueOf(message));
            map.put("unread_message_count", 0);
            map.put("time", sendMessageTime);
            map.put("f_id", String.valueOf(currentUserId));
            referenceSender.setValue(map);

            Map<String, Object> mapp = new HashMap<>();
            mapp.put("u_id", String.valueOf(currentUserId));
            mapp.put("chat_id", chatId);
            mapp.put("recent_message", message);
            mapp.put("unread_message_count", unReadMessageCount);
            mapp.put("time", sendMessageTime);
            mapp.put("f_id", String.valueOf(currentUserId));
            referenceReceiver = databaseReferenceUserTable.child(String.valueOf(mUserId)).child("chats")
                    .child(String.valueOf(chatId));
            referenceReceiver.setValue(mapp);

            unReadMessageCount += 1;

            if (chatId != null) {
                refreshChat();
            }


        } else {

            referenceSender = databaseReferenceUserTable.child(String.valueOf(currentUserId)).child("chats").child(String.valueOf(chatId));
            referenceReceiver = databaseReferenceUserTable.child(String.valueOf(mUserId)).child("chats").child(String.valueOf(chatId));


            referenceSender.child("u_id").setValue(String.valueOf(mUserId));
            referenceSender.child("chat_id").setValue(chatId);
            referenceSender.child("recent_message").setValue(message);
            referenceSender.child("unread_message_count").setValue(0);
            referenceSender.child("time").setValue(sendMessageTime);
            referenceSender.child("f_id").setValue(String.valueOf(currentUserId));
//            referenceSender.child(AAppGlobal.Companion.UserRoleId).setValue(AApplicationGlobal.readUserRoleId(context, "USERROLEID", 0));

            referenceReceiver.child("u_id").setValue(String.valueOf(currentUserId));
            referenceReceiver.child("chat_id").setValue(chatId);
            referenceReceiver.child("recent_message").setValue(message);
            referenceReceiver.child("unread_message_count").setValue(unReadMessageCount);
            referenceReceiver.child("time").setValue(sendMessageTime);
            referenceReceiver.child("f_id").setValue(String.valueOf(currentUserId));
//            referenceReceiver.child(AAppGlobal.Companion.UserRoleId).setValue(AApplicationGlobal.readUserRoleId(context, "USERROLEID", 0));

            unReadMessageCount += 1;

        }
    }

    private void loadChat() {

        DatabaseReference databaseReferenceChat = databaseReferenceOneToOneChat.child(chatId);
        databaseReferenceChat.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                mChats.clear();
                mAdapter.notifyDataSetChanged();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    dataSnapshot.getValue();
                    MessageDetailClass messageDetailClass = dataSnapshot.getValue(MessageDetailClass.class);

                    boolean isFound = false;

                    for (int index = 0; index < mChats.size(); index++) {
                        if (mChats.get(index).getTime().equals(messageDetailClass.getTime())) {
                            isFound = true;
                            break;
                        }

                        if (index == mChats.size() - 1) {

                        }

                    }

                    if (!isFound) {
                        mChats.add(messageDetailClass);
                        mAdapter.notifyDataSetChanged();
                        binding.rvChat.scrollToPosition(mChats.size() - 1);
                    }


                }

                if (loadChatFirstTime) {
                    loadChatFirstTime = false;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void refreshChat() {

        DatabaseReference databaseReferenceChat = databaseReferenceOneToOneChat.child(chatId);
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d("onChildAdded", "onChildAdded:" + dataSnapshot.getValue());

                if (true) {
                    MessageDetailClass messageDetailClass = dataSnapshot.getValue(MessageDetailClass.class);
                    mChats.add(messageDetailClass);
                    try {
                        Thread.sleep(250);
                        mAdapter.notifyDataSetChanged();
                        binding.rvChat.scrollToPosition(mChats.size() - 1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d("onChildChanged", "onChildChanged:" + dataSnapshot.getKey());

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d("onChildRemoved", "onChildRemoved:" + dataSnapshot.getKey());
                String commentKey = dataSnapshot.getKey();

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d("onChildMoved", "onChildMoved:" + dataSnapshot.getKey());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("onCancelled", "postComments:onCancelled", databaseError.toException());

            }
        };
        databaseReferenceChat.addChildEventListener(childEventListener);
    }

    public void StopChat() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference rootRef = database.getReference("friends").child(currentUserId);

        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(mUserId)) {
                    binding.nofriend.setVisibility(View.GONE);
                    binding.edittextChatbox.setVisibility(View.VISIBLE);
                    binding.buttonChatboxSend.setVisibility(View.VISIBLE);
//                    Toast.makeText(context, "This Person is still friend", Toast.LENGTH_LONG).show();
                } else {
//                    Toast.makeText(context, "This Person is no more friend", Toast.LENGTH_LONG).show();
                    binding.nofriend.setVisibility(View.VISIBLE);
                    binding.edittextChatbox.setVisibility(View.GONE);
                    binding.buttonChatboxSend.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void DeleteChat() {
        loaderDialog.startLoadingDialog();
        database.getReference("user_table").child(currentUserId).child("chats").removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {

                            database.getReference("user_table").child(mUserId).child("chats").removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()) {

                                                database.getReference("one_to_one_chat").child(chatId).removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                if (task.isSuccessful()) {
                                                                    RecheckDelete();
//                                                                    try {
//                                                                        Toast.makeText(ChatDetailActivity.this, "Chat is Deleted Successfully...!!", Toast.LENGTH_SHORT).show();
//                                                                    }finally {
//                                                                        finish();
//                                                                    }
                                                                } else {
                                                                    loaderDialog.dismiss();
                                                                }
                                                            }
                                                        });

                                            } else {
                                                loaderDialog.dismiss();
                                            }
                                        }
                                    });

                        } else {
                            loaderDialog.dismiss();
                        }
                    }
                });
    }

    public void RecheckDelete() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference rootRef = database.getReference("user_table").child(currentUserId);

        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("chats")) {
                    database.getReference("user_table").child(currentUserId).child("chats").removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {
                                        try {
                                            loaderDialog.dismiss();
                                            Toast.makeText(ChatDetailActivity.this, "Chat is Deleted Successfully...!!", Toast.LENGTH_SHORT).show();
                                        } finally {
                                            finish();
                                        }
                                    } else {
                                        loaderDialog.dismiss();
                                    }
                                }
                            });

                } else {
                    try {
                        loaderDialog.dismiss();
                        Toast.makeText(ChatDetailActivity.this, "Chat is Deleted Successfully...!!", Toast.LENGTH_SHORT).show();
                    } finally {
                        finish();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

//    public void deletemessages(){
//
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
//        Query sender = ref.child("user_table").child(currentUserId).orderByChild("chats");
//        sender.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
//                    appleSnapshot.getRef().removeValue();
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
////                Log.e(TAG, "onCancelled", databaseError.toException());
//            }
//        });
//
//        Query receiver = ref.child("user_table").child(mUserId).orderByChild("chats");
//        receiver.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
//                    appleSnapshot.getRef().removeValue();
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
////                Log.e(TAG, "onCancelled", databaseError.toException());
//            }
//        });
//
//        Query chatid = ref.child("one_to_one_chat").orderByChild(chatId);
//        chatid.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
//                    appleSnapshot.getRef().removeValue();
//                }
//
//                finish();
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
////                Log.e(TAG, "onCancelled", databaseError.toException());
//            }
//        });
//
//    }
}