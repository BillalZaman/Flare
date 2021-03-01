package com.infotech4it.flare.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.infotech4it.flare.R;
import com.infotech4it.flare.databinding.ItemListCommentBinding;
import com.infotech4it.flare.helpers.AvatarGenerator;
import com.infotech4it.flare.views.models.CommentModelClass;
import com.infotech4it.flare.views.models.CommentingModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bilal Zaman on 28/10/2020.
 */
public class CommentingAdapter extends RecyclerView.Adapter<CommentingAdapter.ViewHolder> {

    public List<CommentModelClass> commentList;
    private Context context;
    private String user_id;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth mAuth;
    String currentUser;

    public CommentingAdapter(List<CommentModelClass> commentList){
        this.commentList = commentList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemListCommentBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_list_comment, parent, false);

        firebaseFirestore =FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser().getUid();
        context = parent.getContext();

        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String CommentMessage = commentList.get(position).getMessage();
        String user_id_comment = commentList.get(position).getUser_id();
        holder.setCommentMessage(CommentMessage);

        firebaseFirestore.collection("Users").document(user_id_comment).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().exists()){

                        String userPhoto = task.getResult().getString("image");
                        String userName = task.getResult().getString("name");

                        holder.setNamePhoto(userPhoto,userName);
                    }
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemListCommentBinding binding;

        public ViewHolder(@NonNull ItemListCommentBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }

        private void setCommentMessage(String message){
            binding.txtUserLastMessage.setText(message);
        }

        private void setNamePhoto(String userImage_URL, String userName){
            binding.txtUserName.setText(userName);
//            Glide.with(context).load(pic).into(binding.imgUser);
            if(userImage_URL!=null){

                Glide.with(context).
                        load(userImage_URL).
                        error(AvatarGenerator.Companion.avatarImage(context, 200,
                                AvatarGenerator.AvatarConstants.Companion.getCIRCLE(),
                                userName,false))
                        .placeholder(AvatarGenerator.Companion.avatarImage(context, 200,
                                AvatarGenerator.AvatarConstants.Companion.getCIRCLE(),
                                userName,false))
                        .into(binding.imgUser);
            }
            else {
                Glide.with(context)
                        .load(AvatarGenerator.Companion.avatarImage(context, 200,
                                AvatarGenerator.AvatarConstants.Companion.getCIRCLE(),
                                userName,false))
                        .into(binding.imgUser);
            }

        }

    }
}
