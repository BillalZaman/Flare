package com.infotech4it.flare.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.infotech4it.flare.views.activities.ChatDetailActivity;
import com.infotech4it.flare.views.adapters.ChatAdapter;
import com.infotech4it.flare.views.adapters.MessageAdapter;
import com.infotech4it.flare.views.models.ChatFragmentModel;
import com.infotech4it.flare.views.models.ChatModel;
import com.infotech4it.flare.views.models.MessageModelClass;
import com.infotech4it.flare.views.models.SelectStudentForChat;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

public class ChatFragment extends Fragment {
    private FragmentChatBinding binding;
    private ArrayList<ChatModel> data;
    private ChatAdapter chatAdapter;
    private ArrayList<MessageModelClass> arrayList = new ArrayList<>();
    private ArrayList<ChatFragmentModel> arrayListSecond = new ArrayList<>();
    private ArrayList<String> arrayListUserIds = new ArrayList<>();
    // Write a message to the database
    private FirebaseAuth mAuth= FirebaseAuth.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReferenceUserTable = database.getReference("user_table");
    private DatabaseReference databaseReferenceChat;
    private ChildEventListener listener;
    private ProgressDialog dialog;
    private boolean firstTime = true;
    MessageAdapter messageAdapter;

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_chat, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReferenceChat = databaseReferenceUserTable.child(uid).child("chats");

        DatabaseReference databaseReferenceName = databaseReferenceUserTable.child(uid).child("name");
        databaseReferenceName.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.e("JKJK",snapshot.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("JKJK",error.toString());
            }
        });

//        loadChat();
    }

    private void init() {
//        chatAdapter = new ChatAdapter(getContext());
//        data = new ArrayList<>();
//        for(int i=0; i<= 20; i++) {
//            data.add(new ChatModel("John Doe", ""+i+12, "See you Tomorrow then!",""));
//        }
//        chatAdapter.setData(data);
//        binding.recyclerview.setAdapter(chatAdapter);

        messageAdapter = new MessageAdapter(getContext(), arrayList);
        binding.recyclerview.setAdapter(messageAdapter);

        binding.recyclerview.addOnItemTouchListener(new RecyclerItemClickListener(getContext(),
                binding.recyclerview, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
//                Intent intent = new Intent(getContext(), MessageDetailActivity.class);
//                startActivity(intent);
                String name = messageAdapter.arrayList.get(position).getTvName();
//                Toast.makeText(getActivity(), name+"", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), ChatDetailActivity.class);
                intent.putExtra("messagePartner", name);
                intent.putExtra("modal", messageAdapter.arrayList.get(position));
                intent.putExtra("existing", true);
                startActivity(intent);

            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));

    }

