package com.infotech4it.flare.views.adapters;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.infotech4it.flare.R;
import com.infotech4it.flare.helpers.AvatarGenerator;
import com.infotech4it.flare.helpers.LoaderDialog;
import com.infotech4it.flare.views.activities.CommentingActivity;
import com.infotech4it.flare.views.models.BlogPost;
import com.infotech4it.flare.views.models.MessageModelClass;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class BlogRecycleAdapter extends RecyclerView.Adapter<BlogRecycleAdapter.ViewHolder> implements Filterable {

    public List<BlogPost> blogList;
    private Context context;
    private String user_id;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth mAuth;
    String blogPostId, currentUser;
    public List<BlogPost> filterlist;
    private ValueFilter valueFilter;
    private LoaderDialog loaderDialog;

    public BlogRecycleAdapter(List<BlogPost> blog_list, Activity activity){
        this.blogList = blog_list;
        this.filterlist = blog_list;
        loaderDialog = new LoaderDialog(activity);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {

        firebaseFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser().getUid();
    }


        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.blog_list_item,parent,false);
        context = parent.getContext();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        final String blogPostId = blogList.get(position).BlogPostId;

        String desc_text = blogList.get(position).getDesc();
        holder.setDescText(desc_text);

        String download_uri = blogList.get(position).getImage_url();
        String thumb_uri = blogList.get(position).getImage_thumb();
        holder.setImage(download_uri,thumb_uri);

        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            user_id = blogList.get(position).getUser_id();
            firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        if (task.getResult().exists()) {
                            String name = task.getResult().getString("name");
                            String image = task.getResult().getString("image");

                            holder.setUserImage(image, name);
                            holder.setUserName(name);

                        }
                    }
                }
            });

        }

        long milliseconds = blogList.get(position).getTimeStamp().getSeconds();

        String pattern = "E, dd MMM yy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String dateString1 = simpleDateFormat.format(new Date(milliseconds));
        pattern = "HH:mm a";
        simpleDateFormat = new SimpleDateFormat(pattern);
        String dateString2 = simpleDateFormat.format(new Date(milliseconds));

        holder.setDate(dateString1+" at "+dateString2);

        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            firebaseFirestore.collection("Posts/" + blogPostId + "/Likes").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                    try {
                        if (!documentSnapshots.isEmpty()) {
                            int count = documentSnapshots.size();
                            holder.setLikes(count);
                        } else {
                            holder.setLikes(0);
                        }
                    }
                    catch (NullPointerException E){

                    }
                }
            });
        }

        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            firebaseFirestore.collection("Posts/" + blogPostId + "/Comments").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                    try {
                        if (!documentSnapshots.isEmpty()) {
                            int countComments = documentSnapshots.size();
                            holder.setComments(countComments);
                        } else {
                            holder.setComments(0);
                        }
                    }
                    catch (NullPointerException E){

                    }
                }
            });
        }

        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            firebaseFirestore.collection("Posts/" + blogPostId + "/Likes").document(currentUser).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {

                    try {
                        if (documentSnapshot.exists()) {
                            holder.BlogLikeBtn.setImageDrawable(context.getDrawable(R.mipmap.image_like_red));
                        } else {
                            holder.BlogLikeBtn.setImageDrawable(context.getDrawable(R.mipmap.image_like_gray));
                        }
                    }
                    catch (NullPointerException E){

                    }
                }
            });
        }

        holder.lyLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (FirebaseAuth.getInstance().getCurrentUser() != null) {

                    firebaseFirestore.collection("Posts/" + blogPostId + "/Likes").document(currentUser).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                            if (!task.getResult().exists()) {
                                Map<String, Object> likeMap = new HashMap<>();
                                likeMap.put("timeStamp", FieldValue.serverTimestamp());
                                firebaseFirestore.collection("Posts/" + blogPostId + "/Likes").document(currentUser).set(likeMap);
                            } else {

                                firebaseFirestore.collection("Posts/" + blogPostId + "/Likes").document(currentUser).delete();
                            }

                        }
                    });


                }
            }
        });

        holder.lyComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent commentIntent = new Intent(context, CommentingActivity.class);
                commentIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                commentIntent.putExtra("BlogPostId",blogPostId);
                context.startActivity(commentIntent);
            }
        });

        holder.lyShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loaderDialog.startLoadingDialog();
                String share = blogList.get(position).getImage_url();
                String txt = blogList.get(position).getDesc();
                if (share!=null && !share.trim().equals("")){
                    if (share.contains("null")){
                        loaderDialog.dismiss();
                        Toast.makeText(context, "You cannot share this Post", Toast.LENGTH_SHORT).show();
                    }else {
                        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                        builder.detectFileUriExposure();
                        StrictMode.setVmPolicy(builder.build());
                        shareImage(share, context);

                    }

                }else{
                    loaderDialog.dismiss();
                    Toast.makeText(context, "You cannot share this Post", Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.lyTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String lati = String.valueOf(blogList.get(position).getLatitude());
                String longi = String.valueOf(blogList.get(position).getLongitude());

                startGoolgeMaps(lati, longi);
            }
        });
    }

    @Override
    public int getItemCount() {
        return blogList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView descView;
        private TextView userName;
        private TextView postDate;
        private ImageView postImage;
        private CircleImageView userImage;
        private View mview;
        private TextView blogLikeCount;
        private TextView blogCommentCount;
        private ImageView BlogLikeBtn;
        private ImageView BlogCommentBtn;
        private ImageView Share;
        LinearLayout lyLike, lyComment, lyShare, lyTrack;

        public ViewHolder(View itemView) {
            super(itemView);
            mview = itemView;

            BlogLikeBtn = mview.findViewById(R.id.blog_like);
            blogCommentCount = mview.findViewById(R.id.blog_comment_count);
            BlogCommentBtn = mview.findViewById(R.id.blog_comment);
            blogLikeCount = mview.findViewById(R.id.blog_like_count);
            userName = mview.findViewById(R.id.commentUsername);
            postDate = mview.findViewById(R.id.blogDate);
            postImage = mview.findViewById(R.id.blogImage);
            userImage = mview.findViewById(R.id.commentProfilePic);
            Share = mview.findViewById(R.id.share);
            lyLike = mview.findViewById(R.id.ly_like);
            lyComment = mview.findViewById(R.id.ly_comment);
            lyShare = mview.findViewById(R.id.ly_share);
            lyTrack = mview.findViewById(R.id.ly_track);
        }

        public void setDescText(String descText){
            descView = mview.findViewById(R.id.blogDescription);
            if (descText.trim().isEmpty()){
                descView.setVisibility(View.GONE);
            }else {
                descView.setVisibility(View.VISIBLE);
                descView.setText(descText);
            }

        }

        public void setUserName(String userNameText){
            userName.setText(userNameText);
        }

        public void setDate(String dateOfPost){
            postDate.setText(dateOfPost);
        }

        public void setImage(String downloadURL, String thumb_url){
            if (downloadURL.contains("null")){
                postImage.setVisibility(View.GONE);
                lyShare.setVisibility(View.GONE);
            }else {
                postImage.setVisibility(View.VISIBLE);
                Glide.with(context).load(downloadURL).thumbnail(Glide.with(context).load(thumb_url)).into(postImage);
            }

        }

        public void setUserImage(String userImage_URL, String userName){
//            Glide.with(context).load(userImage_URL).into(userImage);

            if(userImage_URL!=null){

                Glide.with(context).
                        load(userImage_URL).
                        error(AvatarGenerator.Companion.avatarImage(context, 200,
                                AvatarGenerator.AvatarConstants.Companion.getCIRCLE(),
                                userName,false))
                        .placeholder(AvatarGenerator.Companion.avatarImage(context, 200,
                                AvatarGenerator.AvatarConstants.Companion.getCIRCLE(),
                                userName,false))
                        .into(userImage);
            }
            else {
                Glide.with(context)
                        .load(AvatarGenerator.Companion.avatarImage(context, 200,
                                AvatarGenerator.AvatarConstants.Companion.getCIRCLE(),
                                userName,false))
                        .into(userImage);
            }

        }

        private void setLikes(int count){
            String text = count + " Likes";
            blogLikeCount.setText(text);
        }

        private void setComments(int countC){
            String textC = countC + " Comments";
            blogCommentCount.setText(textC);
        }
    }

    @Override
    public Filter getFilter() {
        if (valueFilter == null){
            valueFilter = new ValueFilter();
        }
        return valueFilter;
    }

    private class ValueFilter extends Filter{
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if (constraint != null && constraint.length()>0){
                List<BlogPost> filterlist2 = new ArrayList<>();
                for (int i = 0; i < filterlist.size(); i++){
                    if ((filterlist.get(i).getUser_name().toUpperCase()).contains(constraint.toString().toUpperCase())){
                        filterlist2.add(filterlist.get(i));
                    }
                }
                results.count = filterlist2.size();
                results.values = filterlist2;
            }
            else {
                results.count = filterlist.size();
                results.values = filterlist;
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            blogList = (ArrayList<BlogPost>) results.values;
            notifyDataSetChanged();
        }
    }

    public void shareImage(String url, final Context context) {
        Picasso.get().load(url).into(new Target() {
            @Override public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                builder.detectFileUriExposure();
                StrictMode.setVmPolicy(builder.build());
                loaderDialog.dismiss();
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("image/*");
                i.putExtra(Intent.EXTRA_STREAM, getLocalBitmapUri(bitmap, context));
                context.startActivity(Intent.createChooser(i, "Share Image"));
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                loaderDialog.dismiss();
            }

            @Override public void onPrepareLoad(Drawable placeHolderDrawable) {
                loaderDialog.dismiss();
            }
        });

    }
    static public Uri getLocalBitmapUri(Bitmap bmp, Context context) {
        Uri bmpUri = null;
        try {
            File file =  new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }

    public void updateAdapter(List<BlogPost>updatedList){
        blogList = updatedList;
        filterlist = updatedList;
        notifyDataSetChanged();
    }


    private void startGoolgeMaps(String latitude, String longitude) {

        String uri = "http://maps.google.com/maps?daddr=" + latitude + "," + longitude + " (" + "Destination" + ")";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setPackage("com.google.android.apps.maps");
        context.startActivity(intent);

    }

}
