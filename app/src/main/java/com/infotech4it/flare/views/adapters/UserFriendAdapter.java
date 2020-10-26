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
import com.infotech4it.flare.databinding.ItemListFriendlistBinding;
import com.infotech4it.flare.helpers.UIHelper;
import com.infotech4it.flare.views.activities.UserFriendListActivity;
import com.infotech4it.flare.views.models.NewsFeedModel;
import com.infotech4it.flare.views.models.UserFriendsModel;

import java.util.ArrayList;

/**
 * Created by Bilal Zaman on 26/10/2020.
 */

public class UserFriendAdapter extends RecyclerView.Adapter<UserFriendAdapter.ViewHolder> {
    private Context context;
    private ArrayList<UserFriendsModel> data;

    public UserFriendAdapter(Context context) {
        this.context = context;
        this.data = new ArrayList<>();
    }

    public void setData(ArrayList<UserFriendsModel> _data) {
        this.data = _data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemListFriendlistBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_list_friendlist, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(context).load(R.drawable.billal).into(holder.binding.imgFriendUser);
        holder.binding.imgFriendUser.setOnClickListener(v->{
            UIHelper.openActivity((Activity) context, UserFriendListActivity.class);
        });
    }

    @Override
    public int getItemCount() {
        return 120;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemListFriendlistBinding binding;

        public ViewHolder(@NonNull ItemListFriendlistBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }
    }
}

