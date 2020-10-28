package com.infotech4it.flare.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.infotech4it.flare.R;
import com.infotech4it.flare.databinding.ItemListCommentBinding;
import com.infotech4it.flare.views.models.CommentingModel;

import java.util.ArrayList;

/**
 * Created by Bilal Zaman on 28/10/2020.
 */
public class CommentingAdapter extends RecyclerView.Adapter<CommentingAdapter.ViewHolder> {
    private Context context;
    private ArrayList<CommentingModel> data;

    public CommentingAdapter(Context context) {
        this.context = context;
        this.data = new ArrayList<>();
    }

    public void setData(ArrayList<CommentingModel> _data) {
        this.data = _data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemListCommentBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_list_comment, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.setOnModel(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemListCommentBinding binding;

        public ViewHolder(@NonNull ItemListCommentBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }
    }
}
