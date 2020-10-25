package com.infotech4it.flare.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.infotech4it.flare.R;
import com.infotech4it.flare.databinding.ItemListChatBinding;
import com.infotech4it.flare.databinding.ItemListFindFriendBinding;
import com.infotech4it.flare.helpers.UIHelper;
import com.infotech4it.flare.interfaces.ChatInterface;
import com.infotech4it.flare.views.models.ChatModel;

import java.util.ArrayList;

/**
 * Created by Bilal Zaman on 25/10/2020.
 */
public class FindFriendAdapter extends RecyclerView.Adapter<FindFriendAdapter.ViewHolder> {
    private Context context;
    private ArrayList<ChatModel> data;

    public FindFriendAdapter(Context context) {
        this.context = context;
        this.data = new ArrayList<>();
    }

    public void setData(ArrayList<ChatModel> _data) {
        this.data = _data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemListFindFriendBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_list_find_friend, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.setOnChatModel(data.get(position));
        holder.binding.imgAddFriend.setOnClickListener(v->{
            UIHelper.showLongToastInCenter(context, "Request Send");
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemListFindFriendBinding binding;

        public ViewHolder(@NonNull ItemListFindFriendBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }
    }
}



