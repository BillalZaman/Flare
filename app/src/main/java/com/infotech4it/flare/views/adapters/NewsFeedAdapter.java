package com.infotech4it.flare.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.infotech4it.flare.R;
import com.infotech4it.flare.databinding.ItemListNewsfeedBinding;
import com.infotech4it.flare.views.models.NewsFeedModel;

import java.util.ArrayList;

/**
 * Created by Bilal Zaman on 16/10/2020.
 */
public class NewsFeedAdapter extends RecyclerView.Adapter<NewsFeedAdapter.ViewHolder> {
    private Context context;
    private ArrayList<NewsFeedModel> data;

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
