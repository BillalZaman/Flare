package com.infotech4it.flare.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.infotech4it.flare.R;
import com.infotech4it.flare.databinding.ItemListUserFriendListBinding;
import com.infotech4it.flare.helpers.UIHelper;
import com.infotech4it.flare.views.models.UserFriendsModel;

import java.util.ArrayList;

/**
 * Created by Bilal Zaman on 26/10/2020.
 */
public class UserFriendListAdapter extends RecyclerView.Adapter<UserFriendListAdapter.ViewHolder> {
    private Context context;
    private ArrayList<UserFriendsModel> data;

    public UserFriendListAdapter(Context context) {
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
        ItemListUserFriendListBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_list_user_friend_list, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.setOnUserFriendModel(data.get(position));
        Glide.with(context).load(R.drawable.billal).into(holder.binding.imgFriendUser);
        holder.binding.parent.setOnClickListener(v -> {
            UIHelper.showLongToastInCenter(context, "not yet implemented");
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemListUserFriendListBinding binding;

        public ViewHolder(@NonNull ItemListUserFriendListBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }
    }
}


