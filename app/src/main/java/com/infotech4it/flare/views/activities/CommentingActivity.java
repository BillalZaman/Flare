package com.infotech4it.flare.views.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.infotech4it.flare.R;
import com.infotech4it.flare.databinding.ActivityCommentingBinding;
import com.infotech4it.flare.views.adapters.CommentingAdapter;
import com.infotech4it.flare.views.models.CommentModelClass;
import com.infotech4it.flare.views.models.CommentingModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentingActivity extends AppCompatActivity {
    private ActivityCommentingBinding binding;
    private ArrayList<CommentingModel> data;
    private CommentingAdapter adapter;
    private String BlogPostId;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth mAuth;
    public List<CommentModelClass> commentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_commenting);
        initialize();
    }

    public void initialize(){
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        BlogPostId = getIntent().getStringExtra("BlogPostId");
        SetName(BlogPostId);

        commentList = new ArrayList<>();
        adapter = new CommentingAdapter(commentList);

        binding.recyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        binding.recyclerview.setAdapter(adapter);

        Query mainQuery = firebaseFirestore.collection("Posts/" + BlogPostId + "/Comments")
                .orderBy("timeStamp", Query.Direction.ASCENDING);

        mainQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {


                for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                    if (doc.getType() == DocumentChange.Type.ADDED) {

                        CommentModelClass commentData = doc.getDocument().toObject(CommentModelClass.class);
                        commentList.add(commentData);
                        adapter.notifyDataSetChanged();
                    }
                }


            }
        });

        binding.buttonChatboxSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String comment = binding.edittextChatbox.getText().toString();

                if(!comment.isEmpty()){

                    Map<String,Object> commentMap = new HashMap<>();
                    commentMap.put("Message",comment);
                    commentMap.put("user_id",mAuth.getCurrentUser().getUid());
                    commentMap.put("timeStamp", FieldValue.serverTimestamp());

                    firebaseFirestore.collection("Posts/" + BlogPostId + "/Comments").add(commentMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {

                            if(task.isSuccessful()){
                                binding.edittextChatbox.setText(null);
                            }

                        }
                    });

                }
            }
        });

        binding.imgBack.setOnClickListener(v->{
            finish();
        });

    }

    public void SetName(String blogID){

        firebaseFirestore.collection("Posts").document(blogID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        String user_id = task.getResult().getString("user_id");

                        GetName(user_id);

                    }
                }
            }
        });

    }

    public void GetName(String userID){

        firebaseFirestore.collection("Users").document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        String name = task.getResult().getString("name");
                        String image = task.getResult().getString("image");

                        binding.textView.setText(name);

                    }
                }
            }
        });

    }
}