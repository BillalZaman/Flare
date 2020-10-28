package com.infotech4it.flare.views.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.infotech4it.flare.R;
import com.infotech4it.flare.databinding.ItemListNewsfeedBinding;
import com.infotech4it.flare.helpers.UIHelper;
import com.infotech4it.flare.views.activities.CommentingActivity;
import com.infotech4it.flare.views.activities.MapActivity;
import com.infotech4it.flare.views.models.NewsFeedModel;

import java.util.ArrayList;

/**
 * Created by Bilal Zaman on 16/10/2020.
 */
public class NewsFeedAdapter extends RecyclerView.Adapter<NewsFeedAdapter.ViewHolder> {
    private Context context;
    private ArrayList<NewsFeedModel> data;
    private boolean likeHit = false;
    private int i =0;

    public NewsFeedAdapter(Context context) {
        this.context = context;
        this.data = new ArrayList<>();
    }

    public void setData(ArrayList<NewsFeedModel> _data) {
        this.data = _data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemListNewsfeedBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_list_newsfeed, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.setOnMOdel(data.get(position));
        Glide.with(context).load(R.drawable.nature).into(holder.binding.imgNews);
        Glide.with(context).load(R.drawable.nature).into(holder.binding.imgUser);

        holder.binding.txtLike.setOnClickListener(v -> {
            UIHelper.showLongToastInCenter(context, "Like");
            if (!likeHit){
                likeHit = true;
                i+=i+1;
                holder.binding.txtLike.setText("Like"+ " " + i);
            }
        });

        holder.binding.txtComment.setOnClickListener(v -> {
            UIHelper.showLongToastInCenter(context, "Comment");
            UIHelper.openActivity((Activity) context, CommentingActivity.class);
        });

        holder.binding.txtShare.setOnClickListener(v -> {
            UIHelper.showLongToastInCenter(context, "Share");
        });

        holder.binding.txtTrack.setOnClickListener(v -> {
            UIHelper.showLongToastInCenter(context, "Track");
            UIHelper.openActivity((Activity) context, MapActivity.class);

        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemListNewsfeedBinding binding;

        public ViewHolder(@NonNull ItemListNewsfeedBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }
    }
}
