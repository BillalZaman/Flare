package com.infotech4it.flare.fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.infotech4it.flare.R;
import com.infotech4it.flare.databinding.FragmentChatBinding;
import com.infotech4it.flare.helpers.AAppGlobal;
import com.infotech4it.flare.helpers.FirebaseParser;
import com.infotech4it.flare.helpers.RecyclerItemClickListener;
import com.infotech4it.flare.helpers.UIHelper;
import com.infotech4it.flare.views.activities.ChatDetailActivity;
import com.infotech4it.flare.views.activities.HomeActivity;
import com.infotech4it.flare.views.adapters.ChatAdapter;
import com.infotech4it.flare.views.adapters.MessageAdapter;
import com.infotech4it.flare.views.models.ChatFragmentModel;
import com.infotech4it.flare.views.models.ChatModel;
import com.infotech4it.flare.views.models.MessageDetailClass;
import com.infotech4it.flare.views.models.MessageModelClass;
import com.infotech4it.flare.views.models.SelectStudentForChat;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class ChatFragment extends Fragment {
    private FragmentChatBinding binding;
    private ArrayList<ChatModel> data;
    private ChatAdapter chatAdapter;
    private ArrayList<MessageModelClass> arrayList = new ArrayList<>();
    private ArrayList<ChatFragmentModel> arrayListSecond = new ArrayList<>();
    private ArrayList<String> arrayListUserIds = new ArrayList<>();
    private FirebaseAuth mAuth= FirebaseAuth.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReferenceUserTable = database.getReference("user_table");
    private DatabaseReference databaseReferenceChat;
    private ChildEventListener listener;
    private ProgressDialog dialog;
    private boolean firstTime = true;
    MessageAdapter messageAdapter;
    private static ChatFragment instance;
    private String currentUserId;
    private boolean fistTime = true;

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_chat, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getActivity() != null) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        }
        instance = this;

        setviews();

        MessageDetailClass messageDetailClass = new MessageDetailClass();
        messageDetailClass.setMessage_type(AAppGlobal.Companion.TYPE_SEND);
        messageDetailClass.setMessage("abc");
        messageDetailClass.setTime(String.valueOf(new Date().getTime()));

        binding.recyclerview.addOnItemTouchListener(new RecyclerItemClickListener(getContext(),
                binding.recyclerview, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String name = messageAdapter.arrayList.get(position).getTvName();
                Intent intent = new Intent(getActivity(), ChatDetailActivity.class);
                intent.putExtra("messagePartner", name);
                intent.putExtra("modal", messageAdapter.arrayList.get(position));
                intent.putExtra("existing", true);
                startActivity(intent);

            }

            @Override
            public void onLongItemClick(View view, int position) {
                String chatid = messageAdapter.arrayList.get(position).getChatId();
                String uid = messageAdapter.arrayList.get(position).getuId();
                showDeleteAlertDialog(uid, chatid, currentUserId);
            }
        }));

        binding.edtFindFriend.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                messageAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReferenceChat = databaseReferenceUserTable.child(String.valueOf(currentUserId)).child("chats");

        loadChat();

    }

    @Override
    public void onResume() {
        super.onResume();
        loadChat();
    }

    public static ChatFragment getInstance() {
        return instance;
    }

    public void setviews() {

        arrayList = new ArrayList<>();
        messageAdapter = new MessageAdapter(getContext(), arrayList);
        binding.recyclerview.setAdapter(messageAdapter);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.recyclerview.setLayoutManager(layoutManager);

        DividerItemDecoration itemDecoration = new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL);

        binding.recyclerview.addItemDecoration(itemDecoration);

    }

    public void showDeleteAlertDialog(String mUserId, String chatId, String currentuid) {
        // create an alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Delete");
        builder.setMessage("Do you want to Delete this Chat Parmanently ?");

        // add a button
        builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DatabaseReference db_node = FirebaseDatabase.getInstance().getReference().child("user_table").child(currentuid).child("chats").child(chatId);
                db_node.removeValue();

                DatabaseReference db_node_first = FirebaseDatabase.getInstance().getReference().child("user_table").child(mUserId).child("chats").child(chatId);
                db_node_first.removeValue();

                DatabaseReference db_node_second = FirebaseDatabase.getInstance().getReference().child("one_to_one_chat").child(chatId);
                db_node_second.removeValue();

                loadChat();
            }
        });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void loadChat() {

        databaseReferenceChat.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (arrayList.size() <= snapshot.getChildrenCount()) {
                    arrayList.clear();
                    arrayListUserIds.clear();
                }

                Iterable<DataSnapshot> iterable = snapshot.getChildren();
                if(!iterable.iterator().hasNext()){
                    fistTime=false;
                    onChildEventListener();
                }

                while (iterable.iterator().hasNext()) {
                    DataSnapshot dataSnapshot = iterable.iterator().next();
                    try {
                        JSONObject jsonObject = getJSON(dataSnapshot);
                        MessageModelClass messageModelClass = FirebaseParser.INSTANCE.parseOneToOneChatParser(jsonObject);

                        if (messageModelClass.getTvMsgTime().length() > 0) {
                            arrayList.add(messageModelClass);
                            arrayListUserIds.add(messageModelClass.getuId());
                        }

                        if (arrayList.size() == snapshot.getChildrenCount()) {
                            getUserDetails();
                        }

                    } catch (Exception e) {
                        Log.e("Exception__", e.toString());
                    }
                }

                messageAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void getUserDetails() {

        for (int index = 0; index < arrayListUserIds.size(); index++) {
            DatabaseReference databaseReferenceUserDetails = databaseReferenceUserTable.child(String.valueOf(arrayListUserIds.get(index)));
            final int finalIndex = index;
            databaseReferenceUserDetails.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    snapshot.toString();
                    Map<String, Object> map = (Map<String, Object>) snapshot.getValue();

                    if (map == null) {
                        return;
                    }

                    if (map.containsKey("name"))
                        arrayList.get(finalIndex).setTvName(String.valueOf(map.get("name")));
                    if (map.containsKey("profile"))
                        arrayList.get(finalIndex).setProfile(String.valueOf(map.get("profile")));

                    if (finalIndex == arrayListUserIds.size() - 1) {

                        bubbleSort(arrayList, arrayListUserIds, messageAdapter);

                        if (fistTime) {
                            fistTime = false;
//                            onChildEventListener();
                        }

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }

    }

    private void onChildEventListener() {
        listener = databaseReferenceChat.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                JSONObject jsonObject=getJSON(snapshot);
                MessageModelClass messageModelClass= FirebaseParser.INSTANCE.parseOneToOneChatParser(jsonObject);
                boolean isAlreadyInList=false;
                for (int x = 0; x < arrayListUserIds.size(); x++) {
                    if (arrayListUserIds.get(x)==messageModelClass.getuId()) {
                        isAlreadyInList=true;
                        break;
                    }
                }

                if(!isAlreadyInList){
                    arrayListUserIds.add(0,messageModelClass.getuId());
                    getSingleUserDetail(0, messageModelClass.getuId(),true);
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                JSONObject jsonObject = getJSON(snapshot);
                MessageModelClass messageModelClass = FirebaseParser.INSTANCE.parseOneToOneChatParser(jsonObject);
                int foundIndex = -1;
                for (int x = 0; x < arrayList.size(); x++) {
                    if (arrayList.get(x).getChatId().equalsIgnoreCase(messageModelClass.getChatId())) {
                        foundIndex = x;
                        //getSingleUserDetail(x,arrayListUserIds.get(x));
                        break;
                    }
                }

                if (foundIndex >= 0) {
                    String userId = arrayListUserIds.get(foundIndex);
                    arrayList.remove(foundIndex);
                    arrayListUserIds.remove(foundIndex);
                    arrayList.add(0, messageModelClass);
                    arrayListUserIds.add(0, userId);
                    getSingleUserDetail(0, userId,false);

                }

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    private void getSingleUserDetail(int finalIndex, String currentUserId,boolean isChildAddedCalled) {

        DatabaseReference databaseReferenceUserDetails = databaseReferenceUserTable.child(String.valueOf(currentUserId));
        databaseReferenceUserDetails.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                snapshot.toString();
                Map<String, Object> map = (Map<String, Object>) snapshot.getValue();
                if (map == null) {
                    return;
                }
                if(isChildAddedCalled){
                    JSONObject data= null;
                    try {
                        data = new JSONObject(map.get("chats").toString());
                        if(!data.keys().hasNext()) {
                            return;
                        }
                        arrayList.add(finalIndex,FirebaseParser.INSTANCE.parseOneToOneChatParser(data.getJSONObject(data.keys().next())));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }


                if (map.containsKey("name"))
                    arrayList.get(finalIndex).setTvName(String.valueOf(map.get("name")));
                if (map.containsKey("profile"))
                    arrayList.get(finalIndex).setProfile(String.valueOf(map.get("profile")));

                messageAdapter.notifyItemInserted(finalIndex);
                bubbleSort(arrayList, arrayListUserIds, messageAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    @Override
    public void onDetach() {
        super.onDetach();
        if (listener != null)

            databaseReferenceChat.removeEventListener(listener);
        listener = null;

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

    public static void bubbleSort(ArrayList<MessageModelClass> arrayList, ArrayList<String> arrayListUserIds,
                                  MessageAdapter messageAdapter) {
        int n = arrayList.size();
        String id = String.valueOf(0);
        for (int i = 0; i < n; i++) {
            for (int j = 1; j < (n - i); j++) {
                if (Long.parseLong(arrayList.get(j - 1).getTvMsgTime()) < Long.parseLong(arrayList.get(j).getTvMsgTime())) {
                    MessageModelClass  messageModelClass=new MessageModelClass( arrayList.get(j - 1));
                    id = arrayListUserIds.get(j - 1);
                    arrayList.set(j - 1, arrayList.get(j));
                    arrayListUserIds.set(j - 1, arrayListUserIds.get(j));
                    arrayList.set(j, messageModelClass);
                    arrayListUserIds.set(j, id);
                }

            }
        }

        messageAdapter.update(arrayList);

    }


}