//    private void loadChat(){
//
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("user_table");
//        ref.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot requestSnapshot: dataSnapshot.getChildren()) {
//                    ChatFragmentModel chatFragmentModel = requestSnapshot.getValue(ChatFragmentModel.class);
//                    arrayListSecond.add(chatFragmentModel);
//
//                    DataSnapshot productsSnapshot = requestSnapshot.child("chats");
//
//                    for (DataSnapshot productSnapshot: productsSnapshot.getChildren()) {
////                        System.out.println(productSnapshot.child("productName").getValue(String.class));
//                        MessageModelClass messageModelClass = productSnapshot.getValue(MessageModelClass.class);
//                        arrayList.add(messageModelClass);
//                    }
//                }
//
//                messageAdapter.update(arrayList);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                throw databaseError.toException(); // don't ignore errors
//            }
//        });
//
//    }

    private void loadChat() {

        dialog = new ProgressDialog(getContext());
        dialog.setMessage("Loading...");
        dialog.show();

//        arrayList.clear();
//        arrayListUserIds.clear();

//        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        databaseReferenceChat = databaseReferenceUserTable.child(uid).child("chats");

        databaseReferenceChat.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                //snapshot.toString();

                if (arrayList.size() <= snapshot.getChildrenCount()) {
                    arrayList.clear();
                    arrayListUserIds.clear();
                }


                Iterable<DataSnapshot> iterable = snapshot.getChildren();

                //added here for handling the case when the there is no data at first time
                if(!iterable.iterator().hasNext()){
                    firstTime =false;
                    onChildEventListener();
                }

                while (iterable.iterator().hasNext()) {

                    DataSnapshot dataSnapshot = iterable.iterator().next();
                    //dataSnapshot.getValue().toString();

                    try {
                        //JSONObject jsonObject=new JSONObject(dataSnapshot.getValue().toString());


                        JSONObject jsonObject = getJSON(dataSnapshot);

                        MessageModelClass messageModelClass = FirebaseParser.INSTANCE.parseOneToOneChatParser(jsonObject);

//                        if (arrayList.size()==snapshot.getChildrenCount()) {
//                            arrayList.clear();
//                            arrayListUserIds.clear();
//                        }

                        if (messageModelClass.getTvMsgTime().length() > 0) {
                            arrayList.add(messageModelClass);
                            arrayListUserIds.add(messageModelClass.getuId());
                        }

                        if (arrayList.size() == snapshot.getChildrenCount()) {

                            getUserDetails();
                        }

//                        getUserDetails();

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

                        if (firstTime) {

                            firstTime = false;

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

    @Override
    public void onResume() {
        super.onResume();
        loadChat();
//        arrayList.clear();
//        arrayListUserIds.clear();
//        messageAdapter.notifyDataSetChanged();

        //added here for handling the case when the there is no data at first time
//        if(firstTime){
//
//            loadChat();
//        }


    }


    private void onChildEventListener() {


        listener = databaseReferenceChat.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                JSONObject jsonObject= getJSON(snapshot);

                MessageModelClass messageModelClass= FirebaseParser.INSTANCE.parseOneToOneChatParser(jsonObject);

                boolean isAlreadyInList=false;

                for (int x = 0; x < arrayListUserIds.size(); x++) {

                    if (arrayListUserIds.get(x)==messageModelClass.getuId()) {
                        isAlreadyInList=true;
                        break;
                    }
                }

                if(!isAlreadyInList){
                    //  arrayList.add(0,messageModelClass);
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
                    //messageAdapter.notifyDataSetChanged();
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


                        arrayList.add(finalIndex,FirebaseParser.INSTANCE.
                                parseOneToOneChatParser(data.getJSONObject(data.keys().next())));


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }


                if (map.containsKey("name"))
                    arrayList.get(finalIndex).setTvName(String.valueOf(map.get("name")));
                if (map.containsKey("profile"))
                    arrayList.get(finalIndex).setProfile(String.valueOf(map.get("profile")));

                messageAdapter.notifyItemInserted(finalIndex);
                //  if (finalIndex ==arrayListUserIds.size()-1) {

                bubbleSort(arrayList, arrayListUserIds, messageAdapter);




                //   }

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
        //  MessageModelClass messageModelClass = new MessageModelClass();
        String id = String.valueOf(0);
        for (int i = 0; i < n; i++) {
            for (int j = 1; j < (n - i); j++) {
                if (Long.parseLong(arrayList.get(j - 1).getTvMsgTime()) < Long.parseLong(arrayList.get(j).getTvMsgTime())) {
                    //swap elements
                    MessageModelClass  messageModelClass=new MessageModelClass( arrayList.get(j - 1));
                    id = arrayListUserIds.get(j - 1);
                    arrayList.set(j - 1, arrayList.get(j));
                    arrayListUserIds.set(j - 1, arrayListUserIds.get(j));
                    arrayList.set(j, messageModelClass);
                    arrayListUserIds.set(j, id);
//                        arr[j-1] = arr[j];
//                        arr[j] = temp;
                }

            }
        }

        messageAdapter.update(arrayList);

    }

}