package com.infotech4it.flare.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.infotech4it.flare.R;
import com.infotech4it.flare.databinding.ItemListMoreBinding;
import com.infotech4it.flare.interfaces.MoreInterface;
import com.infotech4it.flare.views.models.MoreModel;

import java.util.ArrayList;

/**
 * Created by Bilal Zaman on 17/10/2020.
 */
public class MoreAdapter extends RecyclerView.Adapter<MoreAdapter.ViewHolder> {
    private Context context;
    private ArrayList<MoreModel> data;
    private MoreInterface moreInterface;

    public MoreAdapter(Context context) {
        this.context = context;
        this.data = new ArrayList<>();
        moreInterface = (MoreInterface) context;
    }

    public void setData(ArrayList<MoreModel> _data) {
        this.data = _data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemListMoreBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_list_more, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.setOnMOdel(data.get(position));
        holder.binding.parent.setOnClickListener(v -> {
           moreInterface.onMoreClick(position);
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemListMoreBinding binding;

        public ViewHolder(@NonNull ItemListMoreBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }
    }
}

