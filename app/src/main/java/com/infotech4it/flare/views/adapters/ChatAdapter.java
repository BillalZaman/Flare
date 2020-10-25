package com.infotech4it.flare.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.infotech4it.flare.R;
import com.infotech4it.flare.databinding.ItemListChatBinding;
import com.infotech4it.flare.interfaces.ChatInterface;
import com.infotech4it.flare.views.models.ChatModel;

import java.util.ArrayList;

/**
 * Created by Bilal Zaman on 23/10/2020.
 */
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private Context context;
    private ArrayList<ChatModel> data;
    private ChatInterface chatInterface;

    public ChatAdapter(Context context) {
        this.context = context;
        this.data = new ArrayList<>();
        chatInterface = (ChatInterface) context;
    }

    public void setData(ArrayList<ChatModel> _data) {
        this.data = _data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemListChatBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_list_chat, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.setOnChatModel(data.get(position));
        holder.binding.parent.setOnClickListener(v -> {
            chatInterface.chatClick(position);
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemListChatBinding binding;

        public ViewHolder(@NonNull ItemListChatBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }
    }
}


